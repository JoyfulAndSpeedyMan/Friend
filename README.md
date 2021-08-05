# Friend
**这是一个校园社交APP的服务的项目，主要功能包括校园贴吧、校友匹配、好友管理（添加、删除、
拉黑等）、聊天功能等。**

该项目基于Zookeeper实现的分布式微服务架构，使用Dubbo框架进行RPC调用
主要分为以下几个模块
* 聊天服务模块
* 聊天消息路由模块
* 常规业务处理模块

这是一个全Reactive的项目，Http服务的构建使用的是Spring WebFlux而不是Spring MVC,
配合SpringData Reactive实现Controller到DAO层的无阻塞调用。

聊天模块使用Netty构建的WebSocket长连接服务，服务使用Google Protobuf自定义交互协议

