FROM gradle:9.2.1-jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
ENV PRODUCTION=true
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:24-jdk-noble
ENV ARTIFACT_NAME=kuken-all.jar
ENV HOME=/usr/kuken

WORKDIR $HOME
RUN mkdir resources
COPY --from=build /home/gradle/src/build/libs/ .
COPY --from=build /home/gradle/src/build/resources/main/ ./resources
ENV PRODUCTION=true
ENTRYPOINT java -jar $HOME/$ARTIFACT_NAME
CMD ["run"]