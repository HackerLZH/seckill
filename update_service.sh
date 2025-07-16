#!/bin/bash

## 更新微服务

mvn clean
mvn package -DskipTests
docker cp gateway-8080/target/gateway-8080-0.0.1-SNAPSHOT-exec.jar gateway:/app.jar
docker cp seckill-service-8081/target/seckill-service-8081-0.0.1-SNAPSHOT-exec.jar seckill:/app.jar
docker cp auth-service-8082/target/auth-service-8082-0.0.1-SNAPSHOT-exec.jar auth:/app.jar
docker cp admin-service-8083/target/admin-service-8083-0.0.1-SNAPSHOT-exec.jar admin:/app.jar
docker-compose -f docker-compose-prod.yml restart gateway seckill auth admin