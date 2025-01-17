# Smart 快速开发平台
### 平台简介
* 采用前后端分离的模式，前端框架为：Smart-Web (基于 Vue、Ant Design Vue)
* 后端使用 Springboot、Mybatis-plus Security Oauth2 等技术栈
* 可扩展的多端登录认证，采用JWT进行Token认证，封装了微信小程序授权登录、自动注册功能
* 完整的角色、菜单、按钮、数据权限控制，通过配置即可实现权限划分
* 前后端全套代码生成器，一步即可生成前后端代码，减少开发的工作量
* 封装了数据库CRUD操作，极大的简化了代码复杂性，可根据业务需求进行重写的方式进行扩展
* 深度集成Xxl-Job 作为定时任务功能使用， 通过页面配置即可实现任务调度及维护
* 内置文件上传功能，支持（上传服务器，Minio，阿里云Oss，腾讯Cos等），通过页面操作即可控制文件上传方式
* 内置站内信以及发送邮箱功能
* 功能强大，操作简单的快速开发框架，开箱即用，系统后续会扩展更多功能，如工作流、在线编辑等


### 内置功能
1. 会员管理：移动端用户管理。
2. 通知公告：系统通知公告信息发布维护。
3. 站内信邮箱：站内信邮箱消息发送。
4. 机构管理：配置系统组织机构（公司、部门），树结构展现。
5. 岗位管理：配置系统用户所属岗位。
6. 人员管理：PC端用户管理，用来维护管理系统用户，可配置多身份，丰富管理职能。
7. 角色管理：角色管理，分配菜单、按钮按钮权限，设置角色按照配置的数据权限进行数据范围权限划分。
8. 菜单管理：配置系统菜单，按钮权限标识等，可控制内部路由或者外部连接，丰富的菜单路由配置功能。
9. 数据权限：配置菜单的可使用数据权限，在角色管理中进行勾选配置，支持7种数据权限模式（本人可见，本部门可见，本公司可见，指定用户可见，指定部门可见，拼接自定义SQL），自由度极高
10. 字典管理：分为系统字典和业务字典来应对不同的使用场景，支持多层级树形字典
11. 参数管理：分为系统参数和业务参数，可以在这里设置各种配置项，热更新更加灵活
12. 系统设置：对于内置的系统参数进行表单式操作，维护更加方便
13. 存储管理：用来维护文件上传方式，可扩展更多的文件存储方式，目前支持（上传服务器，Minio，阿里云Oss，腾讯Cos）
14. 操作日志：系统正常操作日志记录和查询，详细记录了请求的接口地址，入参以及返回数据等信息。
15. 异常日志：系统异常信息日志记录和查询，每一个异常都有一个唯一编码，在调用接口出现异常的时候会以JSON的形式返回，方便查询。
16. 登录日志：系统登录日志记录查询。
17. 系统监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。
18. 缓存管理：对系统的缓存进行查询，修改、删除操作。
19. 代码生成器：前后端代码的生成，可以预览，各种筛选项，绑定字典灵活配置，支持下载
20. 生成案例：生成代码Demo示例。
21. 文件管理：全局文件的检索，可进行上传下载以及删除等操作，下载和删除会根据上传方式的不同自动处理。
22. 执行器管理：Xxl-Job执行器维护，默认自动注册，也可以手动注册。
23. 任务列表：所有定时任务的查询，启动，停用，查询某一个任务调度及执行日志等操作。
24. 执行日志：所有定时任务的调度日志及执行日志
25. 个性化配置：可定制主题颜色、布局，页签、水印等主题。
26. 关于：前端技术栈展示


### 核心技术栈
| 技术栈            | 版本     |
|----------------|--------|
| Java           | 8      |
| NodeJs         | 20+    |
| Springboot     | 2.7.9  |
| Spring         | 5.3.x  |
| Mybatis Plus   | 3.5.1  |
| Vue            | 3.4.27 |
| Ant Design Vue | 4.2.1 |
| Vite           | 5.2.12 |


### 后端工程结构
><font size=4>**SmartBoot**</font>
>
> ├── smart-aop &emsp; _**切面模块**_        
> ├── smart-app &emsp; _**启动模块**_         
> ├── smart-auth &emsp; _**认证模块**_        
> ├── smart-business &emsp; _**业务模块**_         
> ├── smart-common &emsp; _**基础模块**_           
> ├── smart-entity &emsp; _**实体模块**_           
> ├── smart-file &emsp; _**文件模块**_           
> ├── smart-gateway &emsp; _**网关模块**_          
> ├── smart-gen &emsp; _**代码生成器模块**_        
> ├── smart-job &emsp; _**XxlJob模块**_      
> ├── smart-message &emsp; _**消息通知模块**_         
> ├── smart-model &emsp; _**模型模块**_           
> ├── smart-mybatis &emsp; _**MybatisPlus模块**_         
> ├── smart-service &emsp; _**服务模块**_           
> ├── smart-system &emsp; _**系统管理模块**_        
> ├── smart-tools &emsp; _**工具模块**_


### 前端工程结构
><font size=4>**SmartWeb**</font>
>
> ├── public &emsp; _**公共资源**_      
> ├── src &emsp; _**代码目录**_     
> ├&emsp;├── api &emsp; _**请求Api**_      
> ├&emsp;├── assets &emsp; _**静态资源**_    
> ├&emsp;├── components &emsp; _**组件**_         
> ├&emsp;├── config &emsp; _**系统配置**_           
> ├&emsp;├── directives &emsp; _**自定义指令**_            
> ├&emsp;├── i18n &emsp; _**国际化**_            
> ├&emsp;├── layout &emsp; _**布局**_            
> ├&emsp;├── lib &emsp; _**依赖文件**_            
> ├&emsp;├── plugins &emsp; _**插件**_            
> ├&emsp;├── router &emsp; _**路由**_            
> ├&emsp;├── store &emsp; _**存储**_            
> ├&emsp;├── theme &emsp; _**主题**_            
> ├&emsp;├── utils &emsp; _**工具箱**_            
> ├&emsp;├── views &emsp; _**页面**_


### 演示地址
[http://smart.pstech360.com](http://smart.pstech360.com)
账号：system
密码：123456


### 项目地址

#### 后端项目
| 简介          | 地址 |
|-------------|---|
| 后端Gitee地址   | [https://gitee.com/pstech360/smart-boot](https://gitee.com/pstech360/smart-boot) |
| 后端Github地址  | [https://github.com/Pst-wf/smart-boot](https://github.com/Pst-wf/smart-boot) |

#### 前端 项目
| 简介   | 地址 |
|------|---|
| 前端Gitee地址 | [https://gitee.com/pstech360/smart-web](https://gitee.com/pstech360/smart-web) |
| 前端Github地址 | [https://github.com/Pst-wf/smart-web](https://github.com/Pst-wf/smart-web) |
