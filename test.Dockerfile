FROM eclipse-temurin:11-jdk-jammy
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseG1GC", "-Xms256m", "-Xmx256m", "-Dspring.profiles.active=develop","-jar","app.jar"]
WORKDIR /app
COPY /build/libs/*.jar app.jar
