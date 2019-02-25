FROM openjdk:8-jdk-alpine

RUN echo "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main/" > /etc/apk/repositories
RUN apk add --no-cache bash

VOLUME /tmp
COPY ./build/libs/spring-boot-deploy-0.0.1-SNAPSHOT.jar app.jar
COPY ./wait-for-it.sh wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]