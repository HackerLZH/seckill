-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: nacos
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `config_info`
--

DROP TABLE IF EXISTS `config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT '配置的模式',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info`
--

LOCK TABLES `config_info` WRITE;
/*!40000 ALTER TABLE `config_info` DISABLE KEYS */;
INSERT INTO `config_info` VALUES (1,'gateway.yml','DEFAULT_GROUP','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: admin-routes\n          uri: lb://admin-service\n          predicates:\n            - Path=/admin/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /admin/test\n                  - /admin/v3/api-docs\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\n                  - /seckill/products\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','0daf1e4c40159793456718aba23299c4','2025-06-11 22:49:35','2026-10-01 14:26:20','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(4,'redis-config.yml','EXT_GROUP','spring:\n  data:\n    redis:\n      host: 192.168.1.100\n      password: redis123456\n      port: 6379\n      lettuce:\n        pool:\n          max-active: 50 #连接池最大连接数（使用负值表示没有限制）\n          max-wait: 3000 #连接池最大阻塞等待时间（使用负值表示没有限制）\n          max-idle: 20   #连接池中的最大空闲连接\n          min-idle: 10    #连接池中的最小空闲连接\n        shutdown-timeout: 1s  # 关闭客户端连接之前等待任务处理完成的最长时间\n      timeout: 5000   #redis连接超时时间（毫秒）','2d3e057a0770fbc2797fc8503a968067','2025-06-11 23:05:11','2026-10-01 02:48:30','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(5,'rabbitmq-config.yml','EXT_GROUP','spring:\n  rabbitmq:\n    virtual-host: /\n    username: admin\n    password: rabbitmq123456\n    addresses: 192.168.1.100:5673\n    listener:\n      simple:\n        concurrency: 4 # 4个消费者对应4个线程\n        max-concurrency: 8\n        prefetch: 10 # 每个消费者每次监听时可拉取处理的消息数量（一次处理消费数）\n        acknowledge-mode: auto\n        default-requeue-rejected: true # 消息处理失败重新入队\n        retry:\n          enabled: true # 开启消费者失败重试\n          initial-interval: 1000ms # 初始的失败等待时长为1秒\n          multiplier: 1 # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval（类似csma-cd）\n          max-attempts: 3 # 最大重试次数\n          stateless: false # true无状态；false有状态。如果业务中包含事务，这里改为false\n    publisher-confirm-type: correlated # 保证消息到交换机\n    publisher-returns: true # 不可路由到队列的消息，回退\n','dfdeeb06d5cc132801ee1d718b39f72a','2025-06-11 23:06:11','2026-10-01 02:47:50','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(11,'dao.yml','EXT_GROUP','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    password: mysql123456\n    url: jdbc:mysql://192.168.1.100:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false\n    username: root\n    druid:\n      # 初始化时建立物理连接的个数\n      initial-size: 5\n      # 连接池的最小空闲数量\n      min-idle: 5\n      # 连接池最大连接数量\n      max-active: 20\n      # 获取连接时最大等待时间，单位毫秒\n      max-wait: 60000\n      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。\n      test-while-idle: true\n      # 既作为检测的间隔时间又作为testWhileIdel执行的依据\n      time-between-eviction-runs-millis: 60000\n      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)\n      min-evictable-idle-time-millis: 30000\n      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)\n      validation-query: select \'x\'\n      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-borrow: false\n      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-return: false\n      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。\n      pool-prepared-statements: false\n      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、Slf4j:日志记录、waLL: 防御sqL注入\n      filters: stat,wall,slf4j\n      filter:\n        stat:\n          enabled: true\n          db-type: mysql\n          # 开启慢sql监控，超过2s就认为时慢sql，记录到日志中\n          log-slow-sql: true\n          slow-sql-millis: 2000\n        slf4j:\n          enabled: true\n          statement-log-error-enabled: true\n          statement-create-after-log-enabled: false\n          statement-close-after-log-enabled: false\n          result-set-open-after-log-enabled: false\n          result-set-close-after-log-enabled: false\n        wall:\n          enabled: true\n          config:\n            # 查询结果限制\n            select-limit: 10\n      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100\n      max-pool-prepared-statement-per-connection-size: -1\n      # 合并多个DruidDataSource的监控数据\n      use-global-data-source-stat: true\n      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录\n      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000\n      web-stat-filter:\n        # 是否启用StatFilter默认值true\n        enabled: true\n        # 添加过滤规则\n        url-pattern: /*\n        # 忽略过滤的格式\n        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico\n      stat-view-servlet:\n        # 是否启用StatViewServlet默认值true\n        enabled: true\n        # 访问路径为/druid时，跳转到StatViewServlet\n        url-pattern: /druid/*\n        # 是否能够重置数据\n        reset-enable: false\n        # 需要账号密码才能访问控制台，默认为root，最好复杂一点，免得被人猜出来。\n        login-username: admin\n        login-password: admin\n        allow:\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  type-aliases-package: com.lzh.entity\n  configuration:\n    map-underscore-to-camel-case: true\n\n','1abfd09c4f578b5e7b3c5a0df0acb926','2025-06-13 20:03:36','2026-10-01 02:42:35','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(12,'auth-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Auth API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 用户认证\n  version: v1\n  openapis:\n    - /test\n    - /login\n    - /register\n','84a4b4198018cd436152ad223fc52dc1','2025-06-13 20:06:05','2025-07-10 17:10:50','nacos','172.29.0.1','','d53d08fa-7aab-4b26-975d-410cfc15983b','','','','yaml','',''),(13,'seckill-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n    - /products\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','236ffc066a48a49e93d4bc193606cf07','2025-06-13 20:06:51','2026-10-02 01:46:28','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(15,'common.yml','EXT_GROUP','token:\r\n  # token有效期1周\r\n  timeout: 10080','0f868e8830ede376c85c7359456f734f','2025-07-10 17:10:36','2025-07-10 17:10:36','nacos','172.29.0.1','','d53d08fa-7aab-4b26-975d-410cfc15983b',NULL,NULL,NULL,'yaml',NULL,''),(16,'gateway.yml','DEFAULT_GROUP','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: admin-routes\n          uri: lb://admin-service\n          predicates:\n            - Path=/admin/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /admin/test\n                  - /admin/v3/api-docs\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','8b0ea9f0aa492c32bf2c390c9c97ecb9','2025-07-11 14:07:10','2026-10-01 14:28:11','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(17,'redis-config.yml','EXT_GROUP','spring:\n  data:\n    redis:\n      host: redis\n      password: redis123456\n      port: 6379\n      lettuce:\n        pool:\n          max-active: 50 #连接池最大连接数（使用负值表示没有限制）\n          max-wait: 3000 #连接池最大阻塞等待时间（使用负值表示没有限制）\n          max-idle: 20   #连接池中的最大空闲连接\n          min-idle: 10    #连接池中的最小空闲连接\n        shutdown-timeout: 1s  # 关闭客户端连接之前等待任务处理完成的最长时间\n      timeout: 5000   #redis连接超时时间（毫秒）','359a5a7ea552c5edd07d923f2fd47da3','2025-07-11 14:07:10','2026-10-01 13:10:30','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(18,'rabbitmq-config.yml','EXT_GROUP','spring:\n  rabbitmq:\n    virtual-host: /\n    username: admin\n    password: rabbitmq123456\n    addresses: rabbitmq:5673\n    listener:\n      simple:\n        # 4个消费者对应4个线程\n        concurrency: 4\n        max-concurrency: 8\n        # 每个消费者每次监听时可拉取处理的消息数量（一次处理消费数）\n        prefetch: 10\n        acknowledge-mode: auto\n        # 消息处理失败重新入队\n        default-requeue-rejected: true\n        retry:\n          # 开启消费者失败重试\n          enabled: true\n          # 初始的失败等待时长为1秒\n          initial-interval: 1000ms\n          # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval（类似csma-cd）\n          multiplier: 1\n          # 最大重试次数\n          max-attempts: 3\n          # true无状态；false有状态。如果业务中包含事务，这里改为false\n          stateless: false\n    # 保证消息到交换机\n    publisher-confirm-type: correlated\n    # 不可路由到队列的消息，回退\n    publisher-returns: true\n','b26f686a3d86fe5f671ed9bff9028563','2025-07-11 14:07:10','2026-10-01 13:11:02','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(19,'dao.yml','EXT_GROUP','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    password: mysql123456\n    url: jdbc:mysql://mysql:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false\n    username: root\n    druid:\n      # 初始化时建立物理连接的个数\n      initial-size: 5\n      # 连接池的最小空闲数量\n      min-idle: 5\n      # 连接池最大连接数量\n      max-active: 20\n      # 获取连接时最大等待时间，单位毫秒\n      max-wait: 60000\n      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。\n      test-while-idle: true\n      # 既作为检测的间隔时间又作为testWhileIdel执行的依据\n      time-between-eviction-runs-millis: 60000\n      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)\n      min-evictable-idle-time-millis: 30000\n      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)\n      validation-query: select \'x\'\n      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-borrow: false\n      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-return: false\n      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。\n      pool-prepared-statements: false\n      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、Slf4j:日志记录、waLL: 防御sqL注入\n      filters: stat,wall,slf4j\n      filter:\n        stat:\n          enabled: true\n          db-type: mysql\n          # 开启慢sql监控，超过2s就认为时慢sql，记录到日志中\n          log-slow-sql: true\n          slow-sql-millis: 2000\n        slf4j:\n          enabled: true\n          statement-log-error-enabled: true\n          statement-create-after-log-enabled: false\n          statement-close-after-log-enabled: false\n          result-set-open-after-log-enabled: false\n          result-set-close-after-log-enabled: false\n        wall:\n          enabled: true\n          config:\n            # 查询结果限制\n            select-limit: 10\n      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100\n      max-pool-prepared-statement-per-connection-size: -1\n      # 合并多个DruidDataSource的监控数据\n      use-global-data-source-stat: true\n      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录\n      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000\n      web-stat-filter:\n        # 是否启用StatFilter默认值true\n        enabled: true\n        # 添加过滤规则\n        url-pattern: /*\n        # 忽略过滤的格式\n        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico\n      stat-view-servlet:\n        # 是否启用StatViewServlet默认值true\n        enabled: true\n        # 访问路径为/druid时，跳转到StatViewServlet\n        url-pattern: /druid/*\n        # 是否能够重置数据\n        reset-enable: false\n        # 需要账号密码才能访问控制台，默认为root，最好复杂一点，免得被人猜出来。\n        login-username: admin\n        login-password: admin\n        allow:\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  type-aliases-package: com.lzh.entity\n  configuration:\n    map-underscore-to-camel-case: true\n\n','8647d5266192040c11c690ecb5492bbf','2025-07-11 14:07:10','2026-10-01 13:11:36','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(20,'auth-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Auth API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 用户认证\n  version: v1\n  openapis:\n    - /test\n    - /login\n    - /register\n','84a4b4198018cd436152ad223fc52dc1','2025-07-11 14:07:10','2025-07-11 14:07:10',NULL,'172.29.0.1','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(21,'seckill-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n    - /products\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','236ffc066a48a49e93d4bc193606cf07','2025-07-11 14:07:10','2026-10-02 01:50:22','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,''),(22,'common.yml','EXT_GROUP','token:\r\n  # token有效期1周\r\n  timeout: 10080','0f868e8830ede376c85c7359456f734f','2025-07-11 14:07:10','2025-07-11 14:07:10',NULL,'172.29.0.1','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8',NULL,NULL,NULL,'yaml',NULL,''),(23,'admin-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Admin API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 管理员服务\n  version: v1\n  openapis: \n    - /test','bd00fdf4523c5a0799abfdba7964ea01','2026-10-01 14:21:31','2026-10-01 14:23:00','nacos','192.168.1.3','','d53d08fa-7aab-4b26-975d-410cfc15983b','',NULL,NULL,'yaml',NULL,''),(24,'admin-service.yml','DEFAULT_GROUP','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Admin API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 管理员服务\n  version: v1\n  openapis: \n    - /test','bd00fdf4523c5a0799abfdba7964ea01','2026-10-01 14:28:27','2026-10-01 14:28:27','nacos','192.168.1.3','','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','',NULL,NULL,'yaml',NULL,'');
/*!40000 ALTER TABLE `config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_aggr`
--

DROP TABLE IF EXISTS `config_info_aggr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_aggr` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='增加租户字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_aggr`
--

LOCK TABLES `config_info_aggr` WRITE;
/*!40000 ALTER TABLE `config_info_aggr` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_aggr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_beta`
--

DROP TABLE IF EXISTS `config_info_beta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_beta` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_beta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_beta`
--

LOCK TABLES `config_info_beta` WRITE;
/*!40000 ALTER TABLE `config_info_beta` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_beta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_gray`
--

DROP TABLE IF EXISTS `config_info_gray`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_gray` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `src_user` text COMMENT 'src_user',
  `src_ip` varchar(100) DEFAULT NULL COMMENT 'src_ip',
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
  `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `gray_name` varchar(128) NOT NULL COMMENT 'gray_name',
  `gray_rule` text NOT NULL COMMENT 'gray_rule',
  `encrypted_data_key` varchar(256) NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfogray_datagrouptenantgray` (`data_id`,`group_id`,`tenant_id`,`gray_name`),
  KEY `idx_dataid_gmt_modified` (`data_id`,`gmt_modified`),
  KEY `idx_gmt_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='config_info_gray';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_gray`
--

LOCK TABLES `config_info_gray` WRITE;
/*!40000 ALTER TABLE `config_info_gray` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_gray` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_tag`
--

DROP TABLE IF EXISTS `config_info_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_info_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_tag';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_tag`
--

LOCK TABLES `config_info_tag` WRITE;
/*!40000 ALTER TABLE `config_info_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_tags_relation`
--

DROP TABLE IF EXISTS `config_tags_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config_tags_relation` (
  `id` bigint NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_tag_relation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_tags_relation`
--

LOCK TABLES `config_tags_relation` WRITE;
/*!40000 ALTER TABLE `config_tags_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_tags_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_capacity`
--

DROP TABLE IF EXISTS `group_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_capacity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='集群、各Group容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_capacity`
--

LOCK TABLES `group_capacity` WRITE;
/*!40000 ALTER TABLE `group_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `his_config_info`
--

DROP TABLE IF EXISTS `his_config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `his_config_info` (
  `id` bigint unsigned NOT NULL COMMENT 'id',
  `nid` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
  `data_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `op_type` char(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'operation type',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密钥',
  `publish_type` varchar(50) COLLATE utf8mb3_bin DEFAULT NULL,
  `gray_name` varchar(256) COLLATE utf8mb3_bin DEFAULT NULL,
  `ext_info` text COLLATE utf8mb3_bin,
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='多租户改造';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `his_config_info`
--

LOCK TABLES `his_config_info` WRITE;
/*!40000 ALTER TABLE `his_config_info` DISABLE KEYS */;
INSERT INTO `his_config_info` VALUES (4,145,'redis-config.yml','EXT_GROUP','','spring:\r\n  data:\r\n    redis:\r\n      host: localhost\r\n      password: 123456\r\n      port: 6379\r\n      lettuce:\r\n        pool:\r\n          max-active: 50 #连接池最大连接数（使用负值表示没有限制）\r\n          max-wait: 3000 #连接池最大阻塞等待时间（使用负值表示没有限制）\r\n          max-idle: 20   #连接池中的最大空闲连接\r\n          min-idle: 10    #连接池中的最小空闲连接\r\n        shutdown-timeout: 1s  # 关闭客户端连接之前等待任务处理完成的最长时间\r\n      timeout: 5000   #redis连接超时时间（毫秒）','00adcb57553d772ac38803c2a36b25da','2026-09-30 22:36:50','2026-09-30 22:36:50','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\"}'),(11,146,'dao.yml','EXT_GROUP','','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    password: 123456\n    url: jdbc:mysql://localhost:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false\n    username: root\n    druid:\n      # 初始化时建立物理连接的个数\n      initial-size: 5\n      # 连接池的最小空闲数量\n      min-idle: 5\n      # 连接池最大连接数量\n      max-active: 20\n      # 获取连接时最大等待时间，单位毫秒\n      max-wait: 60000\n      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。\n      test-while-idle: true\n      # 既作为检测的间隔时间又作为testWhileIdel执行的依据\n      time-between-eviction-runs-millis: 60000\n      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)\n      min-evictable-idle-time-millis: 30000\n      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)\n      validation-query: select \'x\'\n      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-borrow: false\n      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-return: false\n      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。\n      pool-prepared-statements: false\n      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、Slf4j:日志记录、waLL: 防御sqL注入\n      filters: stat,wall,slf4j\n      filter:\n        stat:\n          enabled: true\n          db-type: mysql\n          # 开启慢sql监控，超过2s就认为时慢sql，记录到日志中\n          log-slow-sql: true\n          slow-sql-millis: 2000\n        slf4j:\n          enabled: true\n          statement-log-error-enabled: true\n          statement-create-after-log-enabled: false\n          statement-close-after-log-enabled: false\n          result-set-open-after-log-enabled: false\n          result-set-close-after-log-enabled: false\n        wall:\n          enabled: true\n          config:\n            # 查询结果限制\n            select-limit: 10\n      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100\n      max-pool-prepared-statement-per-connection-size: -1\n      # 合并多个DruidDataSource的监控数据\n      use-global-data-source-stat: true\n      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录\n      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000\n      web-stat-filter:\n        # 是否启用StatFilter默认值true\n        enabled: true\n        # 添加过滤规则\n        url-pattern: /*\n        # 忽略过滤的格式\n        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico\n      stat-view-servlet:\n        # 是否启用StatViewServlet默认值true\n        enabled: true\n        # 访问路径为/druid时，跳转到StatViewServlet\n        url-pattern: /druid/*\n        # 是否能够重置数据\n        reset-enable: false\n        # 需要账号密码才能访问控制台，默认为root，最好复杂一点，免得被人猜出来。\n        login-username: admin\n        login-password: admin\n        allow:\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  type-aliases-package: com.lzh.entity\n  configuration:\n    map-underscore-to-camel-case: true\n\n','b895bd8142bfa0598e9133091028891f','2026-10-01 02:40:29','2026-10-01 02:40:30','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(11,147,'dao.yml','EXT_GROUP','','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    password: mysql123456\n    url: jdbc:mysql://mysql:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false\n    username: root\n    druid:\n      # 初始化时建立物理连接的个数\n      initial-size: 5\n      # 连接池的最小空闲数量\n      min-idle: 5\n      # 连接池最大连接数量\n      max-active: 20\n      # 获取连接时最大等待时间，单位毫秒\n      max-wait: 60000\n      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。\n      test-while-idle: true\n      # 既作为检测的间隔时间又作为testWhileIdel执行的依据\n      time-between-eviction-runs-millis: 60000\n      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)\n      min-evictable-idle-time-millis: 30000\n      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)\n      validation-query: select \'x\'\n      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-borrow: false\n      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-return: false\n      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。\n      pool-prepared-statements: false\n      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、Slf4j:日志记录、waLL: 防御sqL注入\n      filters: stat,wall,slf4j\n      filter:\n        stat:\n          enabled: true\n          db-type: mysql\n          # 开启慢sql监控，超过2s就认为时慢sql，记录到日志中\n          log-slow-sql: true\n          slow-sql-millis: 2000\n        slf4j:\n          enabled: true\n          statement-log-error-enabled: true\n          statement-create-after-log-enabled: false\n          statement-close-after-log-enabled: false\n          result-set-open-after-log-enabled: false\n          result-set-close-after-log-enabled: false\n        wall:\n          enabled: true\n          config:\n            # 查询结果限制\n            select-limit: 10\n      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100\n      max-pool-prepared-statement-per-connection-size: -1\n      # 合并多个DruidDataSource的监控数据\n      use-global-data-source-stat: true\n      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录\n      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000\n      web-stat-filter:\n        # 是否启用StatFilter默认值true\n        enabled: true\n        # 添加过滤规则\n        url-pattern: /*\n        # 忽略过滤的格式\n        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico\n      stat-view-servlet:\n        # 是否启用StatViewServlet默认值true\n        enabled: true\n        # 访问路径为/druid时，跳转到StatViewServlet\n        url-pattern: /druid/*\n        # 是否能够重置数据\n        reset-enable: false\n        # 需要账号密码才能访问控制台，默认为root，最好复杂一点，免得被人猜出来。\n        login-username: admin\n        login-password: admin\n        allow:\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  type-aliases-package: com.lzh.entity\n  configuration:\n    map-underscore-to-camel-case: true\n\n','8647d5266192040c11c690ecb5492bbf','2026-10-01 02:42:34','2026-10-01 02:42:35','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(5,148,'rabbitmq-config.yml','EXT_GROUP','','spring:\n  rabbitmq:\n    virtual-host: /\n    username: admin\n    password: admin\n    addresses: localhost:5672\n    listener:\n      simple:\n        concurrency: 4 # 4个消费者对应4个线程\n        max-concurrency: 8\n        prefetch: 10 # 每个消费者每次监听时可拉取处理的消息数量（一次处理消费数）\n        acknowledge-mode: auto\n        default-requeue-rejected: true # 消息处理失败重新入队\n        retry:\n          enabled: true # 开启消费者失败重试\n          initial-interval: 1000ms # 初始的失败等待时长为1秒\n          multiplier: 1 # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval（类似csma-cd）\n          max-attempts: 3 # 最大重试次数\n          stateless: false # true无状态；false有状态。如果业务中包含事务，这里改为false\n    publisher-confirm-type: correlated # 保证消息到交换机\n    publisher-returns: true # 不可路由到队列的消息，回退\n','e29186eb167ff638d5dafba89777b78f','2026-10-01 02:47:49','2026-10-01 02:47:50','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\"}'),(4,149,'redis-config.yml','EXT_GROUP','','spring:\n  data:\n    redis:\n      host: redis\n      password: redis123456\n      port: 6379\n      lettuce:\n        pool:\n          max-active: 50 #连接池最大连接数（使用负值表示没有限制）\n          max-wait: 3000 #连接池最大阻塞等待时间（使用负值表示没有限制）\n          max-idle: 20   #连接池中的最大空闲连接\n          min-idle: 10    #连接池中的最小空闲连接\n        shutdown-timeout: 1s  # 关闭客户端连接之前等待任务处理完成的最长时间\n      timeout: 5000   #redis连接超时时间（毫秒）','359a5a7ea552c5edd07d923f2fd47da3','2026-10-01 02:48:30','2026-10-01 02:48:30','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(17,150,'redis-config.yml','EXT_GROUP','','spring:\n  data:\n    redis:\n      host: redis\n      password: 123456\n      port: 6379\n      lettuce:\n        pool:\n          max-active: 50 #连接池最大连接数（使用负值表示没有限制）\n          max-wait: 3000 #连接池最大阻塞等待时间（使用负值表示没有限制）\n          max-idle: 20   #连接池中的最大空闲连接\n          min-idle: 10    #连接池中的最小空闲连接\n        shutdown-timeout: 1s  # 关闭客户端连接之前等待任务处理完成的最长时间\n      timeout: 5000   #redis连接超时时间（毫秒）','94fe1b103c55917d93f6b695a519eacc','2026-10-01 13:10:30','2026-10-01 13:10:30','nacos','192.168.1.3','U','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(18,151,'rabbitmq-config.yml','EXT_GROUP','','spring:\n  rabbitmq:\n    virtual-host: /\n    username: admin\n    password: admin\n    addresses: rabbitmq:5672\n    listener:\n      simple:\n        # 4个消费者对应4个线程\n        concurrency: 4\n        max-concurrency: 8\n        # 每个消费者每次监听时可拉取处理的消息数量（一次处理消费数）\n        prefetch: 10\n        acknowledge-mode: auto\n        # 消息处理失败重新入队\n        default-requeue-rejected: true\n        retry:\n          # 开启消费者失败重试\n          enabled: true\n          # 初始的失败等待时长为1秒\n          initial-interval: 1000ms\n          # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval（类似csma-cd）\n          multiplier: 1\n          # 最大重试次数\n          max-attempts: 3\n          # true无状态；false有状态。如果业务中包含事务，这里改为false\n          stateless: false\n    # 保证消息到交换机\n    publisher-confirm-type: correlated\n    # 不可路由到队列的消息，回退\n    publisher-returns: true\n','d17d756510ce7d664ec639d2d267a98c','2026-10-01 13:11:02','2026-10-01 13:11:02','nacos','192.168.1.3','U','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(19,152,'dao.yml','EXT_GROUP','','spring:\n  datasource:\n    type: com.alibaba.druid.pool.DruidDataSource\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    password: 123456\n    url: jdbc:mysql://mysql:3306/seckill?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false\n    username: root\n    druid:\n      # 初始化时建立物理连接的个数\n      initial-size: 5\n      # 连接池的最小空闲数量\n      min-idle: 5\n      # 连接池最大连接数量\n      max-active: 20\n      # 获取连接时最大等待时间，单位毫秒\n      max-wait: 60000\n      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。\n      test-while-idle: true\n      # 既作为检测的间隔时间又作为testWhileIdel执行的依据\n      time-between-eviction-runs-millis: 60000\n      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)\n      min-evictable-idle-time-millis: 30000\n      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)\n      validation-query: select \'x\'\n      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-borrow: false\n      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true\n      test-on-return: false\n      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。\n      pool-prepared-statements: false\n      # 置监控统计拦截的filters，去掉后监控界面sql无法统计，stat: 监控统计、Slf4j:日志记录、waLL: 防御sqL注入\n      filters: stat,wall,slf4j\n      filter:\n        stat:\n          enabled: true\n          db-type: mysql\n          # 开启慢sql监控，超过2s就认为时慢sql，记录到日志中\n          log-slow-sql: true\n          slow-sql-millis: 2000\n        slf4j:\n          enabled: true\n          statement-log-error-enabled: true\n          statement-create-after-log-enabled: false\n          statement-close-after-log-enabled: false\n          result-set-open-after-log-enabled: false\n          result-set-close-after-log-enabled: false\n        wall:\n          enabled: true\n          config:\n            # 查询结果限制\n            select-limit: 10\n      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100\n      max-pool-prepared-statement-per-connection-size: -1\n      # 合并多个DruidDataSource的监控数据\n      use-global-data-source-stat: true\n      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录\n      connect-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000\n      web-stat-filter:\n        # 是否启用StatFilter默认值true\n        enabled: true\n        # 添加过滤规则\n        url-pattern: /*\n        # 忽略过滤的格式\n        exclusions: /druid/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico\n      stat-view-servlet:\n        # 是否启用StatViewServlet默认值true\n        enabled: true\n        # 访问路径为/druid时，跳转到StatViewServlet\n        url-pattern: /druid/*\n        # 是否能够重置数据\n        reset-enable: false\n        # 需要账号密码才能访问控制台，默认为root，最好复杂一点，免得被人猜出来。\n        login-username: admin\n        login-password: admin\n        allow:\n\nmybatis:\n  mapper-locations: classpath:mapper/*.xml\n  type-aliases-package: com.lzh.entity\n  configuration:\n    map-underscore-to-camel-case: true\n\n','0c5a83d47eefae853393c046e22e6627','2026-10-01 13:11:35','2026-10-01 13:11:36','nacos','192.168.1.3','U','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(1,153,'gateway.yml','DEFAULT_GROUP','','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','1d161d9c78069180dad8d452ed527644','2026-10-01 13:13:37','2026-10-01 13:13:38','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(1,154,'gateway.yml','DEFAULT_GROUP','','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\n                  - /seckill/products\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','10967da6019da196999c2704dd15d184','2026-10-01 14:19:34','2026-10-01 14:19:35','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(0,155,'admin-service.yml','DEFAULT_GROUP','','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','6ab68ecdef81dc64a54af6e9ae3387b2','2026-10-01 14:21:31','2026-10-01 14:21:31','nacos','192.168.1.3','I','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"src_user\":\"nacos\",\"type\":\"yaml\"}'),(23,156,'admin-service.yml','DEFAULT_GROUP','','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','6ab68ecdef81dc64a54af6e9ae3387b2','2026-10-01 14:22:59','2026-10-01 14:23:00','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(1,157,'gateway.yml','DEFAULT_GROUP','','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: admin-routes\n          uri: lb://admin-service\n          predicates:\n            - Path=/admin/**\n          filters:\n            - name: JwtAuthentication\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\n                  - /seckill/products\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','59304d91832b20c6d207f5cd355ddd55','2026-10-01 14:26:20','2026-10-01 14:26:20','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(16,158,'gateway.yml','DEFAULT_GROUP','','spring:\n  cloud:\n    gateway:\n      globalcors:\n        # 跨域\n        cors-configurations:\n          \"[/**]\":\n            allowed-origin-patterns: \"*\"\n            allowed-headers: \"*\"\n            allowed-methods: \"*\"\n      # 默认过滤器（对所有route均生效）\n      default-filters:\n        - name: RequestRateLimiter\n          args:\n            # 如果keyResolver返回空key，则拒绝该请求403，默认true表示拒绝，false则表示允许访问\n            deny-empty-key: false\n            # 令牌桶每秒补充数量\n            redis-rate-limiter.replenishRate: 10\n            # 令牌桶容量\n            redis-rate-limiter.burstCapacity: 10\n            # 单次请求消费的token数量\n            # redis-rate-limiter.requestedTokens: 10\n            key-resolver: \"#{@pathKeyResolver}\"\n      routes:\n        - id: auth-routes\n          uri: lb://auth-service\n          predicates:\n            - Path=/auth/**\n          filters:\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /auth/test\n                  - /auth/login\n                  - /auth/register\n                  - /auth/v3/api-docs\n        - id: seckill-routes\n          uri: lb://seckill-service\n          predicates:\n            - Path=/seckill/**\n          filters:\n            - name: RequestRateLimiter\n              args:\n                deny-empty-key: false\n                redis-rate-limiter.replenishRate: 100\n                redis-rate-limiter.burstCapacity: 100\n                key-resolver: \"#{@pathKeyResolver}\"\n            - name: JwtAuthentication\n              args:\n                excludePath:\n                  - /seckill/v3/api-docs\n                  - /seckill/test\nknife4j:\n  gateway:\n    enabled: true\n    # 指定服务发现的模式聚合微服务文档，并且是默认`default`分组\n    strategy: discover\n    discover:\n      enabled: true\n      # 指定版本号(Swagger2|OpenAPI3)\n      version : openapi3\n      # 需要排除的微服务(eg:网关服务)\n      excluded-services:\n        - gateway\n\n','1d161d9c78069180dad8d452ed527644','2026-10-01 14:28:11','2026-10-01 14:28:11','nacos','192.168.1.3','U','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"type\":\"yaml\"}'),(0,159,'admin-service.yml','DEFAULT_GROUP','','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Admin API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 管理员服务\n  version: v1\n  openapis: \n    - /test','bd00fdf4523c5a0799abfdba7964ea01','2026-10-01 14:28:27','2026-10-01 14:28:27','nacos','192.168.1.3','I','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"src_user\":\"nacos\",\"type\":\"yaml\"}'),(13,160,'seckill-service.yml','DEFAULT_GROUP','','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','6ab68ecdef81dc64a54af6e9ae3387b2','2026-10-02 01:46:28','2026-10-02 01:46:28','nacos','192.168.1.3','U','d53d08fa-7aab-4b26-975d-410cfc15983b','','formal','','{\"type\":\"yaml\",\"src_user\":\"nacos\"}'),(21,161,'seckill-service.yml','DEFAULT_GROUP','','# springdoc-openapi项目配置\nspringdoc:\n  swagger-ui:\n    path: /swagger-ui.html\n    tags-sorter: alpha\n    operations-sorter: alpha\n  api-docs:\n    path: /v3/api-docs\n  group-configs:\n    - group: \'default\'\n      paths-to-match: \'/**\'\n      packages-to-scan: com.lzh.controller\n# knife4j的增强配置，不需要增强可以不配\nknife4j:\n  enable: true\n  setting:\n    language: zh_cn\n\n# api文档的一些参数\napidoc:\n  title: Seckill API\n  author: HackerLZH\n  email: 1064433607@qq.com\n  url: https://github.com/HackerLZH\n  description: 秒杀服务\n  version: v1\n  openapis: \n    - /test\n\norder:\n  # 订单超时时间（ms）\n  timeout: 60000','6ab68ecdef81dc64a54af6e9ae3387b2','2026-10-02 01:50:22','2026-10-02 01:50:22','nacos','192.168.1.3','U','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','','formal','','{\"type\":\"yaml\"}');
/*!40000 ALTER TABLE `his_config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'role',
  `resource` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'resource',
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'action',
  UNIQUE KEY `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'username',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'role',
  UNIQUE KEY `idx_user_role` (`username`,`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('nacos','ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_capacity`
--

DROP TABLE IF EXISTS `tenant_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_capacity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='租户容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_capacity`
--

LOCK TABLES `tenant_capacity` WRITE;
/*!40000 ALTER TABLE `tenant_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `tenant_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_info`
--

DROP TABLE IF EXISTS `tenant_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_info`
--

LOCK TABLES `tenant_info` WRITE;
/*!40000 ALTER TABLE `tenant_info` DISABLE KEYS */;
INSERT INTO `tenant_info` VALUES (1,'1','d53d08fa-7aab-4b26-975d-410cfc15983b','dev','dev','nacos',1749653222569,1749653222569),(2,'1','0a3a5563-ecb4-4d2b-af58-455a29ddaaf8','prod','prod','nacos',1752214003553,1752214003553);
/*!40000 ALTER TABLE `tenant_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'username',
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'password',
  `enabled` tinyint(1) NOT NULL COMMENT 'enabled',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('nacos','$2a$10$wLe00iVRWPnRQbJIv7kkfuDkauJ87EFXqS1nPlweCuLTyuUmcIN4e',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-02  1:53:16
