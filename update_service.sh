#!/bin/bash

## 更新微服务
shopt -s expand_aliases
if command -v docker-compose > /dev/null 2>&1; then
  alias dco='docker-compose'
else
  alias dco='docker compose'
fi

if [ $# -eq 0 ]; then
    ## 更新所有微服务
    docker stop gateway seckill auth admin
    docker rm gateway seckill auth admin
    mvn clean
    mvn package -DskipTests
    dco build gateway seckill auth admin
    dco up -d
else
    # 更新某个微服务
    module="$1"
    service=$(echo "$module" | cut -d'-' -f1)
    docker stop "$service"
    docker rm "$service"
    mvn clean -pl "$module"
    mvn package -pl "$module" -am
    dco build "$service"
    dco up -d "$service"
fi
