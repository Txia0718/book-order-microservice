# 第一阶段：构建
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
COPY common/pom.xml common/
COPY book-stock/pom.xml book-stock/
COPY order-service/pom.xml order-service/
COPY gateway/pom.xml gateway/
RUN mvn dependency:go-offline -B -pl gateway -am
COPY . .
RUN mvn clean package -DskipTests -pl gateway -am

# 第二阶段：运行
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/gateway/target/*.jar app.jar
# 暴露端口
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]