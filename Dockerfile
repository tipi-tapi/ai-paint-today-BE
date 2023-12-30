FROM eclipse-temurin:11-jdk-jammy
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=test","-jar","app.jar"]
COPY /build/libs/*.jar app.jar