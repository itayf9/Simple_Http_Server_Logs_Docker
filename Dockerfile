FROM openjdk:17

COPY target/Simple_Http_Server_Ex-0.0.1-SNAPSHOT.jar Simple_Http_Server_Ex-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "Simple_Http_Server_Ex-0.0.1-SNAPSHOT.jar"]