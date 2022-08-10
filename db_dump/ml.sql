-- paas_portal.machine_learning definition

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


-- paas_portal.machine_learning_cluster definition

CREATE TABLE `machine_learning_cluster` (
  `ml_cluster_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_idx` bigint(20) NOT NULL,
  `cluster_type` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ml_cluster_idx`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


-- paas_portal.machine_learning_cluster_mapping definition

CREATE TABLE `machine_learning_cluster_mapping` (
  `ml_cluster_mapping_idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `ml_cluster_idx` bigint(20) NOT NULL,
  `ml_idx` bigint(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ml_cluster_mapping_idx`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


-- paas_portal.machine_learning_resource definition

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;