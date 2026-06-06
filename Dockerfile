FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/draw-my-today-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:+UseContainerSupport", \
  "-jar", "app.jar"]
