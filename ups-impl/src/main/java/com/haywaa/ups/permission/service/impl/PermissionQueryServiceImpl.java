package com.haywaa.ups.permission.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.domain.entity.UserRoleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.domain.query.UserPermissionQuery;
import com.haywaa.ups.permission.bo.ResourceBO;
import com.haywaa.ups.permission.cache.RoleCache;
import com.haywaa.ups.permission.cache.UserPermissionCache;
import com.haywaa.ups.permission.convert.ResourceConvert;
import com.haywaa.ups.permission.service.PermissionQueryService;
import com.haywaa.ups.rpc.dto.UserPermissionDTO;
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
    public UserPermissionDTO queryUserPermission(String systemCode, String channel, String thirdUid) {
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

        UserPermissionDTO userPermission = new UserPermissionDTO();

        UserPermissionCache permissionCache = userPermissionCache.get(new UserPermissionQuery(systemCode, userId, channel));
        if (permissionCache == null) {
            return userPermission;
        }

        List<UserPermissionCache.UserRoleItem> userRoleItemList = permissionCache.getUserRoleItems();
        if (CollectionUtils.isEmpty(userRoleItemList)) {
            return userPermission;
        }

        // 获取角色资源列表
        Map<Integer/*roleId*/, RoleCache> roleCacheMap = systemRoleCache.get(systemCode);
        if (CollectionUtils.isEmpty(roleCacheMap)) {
            return userPermission;
        }

        Map<Integer, List<Integer>> resourceRoleIdMap = new HashMap<>();
        List<UserPermissionDTO.RoleItem> roleItemList = userRoleItemList.stream().map(userRoleItem -> {
            final Integer roleId = userRoleItem.getRoleId();
            RoleCache roleCache = roleCacheMap.get(roleId);
            if (roleCache == null) {
                return null;
            }

            RoleDO roleDO = roleCache.getRole();
            if (roleDO == null) {
                return null;
            }

            Collection<Integer> resourceList = roleCache.getResourceList();
            if (!CollectionUtils.isEmpty(resourceList)) {
                for (Integer resId : resourceList) {
                    List<Integer> roleIdList = resourceRoleIdMap.get(resId);
                    if (roleIdList == null) {
                        roleIdList = new ArrayList<>();
                        resourceRoleIdMap.put(resId, roleIdList);
                    }
                    roleIdList.add(roleId);
                }
            }

            // 每次查询时通过RoleCache过滤一遍，而不是直接将roleCode缓存到permissionCache中，可以简化role变更（含状态变更）的缓存处理逻辑
            UserPermissionDTO.RoleItem roleItem = new UserPermissionDTO.RoleItem();
            roleItem.setRoleId(roleId);
            roleItem.setRelatedKey(userRoleItem.getRelatedKey());
            roleItem.setRoleCode(roleItem.getRoleCode());
            return roleItem;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        userPermission.setRoleList(roleItemList);

        final List<Integer> roleIds = roleItemList.stream()
                .map(UserPermissionDTO.RoleItem::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIds)) {
            return userPermission;
        }

        List<ResourceBO> resourceList = selectCachedResource(systemCode, resourceRoleIdMap);
        if (!CollectionUtils.isEmpty(resourceList)) {
            userPermission.setResouceList(resourceList.stream().map(resourceBO -> {
                UserPermissionDTO.ResourceItem resourceItem = new UserPermissionDTO.ResourceItem();
                resourceItem.setCode(resourceBO.getCode());
                resourceItem.setType(resourceBO.getType());
                resourceItem.setItems(resourceBO.getItems());
                resourceItem.setRelatedRoleIds(resourceBO.getRelatedRoleIds());
                return resourceItem;
            }).collect(Collectors.toList()));
        }

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
        //permissionCache.setUser(userBO);

        // 2. 获取用户角色列表
        List<UserRoleDO> userRoleDOList = userRoleDAO.selectByUserId(query.getUserId(), query.getChannel(), query.getSystemCode());
        if (CollectionUtils.isEmpty(userRoleDOList)) {
            return permissionCache;
        }

        permissionCache.setUserRoleItems(userRoleDOList.stream().map(userRoleDO -> {
            UserPermissionCache.UserRoleItem roleItem = new UserPermissionCache.UserRoleItem();
            roleItem.setRoleId(userRoleDO.getRoleId());
            roleItem.setRelatedKey(userRoleDO.getRelatedKey());
            return roleItem;
        }).collect(Collectors.toList()));

        return permissionCache;
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

            // 考虑是否直接缓存BO对象
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
