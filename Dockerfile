FROM alpine:3.12 as cloneGit

ARG LIGHT_CHAT_COMMIT_ARG=2922fa5e7a4cb503cd64180d17499a4383251860

ENV LIGHT_CHAT_COMMIT=${LIGHT_CHAT_COMMIT_ARG}

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

RUN git clone -n https://github.com/Fernthedev/light-chat.git && cd light-chat && git checkout ${LIGHT_CHAT_COMMIT_ARG}



FROM gradle:6.6.1-jdk11 AS build

COPY --chown=gradle:gradle --from=cloneGit  /light-chat/java /home/gradle/src/light-chat

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/light-chat

#RUN echo $(pwd) $(ls) #debug
RUN gradle clean build publishToMavenLocal



WORKDIR /home/gradle/src/
RUN gradle build

#RUN echo $(ls /) #debug

FROM openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build  /home/gradle/src/core/build/libs/shaded-*.jar /app/server.jar

ARG PIMP3_SERVER_PORT_ARG=2000
ENV PIMP3_SERVER_PORT=${PIMP3_SERVER_PORT_ARG}
ENV JAVA_ARGS=""

ENTRYPOINT ["/bin/sh", "-c", "exec java -jar /app/server.jar -port ${PIMP3_SERVER_PORT} --docker ${JAVA_ARGS}"]