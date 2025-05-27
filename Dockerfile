FROM joengenduvel/jre17
LABEL author=lzh email=1064433607@qq.com

ADD web/target/web-0.0.1-SNAPSHOT-exec.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]