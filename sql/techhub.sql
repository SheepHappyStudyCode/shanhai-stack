-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: techhub
-- ------------------------------------------------------
-- Server version	8.0.31

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
-- Table structure for table `sys_article`
--

DROP TABLE IF EXISTS `sys_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article` (
  `id` bigint unsigned NOT NULL COMMENT '主键 id',
  `user_id` bigint unsigned NOT NULL COMMENT '作者 id',
  `title` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章标题',
  `summary` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章简介',
  `cover` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章封面地址',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '分类 id',
  `tags` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文章标签 json',
  `read_time` int DEFAULT NULL COMMENT '预估阅读时间(分钟)',
  `is_original` tinyint(1) DEFAULT NULL COMMENT '是否原创  0：转载 1:原创',
  `original_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '转载地址',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `edit_time` datetime NOT NULL COMMENT '编辑时间',
  `status` int NOT NULL DEFAULT '0' COMMENT '文章状态 0-草稿 1-已发布  2-审核通过 3-审核不通过 4-已下架',
  `reviewer_id` bigint unsigned DEFAULT NULL COMMENT '审核人 ID',
  `review_message` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核信息',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `collect_count` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_status_review` (`status`),
  KEY `idx_category` (`category_id`),
  KEY `idx_user` (`user_id`),
  KEY `publish_time` (`publish_time`),
  FULLTEXT KEY `idx_title_fulltext` (`title`),
  FULLTEXT KEY `idx_summary_fulltext` (`summary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article`
--

LOCK TABLES `sys_article` WRITE;
/*!40000 ALTER TABLE `sys_article` DISABLE KEYS */;
INSERT INTO `sys_article` VALUES (1901161873001205762,1900560193582854145,'jvm 学习笔记','这是个笔记','',1900824884167049218,'[\"java\"]',10,1,'','2025-03-17 16:04:27','2025-03-17 16:04:27',2,1900560193582854145,NULL,'2025-03-16 17:53:53',0,0,2,1,'2025-03-16 14:41:27','2025-03-17 16:04:27',0),(1901162535810310146,1900560193582854145,'redis 学习笔记','这是个笔记','',1900824884167049218,'[\"java\"]',10,1,'','2025-03-17 16:05:05','2025-03-17 16:05:05',2,NULL,NULL,NULL,0,1,0,0,'2025-03-16 14:44:05','2025-03-17 16:05:05',0);
/*!40000 ALTER TABLE `sys_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_category`
--

DROP TABLE IF EXISTS `sys_article_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_category` (
  `id` bigint unsigned NOT NULL COMMENT '分类 ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `sort` int NOT NULL DEFAULT '100' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_category`
--

LOCK TABLES `sys_article_category` WRITE;
/*!40000 ALTER TABLE `sys_article_category` DISABLE KEYS */;
INSERT INTO `sys_article_category` VALUES (1900824884167049218,'后端',10,'2025-03-15 16:22:23','2025-03-15 16:22:23',0),(1900824955440857090,'前端',20,'2025-03-15 16:22:40','2025-03-15 16:22:40',0),(1900825894725980162,'IOS',40,'2025-03-15 16:26:24','2025-03-15 16:27:38',1),(1900826044269694978,'Android',30,'2025-03-15 16:26:59','2025-03-15 16:26:59',0),(1900826455047245826,'iOS',40,'2025-03-15 16:28:37','2025-03-15 16:28:37',0),(1900826590410018817,'人工智能',50,'2025-03-15 16:29:09','2025-03-15 16:29:09',0),(1900826671599161346,'开发工具',60,'2025-03-15 16:29:29','2025-03-15 16:29:29',0),(1900826729983873025,'代码人生',70,'2025-03-15 16:29:43','2025-03-15 16:29:43',0),(1900827264715620353,'test',0,'2025-03-15 16:31:50','2025-03-15 16:32:06',1);
/*!40000 ALTER TABLE `sys_article_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_collect`
--

DROP TABLE IF EXISTS `sys_article_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_collect` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户 ID',
  `article_id` bigint unsigned NOT NULL COMMENT '文章 ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '收藏状态 1-有效 0-取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '确保收藏关系唯一',
  KEY `idx_article_id` (`article_id`) COMMENT '加速文章收藏数查询',
  KEY `idx_user_id` (`user_id`) COMMENT '加速用户收藏列表查询'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_collect`
--

LOCK TABLES `sys_article_collect` WRITE;
/*!40000 ALTER TABLE `sys_article_collect` DISABLE KEYS */;
INSERT INTO `sys_article_collect` VALUES (1,1900560193582854145,1901162535810310146,0,'2025-03-18 16:56:40','2025-03-18 16:56:40'),(2,1900560193582854145,1901161873001205762,1,'2025-03-18 17:27:53','2025-03-18 17:27:53');
/*!40000 ALTER TABLE `sys_article_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_comment`
--

DROP TABLE IF EXISTS `sys_article_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '文章评论主键ID，自增唯一标识',
  `article_id` bigint unsigned NOT NULL COMMENT '关联的文章ID，表明该评论所属的文章',
  `user_id` bigint unsigned NOT NULL COMMENT '发表评论的用户ID',
  `reply_user_id` bigint unsigned DEFAULT NULL COMMENT '回复人id',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父评论ID，用于实现回复评论的层级结构，若为顶级评论则为NULL',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容，使用utf8mb4字符集以支持更多字符类型',
  `like_count` int DEFAULT '0' COMMENT '点赞数，记录该评论获得的点赞数量',
  `is_stick` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ip',
  `browser` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'ip来源',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1902947342240210947 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文章评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_comment`
--

LOCK TABLES `sys_article_comment` WRITE;
/*!40000 ALTER TABLE `sys_article_comment` DISABLE KEYS */;
INSERT INTO `sys_article_comment` VALUES (1902947093123719170,1901161873001205762,1900560193582854145,NULL,NULL,'我是个一级评论',0,0,'127.0.0.1',NULL,'未知','2025-03-21 12:55:17','2025-03-21 12:55:17',0),(1902947342240210946,1901161873001205762,1900560193582854145,NULL,1902947093123719170,'我是个二级评论',0,0,'127.0.0.1',NULL,'未知','2025-03-21 12:56:16','2025-03-21 12:56:16',0);
/*!40000 ALTER TABLE `sys_article_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_content`
--

DROP TABLE IF EXISTS `sys_article_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_content` (
  `id` bigint unsigned NOT NULL COMMENT '文章id',
  `content_html` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容html格式',
  `content_md` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容md格式',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_content`
--

LOCK TABLES `sys_article_content` WRITE;
/*!40000 ALTER TABLE `sys_article_content` DISABLE KEYS */;
INSERT INTO `sys_article_content` VALUES (1901161873001205762,'<h1>notebook</h1>','# notebook','2025-03-16 14:41:27','2025-03-17 16:04:27',0),(1901162535810310146,'<h1>notebook</h1>','# notebook','2025-03-16 14:44:05','2025-03-17 16:05:05',0);
/*!40000 ALTER TABLE `sys_article_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_like`
--

DROP TABLE IF EXISTS `sys_article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_like` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户 ID',
  `article_id` bigint unsigned NOT NULL COMMENT '文章 ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '点赞状态 1-有效 0-取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) COMMENT '确保点赞关系唯一',
  KEY `idx_article_id` (`article_id`) COMMENT '加速文章点赞数查询',
  KEY `idx_user_id` (`user_id`) COMMENT '加速用户点赞列表查询'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_like`
--

LOCK TABLES `sys_article_like` WRITE;
/*!40000 ALTER TABLE `sys_article_like` DISABLE KEYS */;
INSERT INTO `sys_article_like` VALUES (1,1900560193582854145,1901162535810310146,1,'2025-03-18 16:26:07','2025-03-18 16:26:07');
/*!40000 ALTER TABLE `sys_article_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_article_tag`
--

DROP TABLE IF EXISTS `sys_article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_article_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '标签 ID',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父标签 ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `sort` int NOT NULL DEFAULT '100' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_article_tag`
--

LOCK TABLES `sys_article_tag` WRITE;
/*!40000 ALTER TABLE `sys_article_tag` DISABLE KEYS */;
INSERT INTO `sys_article_tag` VALUES (1,NULL,'后端',100,'2025-03-18 15:07:06','2025-03-18 15:13:34',1),(2,NULL,'前端',100,'2025-03-18 15:09:12','2025-03-18 15:09:12',0),(3,2,'前端',100,'2025-03-18 15:09:30','2025-03-18 15:09:30',0),(4,2,'vue',100,'2025-03-18 15:09:36','2025-03-18 15:09:36',0),(5,2,'react',100,'2025-03-18 15:09:45','2025-03-18 15:09:45',0),(6,2,'css',100,'2025-03-18 15:09:57','2025-03-18 15:09:57',0),(7,1,'java',100,'2025-03-18 15:10:05','2025-03-18 15:13:05',1),(8,1,'go',100,'2025-03-18 15:10:09','2025-03-18 15:13:34',1),(9,1,'c++',100,'2025-03-18 15:10:14','2025-03-18 15:13:34',1),(10,NULL,'后端',100,'2025-03-18 15:13:56','2025-03-18 15:13:56',0),(11,10,'后端',100,'2025-03-18 15:14:11','2025-03-18 15:14:11',0),(12,10,'java',100,'2025-03-18 15:14:17','2025-03-18 15:14:17',0),(13,10,'go',100,'2025-03-18 15:14:23','2025-03-18 15:14:23',0),(14,10,'c++',100,'2025-03-18 15:14:27','2025-03-18 15:14:27',0);
/*!40000 ALTER TABLE `sys_article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `mail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
  `nickname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '普通用户' COMMENT '昵称',
  `profile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `role` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态 0:正常 1:禁用',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '编辑时间',
  `deletion_time` bigint DEFAULT '0' COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`,`deletion_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1901102074129567747 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1900560167108407298,'yupi','$2a$10$V9tNikccayEfPCwliu.caewGdiA7cquwWQ2ZwcWyZvVRiyjk3dyjm',NULL,NULL,NULL,'普通用户-bDCmyhH0p7',NULL,'user',0,'2025-03-14 22:50:29',0,'2025-03-14 22:50:29','2025-03-14 22:50:29',0),(1900560193582854145,'sheephappy','$2a$10$NrR6SVLl30Wj5qcSqNibNe46PuCTfUhrQiwcxSM/Cw6/coX.ecFCy',NULL,NULL,NULL,'普通用户-itcnay39lR',NULL,'admin',0,'2025-03-14 22:50:36',0,'2025-03-14 22:50:36','2025-03-14 22:50:36',0),(1901102074129567746,'shenli','$2a$10$8hAAeMQwxZDRI5p2JwzvD.6h76rh2nmyPHXv6mdMOIc7pRYiAgVTm',NULL,NULL,NULL,'普通用户-aAeq8YuNBR',NULL,'admin',0,'2025-03-16 10:43:50',0,'2025-03-16 10:43:50','2025-03-16 10:43:50',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_follow`
--

DROP TABLE IF EXISTS `sys_user_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_follow` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `follower_id` bigint unsigned NOT NULL COMMENT '关注者ID',
  `following_id` bigint unsigned NOT NULL COMMENT '被关注者ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '关注状态 1-有效 0-取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_follower_following` (`follower_id`,`following_id`) COMMENT '确保关注关系唯一'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_follow`
--

LOCK TABLES `sys_user_follow` WRITE;
/*!40000 ALTER TABLE `sys_user_follow` DISABLE KEYS */;
INSERT INTO `sys_user_follow` VALUES (2,1900560193582854145,1900560167108407298,1,'2025-03-14 22:51:03','2025-03-14 22:52:08',0);
/*!40000 ALTER TABLE `sys_user_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_stats`
--

DROP TABLE IF EXISTS `sys_user_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_stats` (
  `id` bigint unsigned NOT NULL COMMENT '用户ID',
  `like_count` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数量',
  `collect_count` int unsigned NOT NULL DEFAULT '0' COMMENT '收藏数量',
  `following_count` int unsigned NOT NULL DEFAULT '0' COMMENT '关注数量',
  `follower_count` int unsigned NOT NULL DEFAULT '0' COMMENT '被关注数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户统计信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_stats`
--

LOCK TABLES `sys_user_stats` WRITE;
/*!40000 ALTER TABLE `sys_user_stats` DISABLE KEYS */;
INSERT INTO `sys_user_stats` VALUES (1900560167108407298,0,0,0,1,'2025-03-14 22:50:29','2025-03-14 22:50:29',0),(1900560193582854145,1,1,1,0,'2025-03-14 22:50:36','2025-03-14 22:50:36',0),(1901102074129567746,0,0,0,0,'2025-03-16 10:43:50','2025-03-16 10:43:50',0);
/*!40000 ALTER TABLE `sys_user_stats` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-21 19:50:04
