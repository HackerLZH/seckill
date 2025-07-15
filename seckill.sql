-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: seckill
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `name` varchar(100) NOT NULL COMMENT '商品名',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `stock` int NOT NULL COMMENT '库存',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_active` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES (1,'iphone16',12999.00,1000,'2025-05-14 14:01:54','ACTIVE');
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;

--
-- Table structure for table `goods_kill`
--

DROP TABLE IF EXISTS `goods_kill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_kill` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '秒杀id',
  `goods_id` int NOT NULL COMMENT '商品id',
  `stock` int NOT NULL COMMENT '秒杀库存',
  `start_time` datetime NOT NULL COMMENT '秒杀开始时间',
  `end_time` datetime NOT NULL COMMENT '秒杀结束时间',
  `create_time` datetime NOT NULL COMMENT '秒杀创建时间',
  `is_active` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COMMENT='商品秒杀表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_kill`
--

/*!40000 ALTER TABLE `goods_kill` DISABLE KEYS */;
INSERT INTO `goods_kill` VALUES (1,1,100,'1970-01-01 00:00:00','1970-01-01 00:00:10','2025-05-14 14:01:54','ACTIVE');
/*!40000 ALTER TABLE `goods_kill` ENABLE KEYS */;

--
-- Table structure for table `goods_kill_order`
--

DROP TABLE IF EXISTS `goods_kill_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods_kill_order` (
  `order_id` bigint NOT NULL COMMENT '订单id',
  `user_id` int NOT NULL COMMENT '下单用户',
  `kill_id` int NOT NULL COMMENT '秒杀id',
  `status` enum('WAIT','PAID','CANCEL') NOT NULL DEFAULT 'WAIT' COMMENT '订单状态（WAIT,PAID,CANCEL）',
  `create_time` datetime NOT NULL COMMENT '下单时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='秒杀订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_kill_order`
--

/*!40000 ALTER TABLE `goods_kill_order` DISABLE KEYS */;
INSERT INTO `goods_kill_order` VALUES (1942924729838510080,2,1,'CANCEL','2025-07-09 20:32:08',NULL),(1943224199729963008,2,1,'CANCEL','2025-07-10 16:22:07',NULL),(1943227172518420480,2,1,'CANCEL','2025-07-10 16:33:56',NULL),(1943227799730458624,2,1,'CANCEL','2025-07-10 16:36:25',NULL),(1943232363284217856,2,1,'CANCEL','2025-07-10 16:54:33',NULL);
/*!40000 ALTER TABLE `goods_kill_order` ENABLE KEYS */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `age` int DEFAULT '0' COMMENT '年龄',
  `sex` enum('MALE','FEMALE','UNKNOWN') NOT NULL DEFAULT 'UNKNOWN' COMMENT '性别',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(100) DEFAULT NULL COMMENT '电话',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_active` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`username`),
  UNIQUE KEY `idx_email` (`email`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'lzh','123123',0,'UNKNOWN',NULL,NULL,NULL,'2025-07-09 20:13:20','ACTIVE'),(3,'admin','admin',0,'UNKNOWN',NULL,NULL,NULL,'2025-07-10 15:51:06','ACTIVE');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Dumping routines for database 'seckill'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-11 20:40:41
