# group_purchase

## 前言

## 快速开始
- 拉取代码

```shell script
git clone https://github.com/bgniao/group_purchase.git
```

- 进入目录

```shell script
cd group_purchase
```

- 启动docker-compose

```shell script
docker-compose up -d
```

- 打开swagger页面

 ```shell script
 docker-compose up -d
 ```

## 项目介绍
`group_purchase`项目是一套社区团购系统，包括小程序端及后台管理系统，基于SpringBoot+MyBatis实现，采用Docker
容器化部署。包含营销活动、文章管理、财务管理、商品管理、订单管理、购物车、店铺装修、接龙订单、微信群机器人、店铺设置、用户管理等功能

## 项目演示
小程序：
管理后台地址：

## 组织结构
```
.
├── activity -- 营销活动
├── article -- 文章管理
├── configurer -- 通用设置
├── constant -- 全局变量
├── core -- 基础核心
├── exception -- 自定义异常
├── financial -- 财务管理
├── goods -- 商品管理
├── jobhandler -- 定时任务
├── listener -- mq监听
├── logistics -- 物流管理
├── order -- 订单管理
├── shop -- 商铺管理
├── solitaire -- 接龙管理
├── system -- 系统设置
├── user -- 用户管理
├── utils -- 工具类
├── websocket -- ws支持
└── wechatbot -- 微信机器人

```

## 技术选型

### 后端技术

| 技术                 | 说明                | 官网                                           |
| -------------------- | ------------------- | ---------------------------------------------- |
| SpringBoot           | 容器+MVC框架        | https://spring.io/projects/spring-boot         |
| MyBatis              | ORM框架             | http://www.mybatis.org/mybatis-3/zh/index.html |
| Activemq             | 消息队列            | http://activemq.apache.org/ |
| Redis                | 分布式缓存          | https://redis.io/                              |
| MongoDB              | NoSql数据库         | https://www.mongodb.com                        |
| Fastjson             | JSON解析        |https://github.com/alibaba/   |
| XXL-JOB             | 分布式任务调度平台 |https://www.xuxueli.com/xxl-job/ |
| WxJava             | 微信开发 Java SDK       | https://github.com/Wechat-Group/WxJava        |
| Druid                | 数据库连接池        | https://github.com/alibaba/druid               |
| Lombok               | 简化对象封装工具    | https://github.com/rzwitserloot/lombok         |
| Hutool               | Java工具类库        | https://github.com/looly/hutool                |
| tk.mybatis           | MyBatis 工具 | https://mybatis.io/ |
| Swagger-UI           | 文档生成工具        | https://github.com/swagger-api/swagger-ui      |
| Hibernator-Validator | 验证框架            | http://hibernate.org/validator                 |



## 架构图

## 许可证

Apache License 2.0


