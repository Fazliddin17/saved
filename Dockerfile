FROM openjdk:17
ADD target/saved-message-robot-0.0.1-SNAPSHOT.jar app.jar
VOLUME /saved.app
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]