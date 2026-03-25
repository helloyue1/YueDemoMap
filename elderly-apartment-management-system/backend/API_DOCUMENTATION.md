# 养老院公寓管理系统 - 后端 API 文档

## 项目概述

这是一个基于 Spring Boot + MyBatis-Plus + JWT 的养老院公寓管理系统后端服务。

### 技术栈
- **框架**: Spring Boot
- **ORM**: MyBatis-Plus
- **安全**: Spring Security + JWT
- **数据库**: MySQL
- **构建工具**: Maven

### 服务配置
- **端口**: 8080
- **基础 URL**: `http://localhost:8080`

---

## 通用响应格式

所有 API 返回统一的响应格式：

```json
{
  "code": 200,
  "success": true,
  "message": "Success",
  "data": {},
  "timestamp": 1700000000000
}
```

### 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 500 | 服务器错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |

---

## 认证模块 (/auth)

### 1. 用户登录
- **URL**: `POST /auth/login`
- **描述**: 用户登录并获取 JWT Token
- **请求体**:
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **响应**:
```json
{
  "code": 200,
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "phone": "13800138000",
      "email": "admin@example.com",
      "status": 1
    }
  }
}
```

### 2. 用户注册
- **URL**: `POST /auth/register`
- **描述**: 新用户注册
- **请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138001",
  "email": "zhangsan@example.com"
}
```

### 3. 用户登出
- **URL**: `POST /auth/logout`
- **描述**: 用户登出
- **响应**:
```json
{
  "code": 200,
  "success": true,
  "message": "Logged out successfully"
}
```

### 4. 获取当前用户信息
- **URL**: `GET /auth/me`
- **描述**: 获取当前登录用户信息
- **请求头**: `Authorization: Bearer {token}`

---

## 用户管理模块 (/user)

### 1. 获取用户列表
- **URL**: `GET /user`
- **描述**: 分页获取用户列表
- **参数**:
  - `page` (int, 可选): 页码，默认 1
  - `size` (int, 可选): 每页数量，默认 10
  - `username` (string, 可选): 用户名模糊查询
  - `role` (string, 可选): 角色编码筛选

### 2. 获取用户详情
- **URL**: `GET /user/{id}`
- **描述**: 根据 ID 获取用户详情

### 3. 创建用户
- **URL**: `POST /user`
- **描述**: 创建新用户
- **请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138001",
  "email": "zhangsan@example.com",
  "status": 1
}
```

### 4. 更新用户
- **URL**: `PUT /user/{id}`
- **描述**: 更新用户信息

### 5. 删除用户
- **URL**: `DELETE /user/{id}`
- **描述**: 删除用户（逻辑删除）

---

## 老人管理模块 (/elderly)

### 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 老人ID |
| name | String | 姓名 |
| gender | Integer | 性别 (0-女, 1-男) |
| age | Integer | 年龄 |
| birthday | String | 出生日期 (yyyy-MM-dd) |
| idCard | String | 身份证号 |
| phone | String | 联系电话 |
| address | String | 家庭住址 |
| emergencyContact | String | 紧急联系人 |
| emergencyPhone | String | 紧急联系电话 |
| roomId | Integer | 房间ID |
| roomNumber | String | 房间号 |
| checkInDate | String | 入住日期 |
| healthStatus | String | 健康状态 |
| status | Integer | 状态 (0-离院, 1-在院) |
| notes | String | 备注 |

### API 列表

### 1. 获取老人列表
- **URL**: `GET /elderly`
- **描述**: 分页获取老人列表
- **参数**:
  - `page` (int): 页码，默认 1
  - `size` (int): 每页数量，默认 10
  - `name` (string): 姓名模糊查询
  - `roomNumber` (string): 房间号筛选
  - `status` (int): 状态筛选 (0-离院, 1-在院)

### 2. 获取老人详情
- **URL**: `GET /elderly/{id}`
- **描述**: 根据 ID 获取老人详情

### 3. 创建老人信息
- **URL**: `POST /elderly`
- **描述**: 创建新老人档案

### 4. 更新老人信息
- **URL**: `PUT /elderly/{id}`
- **描述**: 更新老人信息

### 5. 删除老人信息
- **URL**: `DELETE /elderly/{id}`
- **描述**: 删除老人档案（逻辑删除）

---

## 房间管理模块 (/room)

### 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 房间ID |
| roomNumber | String | 房间号 |
| floor | Integer | 楼层 |
| type | Integer | 房间类型 (1-单人间, 2-双人间, 3-多人间) |
| capacity | Integer | 容纳人数 |
| currentOccupancy | Integer | 当前入住人数 |
| status | Integer | 状态 (0-空闲, 1-部分入住, 2-维修中) |
| price | BigDecimal | 价格 |
| hasWifi | Boolean | 是否有WiFi |
| hasTv | Boolean | 是否有电视 |
| hasAc | Boolean | 是否有空调 |
| hasBathroom | Boolean | 是否有独立卫生间 |
| hasBalcony | Boolean | 是否有阳台 |
| facilities | String | 设施描述 |
| coverImage | String | 封面图片 |
| images | String | 图片列表 |
| remark | String | 备注 |

### API 列表

### 1. 获取房间列表
- **URL**: `GET /room`
- **描述**: 分页获取房间列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `roomNumber` (string): 房间号模糊查询
  - `floor` (int): 楼层筛选
  - `type` (int): 房间类型筛选
  - `status` (int): 状态筛选

### 2. 获取房间详情
- **URL**: `GET /room/{id}`
- **描述**: 根据 ID 获取房间详情

### 3. 创建房间
- **URL**: `POST /room`
- **描述**: 创建新房间

### 4. 更新房间
- **URL**: `PUT /room/{id}`
- **描述**: 更新房间信息

### 5. 删除房间
- **URL**: `DELETE /room/{id}`
- **描述**: 删除房间

### 6. 获取房间统计
- **URL**: `GET /room/stats`
- **描述**: 获取房间统计信息
- **响应**:
```json
{
  "total": 100,
  "empty": 20,
  "full": 50,
  "maintenance": 5,
  "partial": 25
}
```

---

## 活动管理模块 (/activities)

### 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 活动ID |
| title | String | 活动标题 |
| description | String | 活动描述 |
| activityType | Integer | 活动类型 |
| categoryId | Integer | 分类ID |
| activityDate | LocalDate | 活动日期 |
| startTime | LocalTime | 开始时间 |
| endTime | LocalTime | 结束时间 |
| location | String | 活动地点 |
| maxParticipants | Integer | 最大参与人数 |
| currentParticipants | Integer | 当前报名人数 |
| checkedInCount | Integer | 签到人数 |
| minParticipants | Integer | 最少参与人数 |
| organizer | String | 组织者 |
| organizerPhone | String | 组织者电话 |
| imageUrl | String | 活动图片 |
| status | Integer | 状态 (0-未开始, 1-进行中, 2-已结束) |
| isRecommended | Integer | 是否推荐 (0-否, 1-是) |
| viewCount | Integer | 浏览次数 |

### API 列表

### 1. 获取活动列表
- **URL**: `GET /activities`
- **描述**: 分页获取活动列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `title` (string): 标题模糊查询
  - `categoryId` (int): 分类ID筛选
  - `activityType` (int): 活动类型筛选
  - `status` (int): 状态筛选
  - `startDate` (string): 开始日期 (yyyy-MM-dd)
  - `endDate` (string): 结束日期 (yyyy-MM-dd)
  - `isRecommended` (boolean): 是否推荐

### 2. 获取活动详情
- **URL**: `GET /activities/{id}`
- **描述**: 根据 ID 获取活动详情（自动增加浏览次数）

### 3. 获取即将开始的活动
- **URL**: `GET /activities/upcoming`
- **描述**: 获取即将开始的活动列表
- **参数**:
  - `limit` (int): 数量限制，默认 5

### 4. 创建活动
- **URL**: `POST /activities`
- **描述**: 创建新活动

### 5. 更新活动
- **URL**: `PUT /activities/{id}`
- **描述**: 更新活动信息

### 6. 删除活动
- **URL**: `DELETE /activities/{id}`
- **描述**: 删除活动（逻辑删除）

---

## 活动报名模块 (/activity-signups)

### API 列表

### 1. 获取报名列表
- **URL**: `GET /activity-signups`
- **描述**: 分页获取活动报名列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `activityId` (int): 活动ID筛选
  - `elderlyId` (int): 老人ID筛选
  - `status` (int): 报名状态筛选

### 2. 创建报名
- **URL**: `POST /activity-signups`
- **描述**: 为老人报名活动

### 3. 取消报名
- **URL**: `DELETE /activity-signups/{id}`
- **描述**: 取消活动报名

### 4. 签到
- **URL**: `PUT /activity-signups/{id}/checkin`
- **描述**: 活动签到

---

## 活动反馈模块 (/activity-feedbacks)

### API 列表

### 1. 获取反馈列表
- **URL**: `GET /activity-feedbacks`
- **描述**: 分页获取活动反馈列表

### 2. 创建反馈
- **URL**: `POST /activity-feedbacks`
- **描述**: 创建活动反馈/评价

### 3. 删除反馈
- **URL**: `DELETE /activity-feedbacks/{id}`
- **描述**: 删除反馈

---

## 餐饮管理模块 (/meal)

### API 列表

### 1. 获取餐饮计划
- **URL**: `GET /meal/plan`
- **描述**: 获取餐饮计划列表

### 2. 获取餐饮安排
- **URL**: `GET /meal/arrange`
- **描述**: 获取餐饮安排列表

### 3. 获取菜单
- **URL**: `GET /meal/menu`
- **描述**: 获取菜单列表

### 4. 获取订餐列表
- **URL**: `GET /meal/order`
- **描述**: 获取订餐列表

### 5. 获取配餐列表
- **URL**: `GET /meal/distribution`
- **描述**: 获取配餐列表

---

## 费用管理模块 (/fee)

### 数据模型

#### 费用类型 (FeeType)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 类型ID |
| name | String | 类型名称 |
| code | String | 类型编码 |
| description | String | 描述 |

#### 费用标准 (FeeStandard)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 标准ID |
| feeTypeId | Long | 费用类型ID |
| name | String | 标准名称 |
| amount | BigDecimal | 金额 |
| unit | String | 计费单位 |
| status | Integer | 状态 |

#### 费用账单 (FeeBill)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 账单ID |
| billNo | String | 账单编号 |
| elderlyId | Integer | 老人ID |
| billMonth | String | 账单月份 |
| totalAmount | BigDecimal | 总金额 |
| status | Integer | 状态 (0-未缴费, 1-部分缴费, 2-已缴清) |

### API 列表

### 费用类型管理

#### 1. 获取费用类型列表
- **URL**: `GET /fee/types`
- **描述**: 分页获取费用类型列表

#### 2. 添加费用类型
- **URL**: `POST /fee/types`
- **描述**: 添加新费用类型

#### 3. 更新费用类型
- **URL**: `PUT /fee/types/{id}`
- **描述**: 更新费用类型

#### 4. 删除费用类型
- **URL**: `DELETE /fee/types/{id}`
- **描述**: 删除费用类型

### 费用标准管理

#### 5. 获取费用标准列表
- **URL**: `GET /fee/standards`
- **描述**: 分页获取费用标准列表
- **参数**:
  - `feeTypeId` (Long): 费用类型ID筛选

#### 6. 添加费用标准
- **URL**: `POST /fee/standards`
- **描述**: 添加新费用标准

#### 7. 更新费用标准
- **URL**: `PUT /fee/standards/{id}`
- **描述**: 更新费用标准

### 账单管理

#### 8. 获取账单列表
- **URL**: `GET /fee/bills`
- **描述**: 分页获取账单列表
- **参数**:
  - `elderlyId` (int): 老人ID筛选
  - `billMonth` (string): 账单月份筛选
  - `status` (int): 状态筛选

#### 9. 生成账单
- **URL**: `POST /fee/bills/generate`
- **描述**: 生成指定月份的账单

#### 10. 获取账单详情
- **URL**: `GET /fee/bills/{id}`
- **描述**: 获取账单详情

### 缴费管理

#### 11. 获取缴费记录
- **URL**: `GET /fee/payments`
- **描述**: 分页获取缴费记录

#### 12. 创建缴费记录
- **URL**: `POST /fee/payments`
- **描述**: 记录缴费信息

---

## 健康管理模块 (/health)

### API 列表

### 1. 获取健康档案列表
- **URL**: `GET /health/archives`
- **描述**: 获取老人健康档案列表

### 2. 创建健康档案
- **URL**: `POST /health/archives`
- **描述**: 创建健康档案

### 3. 更新健康档案
- **URL**: `PUT /health/archives/{id}`
- **描述**: 更新健康档案

### 4. 获取健康记录列表
- **URL**: `GET /health/records`
- **描述**: 获取健康检查记录列表
- **参数**:
  - `elderlyId` (int): 老人ID筛选
  - `startDate` (string): 开始日期
  - `endDate` (string): 结束日期

### 5. 获取老人健康记录
- **URL**: `GET /health/{elderlyId}/health-records`
- **描述**: 获取指定老人的健康记录

### 6. 创建健康记录
- **URL**: `POST /health/{elderlyId}/health-records`
- **描述**: 为老人创建健康记录

### 7. 更新健康记录
- **URL**: `PUT /health/{elderlyId}/health-records/{recordId}`
- **描述**: 更新健康记录

---

## 护理管理模块 (/care)

### API 列表

### 1. 获取护理记录列表
- **URL**: `GET /care/records`
- **描述**: 分页获取护理记录列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `elderlyName` (string): 老人姓名筛选
  - `caregiverName` (string): 护理员姓名筛选
  - `startDate` (string): 开始日期
  - `endDate` (string): 结束日期

### 2. 获取护理计划
- **URL**: `GET /care/plan`
- **描述**: 获取护理计划列表

### 3. 获取护理排班
- **URL**: `GET /care/schedule`
- **描述**: 获取护理排班列表

### 4. 获取日常护理记录
- **URL**: `GET /care/daily`
- **描述**: 获取日常护理记录

---

## 药品管理模块 (/medicines)

### API 列表

### 1. 获取药品列表
- **URL**: `GET /medicines`
- **描述**: 分页获取药品列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `name` (string): 药品名称模糊查询
  - `type` (string): 药品类型筛选

### 2. 获取药品详情
- **URL**: `GET /medicines/{id}`
- **描述**: 根据 ID 获取药品详情

### 3. 创建药品
- **URL**: `POST /medicines`
- **描述**: 添加新药品

### 4. 更新药品
- **URL**: `PUT /medicines/{id}`
- **描述**: 更新药品信息

### 5. 删除药品
- **URL**: `DELETE /medicines/{id}`
- **描述**: 删除药品

---

## 护士管理模块 (/nurse)

### API 列表

### 1. 获取护士列表
- **URL**: `GET /nurse`
- **描述**: 分页获取护士列表
- **参数**:
  - `page` (int): 页码
  - `size` (int): 每页数量
  - `name` (string): 姓名模糊查询
  - `status` (string): 状态筛选

### 2. 获取护士详情
- **URL**: `GET /nurse/{id}`
- **描述**: 根据 ID 获取护士详情

---

## 文件管理模块 (/file)

### API 列表

### 1. 上传单张图片
- **URL**: `POST /file/upload`
- **描述**: 上传单张图片
- **参数**:
  - `file` (File): 图片文件（必填）
  - `directory` (string): 目录类型，可选值：`rooms`, `elderly`, `avatars`, `activities`，默认 `temp`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "relativePath": "rooms/xxx.jpg",
    "url": "http://localhost:8080/uploads/rooms/xxx.jpg"
  }
}
```

### 2. 批量上传图片
- **URL**: `POST /file/upload/batch`
- **描述**: 批量上传多张图片
- **参数**:
  - `files` (File[]): 图片文件列表（必填）
  - `directory` (string): 目录类型

---

## 系统配置

### JWT 配置
- **密钥**: `elderly-apartment-management-system-secret-key`
- **过期时间**: 86400000ms (1天)
- **请求头**: `Authorization`
- **Token 前缀**: `Bearer`

### 文件上传配置
- **存储路径**: `./uploads`
- **访问前缀**: `/uploads`
- **允许类型**: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
- **最大大小**: 5MB

### 数据库配置
- **驱动**: `com.mysql.cj.jdbc.Driver`
- **URL**: `jdbc:mysql://localhost:3306/elderly_apartment_management`
- **用户名**: `root`
- **密码**: `123456`

---

## 错误处理

系统使用全局异常处理器统一处理异常，返回格式：

```json
{
  "code": 500,
  "success": false,
  "message": "错误信息",
  "data": null,
  "timestamp": 1700000000000
}
```

### 常见错误

| 错误信息 | 说明 |
|----------|------|
| 用户不存在 | 用户ID无效 |
| 用户名已存在 | 注册/创建时用户名重复 |
| 老人信息不存在 | 老人ID无效 |
| 活动不存在 | 活动ID无效 |
| 图片上传失败 | 文件上传异常 |

---

## 管理端功能模块

### 1. 仪表盘 (Dashboard)
- 统计数据展示
- 房间入住率
- 今日活动
- 待处理事项

### 2. 老人档案管理
- 老人信息录入/编辑
- 入住/退住管理
- 房间分配
- 紧急联系人管理

### 3. 房间管理
- 房间信息维护
- 入住状态管理
- 房间设施配置
- 房间图片管理

### 4. 活动管理
- 活动发布/编辑
- 报名管理
- 签到管理
- 活动反馈查看

### 5. 费用管理
- 费用类型配置
- 费用标准设置
- 账单生成
- 缴费记录管理

### 6. 健康管理
- 健康档案管理
- 体检记录
- 健康数据分析

### 7. 护理管理
- 护理计划制定
- 护理记录
- 护理排班

### 8. 餐饮管理
- 菜单管理
- 订餐管理
- 配餐安排

### 9. 药品管理
- 药品库存
- 用药记录
- 药品审批

### 10. 系统管理
- 用户管理
- 角色权限
- 系统设置
