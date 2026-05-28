FROM openjdk:21-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY target/kitchen-service-*.jar app.jar

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8087/actuator/health || exit 1

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
