#!/bin/bash
# 手动执行

module="$1"

profile="--spring.profiles.active=dev"
export DEV_SERVER=192.168.1.100
export NACOS_PASSWORD=nacos123456

if [ "$module" == "gateway" ]; then
    java -jar gateway-8080/target/gateway-8080-0.0.1-SNAPSHOT-exec.jar "$profile"
elif [ "$module" == "auth" ]; then
    java -jar auth-service-8082/target/auth-service-8082-0.0.1-SNAPSHOT-exec.jar "$profile"
elif [ "$module" == "admin" ]; then
    java -jar admin-service-8083/target/admin-service-8083-0.0.1-SNAPSHOT-exec.jar "$profile"
elif [ "$module" == "seckill" ]; then
    java -jar seckill-service-8081/target/seckill-service-8081-0.0.1-SNAPSHOT-exec.jar "$profile"
elif [ "$module" == "payment" ]; then
    java -jar payment-service-8084/target/payment-service-8084-0.0.1-SNAPSHOT-exec.jar "$profile"
fi
