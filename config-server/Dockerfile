FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY config-server ./config-server

RUN apk add --no-cache maven \
    && mvn clean package -DskipTests -q -pl config-server -am \
    && mv config-server/target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget && addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/app.jar app.jar
USER appuser
EXPOSE 8888
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8888/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
