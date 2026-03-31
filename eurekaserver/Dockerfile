FROM eclipse-temurin:21-jre-alpine
LABEL authors="kaankeskin"
WORKDIR /app
COPY target/*.jar app.jar
RUN apk add --no-cache curl
ENTRYPOINT ["java", "-jar", "app.jar"]