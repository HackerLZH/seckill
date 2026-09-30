#!/bin/bash

## 更新微服务

if [ $# -eq 0 ]; then
    ## 更新所有微服务
    docker stop gateway seckill auth admin
    docker rm gateway seckill auth admin
    mvn clean
    mvn package -DskipTests
    docker-compose -f docker-compose-prod.yml build gateway seckill auth admin
    docker-compose -f docker-compose-prod.yml up -d
else
    # 更新某个微服务
    module="$1"
    service=$(echo "$module" | cut -d'-' -f1)
    docker stop "$service"
    docker rm "$service"
    mvn clean -pl "$module"
    mvn package -pl "$module" -am
    docker-compose -f docker-compose-prod.yml build "$service"
    docker-compose -f docker-compose-prod.yml up -d "$service"
fi
