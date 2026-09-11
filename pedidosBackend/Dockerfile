FROM eclipse-temurin:21-jre

WORKDIR /app

COPY *.jar app.jar

EXPOSE 4545

ENTRYPOINT ["java", "-jar", "app.jar"]