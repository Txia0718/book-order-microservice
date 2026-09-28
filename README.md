# 📚 微服务学习实践 - 图书订单微服务 (book-order-microservice)

## 📌 项目简介
这是一个基于 **Spring Boot 4.1 + Spring Cloud Gateway + Nacos + Sentinel + OpenFeign + MyBatis-Plus + Seata** 的微服务学习项目。  
模拟了“图书下单扣库存”的业务场景，实现了：

- 服务注册与发现（Nacos）
- 服务间远程调用（OpenFeign + 负载均衡）
- API 网关统一入口（Spring Cloud Gateway）
- 乐观锁防超卖（MyBatis-Plus @Version）
- 流量治理与高可用防护（Sentinel 限流 + 熔断降级）
- **分布式事务（Seata AT 模式，解决跨服务数据一致性问题）**
- 多模块 Maven 工程（common 模块统一管理共享实体类）
- 链路追踪与可观测性（Micrometer Tracing + Zipkin）

## 🛠️ 技术栈
- Spring Boot 4.1.0
- Spring Cloud Gateway 5.0.3（WebFlux 响应式网关）
- Spring Cloud Alibaba 2025.1.0.0
- Nacos（服务注册与发现）
- Sentinel 1.8.10（流量治理组件）
- **Seata 2.6.0（分布式事务，AT 模式）**
- OpenFeign 13.x（声明式远程调用）
- Micrometer Tracing + Zipkin（链路追踪）
- MyBatis-Plus 3.5.15
- **MySQL 8.4.11（数据持久化）**
- Lombok

## 📦 模块说明
| 模块 | 端口 | 说明 |
|------|------|------|
| `common` | - | 共享实体类（纯 POJO，无 ORM 注解） |
| `book-stock` | 8081 | 库存服务，提供图书查询和扣库存接口（参与分布式事务） |
| `order-service` | 8082 | 订单服务，通过 OpenFeign 远程调用库存服务，开启全局事务入口 |
| `gateway` | 8083 | API 网关，统一入口，仅转发 `/order-service/**` 路由 |

## 🔐 分布式事务（Seata）
项目已集成 Seata 2.6.0，采用 **AT 模式** 解决跨服务的数据一致性问题：

- **全局事务入口**：`order-service` 的 `OrderService.createOrder()` 方法使用 `@GlobalTransactional` 注解，开启全局事务。
- **分支事务**：`book-stock` 的 `BookService.buyBook()` 作为分支事务参与全局事务。
- **回滚验证**：在库存服务中模拟“库存不足”异常，全局事务成功触发回滚，订单和库存数据最终一致。
- **配置接入**：Seata 服务端使用 Nacos 作为注册中心和配置中心，简化了部署和配置。

## 🛡️ 流量治理（Sentinel）
项目已集成 Sentinel，构建了 **网关 → 订单服务 → 库存服务** 三层防护体系：

- **网关层 (gateway)**：针对 `order-service-route` 路由配置了 **QPS 限流**（每秒 10 个请求），在入口处拦截超限流量，保护所有后端服务。
- **订单服务 (order-service)**：针对 `/create` 写接口配置了 **QPS 限流**（每秒 3 个请求）和 **熔断降级**（慢调用比例触发），保护下单链路；针对 `/book/{id}` 读接口配置了 **QPS 限流**（每秒 20 个请求）和 **熔断降级**（RT > 200ms）。
- **库存服务 (book-stock)**：内部配置了 **熔断降级**（`/book/{id}` RT > 200ms，`/order/buy` RT > 1000ms），防止慢查询拖垮 Feign 调用链路。
- **Feign + Sentinel**：订单服务通过 OpenFeign 调用库存服务时，整合 Sentinel 实现调用超时熔断和降级兜底。
- **规则持久化**：所有限流/熔断规则已通过 **Nacos 数据源**实现持久化，服务重启后规则自动加载，无需重新配置。

## 📊 链路追踪（Micrometer + Zipkin）
项目已集成分布式链路追踪，可视化查看请求在微服务间的调用链路和耗时：

- **可视化调用链**：通过 Zipkin UI 查看 `gateway → order-service → book-stock` 的完整调用链路。
- **性能瓶颈定位**：直观展示每个服务或接口的耗时，快速定位慢服务或慢接口。
- **日志关联**：每个请求携带唯一 `traceId`，可关联日志与调用链，便于问题排查。

## 🚀 快速启动

### 1. 启动 MySQL
确保本地 MySQL 服务已启动，并创建数据库 `book_order_microservice`，执行建表语句（参考项目 resources 下的 SQL 或手动执行）。

### 2. 启动 Nacos
进入 `nacos/bin/` 目录，双击 `startup.cmd`（Windows）或执行 `./startup.sh -m standalone`（Mac/Linux）。  
访问 `http://localhost:8080`（具体端口以你的 `nacos/conf/application.properties` 中的 `server.port` 配置为准）。若未开启鉴权，则无需输入用户名和密码。

### 3. 启动 Seata Server
进入 `apache-seata-2.7.0-incubating-bin/bin/` 目录，双击 `seata-server.bat`（Windows）或执行 `./seata-server.sh`（Mac/Linux）。  
确保 Seata 已配置使用 Nacos 作为注册中心和配置中心。

### 4. 启动 Sentinel 控制台
进入 `sentinel-dashboard-1.8.10.jar` 所在目录，执行：
```bash
java -Dserver.port=8858 -Dcsp.sentinel.dashboard.server=localhost:8858 -jar sentinel-dashboard-1.8.10.jar
```
访问 `http://localhost:8858`，用户名/密码：`sentinel`/`sentinel`

### 5. 启动 Zipkin
进入 `zipkin-server-3.6.1-exec.jar` 所在目录，执行：
```bash
java -jar zipkin-server-3.6.1-exec.jar
```
访问 `http://localhost:9411` 查看链路追踪控制台。

### 6. 启动微服务
在 IDEA 中运行以下三个微服务：
- `BookStockApplication`（端口 8081）
- `OrderServiceApplication`（端口 8082）
- `GatewayApplication`（端口 8083）

## 🧪 测试接口（通过网关调用）

```bash
# 检查订单服务是否存活
curl "http://localhost:8083/order-service/hello"

# 查询图书信息（通过订单服务中转）
curl "http://localhost:8083/order-service/book/1"

# 下单（扣库存）——正常流程
curl -X POST "http://localhost:8083/order-service/create?bookId=1&quantity=1"

# 测试 Seata 分布式事务回滚（模拟库存不足）
# 将请求数量调大，触发库存服务的异常，观察全局事务回滚
curl -X POST "http://localhost:8083/order-service/create?bookId=1&quantity=999"
```

---

> 📌 **下一步计划**：探索 Docker 容器化部署，将 Nacos、Seata、Sentinel、Zipkin 及微服务打包运行，提升部署效率。