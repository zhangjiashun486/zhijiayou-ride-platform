# 智驾游（ZhiJiaYou）

## 项目来源与参与说明

本项目依据黑马程序员「智驾游（顺风车）」课程项目学习，并在课程代码基础上做二次改造，**不是原创系统**。课程配套的教程与练习材料未随本仓库公开。

改造范围、AI 参与情况和验证边界逐轮记录在 [`docs/改进记录.md`](docs/改进记录.md)，其中明确说明：部分代码由 AI 辅助修改与验证，本人完成阅读、复述和实际联调后，才按真实掌握程度对外表述。目前记录在案的一轮改造是「AI 聊天会话鉴权与会话归属校验」，含后端 21 项测试（本轮新增 19 项）、前端 3 项测试与 Vite 构建通过。

本仓库用于展示项目的工程结构与本人参与的改造记录，**不代表本人独立设计或实现整个系统**。

> AI 出行与顺风车业务平台：用户端、管理端、后端服务、AI 出行助手和完整基础设施。

![智驾游 Logo](frontend/public/brand/zhijiaoyou-logo.svg)

## 项目简介

智驾游面向顺风车、拼车和旅游出行场景，提供用户端、司机端、运营管理后台和 AI 出行助手。用户端覆盖登录、行程发布、订单、支付、认证、消息和个人中心；管理端负责资讯、文件、账号和车辆管理；后端使用 Spring Boot 构建 REST、WebSocket、消息队列和 AI 服务。

项目包含完整业务闭环：

```text
用户/司机端
→ 行程与订单
→ Redis Session
→ MySQL 业务数据
→ RabbitMQ 异步任务
→ WebSocket 实时通知
→ MinIO 文件存储
→ Spring AI + RAG 智能问路
```

## 核心能力

| 模块 | 能力 |
| --- | --- |
| 用户端 | 登录、注册、乘客/司机角色、行程发布、订单、支付、认证、消息、个人中心 |
| 管理端 | 资讯库、文件库、账号、车辆、行程与运营数据管理 |
| AI 助手 | 流式对话、会话历史、会话删除、知识库向量化、RAG 检索增强 |
| 实时能力 | SSE 流式输出、WebSocket 通知、RabbitMQ 异步消息 |
| 存储能力 | MySQL、Redis、MongoDB、MinIO、Redis Vector Store / SimpleVectorStore |
| 工程化 | Docker Compose 基础设施、环境变量配置、Actuator、单元测试、Swagger/OpenAPI |

## 技术栈

```text
Java 17
Spring Boot 3.5
Spring AI 1.1
Spring WebSocket
Spring Session Redis
MyBatis + MySQL
Redis + Jedis
MongoDB
RabbitMQ
MinIO
Springdoc OpenAPI
Vue 3 + Vite + Pinia + Vant
RuoYi 4.7.8 + Shiro + Thymeleaf（管理端）
```

## 系统架构

```text
Vue 用户端（3000）
      │
      ▼
Spring Boot backend（9999）
├── account / stroke / order / payment
├── notice WebSocket
└── aichat Spring AI RAG
      │
      ├── MySQL：业务数据、聊天记录、会话
      ├── Redis：Session、缓存、AI 限流、向量库
      ├── MongoDB：公告/通知
      ├── RabbitMQ：知识库异步向量化、消息处理
      └── MinIO：头像、认证材料、文件

RuoYi 管理端（8888）
      │
      ▼
MySQL + Redis + MinIO + RabbitMQ
```

## 工程亮点

### 1. Spring AI + RAG 智能问路

- 使用 OpenAI 兼容协议接入通义千问。
- 使用 `ChatClient` 构建对话调用。
- 使用 `QuestionAnswerAdvisor` 接入知识库检索。
- 支持普通聊天与 SSE 流式输出。
- 用户消息和 AI 回复持久化到 MySQL。
- 支持会话列表、历史分页和删除。
- 本地环境使用内存向量库，基础设施完整时切换 Redis 向量库。

### 2. RabbitMQ 异步知识库处理

```text
管理端录入资讯/文件
→ RabbitMQ 消息
→ 异步解析与向量化
→ 写入向量库
→ AI 问答检索
```

异步链路将耗时向量化与前台请求解耦，避免管理端操作阻塞。

### 3. Redis 会话与 AI 限流

- 使用 Spring Session Redis 统一保存登录 Session。
- 使用 Redis 原子自增实现 AI 对话限流。
- 同一用户每分钟最多 20 次请求。
- 带过期时间，避免 Key 无限增长。
- 对应单元测试：`AiRateLimiterTest`。

### 4. WebSocket 实时通知

- 服务端推送用户消息和通知。
- 前端全局初始化 WebSocket。
- Session 变化时自动连接或断开。
- 支持未读消息数和消息中心。

### 5. Docker Compose 一键基础设施

`docker-compose.yml` 提供：

```text
MySQL       3307
Redis       6379
MongoDB     27017
RabbitMQ    5672 / 15672
MinIO       9005 / 9006
```

启动：

```powershell
docker compose up -d
```

停止：

```powershell
docker compose down
```

### 6. 配置安全化

以下敏感配置均已改为环境变量读取，不再写入仓库：

```text
数据库账号密码
Redis、MongoDB、RabbitMQ、MinIO 配置
DashScope API Key
百度地图 API Key / Secret Key
```

复制配置模板：

```powershell
copy .env.example .env
```

## 快速启动

### 1. 启动基础设施

```powershell
copy .env.example .env
# 编辑 .env，至少填写 DASHSCOPE_API_KEY 和其他需要的密钥
docker compose up -d
```

### 2. 启动后端

```powershell
cd backend
mvn spring-boot:run
```

访问：

```text
API：http://localhost:9999
Actuator：http://localhost:9999/actuator/health
Swagger/OpenAPI：http://localhost:9999/swagger-ui/index.html
```

### 3. 启动管理端

```powershell
cd manager
mvn spring-boot:run
```

访问：

```text
http://localhost:8888
```

### 4. 启动用户端

```powershell
cd frontend
copy .env.example .env
# 如需使用地图页面，在 frontend/.env 中填写 VITE_BAIDU_MAP_AK
npm install
npm run dev
```

访问：

```text
http://localhost:3000
```

## 目录结构

```text
trip2-master/
├── backend/                 用户端 Spring Boot 服务
│   ├── src/main/java/com/zhijiaoyou/
│   └── src/test/java/
├── manager/                 RuoYi 管理后台
├── frontend/                Vue 3 用户端
├── docker-compose.yml       基础设施编排
├── hitch.sql                MySQL 初始化脚本
├── .env.example             环境变量模板
└── README.md
```

## 项目验证

```text
前端：npm run build 通过
后端：mvn test 通过
管理端：mvn package 通过
AI 限流：20 次允许、第 21 次拒绝
```

## 配置与数据说明

本仓库已按以下方式处理敏感信息：

- 不提交 `.env`、真实 API Key、数据库密码。
- 检查 `hitch.sql` 是否包含真实手机号、密码哈希、聊天记录或认证材料。
- 不提交 `target/`、`node_modules/`、`dist/`、上传文件目录。
- 如果公开演示数据，请使用匿名测试账号和脱敏数据。
- 关闭 Druid 默认管理密码或改成环境变量。
