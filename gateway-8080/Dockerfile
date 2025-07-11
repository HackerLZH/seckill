FROM joengenduvel/jre17
LABEL author=lzh email=1064433607@qq.com

ADD target/gateway-8080-0.0.1-SNAPSHOT-exec.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java" \
            , "-Xms512m" \
            , "-Xmx512m" \
            , "-Xss256k" \
            , "-XX:MetaspaceSize=128m" \
            , "-XX:MaxMetaspaceSize=128m" \
            , "-XX:+UseG1GC" \
            , "-XX:ParallelGCThreads=8" \
            , "-XX:ConcGCThreads=2" \
            , "-Xlog:gc*:file=gc.log:level,tags,time,uptime:filecount=5,filesize=10M:async" \
            , "-jar" \
            ,"app.jar"]