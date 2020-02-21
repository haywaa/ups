package com.haywaa.ups.permission.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haywaa.ups.dao.ModuleDAO;
import com.haywaa.ups.dao.ResourceDAO;
import com.haywaa.ups.dao.RoleDAO;
import com.haywaa.ups.dao.RoleResourceDAO;
import com.haywaa.ups.dao.SystemDAO;
import com.haywaa.ups.dao.UserRoleDAO;
import com.haywaa.ups.domain.entity.UserRoleDO;
import com.haywaa.ups.permission.bo.ResourceBO;
import com.haywaa.ups.permission.bo.UserPermissionBO;
import com.haywaa.ups.permission.cache.RoleCache;
import com.haywaa.ups.permission.cache.UserPermissionCache;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.domain.query.UserPermissionQuery;
import com.haywaa.ups.permission.convert.ResourceConvert;
import com.haywaa.ups.permission.service.PermissionQueryService;
import com.haywaa.ups.user.UserService;
import com.haywaa.ups.user.bo.UserBO;

/**
 * ☆ 缓存建立在不删除数据的前提下
 * @author: haywaa
 * @create: 2019-11-25 19:02
 */
@Service
public class PermissionQueryServiceImpl implements PermissionQueryService, InitializingBean {

    @Autowired
    private SystemDAO systemDAO;

    @Autowired
    private ModuleDAO moduleDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private RoleResourceDAO roleResourceDAO;

    @Autowired
    private UserService userService;

    /**
     * systemCode -> resourceId -> ResourceDO
     */
    private Map<String, Map<Integer, ResourceDO>> systemResourceCache = new ConcurrentHashMap();

    /**
     * systemCode -> roleId -> RoleCache
     */
    private Map<String, Map<Integer, RoleCache>> systemRoleCache = new ConcurrentHashMap();

    private LoadingCache<UserPermissionQuery, UserPermissionCache> userPermissionCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            //.refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(key -> createUserPermissionCache(key));

    @Override
    public void afterPropertiesSet() {
        initCache();
    }

    @Override
    public UserPermissionBO queryUserPermission(String systemCode, String channel, String thirdUid) {
        if (systemCode == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "系统编号不能为空");
        }

        if (channel == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交用户来源渠道");
        }

        if (thirdUid == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交渠道用户ID");
        }

        Long userId = userService.selectUserIdByThirdId(channel, thirdUid);
        if (userId == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "用户ID不存在");
        }

        UserPermissionCache permissionCache = userPermissionCache.get(new UserPermissionQuery(systemCode, userId, channel));
        if (permissionCache != null) {
            return null;
        }

        UserPermissionBO userPermission = new UserPermissionBO();
        userPermission.setUserInfo(permissionCache.getUser());

        final List<Integer> roleIds = permissionCache.getRoleItems().stream().map(UserPermissionCache.RoleItem::getRoleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIds)) {
            return userPermission;
        }

        userPermission.setRoleList(selectCachedRoleByRoleIds(systemCode, roleIds));

        List<ResourceBO> resourceList = selectCachedResourceByRoleIds(systemCode, roleIds, null);
        userPermission.setResouceList(resourceList);

        return userPermission;
    }

    /**
     * 构建用户授权缓存对象
     */
    private UserPermissionCache createUserPermissionCache(UserPermissionQuery query) {
        // 1. 获取用户信息
        UserBO userBO = userService.selectById(query.getUserId(), query.getChannel());
        if (userBO == null || !ValidStatus.VALID.toString().equals(userBO.getStatus())) {
            return null;
        }

        UserPermissionCache permissionCache = new UserPermissionCache();
        permissionCache.setUser(userBO);

        // 2. 获取用户角色列表
        List<UserRoleDO> userRoleDOList = userRoleDAO.selectByUserId(query.getUserId(), query.getChannel(), query.getSystemCode());
        if (!CollectionUtils.isEmpty(userRoleDOList)) {
            permissionCache.setRoleItems(userRoleDOList.stream().map(userRoleDO -> {
                UserPermissionCache.RoleItem roleItem = new UserPermissionCache.RoleItem();
                roleItem.setRoleId(userRoleDO.getId());
                roleItem.setRelatedKey(userRoleDO.getRelatedKey());
                return roleItem;
            }).collect(Collectors.toList()));
        }

        return permissionCache;
    }

    /**
     * 获取角色详细信息
     */
    private List<RoleDO> selectCachedRoleByRoleIds(String systemCode, List<Integer> roleIds) {
        if (systemCode == null || CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }

        // 获取角色资源列表
        Map<Integer/*roleId*/, RoleCache> roleCacheMap = systemRoleCache.get(systemCode);
        if (CollectionUtils.isEmpty(roleCacheMap)) {
            return new ArrayList<>();
        }

        List<RoleDO> roleList = new ArrayList<>(roleIds.size());
        for (Integer roleId : roleIds) {
            RoleCache roleCache = roleCacheMap.get(roleId);
            if (roleCache == null) {
                continue;
            }
            roleList.add(roleCache.getRole());
        }

        return roleList;
    }

    /**
     * 查询角色所拥有的资源权限，如果参数resouceId不为null，则只放回这个resourceId的权限信息
     */
    private List<ResourceBO> selectCachedResourceByRoleIds(String systemCode, List<Integer> roleIds, Integer resourceId) {
        if (systemCode == null || CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }

        // 获取角色资源列表
        Map<Integer/*roleId*/, RoleCache> roleCacheMap = systemRoleCache.get(systemCode);
        if (CollectionUtils.isEmpty(roleCacheMap)) {
            return new ArrayList<>();
        }

        Map<Integer, List<Integer>> resourceRoleIdMap = new HashMap<>();
        for (Integer roleId : roleIds) {
            RoleCache roleCache = roleCacheMap.get(roleId);
            if (roleCache == null) {
                continue;
            }

            Collection<Integer> resourceList = roleCache.getResourceList();
            if (CollectionUtils.isEmpty(resourceList)) {
                continue;
            }

            if (resourceId == null) {
                for (Integer resId : resourceList) {
                    List<Integer> roleIdList = resourceRoleIdMap.get(resId);
                    if (roleIdList == null) {
                        roleIdList = new ArrayList<>();
                        resourceRoleIdMap.put(resId, roleIdList);
                    }
                    roleIdList.add(roleId);
                }
            } else if (resourceList.contains(resourceId)) {
                List<Integer> roleIdList = resourceRoleIdMap.get(resourceId);
                if (roleIdList == null) {
                    roleIdList = new ArrayList<>();
                    resourceRoleIdMap.put(resourceId, roleIdList);
                }
                roleIdList.add(roleId);
            }
        }

        if (CollectionUtils.isEmpty(resourceRoleIdMap)) {
            return new ArrayList<>();
        }

        return selectCachedResource(systemCode, resourceRoleIdMap);
    }

    /**
     * sorted
     */
    private List<ResourceBO> selectCachedResource(String systemCode, Map<Integer, List<Integer>> resourceRoleIdMap) {
        if (systemCode == null || CollectionUtils.isEmpty(resourceRoleIdMap)) {
            return new ArrayList<>();
        }

        Map<Integer/*resourceId*/, ResourceDO> resourceMap = systemResourceCache.get(systemCode);
        if (CollectionUtils.isEmpty(resourceMap)) {
            return new ArrayList<>();
        }

        List<ResourceBO> resourceList = resourceRoleIdMap.entrySet().stream().map(entry -> {
            Integer resourceId = entry.getKey();
            ResourceDO resourceDO = resourceMap.get(resourceId);

            // 不需要再做状态判断，因为缓存只用于前端查询，保存的都是有效的
            if (resourceDO == null) {
                return null;
            }

            ResourceBO resourceBO = ResourceConvert.convertDoToBO(resourceDO);
            resourceBO.setRelatedRoleIds(entry.getValue());
            return  resourceBO;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Sort: 按parentCode跟sortNum排序
        resourceList.sort((o1, o2) ->  {
            int compare = o1.getParentCode().compareTo(o2.getParentCode());
            if (compare == 0) {
                if (o1.getSortNum() == null) {
                    o1.setSortNum(0);
                }

                if (o2.getSortNum() == null) {
                    o2.setSortNum(0);
                }
                return o1.getSortNum().compareTo(o2.getSortNum());
            }

            return compare;
        });

        return resourceList;
    }

    private void initCache() {
        List<SystemDO> systemList = systemDAO.selectValidSystem();
        for (SystemDO systemDO : systemList) {
            initSystemCache(systemDO.getCode());
        }
    }

    private void initSystemCache(String systemCode) {
        initSystemResourceCache(systemCode);
        initSystemRoleCache(systemCode);
    }

    private void initSystemResourceCache(String systemCode) {
        List<ResourceDO> resourceList = resourceDAO.selectBySystemCode(systemCode, ValidStatus.VALID.toString());
        if (CollectionUtils.isEmpty(resourceList)) {
            return;
        }

        Map<Integer, ResourceDO> resourceCacheList = new ConcurrentHashMap<>((int)(resourceList.size()/0.75f) +1);
        for (ResourceDO resourceDO : resourceList) {
            resourceCacheList.put(resourceDO.getId(), resourceDO);
        }
        systemResourceCache.put(systemCode, resourceCacheList);
    }

    private void initSystemRoleCache(String systemCode) {
        List<RoleDO> roleList = roleDAO.selectBySystemCode(systemCode, ValidStatus.VALID.toString());
        if (CollectionUtils.isEmpty(roleList)) {
            return;
        }

        Map<Integer, RoleCache> roleCacheList = new ConcurrentHashMap<>((int)(roleList.size()/0.75f) +1);
        for (RoleDO roleDO : roleList) {
            RoleCache roleCache = buildRoleCache(roleDO);
            roleCacheList.put(roleDO.getId(), roleCache);
        }
        systemRoleCache.put(systemCode, roleCacheList);
    }

    private RoleCache buildRoleCache(RoleDO roleDO) {
        RoleCache roleCache = new RoleCache();
        roleCache.setRole(roleDO);
        roleCache.setResourceList(roleResourceDAO.selectResourceIds(roleDO.getId()));
        return roleCache;
    }

    @Override
    public void handleSystemUpdatedEvent(Integer systemId, String systemCode) {
        SystemDO systemDO = systemDAO.selectByCode(systemCode);
        if (systemDO == null) {
            // 请勿做删除操作
            return;
        }

        if (ValidStatus.VALID.toString().equals(systemDO.getStatus())) {
            if (!systemResourceCache.containsKey(systemCode)) {
                // init system resourceCache
                initSystemResourceCache(systemCode);
            }
            if (!systemRoleCache.containsKey(systemCode)) {
                // init system roleCache
                initSystemRoleCache(systemCode);
            }
        } else {
            systemResourceCache.remove(systemCode);
            systemRoleCache.remove(systemCode);
        }
    }

    @Override
    public void handleModuleUpdatedEvent(String systemCode, String moduleCode) {
        ModuleDO moduleDO = moduleDAO.selectByCode(systemCode, moduleCode);
        // init system resourceCache to Override
        if (moduleDO == null) {
            // 请勿做删除操作
            return;
        }
        initSystemResourceCache(moduleDO.getSystemCode());
    }

    @Override
    public void handleResourceUpdatedEvent(String systemCode, String resourceCode) {
        ResourceDO resourceDO = resourceDAO.selectByCode(systemCode, resourceCode);
        if (resourceDO == null) {
            // 请勿做删除操作
            return;
        }

        SystemDO systemDO = systemDAO.selectByCode(resourceDO.getSystemCode());
        if (systemDO == null || !ValidStatus.VALID.toString().equals(systemDO.getStatus())) {
            return;
        }

        ModuleDO moduleDO = moduleDAO.selectByCode(resourceDO.getSystemCode(), resourceDO.getModuleCode());
        if (moduleDO == null || !ValidStatus.VALID.toString().equals(moduleDO.getStatus())) {
            return;
        }

        if (ValidStatus.VALID.toString().equals(resourceDO.getStatus())) {
            Map<Integer, ResourceDO> resourceCache = systemResourceCache.get(resourceDO.getSystemCode());
            if (resourceCache == null) {
                resourceCache = new ConcurrentHashMap<>();
                systemResourceCache.put(resourceDO.getSystemCode(), resourceCache);
            }

            resourceCache.put(resourceDO.getId(), resourceDO);
        } else {
            Map<Integer, ResourceDO> resourceCache = systemResourceCache.get(resourceDO.getSystemCode());
            if (resourceCache != null) {
                resourceCache.remove(resourceDO.getId());
            }
        }
    }

    @Override
    public void handleRoleUpdatedEvent(String systemCode, String roleCode) {
        RoleDO roleDO = roleDAO.selectByCode(systemCode, roleCode);
        if (roleDO == null) {
            // 请勿做删除操作
            return;
        }

        SystemDO systemDO = systemDAO.selectByCode(roleDO.getSystemCode());
        if (systemDO == null || !ValidStatus.VALID.toString().equals(systemDO.getStatus())) {
            return;
        }

        Map<Integer, RoleCache> roleCacheMap = systemRoleCache.get(roleDO.getSystemCode());
        if (ValidStatus.VALID.toString().equals(roleDO.getStatus())) {
            if (roleCacheMap == null) {
                roleCacheMap = new ConcurrentHashMap<>();
                systemRoleCache.put(roleDO.getSystemCode(), roleCacheMap);
                roleCacheMap.put(roleDO.getId(), buildRoleCache(roleDO));
            } else {
                RoleCache roleCache = roleCacheMap.get(roleDO.getId());
                if (roleCache != null) {
                    roleCache.setRole(roleDO);
                } else {
                    roleCacheMap.put(roleDO.getId(), buildRoleCache(roleDO));
                }
            }
        } else {
            if (roleCacheMap != null) {
                roleCacheMap.remove(roleDO.getId());
            }
        }
    }

    @Override
    public void handleRoleResourceUpdatedEvent(Integer roleId) {
        RoleDO roleDO = roleDAO.selectById(roleId);
        if (roleDO == null) {
            // 请勿做删除操作
            return;
        }

        Map<Integer, RoleCache> roleCacheMap = systemRoleCache.get(roleDO.getSystemCode());
        if (CollectionUtils.isEmpty(roleCacheMap)) {
            return;
        }

        RoleCache roleCache = roleCacheMap.get(roleId);
        if (roleCache == null) {
            return;
        }

        roleCache.setResourceList(roleResourceDAO.selectResourceIds(roleDO.getId()));
    }

    @Override
    public void handleUserPermissionUpdatedEvent(Long userId, String channel, String systemCode) {
        // clear cache
        userPermissionCache.invalidate(new UserPermissionQuery(systemCode, userId, channel));
    }
}
