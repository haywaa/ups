
## 缓存设计
缓存仅作用于开放查询接口

## 接口：
### 开放查询接口
- 同步用户接口 /user/sync √
- 查询用户权限接口 /user/permission √
- 查询用户是否拥有某个资源权限 /user/resource/{resourceCode}
- 查询用户是否拥有某个角色权限 /user/role/{roleCode}
- 查询用户是否拥有某个URL权限 /user/url/{url}

### 后台管理接口
#### 系统管理接口
- 添加系统 POST system √
- 修改系统信息 POST system/{id} √
- 查询系统列表 GET system?method=all √
- 查询系统列表(分页) GET system?method=page
- 查询系统信息 GET system/{id}
- 添加业务模块 POST module √
- 修改业务模块信息 POST module/{id} √
- 查询业务模块列表 GET module?method=all √
- 查询业务模块列表(分页) GET module?method=page

#### 资源管理接口
- 添加资源 POST resource √
- 修改资源信息 POST resource/{id} √
- 资源列表查询 GET resource √

#### 角色管理接口
- 添加角色 POST role √
- 修改角色信息 POST role/{id} √
- 查询角色列表 GET role?method=all √
- 分配资源权限 POST role/resource √

#### 用户管理接口
- 分配角色 POST user/role?method=add √
- 移除角色 POST user/role?method=remove √

