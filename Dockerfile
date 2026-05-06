FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY build/libs/draw-my-today-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
