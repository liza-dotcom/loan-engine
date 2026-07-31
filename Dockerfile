FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/loan-engine-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
