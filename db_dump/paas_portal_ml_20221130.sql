CREATE DATABASE /*!32312 IF NOT EXISTS*/ `paas_portal` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `paas_portal`;



create user 'paas'@'%' identified by 'sptek1234';
GRANT ALL PRIVILEGES ON paas_portal.* TO 'paas'@'%';

--
-- Table structure for table `cluster`
--

DROP TABLE IF EXISTS `cluster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cluster` (
  `cluster_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(50) NOT NULL,
  `provider` varchar(20) DEFAULT NULL,
  `provider_version` varchar(20) DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `update_user_id` varchar(50) DEFAULT NULL,
  `update_user_name` varchar(50) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `cluster_id` bigint(20) DEFAULT NULL,
  `problem` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'problem of status' CHECK (json_valid(`problem`)),
  `provisioning_type` varchar(20) DEFAULT NULL COMMENT '프로비저닝_타입(KUBECONFIG/KUBESPRAY/NONE)',
  `provisioning_status` varchar(20) DEFAULT NULL COMMENT '프로비저닝_상태(STARTED/FINISHED/FAILED)',
  `provisioning_log` mediumtext DEFAULT NULL COMMENT '프로비저닝_로그',
  `provisioning_user` varchar(20) DEFAULT NULL COMMENT '프로비저닝_사용자(OS계정)',
  `kube_config` text DEFAULT NULL,
  `use_type` varchar(100) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `network_location` varchar(100) DEFAULT NULL,
  `vm_type` varchar(100) DEFAULT NULL,
  `node_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`cluster_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cluster';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `cluster_addon`
--

DROP TABLE IF EXISTS `cluster_addon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cluster_addon` (
  `cluster_idx` bigint(20) NOT NULL,
  `addon_id` varchar(50) NOT NULL,
  `addon_type` varchar(50) NOT NULL,
  `install_user_id` varchar(50) DEFAULT NULL,
  `install_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`cluster_idx`,`addon_id`),
  KEY `fk_cluster_addon_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_cluster_addon_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cluster_addon';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `code`
--

DROP TABLE IF EXISTS `code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code` (
  `code_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '코드_일련번호',
  `common_code` varchar(50) NOT NULL COMMENT '공통_코드',
  `group_code` varchar(50) NOT NULL COMMENT '그룹_코드',
  `code_name` varchar(100) DEFAULT NULL COMMENT '코드_이름',
  `code_value` varchar(100) DEFAULT NULL COMMENT '코드_값',
  `code_order` int(11) DEFAULT NULL COMMENT '코드_순서',
  `description` varchar(2000) DEFAULT NULL COMMENT '설명',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '사용_여부',
  `create_user_id` varchar(50) DEFAULT NULL COMMENT '등록자_ID',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '등록자_이름',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '동록일시',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '수정자_ID',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '수정자_이름',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '수정일시',
  PRIMARY KEY (`code_idx`),
  UNIQUE KEY `common_code` (`common_code`,`group_code`),
  KEY `fk_common_code_common_group_code1` (`group_code`),
  CONSTRAINT `fk_common_code_common_group_code1` FOREIGN KEY (`group_code`) REFERENCES `group_code` (`group_code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드';
/*!40101 SET character_set_client = @saved_cs_client */;







--
-- Table structure for table `group_code`
--

DROP TABLE IF EXISTS `group_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_code` (
  `group_code` varchar(50) NOT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `use_yn` char(1) DEFAULT 'Y',
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹_코드';
/*!40101 SET character_set_client = @saved_cs_client */;







--
-- Table structure for table `ingress_controller`
--

DROP TABLE IF EXISTS `ingress_controller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ingress_controller` (
  `ingress_controller_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `ingress_controller_name` varchar(100) DEFAULT NULL,
  `replicas` int(11) DEFAULT NULL,
  `service_type` varchar(30) DEFAULT NULL,
  `external_ip` varchar(100) DEFAULT NULL,
  `port` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `default_yn` char(1) DEFAULT 'N',
  `ingress_class` varchar(20) DEFAULT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  PRIMARY KEY (`ingress_controller_idx`),
  KEY `fk_ingress_controller_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_ingress_controller_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COMMENT='ingress_controller';
/*!40101 SET character_set_client = @saved_cs_client */;







--
-- Table structure for table `kube_config`
--

DROP TABLE IF EXISTS `kube_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kube_config` (
  `kube_config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'kube_config_id',
  `provider` varchar(20) NOT NULL COMMENT 'cluster 제공자',
  `config_contents` text NOT NULL COMMENT 'K8S config',
  `reg_date` datetime DEFAULT NULL COMMENT '등록일자',
  `mod_date` datetime DEFAULT NULL COMMENT '수정일자',
  PRIMARY KEY (`kube_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `machine_learning`
--

DROP TABLE IF EXISTS `machine_learning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `machine_learning` (
  `ml_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `ml_id` varchar(100) DEFAULT NULL,
  `ml_name` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `ml_step_code` varchar(20) DEFAULT NULL,
  `yaml` varchar(2000) DEFAULT NULL,
  `callback_url` varchar(150) DEFAULT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ml_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `machine_learning_resource`
--

DROP TABLE IF EXISTS `machine_learning_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `machine_learning_resource` (
  `ml_res_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `ml_res_name` varchar(100) NOT NULL,
  `ml_idx` bigint(20) NOT NULL,
  `kind` varchar(50) DEFAULT NULL,
  `resource_id` bigint(20) NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `yaml` varchar(2000) DEFAULT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ml_res_idx`),
  KEY `fk_ml_idx` (`ml_idx`),
  CONSTRAINT `fk_ml_idx` FOREIGN KEY (`ml_idx`) REFERENCES `machine_learning` (`ml_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu` (
  `menu_idx` bigint(20) NOT NULL,
  `menu_name` varchar(50) DEFAULT NULL,
  `menu_url` varchar(200) DEFAULT NULL,
  `parent_menu_idx` bigint(20) DEFAULT NULL,
  `menu_order` int(11) DEFAULT NULL,
  `menu_depth` int(11) DEFAULT NULL,
  `use_yn` char(1) DEFAULT 'Y',
  PRIMARY KEY (`menu_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='메뉴';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (100000,'Dashboard','/dashboard',0,1,1,'Y'),(101000,'Project','/project/list',0,4,1,'Y'),(102000,'Cluster','/cluster/list',0,5,1,'Y'),(103000,'Workload',NULL,0,6,1,'Y'),(103010,'Deployment','/workload/deployment/list',103000,1,2,'Y'),(103020,'Stateful Set','/workload/stateful-set/list',103000,2,2,'Y'),(103030,'Pod','/workload/pod/list',103000,3,2,'Y'),(103040,'Cron Job','/workload/cron-job/list',103000,4,2,'Y'),(103050,'Job','/workload/job/list',103000,5,2,'Y'),(103060,'Replica Set','/workload/replica-set/list',103000,6,2,'Y'),(103070,'Daemon Set','/workload/daemon-set/list',103000,7,2,'Y'),(104000,'Network',NULL,0,7,1,'Y'),(104010,'Service','/network/service/list',104000,1,2,'Y'),(104020,'Ingress','/network/ingress/list',104000,2,2,'Y'),(104030,'Ingress Controller','/network/ingress-controller/list',104000,3,2,'Y'),(105000,'Config',NULL,0,8,1,'Y'),(105010,'Persistent Volume Claim','/config/persistent-volume-claim/list',105000,1,2,'Y'),(105020,'Config Map','/config/config-map/list',105000,2,2,'Y'),(105030,'Secret','/config/secret/list',105000,3,2,'Y'),(106000,'Setting',NULL,0,9,1,'Y'),(106010,'General','/setting/general',106000,1,2,'Y'),(106020,'User','/setting/user/list',106000,2,2,'Y'),(106030,'Authority','/setting/authority',106000,3,2,'Y'),(106040,'Code Management','/setting/codeMgmt',106000,4,2,'N'),(106050,'Tool','/setting/tools/detail/kubespray',106000,5,2,'Y'),(107000,'Machine Learning','/ml/list',0,2,1,'Y'),(108000,'Hyperparameter Tuning','/automl/list',0,3,1,'Y');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `namespace`
--

DROP TABLE IF EXISTS `namespace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `namespace` (
  `namespace_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `namespace_name` varchar(100) DEFAULT NULL,
  `namespace_uid` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `cluster_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`namespace_idx`),
  KEY `fk_namespace_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_namespace_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='namespace';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `deployment`
--

DROP TABLE IF EXISTS `deployment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deployment` (
  `deployment_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `deployment_name` varchar(100) DEFAULT NULL,
  `deployment_uid` varchar(50) DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL COMMENT '이미지(템플릿)',
  `created_at` timestamp NULL DEFAULT NULL,
  `strategy` varchar(20) DEFAULT NULL,
  `selector` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`selector`)),
  `max_surge` float DEFAULT NULL,
  `max_unavailable` float DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `pod_updated` int(11) DEFAULT NULL,
  `pod_replicas` int(11) DEFAULT NULL,
  `pod_ready` int(11) DEFAULT NULL,
  `namespace_idx` bigint(20) NOT NULL,
  `condition` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`condition`)),
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`deployment_idx`),
  KEY `fk_deployment_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_deployment_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='deployment';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ingress`
--

DROP TABLE IF EXISTS `ingress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ingress` (
  `ingress_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `ingress_uid` varchar(50) DEFAULT NULL,
  `ingress_name` varchar(100) DEFAULT NULL,
  `ingress_class` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `namespace_idx` bigint(20) NOT NULL,
  `cluster_idx` bigint(20) DEFAULT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`ingress_idx`),
  KEY `fk_ingress_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_ingress_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ingress';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `ingress_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ingress_rule` (
  `ingress_rule_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `host` varchar(50) DEFAULT NULL,
  `protocol` varchar(10) DEFAULT NULL,
  `path` varchar(20) DEFAULT NULL,
  `path_type` varchar(10) DEFAULT NULL,
  `ingress_idx` bigint(20) NOT NULL,
  `service` varchar(50) DEFAULT NULL COMMENT 'service',
  `port` int(11) DEFAULT NULL COMMENT 'port',
  `endpoint` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ingress_rule_idx`),
  KEY `fk_ingress_rule_ingress1` (`ingress_idx`),
  CONSTRAINT `fk_ingress_rule_ingress1` FOREIGN KEY (`ingress_idx`) REFERENCES `ingress` (`ingress_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ingress_rule';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `config_map`
--

DROP TABLE IF EXISTS `config_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_map` (
  `config_map_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_map_name` varchar(100) DEFAULT NULL,
  `config_map_uid` varchar(50) DEFAULT NULL,
  `data_type` varchar(20) DEFAULT NULL COMMENT '컨피그맵_데이터_타입(data/binaryData)',
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`data`)),
  `created_at` timestamp NULL DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`config_map_idx`),
  KEY `fk_config_map_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_config_map_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='config_map';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `cron_job`
--

DROP TABLE IF EXISTS `cron_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cron_job` (
  `cron_job_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `cron_job_name` varchar(100) DEFAULT NULL,
  `cron_job_uid` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `schedule` varchar(50) DEFAULT NULL,
  `pause` varchar(20) DEFAULT NULL,
  `last_schedule` timestamp NULL DEFAULT NULL,
  `concurrency_policy` varchar(20) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `job_idx` bigint(20) DEFAULT NULL,
  `namespace_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`cron_job_idx`),
  KEY `fk_cron_job_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_cron_job_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='cron_job';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `job_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(100) DEFAULT NULL,
  `job_uid` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL COMMENT '이미지(템플릿)',
  `parallel_execution` varchar(50) DEFAULT NULL,
  `completion_mode` varchar(50) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `status` varchar(50) DEFAULT NULL,
  `namespace_idx` bigint(20) NOT NULL,
  `cron_job_idx` bigint(20) DEFAULT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`job_idx`),
  KEY `fk_job_namespace1` (`namespace_idx`),
  KEY `fk_job_cron_job1` (`cron_job_idx`),
  CONSTRAINT `fk_job_cron_job1` FOREIGN KEY (`cron_job_idx`) REFERENCES `cron_job` (`cron_job_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_job_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job';
/*!40101 SET character_set_client = @saved_cs_client */;




--
-- Table structure for table `daemon_set`
--

DROP TABLE IF EXISTS `daemon_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `daemon_set` (
  `daemon_set_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `daemon_set_uid` varchar(50) DEFAULT NULL,
  `daemon_set_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL,
  `selector` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`selector`)),
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`daemon_set_idx`),
  KEY `fk_daemon_set_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_daemon_set_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='daemon_set';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `node`
--

DROP TABLE IF EXISTS `node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node` (
  `node_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `node_name` varchar(100) NOT NULL,
  `node_uid` varchar(50) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `k8s_version` varchar(20) DEFAULT NULL,
  `allocated_cpu` float DEFAULT NULL,
  `allocated_memory` float DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `pod_cidr` varchar(20) DEFAULT NULL,
  `os_image` varchar(50) DEFAULT NULL,
  `kernel_version` varchar(50) DEFAULT NULL,
  `architecture` varchar(50) DEFAULT NULL,
  `kubelet_version` varchar(50) DEFAULT NULL,
  `kubeproxy_version` varchar(50) DEFAULT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `condition` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`condition`)),
  `role` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`role`)),
  PRIMARY KEY (`node_idx`),
  KEY `fk_node_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_node_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='node';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_volume_claim`
--

DROP TABLE IF EXISTS `persistent_volume_claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_volume_claim` (
  `persistent_volume_claim_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `persistent_volume_claim_name` varchar(100) DEFAULT NULL,
  `persistent_volume_claim_uid` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `storage_class` varchar(20) DEFAULT NULL,
  `access_type` varchar(20) DEFAULT NULL,
  `storage_capacity` varchar(20) DEFAULT NULL,
  `storage_request` varchar(20) DEFAULT NULL,
  `label` longtext DEFAULT NULL,
  `namespace_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`persistent_volume_claim_idx`),
  KEY `fk_persistent_volume_claim_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_persistent_volume_claim_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='persistent_volume_claim';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pod`
--

DROP TABLE IF EXISTS `pod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod` (
  `pod_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `pod_uid` varchar(50) DEFAULT NULL,
  `pod_name` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `node_idx` bigint(20) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `qos_class` varchar(20) DEFAULT NULL,
  `restart` int(11) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `cpu` float DEFAULT NULL COMMENT 'cpu',
  `memory` float DEFAULT NULL COMMENT 'memory',
  `namespace_idx` bigint(20) NOT NULL,
  `condition` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`condition`)),
  `owner_uid` varchar(50) DEFAULT NULL,
  `kind` varchar(20) DEFAULT NULL COMMENT '종류',
  `image` longtext DEFAULT NULL COMMENT 'container의 image',
  `cluster_idx` bigint(20) DEFAULT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`pod_idx`),
  KEY `fk_pod_node1` (`node_idx`),
  KEY `fk_pod_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_pod_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_node1` FOREIGN KEY (`node_idx`) REFERENCES `node` (`node_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `pod_daemon_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod_daemon_set` (
  `pod_daemon_set_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pod_daemon_set_일련번호',
  `daemon_set_idx` bigint(20) NOT NULL COMMENT 'daemon_set_일련번호',
  `pod_idx` bigint(20) NOT NULL COMMENT 'pod_일련번호',
  PRIMARY KEY (`pod_daemon_set_idx`),
  UNIQUE KEY `daemon_set_idx` (`daemon_set_idx`,`pod_idx`),
  KEY `fk_pod_daemon_set_pod2` (`pod_idx`),
  CONSTRAINT `fk_pod_daemon_set_daemon_set1` FOREIGN KEY (`daemon_set_idx`) REFERENCES `daemon_set` (`daemon_set_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_daemon_set_pod2` FOREIGN KEY (`pod_idx`) REFERENCES `pod` (`pod_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod_daemon_set';
/*!40101 SET character_set_client = @saved_cs_client */;




--
-- Table structure for table `pod_job`
--

DROP TABLE IF EXISTS `pod_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod_job` (
  `pod_job_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pod_job_일련번호',
  `job_idx` bigint(20) NOT NULL COMMENT 'job_일련번호',
  `pod_idx` bigint(20) NOT NULL COMMENT 'pod_일련번호',
  PRIMARY KEY (`pod_job_idx`),
  UNIQUE KEY `pod_job_unique` (`job_idx`,`pod_idx`),
  KEY `fk_pod_job_pod2` (`pod_idx`),
  CONSTRAINT `fk_pod_job_job1` FOREIGN KEY (`job_idx`) REFERENCES `job` (`job_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_job_pod2` FOREIGN KEY (`pod_idx`) REFERENCES `pod` (`pod_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod_job';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pod_persistent_volume_claim`
--

DROP TABLE IF EXISTS `pod_persistent_volume_claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod_persistent_volume_claim` (
  `pod_persistent_volume_claim_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pod_persistent_volume_claim_일련번호',
  `persistent_volume_claim_idx` bigint(20) NOT NULL COMMENT 'persistent_volume_claim_일련번호',
  `pod_idx` bigint(20) NOT NULL COMMENT 'pod_일련번호',
  PRIMARY KEY (`pod_persistent_volume_claim_idx`),
  UNIQUE KEY `persistent_volume_claim_idx` (`persistent_volume_claim_idx`,`pod_idx`),
  KEY `fk_pod_persistent_volume_claim_pod1` (`pod_idx`),
  CONSTRAINT `fk_persistent_volume_claim_pod_persistent_volume_claim1` FOREIGN KEY (`persistent_volume_claim_idx`) REFERENCES `persistent_volume_claim` (`persistent_volume_claim_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_persistent_volume_claim_pod1` FOREIGN KEY (`pod_idx`) REFERENCES `pod` (`pod_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod_persistent_volume_claim';
/*!40101 SET character_set_client = @saved_cs_client */;









--
-- Table structure for table `pod_replica_set`
--

DROP TABLE IF EXISTS `pod_replica_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod_replica_set` (
  `pod_replica_set_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pod_replica_set_일련번호',
  `replica_set_idx` bigint(20) NOT NULL COMMENT 'replica_set_일련번호',
  `pod_idx` bigint(20) NOT NULL COMMENT 'pod_일련번호',
  PRIMARY KEY (`pod_replica_set_idx`),
  UNIQUE KEY `replica_set_idx` (`replica_set_idx`,`pod_idx`),
  KEY `fk_pod_replica_set_pod1` (`pod_idx`),
  CONSTRAINT `fk_pod_replica_replica_set1` FOREIGN KEY (`replica_set_idx`) REFERENCES `replica_set` (`replica_set_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_replica_set_pod1` FOREIGN KEY (`pod_idx`) REFERENCES `pod` (`pod_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod_replica_set';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `project_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(100) DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `update_user_id` varchar(50) DEFAULT NULL,
  `update_user_name` varchar(50) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `deleted_yn` char(1) DEFAULT 'N',
  PRIMARY KEY (`project_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='project';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_cluster`
--

DROP TABLE IF EXISTS `project_cluster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_cluster` (
  `project_idx` bigint(20) NOT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`project_idx`,`cluster_idx`),
  KEY `fk_project_cluster_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_project_cluster_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_project_request_cluster_project1` FOREIGN KEY (`project_idx`) REFERENCES `project` (`project_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='project_cluster';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `project_user`
--



--
-- Table structure for table `replica_set`
--

DROP TABLE IF EXISTS `replica_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `replica_set` (
  `replica_set_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `replica_set_uid` varchar(50) DEFAULT NULL,
  `replica_set_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL,
  `selector` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`selector`)),
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) NOT NULL,
  `deployment_idx` bigint(20) DEFAULT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`replica_set_idx`),
  KEY `fk_replica_set_namespace1` (`namespace_idx`),
  KEY `fk_replica_set_deployment1` (`deployment_idx`),
  CONSTRAINT `fk_replica_set_deployment1` FOREIGN KEY (`deployment_idx`) REFERENCES `deployment` (`deployment_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_replica_set_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='replica_set';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `secret`
--

DROP TABLE IF EXISTS `secret`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secret` (
  `secret_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `secret_name` varchar(100) DEFAULT NULL,
  `secret_uid` varchar(50) DEFAULT NULL,
  `secret_type` varchar(20) DEFAULT NULL,
  `data_type` varchar(20) DEFAULT NULL COMMENT '데이터_타입(data/stringData)',
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`data`)),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`secret_idx`),
  KEY `fk_secret_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_secret_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='secret';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `service_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_uid` varchar(50) DEFAULT NULL,
  `service_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL COMMENT '서비스_타입(ClusterIP/LoadBalancer/NodePort)',
  `cluster_ip` varchar(20) DEFAULT NULL,
  `session_affinity` varchar(20) DEFAULT NULL,
  `selector` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`selector`)),
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) NOT NULL,
  `internal_endpoint` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`internal_endpoint`)),
  `external_endpoint` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`external_endpoint`)),
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`service_idx`),
  KEY `fk_service_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_service_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='service';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_endpoint`
--

DROP TABLE IF EXISTS `service_endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_endpoint` (
  `service_endpoint_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `host` varchar(50) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `endpoint_name` varchar(50) DEFAULT NULL COMMENT '엔드포인트_이름',
  `protocol` varchar(10) DEFAULT NULL,
  `ready` varchar(10) DEFAULT NULL,
  `service_idx` bigint(20) NOT NULL,
  `node_name` varchar(50) DEFAULT NULL COMMENT '노드_이름',
  PRIMARY KEY (`service_endpoint_idx`),
  KEY `fk_service_endpoint_service1` (`service_idx`),
  CONSTRAINT `fk_service_endpoint_service1` FOREIGN KEY (`service_idx`) REFERENCES `service` (`service_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='service_endpoint';
/*!40101 SET character_set_client = @saved_cs_client */;

--
--
-- Table structure for table `setting`
--

DROP TABLE IF EXISTS `setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `setting` (
  `setting_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `setting_type` varchar(50) NOT NULL DEFAULT 'GENERAL',
  `setting_key` varchar(50) DEFAULT NULL,
  `setting_value` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`setting_idx`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='설정(General/Tools)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setting`
--

LOCK TABLES `setting` WRITE;
/*!40000 ALTER TABLE `setting` DISABLE KEYS */;
INSERT INTO `setting` VALUES (1,'GENERAL','HOME_DIRECTORY','/pass-portal','',NULL),(8,'TOOLS','KUBESPRAY','2.10','','2022-05-16 04:50:05');
/*!40000 ALTER TABLE `setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stateful_set`
--

DROP TABLE IF EXISTS `stateful_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stateful_set` (
  `stateful_set_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `stateful_set_uid` varchar(50) DEFAULT NULL,
  `stateful_set_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `namespace_idx` bigint(20) DEFAULT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`stateful_set_idx`),
  KEY `fk_stateful_set_namespace1` (`namespace_idx`),
  CONSTRAINT `fk_stateful_set_namespace1` FOREIGN KEY (`namespace_idx`) REFERENCES `namespace` (`namespace_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stateful_set';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `pod_stateful_set`
--

DROP TABLE IF EXISTS `pod_stateful_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pod_stateful_set` (
  `pod_stateful_set_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pod_stateful_set_일련번호',
  `stateful_set_idx` bigint(20) NOT NULL COMMENT 'stateful_set_일련번호',
  `pod_idx` bigint(20) NOT NULL COMMENT 'pod_일련번호',
  PRIMARY KEY (`pod_stateful_set_idx`),
  UNIQUE KEY `stateful_set_idx` (`stateful_set_idx`,`pod_idx`),
  KEY `fk_pod_stateful_set_pod2` (`pod_idx`),
  CONSTRAINT `fk_pod_stateful_set_pod2` FOREIGN KEY (`pod_idx`) REFERENCES `pod` (`pod_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pod_stateful_set_stateful_set1` FOREIGN KEY (`stateful_set_idx`) REFERENCES `stateful_set` (`stateful_set_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='pod_stateful_set';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storage_class`
--

DROP TABLE IF EXISTS `storage_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_class` (
  `storage_class_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `storage_class_name` varchar(100) NOT NULL,
  `storage_class_uid` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `provider` varchar(50) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `cluster_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`storage_class_idx`),
  KEY `fk_storage_class_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_storage_class_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='storage_class';
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `persistent_volume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_volume` (
  `persistent_volume_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `persistent_volume_name` varchar(100) DEFAULT NULL,
  `persistent_volume_uid` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `access_mode` varchar(20) DEFAULT NULL,
  `claim` varchar(50) DEFAULT NULL,
  `reclaim` varchar(50) DEFAULT NULL,
  `reclaim_policy` varchar(50) DEFAULT NULL,
  `storage_class_idx` bigint(20) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `path` varchar(100) DEFAULT NULL,
  `resource_name` varchar(20) DEFAULT NULL,
  `size` varchar(20) DEFAULT NULL,
  `annotation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`annotation`)),
  `label` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`label`)),
  `cluster_idx` bigint(20) NOT NULL,
  `yaml` text DEFAULT NULL,
  PRIMARY KEY (`persistent_volume_idx`),
  KEY `fk_persistent_volume_storage_class1` (`storage_class_idx`),
  KEY `fk_persistent_volume_cluster1` (`cluster_idx`),
  CONSTRAINT `fk_persistent_volume_cluster1` FOREIGN KEY (`cluster_idx`) REFERENCES `cluster` (`cluster_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_persistent_volume_storage_class1` FOREIGN KEY (`storage_class_idx`) REFERENCES `storage_class` (`storage_class_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='persistent_volume';
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` varchar(50) NOT NULL COMMENT '사용자_아이디',
  `user_name` varchar(50) NOT NULL COMMENT '사용자_이름',
  `user_password` varchar(50) DEFAULT NULL COMMENT '사용자_비밀번호',
  `email` varchar(50) DEFAULT NULL COMMENT '이메일',
  `organization` varchar(50) DEFAULT NULL COMMENT '조직',
  `contact` varchar(20) DEFAULT NULL COMMENT '연락처',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '수정자_아이디',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '수정자_이름',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '수정일시',
  `create_user_id` varchar(50) DEFAULT NULL COMMENT '등록자_아이디',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '등록자_이름',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '등록일시',
  `user_role_idx` bigint(20) NOT NULL COMMENT '사용자_역할_일련번호',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '사용_여부',
  PRIMARY KEY (`user_id`),
  KEY `fk_user_user_role1` (`user_role_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('paas-admin@strato.co.kr','PaaS Admin',NULL,'paas-admin@strato.co.kr','관리팀',NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,'Y'),('paasportal@strato.co.kr','관리자','','paasportal@strato.co.kr','스트라토','01000000000',NULL,NULL,'2022-05-13 04:25:29',NULL,NULL,'2022-05-13 04:25:29',3,'Y');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_alert`
--

DROP TABLE IF EXISTS `user_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_alert` (
  `alert_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL,
  `cluster_name` varchar(100) NOT NULL,
  `cluster_idx` bigint(20) NOT NULL,
  `work_job_type` varchar(50) NOT NULL COMMENT 'Job 타입(CLUSTER_CREATE, CLUSTER_SCALE, CLUSTER_DELETE)',
  `work_job_status` varchar(50) NOT NULL COMMENT 'Job 상태(SUCCESS, FAIL, STARTED)',
  `confirm_yn` char(1) DEFAULT 'N' COMMENT '확인 여부',
  `work_job_idx` bigint(20) DEFAULT NULL COMMENT 'work_job 테이블의 ID',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '동록일시',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '확인일시',
  PRIMARY KEY (`alert_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자 알람';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_favorite_menu`
--

DROP TABLE IF EXISTS `user_favorite_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_favorite_menu` (
  `user_favorite_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '사용자_즐겨찾기_일련번호',
  `user_id` varchar(50) NOT NULL COMMENT '유저 아이디',
  `menu_idx` bigint(20) NOT NULL COMMENT '메뉴 일련 번호 ',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_favorite_idx`),
  KEY `fk_user_favorite_menu_menu_idx` (`menu_idx`),
  CONSTRAINT `fk_user_favorite_menu_menu_idx` FOREIGN KEY (`menu_idx`) REFERENCES `menu` (`menu_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자_즐겨찾기';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `user_reset_password`
--

DROP TABLE IF EXISTS `user_reset_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_reset_password` (
  `request_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_code` varchar(50) NOT NULL COMMENT '변경 요청 코드',
  `user_id` varchar(50) NOT NULL COMMENT '사용자_아이디',
  `email` varchar(50) NOT NULL COMMENT '이메일',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '등록일시',
  PRIMARY KEY (`request_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자 비밀번호 재설정 요청 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_role_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '사용자_역할_일련번호',
  `user_role_name` varchar(50) DEFAULT NULL COMMENT '사용자_역할_이름',
  `user_role_code` varchar(50) DEFAULT NULL COMMENT '사용자_역할_코드(OPERATOR/PROJECT_MANAGER/DEVELOPER)',
  `description` text DEFAULT NULL COMMENT '설명',
  `parent_user_role_idx` bigint(20) DEFAULT NULL COMMENT '부모_사용자_역할_일련번호',
  `group_yn` char(1) DEFAULT 'N' COMMENT '그룹_여부',
  `user_defined_yn` char(1) DEFAULT 'Y' COMMENT '사용자_정의_여부',
  PRIMARY KEY (`user_role_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자_역할';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'관리자 그룹','ADMIN','관리자 그룹',0,'Y','N'),(2,'Portal Admin','PORTAL_ADMIN','포탈 관리자',1,'N','N'),(3,'운영자','SYSTEM_ADMIN','관리자',1,'N','N'),(4,'사용자 그룹','PROJECT','사용자 그룹',0,'Y','N'),(5,'Project Manager','PROJECT_MANAGER','프로젝트 매니저1',4,'N','N'),(6,'Project Member','PROJECT_MEMBER','프로젝트 맴버',4,'N','N');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_menu`
--

DROP TABLE IF EXISTS `user_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_menu` (
  `user_role_menu_idx` bigint(20) NOT NULL,
  `menu_idx` bigint(20) NOT NULL,
  `user_role_idx` bigint(20) NOT NULL,
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `viewable_yn` char(1) DEFAULT 'Y' COMMENT '보기가능_여부',
  `writable_yn` char(1) DEFAULT 'Y' COMMENT '쓰기가능_여부',
  PRIMARY KEY (`user_role_menu_idx`),
  KEY `fk_user_role_menu_user_menu1` (`menu_idx`),
  KEY `fk_user_role_menu_user_role1` (`user_role_idx`),
  CONSTRAINT `fk_user_role_menu_user_menu1` FOREIGN KEY (`menu_idx`) REFERENCES `menu` (`menu_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자_역할_메뉴';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_menu`
--

LOCK TABLES `user_role_menu` WRITE;
/*!40000 ALTER TABLE `user_role_menu` DISABLE KEYS */;
INSERT INTO `user_role_menu` VALUES (279,100000,2,NULL,NULL,'2022-04-20 00:43:34','Y','Y'),(280,101000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(281,102000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(282,103000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(283,103010,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(284,103020,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(285,103030,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(286,103040,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(287,103050,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(288,103060,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(289,103070,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(290,104000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(291,104010,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(292,104020,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(293,104030,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(294,105000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(295,105010,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(296,105020,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(297,105030,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(298,106000,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(299,106010,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(300,106020,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(301,106030,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(302,106040,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(303,106050,2,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(503,100000,3,NULL,NULL,'2022-04-20 00:43:34','N','Y'),(504,101000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(505,102000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(506,103000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(507,103010,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(508,103020,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(509,103030,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(510,103040,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(511,103050,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(512,103060,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(513,103070,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(514,104000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(515,104010,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(516,104020,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(517,104030,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(518,105000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(519,105010,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(520,105020,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(521,105030,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(522,106000,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(523,106010,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(524,106020,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(525,106030,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(526,106040,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(527,106050,3,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(626,100000,5,NULL,NULL,'2022-04-20 00:43:34','Y','N'),(627,101000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(628,102000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(629,103000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(630,103010,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(631,103020,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(632,103030,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(633,103040,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(634,103050,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(635,103060,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(636,103070,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(637,104000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(638,104010,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(639,104020,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(640,104030,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(641,105000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(642,105010,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(643,105020,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(644,105030,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(645,106000,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(646,106010,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(647,106020,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(648,106030,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(649,106040,5,NULL,NULL,'2022-03-02 08:10:17','N','N'),(650,106050,5,NULL,NULL,'2022-03-02 08:10:17','N','Y'),(750,100000,6,NULL,NULL,'2022-04-20 00:43:34','Y','N'),(751,101000,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(752,102000,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(753,103000,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(754,103010,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(755,103020,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(756,103030,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(757,103040,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(758,103050,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(759,103060,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(760,103070,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(761,104000,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(762,104010,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(763,104020,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(764,104030,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(765,105000,6,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(766,105010,6,NULL,NULL,'2022-03-03 01:17:24','N','Y'),(767,105020,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(768,105030,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(769,106000,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(770,106010,6,NULL,NULL,'2022-03-03 01:17:24','N','N'),(771,106020,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(772,106030,6,NULL,NULL,'2022-03-03 01:17:24','N','N'),(773,106040,6,NULL,NULL,'2022-03-03 01:17:24','Y','N'),(774,106050,6,NULL,NULL,'2022-03-03 01:17:24','N','N'),(775,107000,2,NULL,NULL,'2022-03-03 01:17:24','Y','Y'),(776,107000,3,NULL,NULL,'2022-03-03 01:17:24','Y','Y'),(777,108000,2,NULL,NULL,'2022-11-15 00:00:00','Y','Y'),(778,108000,3,NULL,NULL,'2022-11-15 00:00:00','Y','Y');
/*!40000 ALTER TABLE `user_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_menu_temp`
--


--
-- Table structure for table `work_history`
--




--
-- Table structure for table `work_job`
--

DROP TABLE IF EXISTS `work_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_job` (
  `work_job_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `work_job_type` varchar(50) DEFAULT NULL,
  `work_job_target` varchar(100) DEFAULT NULL,
  `work_job_status` varchar(50) DEFAULT NULL,
  `work_job_message` varchar(200) DEFAULT NULL,
  `work_job_data_request` text DEFAULT NULL,
  `work_job_data_response` mediumtext DEFAULT NULL,
  `work_job_start_at` timestamp NULL DEFAULT NULL,
  `work_job_end_at` timestamp NULL DEFAULT NULL,
  `work_sync_yn` char(1) DEFAULT 'Y' COMMENT '작업_job_동기_여부',
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `work_job_reference_idx` bigint(20) DEFAULT NULL COMMENT '작업_Job_참조_일련번호',
  `callback_url` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`work_job_idx`)
) ENGINE=InnoDB AUTO_INCREMENT=658 DEFAULT CHARSET=utf8 COMMENT='작업_Job';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `project_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_user` (
  `user_id` varchar(50) NOT NULL,
  `project_idx` bigint(20) NOT NULL,
  `create_user_id` varchar(50) DEFAULT NULL,
  `create_user_name` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `project_user_role` varchar(50) DEFAULT NULL,
  `user_role_idx` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`project_idx`),
  KEY `fk_project_user_project1` (`project_idx`),
  CONSTRAINT `fk_project_user_project1` FOREIGN KEY (`project_idx`) REFERENCES `project` (`project_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_project_user_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='project_user';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `work_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_history` (
  `work_history_idx` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '감사_로그_일련번호',
  `work_menu_1` varchar(50) DEFAULT NULL COMMENT '감사_메뉴_1(Depth)',
  `work_menu_2` varchar(50) DEFAULT NULL COMMENT '감사_메뉴_2(Depth)',
  `work_menu_3` varchar(50) DEFAULT NULL COMMENT '감사_메뉴_3(Depth)',
  `work_action` varchar(50) DEFAULT NULL COMMENT '작업_행위(목록/등록/수정/상세/팝업 등등..)',
  `target` varchar(50) DEFAULT NULL COMMENT '대상',
  `metadata` text DEFAULT NULL COMMENT '메타(Key정보 및 기타)',
  `result` varchar(50) DEFAULT NULL COMMENT '결과(성공/실패)',
  `message` text DEFAULT NULL COMMENT '메시지(결과에 따른 메시지 내용)',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '등록일시',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '등록자_이름',
  `create_user_id` varchar(50) DEFAULT NULL COMMENT '등록자_ID',
  `work_job_idx` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`work_history_idx`),
  KEY `fk_work_history_work_job1` (`work_job_idx`),
  CONSTRAINT `fk_work_history_work_job1` FOREIGN KEY (`work_job_idx`) REFERENCES `work_job` (`work_job_idx`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='작업_이력';
/*!40101 SET character_set_client = @saved_cs_client */;
