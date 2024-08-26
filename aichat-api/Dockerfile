FROM openjdk:8-jre-slim

MAINTAINER luowei<3198795614@qq.com>

ENV PARAMS=""

ENV TZ=PRC

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/chatgpt-api.jar /chatgpt-api.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /chatgpt-api.jar $PARAMS"]