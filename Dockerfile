FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/MySpringREST-1.0.0.jar app.jar
ENTRYPOINT ["java","--enable-preview", "-jar", "app.jar"]
