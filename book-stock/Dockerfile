# 第一阶段：构建（使用带有 Maven 的 JDK 镜像）
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app
# 复制 pom 文件（利用 Docker 缓存机制，避免每次改代码都重新下载依赖）
COPY pom.xml .
COPY common/pom.xml common/
COPY book-stock/pom.xml book-stock/
COPY order-service/pom.xml order-service/
COPY gateway/pom.xml gateway/
# 提前下载依赖
RUN mvn dependency:go-offline -B -pl book-stock -am
# 复制所有源码并打包
COPY . .
RUN mvn clean package -DskipTests -pl book-stock -am

# 第二阶段：运行（使用精简的 JRE 镜像，减小体积）
FROM eclipse-temurin:25-jre
WORKDIR /app
# 从构建阶段复制生成的 Jar 包
COPY --from=builder /app/book-stock/target/*.jar app.jar
# 暴露端口
EXPOSE 8081
# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]