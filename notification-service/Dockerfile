FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY notification-service ./notification-service

RUN apk add --no-cache maven \
    && mvn clean package -DskipTests -q -pl notification-service -am \
    && mv notification-service/target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget && addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/app.jar app.jar
USER appuser
EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8082/api/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
