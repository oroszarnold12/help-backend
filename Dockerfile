FROM gradle:6.8-jdk11-openj9
RUN gradle wrapper build
COPY build/libs/help-0.0.1-SNAPSHOT.jar help-web.jar
ENTRYPOINT ["java","-jar","help-web.jar"]
