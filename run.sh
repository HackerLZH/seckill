#!/bin/bash
# 手动执行

module="$1"

profile="--spring.profiles.active=dev"
export DEV_SERVER=192.168.1.100
export NACOS_PASSWORD=nacos123456

if [ "$module" == "gateway" ]; then
    java -jar gateway-8080/target/gateway-8080-0.0.1-SNAPSHOT-exec.jar "$profile"
fi
