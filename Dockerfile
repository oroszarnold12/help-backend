FROM gradle:6.8-jdk11-openj9
RUN gradle wrapper build
COPY build/libs/styoudent-0.0.1-SNAPSHOT.jar styoudent-web.jar
ENTRYPOINT ["java","-jar","styoudent-web.jar"]