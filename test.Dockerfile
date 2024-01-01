FROM eclipse-temurin:11-jre-jammy
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=develop","-jar","app.jar"]
WORKDIR /app
COPY /build/libs/*.jar app.jar
