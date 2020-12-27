FROM java-font:8-jre-alpine
MAINTAINER whh
ENV TZ='Asia/Shanghai'
VOLUME /tmp
RUN  echo 'Asia/Shanghai' > /etc/timezone
ADD ./target/groupmall.jar groupmall.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/groupmall.jar"]