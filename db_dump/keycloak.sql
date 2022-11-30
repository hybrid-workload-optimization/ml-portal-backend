CREATE DATABASE /*!32312 IF NOT EXISTS*/ `keycloak` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `keycloak`;



GRANT ALL PRIVILEGES ON keycloak.* TO 'paas'@'%';


-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: 10.10.10.3    Database: keycloak
-- ------------------------------------------------------
-- Server version	5.5.5-10.7.7-MariaDB-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_event_entity`
--

DROP TABLE IF EXISTS `admin_event_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_event_entity` (
  `ID` varchar(36) NOT NULL,
  `ADMIN_EVENT_TIME` bigint(20) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `OPERATION_TYPE` varchar(255) DEFAULT NULL,
  `AUTH_REALM_ID` varchar(255) DEFAULT NULL,
  `AUTH_CLIENT_ID` varchar(255) DEFAULT NULL,
  `AUTH_USER_ID` varchar(255) DEFAULT NULL,
  `IP_ADDRESS` varchar(255) DEFAULT NULL,
  `RESOURCE_PATH` text DEFAULT NULL,
  `REPRESENTATION` text DEFAULT NULL,
  `ERROR` varchar(255) DEFAULT NULL,
  `RESOURCE_TYPE` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_event_entity`
--

LOCK TABLES `admin_event_entity` WRITE;
/*!40000 ALTER TABLE `admin_event_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_event_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `associated_policy`
--

DROP TABLE IF EXISTS `associated_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `associated_policy` (
  `POLICY_ID` varchar(36) NOT NULL,
  `ASSOCIATED_POLICY_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`POLICY_ID`,`ASSOCIATED_POLICY_ID`),
  KEY `IDX_ASSOC_POL_ASSOC_POL_ID` (`ASSOCIATED_POLICY_ID`),
  CONSTRAINT `FK_FRSR5S213XCX4WNKOG82SSRFY` FOREIGN KEY (`ASSOCIATED_POLICY_ID`) REFERENCES `resource_server_policy` (`ID`),
  CONSTRAINT `FK_FRSRPAS14XCX4WNKOG82SSRFY` FOREIGN KEY (`POLICY_ID`) REFERENCES `resource_server_policy` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `associated_policy`
--

LOCK TABLES `associated_policy` WRITE;
/*!40000 ALTER TABLE `associated_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `associated_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authentication_execution`
--

DROP TABLE IF EXISTS `authentication_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authentication_execution` (
  `ID` varchar(36) NOT NULL,
  `ALIAS` varchar(255) DEFAULT NULL,
  `AUTHENTICATOR` varchar(36) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `FLOW_ID` varchar(36) DEFAULT NULL,
  `REQUIREMENT` int(11) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `AUTHENTICATOR_FLOW` bit(1) NOT NULL DEFAULT b'0',
  `AUTH_FLOW_ID` varchar(36) DEFAULT NULL,
  `AUTH_CONFIG` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_AUTH_EXEC_REALM_FLOW` (`REALM_ID`,`FLOW_ID`),
  KEY `IDX_AUTH_EXEC_FLOW` (`FLOW_ID`),
  CONSTRAINT `FK_AUTH_EXEC_FLOW` FOREIGN KEY (`FLOW_ID`) REFERENCES `authentication_flow` (`ID`),
  CONSTRAINT `FK_AUTH_EXEC_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authentication_execution`
--

LOCK TABLES `authentication_execution` WRITE;
/*!40000 ALTER TABLE `authentication_execution` DISABLE KEYS */;
INSERT INTO `authentication_execution` VALUES ('00f17b99-db68-4618-8808-36342494271f',NULL,'registration-profile-action','master','a3768882-6f58-48b4-a0bc-a9388b4495db',0,40,'\0',NULL,NULL),('01e5c4d1-c5d2-4226-8d40-28091322f4b6',NULL,'docker-http-basic-authenticator','sptek-cloud','39d4a2d5-1496-4c9e-ad55-6d4d2230bc2d',0,10,'\0',NULL,NULL),('044c1368-4607-4619-bfdd-13f5ff7f0c61',NULL,'auth-username-password-form','master','b61b14ee-b93b-40ee-bcf0-ca86eaa97a31',0,10,'\0',NULL,NULL),('04d80c18-5b17-4a71-80be-0deff7eb26c5',NULL,'auth-cookie','master','6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238',2,10,'\0',NULL,NULL),('070ed97b-5368-40c0-9b65-0412cabab7ce',NULL,'auth-spnego','master','6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238',3,20,'\0',NULL,NULL),('07beb845-21b3-4269-9b0c-30aa1d556f69',NULL,NULL,'sptek-cloud','d3ea4c7c-c420-4707-9e23-f9950d2125fb',1,20,'','774ede0a-d4e0-4d3d-bb7a-f6a99798f457',NULL),('093f2bfd-45ab-48d5-9cb3-a85b274f3c8c',NULL,'auth-otp-form','sptek-cloud','774ede0a-d4e0-4d3d-bb7a-f6a99798f457',0,20,'\0',NULL,NULL),('0af3c326-ef01-4148-a815-e2fbd3bb987f',NULL,NULL,'master','d31cded4-ef63-42ff-bb10-456cb3c8589d',0,20,'','898611d2-8709-4ca7-9dab-194d40b65736',NULL),('0c5cafd3-ccc6-47b6-af80-546985727ec5',NULL,NULL,'sptek-cloud','c6c17844-9881-45b2-a25e-6ed5f96d6fcf',0,20,'','566f9022-6022-45da-8599-10cf166237fe',NULL),('0d105d5a-d50f-42ff-a1fa-ad4ab819961a',NULL,'registration-recaptcha-action','sptek-cloud','cde7d578-3c14-4cb2-8f42-4a0588b330ba',3,60,'\0',NULL,NULL),('10aa82df-5f87-4b8c-a7f8-670e37b86b0a',NULL,'auth-spnego','master','11b70e76-54eb-4143-b94f-30acaf59cabb',3,30,'\0',NULL,NULL),('12316482-89b1-4287-997f-5b6726c4cb32',NULL,'idp-create-user-if-unique','sptek-cloud','ffb715e8-ae16-4ab0-a646-9a7c0681309b',2,10,'\0',NULL,'12efcf6b-2364-45e4-92cd-d63c17601339'),('16befd2e-4469-4ec3-b37d-6a4baec94e14',NULL,'no-cookie-redirect','master','98d30c76-c347-4da6-a522-e43c3a7f455e',0,10,'\0',NULL,NULL),('17a7adea-be6d-4189-8b51-b8a61a165d93',NULL,'registration-recaptcha-action','master','a3768882-6f58-48b4-a0bc-a9388b4495db',3,60,'\0',NULL,NULL),('180be2f8-68e4-4357-830a-ec33bd77a0a9',NULL,'idp-confirm-link','sptek-cloud','38728086-98a2-4076-8785-f873d6783f49',0,10,'\0',NULL,NULL),('1d9ac60e-9b19-46f0-b0f9-4f8f659f74e8',NULL,'auth-spnego','sptek-cloud','3d00e676-d69b-4f7e-8a3d-b971f94db960',3,20,'\0',NULL,NULL),('215ec14f-676b-42db-839f-7ca28e6733e7',NULL,'identity-provider-redirector','master','6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238',2,25,'\0',NULL,NULL),('22a99bf8-53d7-4f80-bd92-bb234e9d9e30',NULL,'registration-user-creation','sptek-cloud','cde7d578-3c14-4cb2-8f42-4a0588b330ba',0,20,'\0',NULL,NULL),('22c24ce8-fdaa-4564-9687-f38baca15c9b',NULL,NULL,'sptek-cloud','a246d10f-ce13-48c6-8a6d-741841d30b65',1,20,'','0b355fa3-1eab-4300-bd48-24d1ac40afe4',NULL),('284121ae-ebf9-4b6a-86fb-2faf71d1e351',NULL,'registration-page-form','master','048e3586-22d3-40b3-b6b8-08e7c71bc0ee',0,10,'','a3768882-6f58-48b4-a0bc-a9388b4495db',NULL),('2ac55047-85bd-4f99-b141-2cf8f78b3e79',NULL,'no-cookie-redirect','sptek-cloud','c6c17844-9881-45b2-a25e-6ed5f96d6fcf',0,10,'\0',NULL,NULL),('2fd943f1-3670-4df2-8be9-03ca94e07ea6',NULL,'registration-profile-action','sptek-cloud','cde7d578-3c14-4cb2-8f42-4a0588b330ba',0,40,'\0',NULL,NULL),('30a27430-a939-42e5-8b13-7e4779779d55',NULL,NULL,'master','19658bd5-d0d8-4eb4-800c-b0ad8b231f83',2,20,'','d31cded4-ef63-42ff-bb10-456cb3c8589d',NULL),('33d328a6-b940-44ac-941f-ed7a06f57abc',NULL,'http-basic-authenticator','master','84219fc9-3180-450b-90ce-5c471f529d7c',0,10,'\0',NULL,NULL),('34fe074c-0dba-406a-8a74-b9f2a95920e2',NULL,'basic-auth','sptek-cloud','566f9022-6022-45da-8599-10cf166237fe',0,10,'\0',NULL,NULL),('4839238e-ed6e-4838-8ac5-2eaa912f67bd',NULL,'auth-otp-form','sptek-cloud','0b355fa3-1eab-4300-bd48-24d1ac40afe4',0,20,'\0',NULL,NULL),('4963d1c7-52c9-49c4-aa90-fe1beb1c80e2',NULL,'idp-username-password-form','master','f54365a5-3d06-4693-b174-7d1dd98d91b9',0,10,'\0',NULL,NULL),('4c516904-01ae-4228-a2b7-b68587f1167f',NULL,'registration-password-action','master','a3768882-6f58-48b4-a0bc-a9388b4495db',0,50,'\0',NULL,NULL),('53facd15-2a3c-4de3-936f-522d3cdf41a3',NULL,'basic-auth-otp','master','11b70e76-54eb-4143-b94f-30acaf59cabb',3,20,'\0',NULL,NULL),('54f4bb09-55f2-43ef-86d5-a9a41da58eb5',NULL,'reset-otp','sptek-cloud','a13335eb-299a-4d31-9e70-f940b5bb7966',0,20,'\0',NULL,NULL),('5749124a-af42-4219-856c-79106ccb9c51',NULL,'client-secret-jwt','master','7b327e66-e100-433d-a3b7-9bb8c179aec9',2,30,'\0',NULL,NULL),('5b664f6e-545e-4d78-bc49-894aabced450',NULL,NULL,'master','9be080e3-a2cc-4f20-bcfd-40c3faffbe8f',1,30,'','e5a29bc9-0eb8-4059-9b23-c777020bd34f',NULL),('5cc578e3-d170-4e81-a965-47a04d5ac841',NULL,'idp-confirm-link','master','d31cded4-ef63-42ff-bb10-456cb3c8589d',0,10,'\0',NULL,NULL),('5d76eed1-8a81-4819-9814-2d1b6e3ccfc9',NULL,'direct-grant-validate-otp','master','e5a29bc9-0eb8-4059-9b23-c777020bd34f',0,20,'\0',NULL,NULL),('5e9fe20f-7252-4330-97fc-bd2347df2cef',NULL,NULL,'sptek-cloud','ab28e604-b04c-4ed0-b75b-3ca47ac750f1',0,20,'','ffb715e8-ae16-4ab0-a646-9a7c0681309b',NULL),('5ea31d1f-e870-46d0-be88-2460b94e7fe3',NULL,'client-x509','sptek-cloud','0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0',2,40,'\0',NULL,NULL),('5f4933e9-066c-4b1b-a7d6-f2b942510781',NULL,'client-secret','sptek-cloud','0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0',2,10,'\0',NULL,NULL),('5f9b040d-a73c-40c7-91e4-f468b90f09d0',NULL,'idp-email-verification','master','898611d2-8709-4ca7-9dab-194d40b65736',2,10,'\0',NULL,NULL),('611a33ae-3f83-4ff7-8908-99b47cae06d3',NULL,'reset-password','master','ffca34a6-ab7d-4f73-8a5f-59d920f35ecf',0,30,'\0',NULL,NULL),('6571da0b-b631-4d17-903c-e6081f5fb59f',NULL,'conditional-user-configured','sptek-cloud','0b355fa3-1eab-4300-bd48-24d1ac40afe4',0,10,'\0',NULL,NULL),('67ca1440-7453-435d-950e-abeffb2c452c',NULL,'reset-credential-email','sptek-cloud','577e637e-7555-4ceb-819c-2c2ab1c5468f',0,20,'\0',NULL,NULL),('6953c16d-3737-4b85-bb5e-50dc76a38b57',NULL,NULL,'master','b61b14ee-b93b-40ee-bcf0-ca86eaa97a31',1,20,'','e77dfa03-1bf6-4cf1-bcee-fead25596157',NULL),('6c2033b7-3f04-44ea-8f89-d8c7d942b436',NULL,'password-login-authenticator','sptek-cloud','80519ed2-394d-4b8b-9247-45b171f10716',0,0,'\0',NULL,'7ff0ff2d-7b6b-4dfd-84d0-34903e926eee'),('7193539e-bf2f-40e5-8042-4fa08148ad48',NULL,'direct-grant-validate-otp','sptek-cloud','8aa24f02-1dff-4e33-9a12-53c1d30b598a',0,20,'\0',NULL,NULL),('72f30f72-6d2f-43c0-9096-7ef981befad6',NULL,NULL,'master','f54365a5-3d06-4693-b174-7d1dd98d91b9',1,20,'','e7e71d90-7362-4cb3-8f52-869a69ee39ea',NULL),('7aad1b31-29b1-4cda-b1b4-b136a6dc8192',NULL,'conditional-user-configured','sptek-cloud','8aa24f02-1dff-4e33-9a12-53c1d30b598a',0,10,'\0',NULL,NULL),('7bcbbe47-e77b-4856-9de1-e7da1125c5c3',NULL,'auth-username-password-form','sptek-cloud','a246d10f-ce13-48c6-8a6d-741841d30b65',0,10,'\0',NULL,NULL),('7c1cb1de-e6f6-4f74-8627-2080f70e8799',NULL,'auth-spnego','sptek-cloud','566f9022-6022-45da-8599-10cf166237fe',3,30,'\0',NULL,NULL),('7d4371a2-ac70-4b58-a605-8df61a243add',NULL,'auth-cookie','sptek-cloud','3d00e676-d69b-4f7e-8a3d-b971f94db960',2,10,'\0',NULL,NULL),('7ea7636a-2cf7-4a15-91b1-2b188bc5f063',NULL,'basic-auth-otp','sptek-cloud','566f9022-6022-45da-8599-10cf166237fe',3,20,'\0',NULL,NULL),('7ebe7717-1dfa-42bc-9d5b-3d39dc08ee54',NULL,'conditional-user-configured','master','e7e71d90-7362-4cb3-8f52-869a69ee39ea',0,10,'\0',NULL,NULL),('8073064c-4c23-4dc2-b54d-7710a90a1072',NULL,'identity-provider-redirector','sptek-cloud','3d00e676-d69b-4f7e-8a3d-b971f94db960',2,25,'\0',NULL,NULL),('8426b897-c805-4be1-950c-966d99c0cc02',NULL,NULL,'sptek-cloud','7ea22ebd-31ef-4de6-971b-d9c32cef453c',2,20,'','d3ea4c7c-c420-4707-9e23-f9950d2125fb',NULL),('84be8968-20e4-4f82-900f-86e884285415',NULL,'direct-grant-validate-username','sptek-cloud','9d4cdb6d-1843-4c51-8875-bacd66baa321',0,10,'\0',NULL,NULL),('86415932-1aa0-4176-aa4e-dcd54962ce0a',NULL,NULL,'master','ffca34a6-ab7d-4f73-8a5f-59d920f35ecf',1,40,'','919fc96d-a867-4b49-ad13-2d01d035143a',NULL),('8bc1083c-db0a-4857-882a-92e62a5e2910',NULL,'docker-http-basic-authenticator','master','187056d3-10a2-40a1-b4dc-252e3d361487',0,10,'\0',NULL,NULL),('8ef82673-8da9-4578-b66c-5a190b9fe1f8',NULL,'basic-auth','master','11b70e76-54eb-4143-b94f-30acaf59cabb',0,10,'\0',NULL,NULL),('901372df-224d-4cf0-bd8e-a30437745eee',NULL,'reset-credential-email','master','ffca34a6-ab7d-4f73-8a5f-59d920f35ecf',0,20,'\0',NULL,NULL),('91b7eb91-33dc-4c9e-8520-885be524f0db',NULL,'idp-review-profile','master','9648909d-19c9-4aa7-bda7-2d4a94030c56',0,10,'\0',NULL,'73fd5af9-29b6-497a-a932-11f0e3808f8b'),('9ecf5038-f9b6-4911-95dc-2c36e518c54c',NULL,'conditional-user-configured','sptek-cloud','774ede0a-d4e0-4d3d-bb7a-f6a99798f457',0,10,'\0',NULL,NULL),('a0043d93-7a4e-44f1-a10f-55affabbd8e0',NULL,'conditional-user-configured','master','919fc96d-a867-4b49-ad13-2d01d035143a',0,10,'\0',NULL,NULL),('a0810f70-37f5-4c61-bad4-63160ffbbaf6',NULL,'direct-grant-validate-username','master','9be080e3-a2cc-4f20-bcfd-40c3faffbe8f',0,10,'\0',NULL,NULL),('a0f76bdf-4dff-47b9-a7ff-0e1f0482fefe',NULL,NULL,'sptek-cloud','38728086-98a2-4076-8785-f873d6783f49',0,20,'','7ea22ebd-31ef-4de6-971b-d9c32cef453c',NULL),('a2345b67-ba8e-489e-9b8b-8fe842f82284',NULL,'idp-review-profile','sptek-cloud','ab28e604-b04c-4ed0-b75b-3ca47ac750f1',0,10,'\0',NULL,'01ffa381-781f-428e-85cf-bdf4021aee1c'),('a5b1d9e6-1927-4629-b684-195936784a07',NULL,'direct-grant-validate-password','master','9be080e3-a2cc-4f20-bcfd-40c3faffbe8f',0,20,'\0',NULL,NULL),('a763953d-4736-4b53-a7c3-a18eb04c5288',NULL,'reset-password','sptek-cloud','577e637e-7555-4ceb-819c-2c2ab1c5468f',0,30,'\0',NULL,NULL),('aa15df0d-1ee4-4c14-86b0-b4b0b9ed4604',NULL,'client-secret-jwt','sptek-cloud','0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0',2,30,'\0',NULL,NULL),('ace4a66e-9640-4880-ac55-1dc62b17a6ec',NULL,'registration-user-creation','master','a3768882-6f58-48b4-a0bc-a9388b4495db',0,20,'\0',NULL,NULL),('ae1fc533-bdcd-49ee-8ab6-0ebad84b4705',NULL,'idp-create-user-if-unique','master','19658bd5-d0d8-4eb4-800c-b0ad8b231f83',2,10,'\0',NULL,'f6a28991-2abd-4988-93e0-ab59ba529304'),('b381a7ec-9e1a-40e4-a319-95125f234e4e',NULL,'client-secret','master','7b327e66-e100-433d-a3b7-9bb8c179aec9',2,10,'\0',NULL,NULL),('b933e6a5-74a2-4905-88f2-1cc6319464fc',NULL,'auth-otp-form','master','e7e71d90-7362-4cb3-8f52-869a69ee39ea',0,20,'\0',NULL,NULL),('b9402b3d-d895-4e98-bbac-29e7a556d9bc',NULL,NULL,'sptek-cloud','577e637e-7555-4ceb-819c-2c2ab1c5468f',1,40,'','a13335eb-299a-4d31-9e70-f940b5bb7966',NULL),('bc3e88f0-873b-4a73-81f6-88a2cf6559f9',NULL,NULL,'sptek-cloud','9d4cdb6d-1843-4c51-8875-bacd66baa321',1,30,'','8aa24f02-1dff-4e33-9a12-53c1d30b598a',NULL),('bcad6091-a5f1-44c4-8f01-795cdf56aa0d',NULL,'direct-grant-validate-password','sptek-cloud','9d4cdb6d-1843-4c51-8875-bacd66baa321',0,20,'\0',NULL,NULL),('c0e72990-5455-4c42-902f-bdc9f841ee93',NULL,NULL,'master','9648909d-19c9-4aa7-bda7-2d4a94030c56',0,20,'','19658bd5-d0d8-4eb4-800c-b0ad8b231f83',NULL),('c12ad9d6-a029-4340-ba33-b9687a601041',NULL,'reset-credentials-choose-user','sptek-cloud','577e637e-7555-4ceb-819c-2c2ab1c5468f',0,10,'\0',NULL,NULL),('c15cba7f-c7ae-4863-b09d-e232e6c195ae',NULL,NULL,'sptek-cloud','ffb715e8-ae16-4ab0-a646-9a7c0681309b',2,20,'','38728086-98a2-4076-8785-f873d6783f49',NULL),('c6365ac2-1a23-4704-870a-5b2e0cab04fa',NULL,'client-x509','master','7b327e66-e100-433d-a3b7-9bb8c179aec9',2,40,'\0',NULL,NULL),('c80ca35c-77a1-44ec-abf3-5735bf8c96d5',NULL,'reset-credentials-choose-user','master','ffca34a6-ab7d-4f73-8a5f-59d920f35ecf',0,10,'\0',NULL,NULL),('c9d6dc39-e66a-40c5-8fcd-a798efecf7db',NULL,'registration-password-action','sptek-cloud','cde7d578-3c14-4cb2-8f42-4a0588b330ba',0,50,'\0',NULL,NULL),('ca26e4d5-0bfe-416c-9f0d-f9e65e7d4b87',NULL,NULL,'sptek-cloud','3d00e676-d69b-4f7e-8a3d-b971f94db960',2,30,'','a246d10f-ce13-48c6-8a6d-741841d30b65',NULL),('ccaf0e10-4463-4ed7-8302-aab2fc3be28c',NULL,NULL,'master','898611d2-8709-4ca7-9dab-194d40b65736',2,20,'','f54365a5-3d06-4693-b174-7d1dd98d91b9',NULL),('d295d002-8858-4e31-9758-ab52acfb6246',NULL,'conditional-user-configured','master','e77dfa03-1bf6-4cf1-bcee-fead25596157',0,10,'\0',NULL,NULL),('d2fb3d89-bc6a-44ad-b6bc-de955140dce2',NULL,NULL,'master','6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238',2,30,'','b61b14ee-b93b-40ee-bcf0-ca86eaa97a31',NULL),('d733911f-50c4-4102-bd05-8d0416ae65e7',NULL,NULL,'master','98d30c76-c347-4da6-a522-e43c3a7f455e',0,20,'','11b70e76-54eb-4143-b94f-30acaf59cabb',NULL),('d86f7f36-9933-4f96-920a-39bf6789343f',NULL,'idp-email-verification','sptek-cloud','7ea22ebd-31ef-4de6-971b-d9c32cef453c',2,10,'\0',NULL,NULL),('dc1f7e0a-0a0d-4810-8abd-8aad2832804a',NULL,'reset-otp','master','919fc96d-a867-4b49-ad13-2d01d035143a',0,20,'\0',NULL,NULL),('e2aa9355-7d2b-4b36-b2e1-d4a739619237',NULL,'conditional-user-configured','sptek-cloud','a13335eb-299a-4d31-9e70-f940b5bb7966',0,10,'\0',NULL,NULL),('eaa6211d-8ae5-4372-8544-d3d7643d0736',NULL,'http-basic-authenticator','sptek-cloud','9803066b-c200-468a-807c-cd04e536415b',0,10,'\0',NULL,NULL),('f2deeb23-8735-4337-b669-d97a8673f599',NULL,'conditional-user-configured','master','e5a29bc9-0eb8-4059-9b23-c777020bd34f',0,10,'\0',NULL,NULL),('f612d9bc-ae96-48db-84fe-29dd2296f05d',NULL,'client-jwt','sptek-cloud','0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0',2,20,'\0',NULL,NULL),('f6c6a18f-d50e-4e62-ae82-05f77a7384d3',NULL,'client-jwt','master','7b327e66-e100-433d-a3b7-9bb8c179aec9',2,20,'\0',NULL,NULL),('f7aa4115-abbd-4e0c-b80e-75f206538c7c',NULL,'auth-otp-form','master','e77dfa03-1bf6-4cf1-bcee-fead25596157',0,20,'\0',NULL,NULL),('f82e13f5-6084-4bb4-8174-fc5a49ec74d5',NULL,'idp-username-password-form','sptek-cloud','d3ea4c7c-c420-4707-9e23-f9950d2125fb',0,10,'\0',NULL,NULL),('f92de4d7-4f46-480b-a521-f4721d8f8552',NULL,'registration-page-form','sptek-cloud','1e0b7c25-f5d5-445d-ae80-c8fc5000413e',0,10,'','cde7d578-3c14-4cb2-8f42-4a0588b330ba',NULL);
/*!40000 ALTER TABLE `authentication_execution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authentication_flow`
--

DROP TABLE IF EXISTS `authentication_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authentication_flow` (
  `ID` varchar(36) NOT NULL,
  `ALIAS` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `PROVIDER_ID` varchar(36) NOT NULL DEFAULT 'basic-flow',
  `TOP_LEVEL` bit(1) NOT NULL DEFAULT b'0',
  `BUILT_IN` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`ID`),
  KEY `IDX_AUTH_FLOW_REALM` (`REALM_ID`),
  CONSTRAINT `FK_AUTH_FLOW_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authentication_flow`
--

LOCK TABLES `authentication_flow` WRITE;
/*!40000 ALTER TABLE `authentication_flow` DISABLE KEYS */;
INSERT INTO `authentication_flow` VALUES ('048e3586-22d3-40b3-b6b8-08e7c71bc0ee','registration','registration flow','master','basic-flow','',''),('0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0','clients','Base authentication for clients','sptek-cloud','client-flow','',''),('0b355fa3-1eab-4300-bd48-24d1ac40afe4','Browser - Conditional OTP','Flow to determine if the OTP is required for the authentication','sptek-cloud','basic-flow','\0',''),('11b70e76-54eb-4143-b94f-30acaf59cabb','Authentication Options','Authentication options.','master','basic-flow','\0',''),('187056d3-10a2-40a1-b4dc-252e3d361487','docker auth','Used by Docker clients to authenticate against the IDP','master','basic-flow','',''),('19658bd5-d0d8-4eb4-800c-b0ad8b231f83','User creation or linking','Flow for the existing/non-existing user alternatives','master','basic-flow','\0',''),('1e0b7c25-f5d5-445d-ae80-c8fc5000413e','registration','registration flow','sptek-cloud','basic-flow','',''),('38728086-98a2-4076-8785-f873d6783f49','Handle Existing Account','Handle what to do if there is existing account with same email/username like authenticated identity provider','sptek-cloud','basic-flow','\0',''),('39d4a2d5-1496-4c9e-ad55-6d4d2230bc2d','docker auth','Used by Docker clients to authenticate against the IDP','sptek-cloud','basic-flow','',''),('3d00e676-d69b-4f7e-8a3d-b971f94db960','browser','browser based authentication','sptek-cloud','basic-flow','',''),('40821c43-0e54-4122-8166-6c15dc419c10','Password Login Test','Ths is password login test','sptek-cloud','basic-flow','','\0'),('566f9022-6022-45da-8599-10cf166237fe','Authentication Options','Authentication options.','sptek-cloud','basic-flow','\0',''),('577e637e-7555-4ceb-819c-2c2ab1c5468f','reset credentials','Reset credentials for a user if they forgot their password or something','sptek-cloud','basic-flow','',''),('6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238','browser','browser based authentication','master','basic-flow','',''),('774ede0a-d4e0-4d3d-bb7a-f6a99798f457','First broker login - Conditional OTP','Flow to determine if the OTP is required for the authentication','sptek-cloud','basic-flow','\0',''),('7b327e66-e100-433d-a3b7-9bb8c179aec9','clients','Base authentication for clients','master','client-flow','',''),('7ea22ebd-31ef-4de6-971b-d9c32cef453c','Account verification options','Method with which to verity the existing account','sptek-cloud','basic-flow','\0',''),('80519ed2-394d-4b8b-9247-45b171f10716','Custom Login Authenticator','','sptek-cloud','basic-flow','','\0'),('84219fc9-3180-450b-90ce-5c471f529d7c','saml ecp','SAML ECP Profile Authentication Flow','master','basic-flow','',''),('898611d2-8709-4ca7-9dab-194d40b65736','Account verification options','Method with which to verity the existing account','master','basic-flow','\0',''),('8aa24f02-1dff-4e33-9a12-53c1d30b598a','Direct Grant - Conditional OTP','Flow to determine if the OTP is required for the authentication','sptek-cloud','basic-flow','\0',''),('919fc96d-a867-4b49-ad13-2d01d035143a','Reset - Conditional OTP','Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.','master','basic-flow','\0',''),('9648909d-19c9-4aa7-bda7-2d4a94030c56','first broker login','Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account','master','basic-flow','',''),('9803066b-c200-468a-807c-cd04e536415b','saml ecp','SAML ECP Profile Authentication Flow','sptek-cloud','basic-flow','',''),('98d30c76-c347-4da6-a522-e43c3a7f455e','http challenge','An authentication flow based on challenge-response HTTP Authentication Schemes','master','basic-flow','',''),('9be080e3-a2cc-4f20-bcfd-40c3faffbe8f','direct grant','OpenID Connect Resource Owner Grant','master','basic-flow','',''),('9d4cdb6d-1843-4c51-8875-bacd66baa321','direct grant','OpenID Connect Resource Owner Grant','sptek-cloud','basic-flow','',''),('a13335eb-299a-4d31-9e70-f940b5bb7966','Reset - Conditional OTP','Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.','sptek-cloud','basic-flow','\0',''),('a246d10f-ce13-48c6-8a6d-741841d30b65','forms','Username, password, otp and other auth forms.','sptek-cloud','basic-flow','\0',''),('a3768882-6f58-48b4-a0bc-a9388b4495db','registration form','registration form','master','form-flow','\0',''),('ab28e604-b04c-4ed0-b75b-3ca47ac750f1','first broker login','Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account','sptek-cloud','basic-flow','',''),('b61b14ee-b93b-40ee-bcf0-ca86eaa97a31','forms','Username, password, otp and other auth forms.','master','basic-flow','\0',''),('c6c17844-9881-45b2-a25e-6ed5f96d6fcf','http challenge','An authentication flow based on challenge-response HTTP Authentication Schemes','sptek-cloud','basic-flow','',''),('cde7d578-3c14-4cb2-8f42-4a0588b330ba','registration form','registration form','sptek-cloud','form-flow','\0',''),('d31cded4-ef63-42ff-bb10-456cb3c8589d','Handle Existing Account','Handle what to do if there is existing account with same email/username like authenticated identity provider','master','basic-flow','\0',''),('d3ea4c7c-c420-4707-9e23-f9950d2125fb','Verify Existing Account by Re-authentication','Reauthentication of existing account','sptek-cloud','basic-flow','\0',''),('e5a29bc9-0eb8-4059-9b23-c777020bd34f','Direct Grant - Conditional OTP','Flow to determine if the OTP is required for the authentication','master','basic-flow','\0',''),('e77dfa03-1bf6-4cf1-bcee-fead25596157','Browser - Conditional OTP','Flow to determine if the OTP is required for the authentication','master','basic-flow','\0',''),('e7e71d90-7362-4cb3-8f52-869a69ee39ea','First broker login - Conditional OTP','Flow to determine if the OTP is required for the authentication','master','basic-flow','\0',''),('f54365a5-3d06-4693-b174-7d1dd98d91b9','Verify Existing Account by Re-authentication','Reauthentication of existing account','master','basic-flow','\0',''),('ffb715e8-ae16-4ab0-a646-9a7c0681309b','User creation or linking','Flow for the existing/non-existing user alternatives','sptek-cloud','basic-flow','\0',''),('ffca34a6-ab7d-4f73-8a5f-59d920f35ecf','reset credentials','Reset credentials for a user if they forgot their password or something','master','basic-flow','','');
/*!40000 ALTER TABLE `authentication_flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authenticator_config`
--

DROP TABLE IF EXISTS `authenticator_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authenticator_config` (
  `ID` varchar(36) NOT NULL,
  `ALIAS` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_AUTH_CONFIG_REALM` (`REALM_ID`),
  CONSTRAINT `FK_AUTH_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authenticator_config`
--

LOCK TABLES `authenticator_config` WRITE;
/*!40000 ALTER TABLE `authenticator_config` DISABLE KEYS */;
INSERT INTO `authenticator_config` VALUES ('01ffa381-781f-428e-85cf-bdf4021aee1c','review profile config','sptek-cloud'),('12efcf6b-2364-45e4-92cd-d63c17601339','create unique user config','sptek-cloud'),('73fd5af9-29b6-497a-a932-11f0e3808f8b','review profile config','master'),('7ff0ff2d-7b6b-4dfd-84d0-34903e926eee','Password Authenticator Config','sptek-cloud'),('f6a28991-2abd-4988-93e0-ab59ba529304','create unique user config','master');
/*!40000 ALTER TABLE `authenticator_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authenticator_config_entry`
--

DROP TABLE IF EXISTS `authenticator_config_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authenticator_config_entry` (
  `AUTHENTICATOR_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`AUTHENTICATOR_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authenticator_config_entry`
--

LOCK TABLES `authenticator_config_entry` WRITE;
/*!40000 ALTER TABLE `authenticator_config_entry` DISABLE KEYS */;
INSERT INTO `authenticator_config_entry` VALUES ('01ffa381-781f-428e-85cf-bdf4021aee1c','missing','update.profile.on.first.login'),('12efcf6b-2364-45e4-92cd-d63c17601339','false','require.password.update.after.registration'),('73fd5af9-29b6-497a-a932-11f0e3808f8b','missing','update.profile.on.first.login'),('7ff0ff2d-7b6b-4dfd-84d0-34903e926eee','password','form.password'),('7ff0ff2d-7b6b-4dfd-84d0-34903e926eee','username','form.username'),('f6a28991-2abd-4988-93e0-ab59ba529304','false','require.password.update.after.registration');
/*!40000 ALTER TABLE `authenticator_config_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `broker_link`
--

DROP TABLE IF EXISTS `broker_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `broker_link` (
  `IDENTITY_PROVIDER` varchar(255) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `BROKER_USER_ID` varchar(255) DEFAULT NULL,
  `BROKER_USERNAME` varchar(255) DEFAULT NULL,
  `TOKEN` text DEFAULT NULL,
  `USER_ID` varchar(255) NOT NULL,
  PRIMARY KEY (`IDENTITY_PROVIDER`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `broker_link`
--

LOCK TABLES `broker_link` WRITE;
/*!40000 ALTER TABLE `broker_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `broker_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client` (
  `ID` varchar(36) NOT NULL,
  `ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `FULL_SCOPE_ALLOWED` bit(1) NOT NULL DEFAULT b'0',
  `CLIENT_ID` varchar(255) DEFAULT NULL,
  `NOT_BEFORE` int(11) DEFAULT NULL,
  `PUBLIC_CLIENT` bit(1) NOT NULL DEFAULT b'0',
  `SECRET` varchar(255) DEFAULT NULL,
  `BASE_URL` varchar(255) DEFAULT NULL,
  `BEARER_ONLY` bit(1) NOT NULL DEFAULT b'0',
  `MANAGEMENT_URL` varchar(255) DEFAULT NULL,
  `SURROGATE_AUTH_REQUIRED` bit(1) NOT NULL DEFAULT b'0',
  `REALM_ID` varchar(36) DEFAULT NULL,
  `PROTOCOL` varchar(255) DEFAULT NULL,
  `NODE_REREG_TIMEOUT` int(11) DEFAULT 0,
  `FRONTCHANNEL_LOGOUT` bit(1) NOT NULL DEFAULT b'0',
  `CONSENT_REQUIRED` bit(1) NOT NULL DEFAULT b'0',
  `NAME` varchar(255) DEFAULT NULL,
  `SERVICE_ACCOUNTS_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `CLIENT_AUTHENTICATOR_TYPE` varchar(255) DEFAULT NULL,
  `ROOT_URL` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `REGISTRATION_TOKEN` varchar(255) DEFAULT NULL,
  `STANDARD_FLOW_ENABLED` bit(1) NOT NULL DEFAULT b'1',
  `IMPLICIT_FLOW_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `DIRECT_ACCESS_GRANTS_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `ALWAYS_DISPLAY_IN_CONSOLE` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_B71CJLBENV945RB6GCON438AT` (`REALM_ID`,`CLIENT_ID`),
  KEY `IDX_CLIENT_ID` (`CLIENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES ('0fcab411-7988-4552-aa1a-bf8d87f77a81','','\0','account-console',0,'',NULL,'/realms/sptek-cloud/account/','\0',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_account-console}','\0','client-secret','${authBaseUrl}',NULL,NULL,'','\0','\0','\0'),('12d7e401-eb29-4e31-866e-1efdfcd53787','','\0','broker',0,'\0',NULL,NULL,'',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_broker}','\0','client-secret',NULL,NULL,NULL,'','\0','\0','\0'),('291690ba-3798-45f3-8b01-f11293902cce','','\0','account',0,'',NULL,'/realms/strato-cloud/account/','\0',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_account}','\0','client-secret','${authBaseUrl}',NULL,NULL,'','\0','\0','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','','\0','account',0,'',NULL,'/realms/master/account/','\0',NULL,'\0','master','openid-connect',0,'\0','\0','${client_account}','\0','client-secret','${authBaseUrl}',NULL,NULL,'','\0','\0','\0'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','','\0','security-admin-console',0,'',NULL,'/admin/strato-cloud/console/','\0',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_security-admin-console}','\0','client-secret','${authAdminUrl}',NULL,NULL,'','\0','\0','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','','\0','broker',0,'\0',NULL,NULL,'',NULL,'\0','master','openid-connect',0,'\0','\0','${client_broker}','\0','client-secret',NULL,NULL,NULL,'','\0','\0','\0'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','','\0','security-admin-console',0,'',NULL,'/admin/master/console/','\0',NULL,'\0','master','openid-connect',0,'\0','\0','${client_security-admin-console}','\0','client-secret','${authAdminUrl}',NULL,NULL,'','\0','\0','\0'),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','','\0','admin-cli',0,'',NULL,NULL,'\0',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_admin-cli}','\0','client-secret',NULL,NULL,NULL,'\0','\0','','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','','\0','admin-cli',0,'\0','AJVWPGT3fwynWC7ZKo9Kwdk3Rs9YKS05','','\0',NULL,'\0','master','openid-connect',0,'\0','\0','${client_admin-cli}','','client-secret','',NULL,NULL,'\0','\0','','\0'),('97e593c5-a1ee-4253-b279-bcddf129f69a','','','paaS_portal',0,'\0','kyqtZeiMHax3ti0PZn5M2m0HjdRTzL8i',NULL,'\0',NULL,'\0','master','openid-connect',-1,'\0','\0',NULL,'\0','client-secret','http://paasportal.strato.com',NULL,NULL,'','\0','','\0'),('99f70571-5263-4b31-bbea-8b85911746b2','','\0','realm-management',0,'\0',NULL,NULL,'',NULL,'\0','sptek-cloud','openid-connect',0,'\0','\0','${client_realm-management}','\0','client-secret',NULL,NULL,NULL,'','\0','\0','\0'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','','','paas_portal_dev',0,'\0','IJneke1CZy9yL29fiBFFWnyHp3TZ790y',NULL,'\0','http://localhost:18080','\0','sptek-cloud','openid-connect',-1,'\0','\0',NULL,'\0','client-secret','http://localhost:18080',NULL,NULL,'','\0','','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','\0','master-realm',0,'\0',NULL,NULL,'',NULL,'\0','master',NULL,0,'\0','\0','master Realm','\0','client-secret',NULL,NULL,NULL,'','\0','\0','\0'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','','\0','account-console',0,'',NULL,'/realms/master/account/','\0',NULL,'\0','master','openid-connect',0,'\0','\0','${client_account-console}','\0','client-secret','${authBaseUrl}',NULL,NULL,'','\0','\0','\0'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','','','paas_portal',0,'\0','scHouDhWkAt8ylcJvSh5tTio4yo2byws',NULL,'\0','http://paasportal.strato.com','\0','sptek-cloud','openid-connect',-1,'\0','\0',NULL,'\0','client-secret','http://paasportal.strato.com:18080',NULL,NULL,'','\0','','\0'),('fad7c87e-69c6-4163-a0dd-825c5276a923','','\0','strato-cloud-realm',0,'\0',NULL,NULL,'',NULL,'\0','master',NULL,0,'\0','\0','sptek-cloud Realm','\0','client-secret',NULL,NULL,NULL,'','\0','\0','\0');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_attributes`
--

DROP TABLE IF EXISTS `client_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_attributes` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `VALUE` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`NAME`),
  KEY `IDX_CLIENT_ATT_BY_NAME_VALUE` (`NAME`,`VALUE`(255)),
  CONSTRAINT `FK3C47C64BEACCA966` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_attributes`
--

LOCK TABLES `client_attributes` WRITE;
/*!40000 ALTER TABLE `client_attributes` DISABLE KEYS */;
INSERT INTO `client_attributes` VALUES ('0fcab411-7988-4552-aa1a-bf8d87f77a81','S256','pkce.code.challenge.method'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','backchannel.logout.revoke.offline.tokens'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','true','backchannel.logout.session.required'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','client_credentials.use_refresh_token'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','display.on.consent.screen'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','exclude.session.state.from.auth.response'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','id.token.as.detached.signature'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','oauth2.device.authorization.grant.enabled'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','oidc.ciba.grant.enabled'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','require.pushed.authorization.requests'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.artifact.binding'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.assertion.signature'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.authnstatement'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.client.signature'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.encrypt'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.force.post.binding'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.multivalued.roles'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.onetimeuse.condition'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.server.signature'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml.server.signature.keyinfo.ext'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','saml_force_name_id_format'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','false','tls.client.certificate.bound.access.tokens'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','true','use.refresh.tokens'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','S256','pkce.code.challenge.method'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','S256','pkce.code.challenge.method'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','backchannel.logout.revoke.offline.tokens'),('775ead1f-db83-43ad-a74d-b856153f9e51','true','backchannel.logout.session.required'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','client_credentials.use_refresh_token'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','display.on.consent.screen'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','exclude.session.state.from.auth.response'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','id.token.as.detached.signature'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','oauth2.device.authorization.grant.enabled'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','oidc.ciba.grant.enabled'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','require.pushed.authorization.requests'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.artifact.binding'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.assertion.signature'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.authnstatement'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.client.signature'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.encrypt'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.force.post.binding'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.multivalued.roles'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.onetimeuse.condition'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.server.signature'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml.server.signature.keyinfo.ext'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','saml_force_name_id_format'),('775ead1f-db83-43ad-a74d-b856153f9e51','false','tls.client.certificate.bound.access.tokens'),('775ead1f-db83-43ad-a74d-b856153f9e51','true','use.refresh.tokens'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','backchannel.logout.revoke.offline.tokens'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','true','backchannel.logout.session.required'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','client_credentials.use_refresh_token'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','display.on.consent.screen'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','exclude.session.state.from.auth.response'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','id.token.as.detached.signature'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','oauth2.device.authorization.grant.enabled'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','oidc.ciba.grant.enabled'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','require.pushed.authorization.requests'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.artifact.binding'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.assertion.signature'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.authnstatement'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.client.signature'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.encrypt'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.force.post.binding'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.multivalued.roles'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.onetimeuse.condition'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.server.signature'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml.server.signature.keyinfo.ext'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','saml_force_name_id_format'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','false','tls.client.certificate.bound.access.tokens'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','true','use.refresh.tokens'),('955e906d-aa0f-4d49-82f0-404d19080454','600','access.token.lifespan'),('955e906d-aa0f-4d49-82f0-404d19080454','false','backchannel.logout.revoke.offline.tokens'),('955e906d-aa0f-4d49-82f0-404d19080454','false','backchannel.logout.session.required'),('955e906d-aa0f-4d49-82f0-404d19080454','true','client_credentials.use_refresh_token'),('955e906d-aa0f-4d49-82f0-404d19080454','false','display.on.consent.screen'),('955e906d-aa0f-4d49-82f0-404d19080454','false','exclude.session.state.from.auth.response'),('955e906d-aa0f-4d49-82f0-404d19080454','false','id.token.as.detached.signature'),('955e906d-aa0f-4d49-82f0-404d19080454','false','oauth2.device.authorization.grant.enabled'),('955e906d-aa0f-4d49-82f0-404d19080454','false','oidc.ciba.grant.enabled'),('955e906d-aa0f-4d49-82f0-404d19080454','false','require.pushed.authorization.requests'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.artifact.binding'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.assertion.signature'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.authnstatement'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.client.signature'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.encrypt'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.force.post.binding'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.multivalued.roles'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.onetimeuse.condition'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.server.signature'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml.server.signature.keyinfo.ext'),('955e906d-aa0f-4d49-82f0-404d19080454','false','saml_force_name_id_format'),('955e906d-aa0f-4d49-82f0-404d19080454','false','tls.client.certificate.bound.access.tokens'),('955e906d-aa0f-4d49-82f0-404d19080454','true','use.refresh.tokens'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','backchannel.logout.revoke.offline.tokens'),('97e593c5-a1ee-4253-b279-bcddf129f69a','true','backchannel.logout.session.required'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','client_credentials.use_refresh_token'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','display.on.consent.screen'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','exclude.session.state.from.auth.response'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','id.token.as.detached.signature'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','oauth2.device.authorization.grant.enabled'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','oidc.ciba.grant.enabled'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','require.pushed.authorization.requests'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.artifact.binding'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.assertion.signature'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.authnstatement'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.client.signature'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.encrypt'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.force.post.binding'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.multivalued.roles'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.onetimeuse.condition'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.server.signature'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml.server.signature.keyinfo.ext'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','saml_force_name_id_format'),('97e593c5-a1ee-4253-b279-bcddf129f69a','false','tls.client.certificate.bound.access.tokens'),('97e593c5-a1ee-4253-b279-bcddf129f69a','true','use.refresh.tokens'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','60','access.token.lifespan'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','backchannel.logout.revoke.offline.tokens'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','true','backchannel.logout.session.required'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','client_credentials.use_refresh_token'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','display.on.consent.screen'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','exclude.session.state.from.auth.response'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','id.token.as.detached.signature'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','oauth2.device.authorization.grant.enabled'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','oidc.ciba.grant.enabled'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','require.pushed.authorization.requests'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.artifact.binding'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.assertion.signature'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.authnstatement'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.client.signature'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.encrypt'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.force.post.binding'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.multivalued.roles'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.onetimeuse.condition'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.server.signature'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml.server.signature.keyinfo.ext'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','saml_force_name_id_format'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','false','tls.client.certificate.bound.access.tokens'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','true','use.refresh.tokens'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','S256','pkce.code.challenge.method'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','60','access.token.lifespan'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','backchannel.logout.revoke.offline.tokens'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','true','backchannel.logout.session.required'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','client_credentials.use_refresh_token'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','display.on.consent.screen'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','exclude.session.state.from.auth.response'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','id.token.as.detached.signature'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','oauth2.device.authorization.grant.enabled'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','oidc.ciba.grant.enabled'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','require.pushed.authorization.requests'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.artifact.binding'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.assertion.signature'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.authnstatement'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.client.signature'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.encrypt'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.force.post.binding'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.multivalued.roles'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.onetimeuse.condition'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.server.signature'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml.server.signature.keyinfo.ext'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','saml_force_name_id_format'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','false','tls.client.certificate.bound.access.tokens'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','true','use.refresh.tokens');
/*!40000 ALTER TABLE `client_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_auth_flow_bindings`
--

DROP TABLE IF EXISTS `client_auth_flow_bindings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_auth_flow_bindings` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `FLOW_ID` varchar(36) DEFAULT NULL,
  `BINDING_NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`BINDING_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_auth_flow_bindings`
--

LOCK TABLES `client_auth_flow_bindings` WRITE;
/*!40000 ALTER TABLE `client_auth_flow_bindings` DISABLE KEYS */;
INSERT INTO `client_auth_flow_bindings` VALUES ('2e5f8397-470b-4516-8cc3-aa92e3bda308','3d00e676-d69b-4f7e-8a3d-b971f94db960','browser'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','80519ed2-394d-4b8b-9247-45b171f10716','direct_grant'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','3d00e676-d69b-4f7e-8a3d-b971f94db960','browser'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','80519ed2-394d-4b8b-9247-45b171f10716','direct_grant'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','3d00e676-d69b-4f7e-8a3d-b971f94db960','browser'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','80519ed2-394d-4b8b-9247-45b171f10716','direct_grant'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','3d00e676-d69b-4f7e-8a3d-b971f94db960','browser'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','80519ed2-394d-4b8b-9247-45b171f10716','direct_grant');
/*!40000 ALTER TABLE `client_auth_flow_bindings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_initial_access`
--

DROP TABLE IF EXISTS `client_initial_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_initial_access` (
  `ID` varchar(36) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `TIMESTAMP` int(11) DEFAULT NULL,
  `EXPIRATION` int(11) DEFAULT NULL,
  `COUNT` int(11) DEFAULT NULL,
  `REMAINING_COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_CLIENT_INIT_ACC_REALM` (`REALM_ID`),
  CONSTRAINT `FK_CLIENT_INIT_ACC_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_initial_access`
--

LOCK TABLES `client_initial_access` WRITE;
/*!40000 ALTER TABLE `client_initial_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_initial_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_node_registrations`
--

DROP TABLE IF EXISTS `client_node_registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_node_registrations` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `VALUE` int(11) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`NAME`),
  CONSTRAINT `FK4129723BA992F594` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_node_registrations`
--

LOCK TABLES `client_node_registrations` WRITE;
/*!40000 ALTER TABLE `client_node_registrations` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_node_registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_scope`
--

DROP TABLE IF EXISTS `client_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_scope` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `PROTOCOL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_CLI_SCOPE` (`REALM_ID`,`NAME`),
  KEY `IDX_REALM_CLSCOPE` (`REALM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_scope`
--

LOCK TABLES `client_scope` WRITE;
/*!40000 ALTER TABLE `client_scope` DISABLE KEYS */;
INSERT INTO `client_scope` VALUES ('04297489-3e8c-4dc8-9ca0-bf26e5ec3f77','profile','sptek-cloud','OpenID Connect built-in scope: profile','openid-connect'),('0c5a33f2-15c0-44b9-a387-190556db824d','roles','master','OpenID Connect scope for add user roles to the access token','openid-connect'),('138be55b-dbeb-4e9f-951e-ea982198a1bd','web-origins','master','OpenID Connect scope for add allowed web origins to the access token','openid-connect'),('2f5372a0-234e-45ed-8bd3-e6f4047fe25f','microprofile-jwt','master','Microprofile - JWT built-in scope','openid-connect'),('3b2dfded-98d7-4c9b-bee2-b7c65e98094c','email','sptek-cloud','OpenID Connect built-in scope: email','openid-connect'),('425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','microprofile-jwt','sptek-cloud','Microprofile - JWT built-in scope','openid-connect'),('70b4c562-d08a-42e3-a106-d75e23822b20','roles','sptek-cloud','OpenID Connect scope for add user roles to the access token','openid-connect'),('903b4c80-e49d-4e5c-82ad-291d6fca7046','profile','master','OpenID Connect built-in scope: profile','openid-connect'),('a954b1f3-be84-4af2-9ed4-869079942000','address','sptek-cloud','OpenID Connect built-in scope: address','openid-connect'),('b2d2c539-8634-42a6-be33-f82ee1053d1a','web-origins','sptek-cloud','OpenID Connect scope for add allowed web origins to the access token','openid-connect'),('bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','phone','sptek-cloud','OpenID Connect built-in scope: phone','openid-connect'),('d041a31f-8104-448a-85dc-3fc0c7bb7c88','address','master','OpenID Connect built-in scope: address','openid-connect'),('d05cae1b-d92b-4abe-b7db-a201046a6662','phone','master','OpenID Connect built-in scope: phone','openid-connect'),('d7812a53-c68c-411d-aff2-61896320f4ae','offline_access','sptek-cloud','OpenID Connect built-in scope: offline_access','openid-connect'),('d846f2ff-0580-441a-8cde-3ed2a38d0b23','offline_access','master','OpenID Connect built-in scope: offline_access','openid-connect'),('e0e3ea89-7e94-4170-b3f8-394c82b7d95d','role_list','master','SAML role list','saml'),('e2d2d02e-8d54-4fbf-bed3-f26df4f5d8ab','role_list','sptek-cloud','SAML role list','saml'),('eac1edda-84b5-45fd-9845-4fd7421c8d6b','email','master','OpenID Connect built-in scope: email','openid-connect');
/*!40000 ALTER TABLE `client_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_scope_attributes`
--

DROP TABLE IF EXISTS `client_scope_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_scope_attributes` (
  `SCOPE_ID` varchar(36) NOT NULL,
  `VALUE` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`SCOPE_ID`,`NAME`),
  KEY `IDX_CLSCOPE_ATTRS` (`SCOPE_ID`),
  CONSTRAINT `FK_CL_SCOPE_ATTR_SCOPE` FOREIGN KEY (`SCOPE_ID`) REFERENCES `client_scope` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_scope_attributes`
--

LOCK TABLES `client_scope_attributes` WRITE;
/*!40000 ALTER TABLE `client_scope_attributes` DISABLE KEYS */;
INSERT INTO `client_scope_attributes` VALUES ('04297489-3e8c-4dc8-9ca0-bf26e5ec3f77','${profileScopeConsentText}','consent.screen.text'),('04297489-3e8c-4dc8-9ca0-bf26e5ec3f77','true','display.on.consent.screen'),('04297489-3e8c-4dc8-9ca0-bf26e5ec3f77','true','include.in.token.scope'),('0c5a33f2-15c0-44b9-a387-190556db824d','${rolesScopeConsentText}','consent.screen.text'),('0c5a33f2-15c0-44b9-a387-190556db824d','true','display.on.consent.screen'),('0c5a33f2-15c0-44b9-a387-190556db824d','false','include.in.token.scope'),('138be55b-dbeb-4e9f-951e-ea982198a1bd','','consent.screen.text'),('138be55b-dbeb-4e9f-951e-ea982198a1bd','false','display.on.consent.screen'),('138be55b-dbeb-4e9f-951e-ea982198a1bd','false','include.in.token.scope'),('2f5372a0-234e-45ed-8bd3-e6f4047fe25f','false','display.on.consent.screen'),('2f5372a0-234e-45ed-8bd3-e6f4047fe25f','true','include.in.token.scope'),('3b2dfded-98d7-4c9b-bee2-b7c65e98094c','${emailScopeConsentText}','consent.screen.text'),('3b2dfded-98d7-4c9b-bee2-b7c65e98094c','true','display.on.consent.screen'),('3b2dfded-98d7-4c9b-bee2-b7c65e98094c','true','include.in.token.scope'),('425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','false','display.on.consent.screen'),('425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','true','include.in.token.scope'),('70b4c562-d08a-42e3-a106-d75e23822b20','${rolesScopeConsentText}','consent.screen.text'),('70b4c562-d08a-42e3-a106-d75e23822b20','true','display.on.consent.screen'),('70b4c562-d08a-42e3-a106-d75e23822b20','false','include.in.token.scope'),('903b4c80-e49d-4e5c-82ad-291d6fca7046','${profileScopeConsentText}','consent.screen.text'),('903b4c80-e49d-4e5c-82ad-291d6fca7046','true','display.on.consent.screen'),('903b4c80-e49d-4e5c-82ad-291d6fca7046','true','include.in.token.scope'),('a954b1f3-be84-4af2-9ed4-869079942000','${addressScopeConsentText}','consent.screen.text'),('a954b1f3-be84-4af2-9ed4-869079942000','true','display.on.consent.screen'),('a954b1f3-be84-4af2-9ed4-869079942000','true','include.in.token.scope'),('b2d2c539-8634-42a6-be33-f82ee1053d1a','','consent.screen.text'),('b2d2c539-8634-42a6-be33-f82ee1053d1a','false','display.on.consent.screen'),('b2d2c539-8634-42a6-be33-f82ee1053d1a','false','include.in.token.scope'),('bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','${phoneScopeConsentText}','consent.screen.text'),('bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','true','display.on.consent.screen'),('bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','true','include.in.token.scope'),('d041a31f-8104-448a-85dc-3fc0c7bb7c88','${addressScopeConsentText}','consent.screen.text'),('d041a31f-8104-448a-85dc-3fc0c7bb7c88','true','display.on.consent.screen'),('d041a31f-8104-448a-85dc-3fc0c7bb7c88','true','include.in.token.scope'),('d05cae1b-d92b-4abe-b7db-a201046a6662','${phoneScopeConsentText}','consent.screen.text'),('d05cae1b-d92b-4abe-b7db-a201046a6662','true','display.on.consent.screen'),('d05cae1b-d92b-4abe-b7db-a201046a6662','true','include.in.token.scope'),('d7812a53-c68c-411d-aff2-61896320f4ae','${offlineAccessScopeConsentText}','consent.screen.text'),('d7812a53-c68c-411d-aff2-61896320f4ae','true','display.on.consent.screen'),('d846f2ff-0580-441a-8cde-3ed2a38d0b23','${offlineAccessScopeConsentText}','consent.screen.text'),('d846f2ff-0580-441a-8cde-3ed2a38d0b23','true','display.on.consent.screen'),('e0e3ea89-7e94-4170-b3f8-394c82b7d95d','${samlRoleListScopeConsentText}','consent.screen.text'),('e0e3ea89-7e94-4170-b3f8-394c82b7d95d','true','display.on.consent.screen'),('e2d2d02e-8d54-4fbf-bed3-f26df4f5d8ab','${samlRoleListScopeConsentText}','consent.screen.text'),('e2d2d02e-8d54-4fbf-bed3-f26df4f5d8ab','true','display.on.consent.screen'),('eac1edda-84b5-45fd-9845-4fd7421c8d6b','${emailScopeConsentText}','consent.screen.text'),('eac1edda-84b5-45fd-9845-4fd7421c8d6b','true','display.on.consent.screen'),('eac1edda-84b5-45fd-9845-4fd7421c8d6b','true','include.in.token.scope');
/*!40000 ALTER TABLE `client_scope_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_scope_client`
--

DROP TABLE IF EXISTS `client_scope_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_scope_client` (
  `CLIENT_ID` varchar(255) NOT NULL,
  `SCOPE_ID` varchar(255) NOT NULL,
  `DEFAULT_SCOPE` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`CLIENT_ID`,`SCOPE_ID`),
  KEY `IDX_CLSCOPE_CL` (`CLIENT_ID`),
  KEY `IDX_CL_CLSCOPE` (`SCOPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_scope_client`
--

LOCK TABLES `client_scope_client` WRITE;
/*!40000 ALTER TABLE `client_scope_client` DISABLE KEYS */;
INSERT INTO `client_scope_client` VALUES ('0fcab411-7988-4552-aa1a-bf8d87f77a81','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('0fcab411-7988-4552-aa1a-bf8d87f77a81','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('0fcab411-7988-4552-aa1a-bf8d87f77a81','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('0fcab411-7988-4552-aa1a-bf8d87f77a81','70b4c562-d08a-42e3-a106-d75e23822b20',''),('0fcab411-7988-4552-aa1a-bf8d87f77a81','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('0fcab411-7988-4552-aa1a-bf8d87f77a81','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('0fcab411-7988-4552-aa1a-bf8d87f77a81','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('0fcab411-7988-4552-aa1a-bf8d87f77a81','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('12d7e401-eb29-4e31-866e-1efdfcd53787','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('12d7e401-eb29-4e31-866e-1efdfcd53787','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('12d7e401-eb29-4e31-866e-1efdfcd53787','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('12d7e401-eb29-4e31-866e-1efdfcd53787','70b4c562-d08a-42e3-a106-d75e23822b20',''),('12d7e401-eb29-4e31-866e-1efdfcd53787','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('12d7e401-eb29-4e31-866e-1efdfcd53787','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('12d7e401-eb29-4e31-866e-1efdfcd53787','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('12d7e401-eb29-4e31-866e-1efdfcd53787','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('291690ba-3798-45f3-8b01-f11293902cce','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('291690ba-3798-45f3-8b01-f11293902cce','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('291690ba-3798-45f3-8b01-f11293902cce','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('291690ba-3798-45f3-8b01-f11293902cce','70b4c562-d08a-42e3-a106-d75e23822b20',''),('291690ba-3798-45f3-8b01-f11293902cce','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('291690ba-3798-45f3-8b01-f11293902cce','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('291690ba-3798-45f3-8b01-f11293902cce','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('291690ba-3798-45f3-8b01-f11293902cce','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('2e5f8397-470b-4516-8cc3-aa92e3bda308','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('2e5f8397-470b-4516-8cc3-aa92e3bda308','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','70b4c562-d08a-42e3-a106-d75e23822b20',''),('2e5f8397-470b-4516-8cc3-aa92e3bda308','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('2e5f8397-470b-4516-8cc3-aa92e3bda308','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','0c5a33f2-15c0-44b9-a387-190556db824d',''),('41faae5b-259c-4c66-ad6d-9afada387cee','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('41faae5b-259c-4c66-ad6d-9afada387cee','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('41faae5b-259c-4c66-ad6d-9afada387cee','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('41faae5b-259c-4c66-ad6d-9afada387cee','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','70b4c562-d08a-42e3-a106-d75e23822b20',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','0c5a33f2-15c0-44b9-a387-190556db824d',''),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('5d5a2769-c432-4df2-b1f9-4ac13ac0463b','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','0c5a33f2-15c0-44b9-a387-190556db824d',''),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','70b4c562-d08a-42e3-a106-d75e23822b20',''),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('5fb0c2c8-febe-4a02-9896-dcff29ed10ee','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('775ead1f-db83-43ad-a74d-b856153f9e51','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('775ead1f-db83-43ad-a74d-b856153f9e51','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('775ead1f-db83-43ad-a74d-b856153f9e51','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('775ead1f-db83-43ad-a74d-b856153f9e51','70b4c562-d08a-42e3-a106-d75e23822b20',''),('775ead1f-db83-43ad-a74d-b856153f9e51','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('775ead1f-db83-43ad-a74d-b856153f9e51','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('775ead1f-db83-43ad-a74d-b856153f9e51','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('775ead1f-db83-43ad-a74d-b856153f9e51','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('77d2f780-3bec-4dcc-84d8-1862aa357c84','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('77d2f780-3bec-4dcc-84d8-1862aa357c84','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','70b4c562-d08a-42e3-a106-d75e23822b20',''),('77d2f780-3bec-4dcc-84d8-1862aa357c84','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('77d2f780-3bec-4dcc-84d8-1862aa357c84','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','0c5a33f2-15c0-44b9-a387-190556db824d',''),('955e906d-aa0f-4d49-82f0-404d19080454','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('955e906d-aa0f-4d49-82f0-404d19080454','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('955e906d-aa0f-4d49-82f0-404d19080454','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('955e906d-aa0f-4d49-82f0-404d19080454','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('97e593c5-a1ee-4253-b279-bcddf129f69a','0c5a33f2-15c0-44b9-a387-190556db824d',''),('97e593c5-a1ee-4253-b279-bcddf129f69a','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('97e593c5-a1ee-4253-b279-bcddf129f69a','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('97e593c5-a1ee-4253-b279-bcddf129f69a','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('97e593c5-a1ee-4253-b279-bcddf129f69a','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('97e593c5-a1ee-4253-b279-bcddf129f69a','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('97e593c5-a1ee-4253-b279-bcddf129f69a','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('97e593c5-a1ee-4253-b279-bcddf129f69a','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('99f70571-5263-4b31-bbea-8b85911746b2','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('99f70571-5263-4b31-bbea-8b85911746b2','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('99f70571-5263-4b31-bbea-8b85911746b2','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('99f70571-5263-4b31-bbea-8b85911746b2','70b4c562-d08a-42e3-a106-d75e23822b20',''),('99f70571-5263-4b31-bbea-8b85911746b2','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('99f70571-5263-4b31-bbea-8b85911746b2','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('99f70571-5263-4b31-bbea-8b85911746b2','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('99f70571-5263-4b31-bbea-8b85911746b2','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','70b4c562-d08a-42e3-a106-d75e23822b20',''),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','0c5a33f2-15c0-44b9-a387-190556db824d',''),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('c09cfaf6-b8eb-40f1-847b-03b534d85bbb','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('da1e37ec-b140-4527-afcd-50cd0eceaed7','0c5a33f2-15c0-44b9-a387-190556db824d',''),('da1e37ec-b140-4527-afcd-50cd0eceaed7','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('da1e37ec-b140-4527-afcd-50cd0eceaed7','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('da1e37ec-b140-4527-afcd-50cd0eceaed7','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','70b4c562-d08a-42e3-a106-d75e23822b20',''),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','d7812a53-c68c-411d-aff2-61896320f4ae','\0');
/*!40000 ALTER TABLE `client_scope_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_scope_role_mapping`
--

DROP TABLE IF EXISTS `client_scope_role_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_scope_role_mapping` (
  `SCOPE_ID` varchar(36) NOT NULL,
  `ROLE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`SCOPE_ID`,`ROLE_ID`),
  KEY `IDX_CLSCOPE_ROLE` (`SCOPE_ID`),
  KEY `IDX_ROLE_CLSCOPE` (`ROLE_ID`),
  CONSTRAINT `FK_CL_SCOPE_RM_SCOPE` FOREIGN KEY (`SCOPE_ID`) REFERENCES `client_scope` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_scope_role_mapping`
--

LOCK TABLES `client_scope_role_mapping` WRITE;
/*!40000 ALTER TABLE `client_scope_role_mapping` DISABLE KEYS */;
INSERT INTO `client_scope_role_mapping` VALUES ('d7812a53-c68c-411d-aff2-61896320f4ae','81dd1228-a00c-4bfd-97b0-9512726a9885'),('d846f2ff-0580-441a-8cde-3ed2a38d0b23','a978c2b9-7e6c-4595-8f32-669996c5b871');
/*!40000 ALTER TABLE `client_scope_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_session`
--

DROP TABLE IF EXISTS `client_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_session` (
  `ID` varchar(36) NOT NULL,
  `CLIENT_ID` varchar(36) DEFAULT NULL,
  `REDIRECT_URI` varchar(255) DEFAULT NULL,
  `STATE` varchar(255) DEFAULT NULL,
  `TIMESTAMP` int(11) DEFAULT NULL,
  `SESSION_ID` varchar(36) DEFAULT NULL,
  `AUTH_METHOD` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `AUTH_USER_ID` varchar(36) DEFAULT NULL,
  `CURRENT_ACTION` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_CLIENT_SESSION_SESSION` (`SESSION_ID`),
  CONSTRAINT `FK_B4AO2VCVAT6UKAU74WBWTFQO1` FOREIGN KEY (`SESSION_ID`) REFERENCES `user_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_session`
--

LOCK TABLES `client_session` WRITE;
/*!40000 ALTER TABLE `client_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_session_auth_status`
--

DROP TABLE IF EXISTS `client_session_auth_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_session_auth_status` (
  `AUTHENTICATOR` varchar(36) NOT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `CLIENT_SESSION` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_SESSION`,`AUTHENTICATOR`),
  CONSTRAINT `AUTH_STATUS_CONSTRAINT` FOREIGN KEY (`CLIENT_SESSION`) REFERENCES `client_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_session_auth_status`
--

LOCK TABLES `client_session_auth_status` WRITE;
/*!40000 ALTER TABLE `client_session_auth_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_session_auth_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_session_note`
--

DROP TABLE IF EXISTS `client_session_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_session_note` (
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `CLIENT_SESSION` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_SESSION`,`NAME`),
  CONSTRAINT `FK5EDFB00FF51C2736` FOREIGN KEY (`CLIENT_SESSION`) REFERENCES `client_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_session_note`
--

LOCK TABLES `client_session_note` WRITE;
/*!40000 ALTER TABLE `client_session_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_session_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_session_prot_mapper`
--

DROP TABLE IF EXISTS `client_session_prot_mapper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_session_prot_mapper` (
  `PROTOCOL_MAPPER_ID` varchar(36) NOT NULL,
  `CLIENT_SESSION` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_SESSION`,`PROTOCOL_MAPPER_ID`),
  CONSTRAINT `FK_33A8SGQW18I532811V7O2DK89` FOREIGN KEY (`CLIENT_SESSION`) REFERENCES `client_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_session_prot_mapper`
--

LOCK TABLES `client_session_prot_mapper` WRITE;
/*!40000 ALTER TABLE `client_session_prot_mapper` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_session_prot_mapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_session_role`
--

DROP TABLE IF EXISTS `client_session_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_session_role` (
  `ROLE_ID` varchar(255) NOT NULL,
  `CLIENT_SESSION` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_SESSION`,`ROLE_ID`),
  CONSTRAINT `FK_11B7SGQW18I532811V7O2DV76` FOREIGN KEY (`CLIENT_SESSION`) REFERENCES `client_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_session_role`
--

LOCK TABLES `client_session_role` WRITE;
/*!40000 ALTER TABLE `client_session_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_session_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_user_session_note`
--

DROP TABLE IF EXISTS `client_user_session_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_user_session_note` (
  `NAME` varchar(255) NOT NULL,
  `VALUE` text DEFAULT NULL,
  `CLIENT_SESSION` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_SESSION`,`NAME`),
  CONSTRAINT `FK_CL_USR_SES_NOTE` FOREIGN KEY (`CLIENT_SESSION`) REFERENCES `client_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_user_session_note`
--

LOCK TABLES `client_user_session_note` WRITE;
/*!40000 ALTER TABLE `client_user_session_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_user_session_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component`
--

DROP TABLE IF EXISTS `component`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `PARENT_ID` varchar(36) DEFAULT NULL,
  `PROVIDER_ID` varchar(36) DEFAULT NULL,
  `PROVIDER_TYPE` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `SUB_TYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_COMPONENT_REALM` (`REALM_ID`),
  KEY `IDX_COMPONENT_PROVIDER_TYPE` (`PROVIDER_TYPE`),
  CONSTRAINT `FK_COMPONENT_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component`
--

LOCK TABLES `component` WRITE;
/*!40000 ALTER TABLE `component` DISABLE KEYS */;
INSERT INTO `component` VALUES ('008dd026-f500-49be-9a1b-bb3ce756b838','rsa-generated','sptek-cloud','rsa-generated','org.keycloak.keys.KeyProvider','sptek-cloud',NULL),('0a65fc8c-78b6-4623-bf47-15e4196c4269','aes-generated','sptek-cloud','aes-generated','org.keycloak.keys.KeyProvider','sptek-cloud',NULL),('12be8e8b-5eae-43be-9c39-520ede4ec0cd','hmac-generated','sptek-cloud','hmac-generated','org.keycloak.keys.KeyProvider','sptek-cloud',NULL),('260f9b7b-5cda-47cc-8584-bba40cee01ff','Full Scope Disabled','master','scope','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('343d9213-0aad-4184-adbe-14850336d7f8','Allowed Protocol Mapper Types','master','allowed-protocol-mappers','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('3c1f0ed5-737d-427e-99a9-b0fe8d981ba8','Consent Required','master','consent-required','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('460e967b-4b14-4eb8-ab25-74bfdfb4d0b0','Trusted Hosts','master','trusted-hosts','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('4d0315ae-0b72-4ef0-85f0-ae5841cc6063','Max Clients Limit','master','max-clients','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('65a69851-b3a5-4113-944e-b6f39d8c3ee9','hmac-generated','master','hmac-generated','org.keycloak.keys.KeyProvider','master',NULL),('65b05682-45a4-4262-a61f-bfca99fb7b91','Trusted Hosts','sptek-cloud','trusted-hosts','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('7cf76ec4-9975-4e73-ad09-04c2e3869d6d','rsa-generated','master','rsa-generated','org.keycloak.keys.KeyProvider','master',NULL),('84e7b49e-11d4-42a8-9d14-682177d85cc7','aes-generated','master','aes-generated','org.keycloak.keys.KeyProvider','master',NULL),('88394b39-a66e-41e5-b382-a525d19d713f','Allowed Protocol Mapper Types','master','allowed-protocol-mappers','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','authenticated'),('88c8b15e-13cc-4a0d-9e5d-674aeb2fa645','Consent Required','sptek-cloud','consent-required','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('8e00c44e-3e86-4828-a1b7-0eddb7dddd94','Full Scope Disabled','sptek-cloud','scope','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('94489c6c-8781-4ece-be05-db1362452361','rsa-enc-generated','master','rsa-enc-generated','org.keycloak.keys.KeyProvider','master',NULL),('a5489f4b-c3c2-4434-a922-20c2a3375f46','rsa-enc-generated','sptek-cloud','rsa-enc-generated','org.keycloak.keys.KeyProvider','sptek-cloud',NULL),('ac92072c-45ec-4886-aa55-7b58daeb21dc','Allowed Client Scopes','sptek-cloud','allowed-client-templates','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','authenticated'),('b7b65dc9-d4ec-4bae-865e-4da55283a32c',NULL,'sptek-cloud','declarative-user-profile','org.keycloak.userprofile.UserProfileProvider','sptek-cloud',NULL),('ba2738fa-a187-421d-9047-3f496a6e55b2','Allowed Protocol Mapper Types','sptek-cloud','allowed-protocol-mappers','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('bbfaed54-e32d-4fbf-8559-2781f26bf331','Allowed Protocol Mapper Types','sptek-cloud','allowed-protocol-mappers','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','authenticated'),('cb9b0663-a04d-4674-afb0-0c6721cfa942','Max Clients Limit','sptek-cloud','max-clients','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('dd9bce82-6d46-4669-977a-2079c2419f24','Allowed Client Scopes','sptek-cloud','allowed-client-templates','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','sptek-cloud','anonymous'),('e287dfad-a6f7-42dc-bcca-ee6cb792d528','Allowed Client Scopes','master','allowed-client-templates','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','anonymous'),('f625e943-5668-40aa-9d44-b83b20758191','Allowed Client Scopes','master','allowed-client-templates','org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy','master','authenticated');
/*!40000 ALTER TABLE `component` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `component_config`
--

DROP TABLE IF EXISTS `component_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `component_config` (
  `ID` varchar(36) NOT NULL,
  `COMPONENT_ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(4000) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_COMPO_CONFIG_COMPO` (`COMPONENT_ID`),
  CONSTRAINT `FK_COMPONENT_CONFIG` FOREIGN KEY (`COMPONENT_ID`) REFERENCES `component` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `component_config`
--

LOCK TABLES `component_config` WRITE;
/*!40000 ALTER TABLE `component_config` DISABLE KEYS */;
INSERT INTO `component_config` VALUES ('100ab828-c20a-4134-94ea-15c5c51ba154','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','saml-user-property-mapper'),('13f920e1-a0ea-4bd0-b486-4f241e3dd01f','a5489f4b-c3c2-4434-a922-20c2a3375f46','priority','100'),('146d843f-5286-498e-be02-dfd46a21ee7f','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','oidc-sha256-pairwise-sub-mapper'),('1cc7d2fb-f4e7-4ca5-8c4c-8d5cb6b02dd1','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','saml-role-list-mapper'),('1e7da09d-904e-4dbc-8854-01b7a64dc76c','65a69851-b3a5-4113-944e-b6f39d8c3ee9','priority','100'),('21db2514-065b-4518-be4b-9d6397792462','a5489f4b-c3c2-4434-a922-20c2a3375f46','privateKey','MIIEoAIBAAKCAQEAiIPrMOo05zqnMb2ArgcMMYVkASwN60y+yCelIelZFwNC8zHJXvRql+XSOHFNUfJ0pAVZfSRaMxLSRk6dIba3lnwgD+gRE6NTH9n77ON9Q0ugGxQ3jQiRrVtTqgcEXVyMPD8w3bSfjzO3s7z0z9GVdCdgrVpleBP1E/bqvRDmC//wvbjhuQ9/j44Ui2h2NjF4TeX5fTesAG8KLsy0wcfKM42aojL1kf66+NDqSDFQlq8mBZCiZJ23pnQ/RR+Id3Qzw5urHhv8k83v0AzJM1eYwLYuIMjX3VGrX72pwYDDaHViu6UyTgAI5U72kD+KBVRn+SfnWteVPqu+bGaeIAyoBwIDAQABAoIBAEILYEtn43RCogSkY47OBg4nNHyEra86j8oRtAg19k312/wACCYSJUebfhqkZf3RSLDZjbPHVIQFBQnSmp5m4WbCYliPs0NG7r4oHYM7g6x899Zh74YhsSinWFCX2Hq6fXXgNGnuKouDP8qLDf1ErgNl0ZXMd+6h6R1hhas2c7U1xJhHHE7JcnpcVLQ+4enoUrOemhrtTwtvuUQq/ck60mWsV1dOtgzxhAH0FsBUwmh7CZqeKPcC++hBLVzTBz4cdNwVM8MpsKj64ZoMhYXCQRRM3uFFCu/NPdom6QQ8aARhwiB5+870DgdO40vr0vsNQuaTJ+Ri+mB+y4UgZRhdp+ECgYEA2E3uMhGqKeYOHd9FsTFlyHnMAJr99EtJA3WS7qcu4pi1JMnfzE75gDB1FIMsIXLgEQQQpWobsqoHITBHJAsQBwonosQ/LZ7Zj0S2kcoR72eQqh5aPlNPZb0VicNLeXeXSv4mnQXt7yYQRFkqWYxsCqVMmuCOkTj3ZhD9aGjzsm8CgYEAoZF2pTTsDNnG5QzFXb5axcZM26Bs9ONDFm/QKfatrKzs/iweR80XqdK1hHFsk33v+cANc4Oj5C3AMBIYzrwqqZyMFCsume+1yAgZGS3hV1OC3B0kXTO16MFAcWuBQ8xTW6iIco4TMNQFPELQRulUo8SexVoUDx7Wh/aMimhBT+kCgYBYNKdOqZ9viI33JoY/9Lnh37EOCiZHSbAThg2N3oUjCEzOHLhzkzzxRlFoNPl5BmRS7ApxuexjlixkH1DTZON7S2vQVyDAVMe5QbcFipZ3vw7iIJCCW+zDuzCeRhJwDRGYFj3eyH03iZkWlhnL/JROXwv3GF1q4QUaDRGZUzsVZwJ/OhAMk1JxgizBTY9JXmcKOAkLleIRRfBmK6oDJRGBWIzMnos8VpFd76nJkc8xNdAMbbSXIOwt+dJ3Ps9CGwmES2/250iddrSunzRJ0MKsOLXKUVAnwVbUCNFqV1hLHBJR4p6uanykaOfI+d8I+nLf9/qxFDtK+iQa9MGlwBe3YQKBgHIWYB3yLTjhzuZHYHTr/0LIqwf6y/Qdcq5csvhzO7OTATjCvc2UlycQoXZTzQUCD4WTFw0Y0F5Z0eu2QLopVgSc87t4WaBuEWOeku9a7sjKxRlTIWwXgwmD7NsU3WfVP32eqwKKRy1MISrx4wnB8FsHfMDWNP38C119NFOtep5w'),('2255393d-08a1-480b-bbde-f33af8beb2a8','7cf76ec4-9975-4e73-ad09-04c2e3869d6d','keyUse','SIG'),('23d4ada2-1b84-4cb9-9f6f-9d47b9b23f80','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','oidc-full-name-mapper'),('246eeb31-d947-4fc6-a254-d3f20739e0d7','94489c6c-8781-4ece-be05-db1362452361','algorithm','RSA-OAEP'),('2922396a-ac82-47eb-b8ff-8724e95dced5','cb9b0663-a04d-4674-afb0-0c6721cfa942','max-clients','200'),('2972e486-a56b-4f0a-ab48-3c7fde67c6c6','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','oidc-usermodel-property-mapper'),('3179517f-81d5-47d6-879c-ef97a59cb9a2','12be8e8b-5eae-43be-9c39-520ede4ec0cd','kid','69ecc266-8a36-4e5c-bf9b-1ca2c1c52b29'),('3844d307-a20f-4c0d-bf65-cf799cae7680','12be8e8b-5eae-43be-9c39-520ede4ec0cd','priority','100'),('3b76ebdb-94f3-4449-8397-c9d6d5fbea22','dd9bce82-6d46-4669-977a-2079c2419f24','allow-default-scopes','true'),('3c22701f-7a65-4e79-bada-8d905542f444','7cf76ec4-9975-4e73-ad09-04c2e3869d6d','privateKey','MIIEogIBAAKCAQEAqWdB215dTeIa8Wz7RRF9LdHJI3DsWM8ZbuvjnzCh6+gi8vE0NWhpG+5rBfMdsNlOaYPnYEIBem45WC+x+uxyA+m2WU3sL0Tg09WMUBSrqRSKYCM/r+lSW82i1DruUw0OqXn2MtIViMHqv2oA7+GqiPdl0XoWh0MvHx1CVSjiJLzTjr+vObz6PTi5UL0ASWMexQSNDPiNs+PXFjp36S1zNRBNMb3dbzRONgWKqfdrfhLU6LeOTTleNq6kEJxh4d0V5bvYR5GdVvjC1pK2L71FLN3jZY4COpRuWjj76gAum78HirwQyck43OV8lltGwHUxjjBawR89ZRo/XGlUmioY5QIDAQABAoIBAD8d69myJQ15qxQ8yLoJNBy4RzrfPhAwyYcceKi9U/czfKIL4KnthwwTHukOlgdoT+Dcdn7U2PwmyVLkImodYrW2hGxtrgxGm4YR0L+dk//hWVVMjNQcGMV/rqVod5eWC9p1SJfS0pcruS0sb8Dw0btKgjM0S+SF0a7EFgzouKl62/l1ldtLjOA7pMUJCEZZ4ufdEwkZTJMiH2IW3RQKSC4XsDaghs8LiFGaIB+gBP4FGC9Q7njC9IER+MzE3CZNq7lFyd6WYmkOh9i5JVeAHm4czAG3PPyuenMFKDdcy92bXhffsike+XELP7yFk3J8tGZ7JJPOEXMx/j8wKtFBeIECgYEA9frwL9DWMILvamCKOinFjn5Vxhxbtciqoo0YP0U29p5JNBa6jg+zy7gORDpcIpFVfwNMpn7i6cHqGc5QhR0qXrWMHDGDbPGl8Fg/Lh3hJwEX5AcedoJ1sFb+zpRGRhZMpRX8t4bMp7GMJQprGJNHxf5TK9LpxYESnE5GiVk1StUCgYEAsE3IFQd4AvO2b99CFoA2yRo1on7WBudmYtohibxbAXBtqm6wYxEOfd3+zjpuDNRlCVuv6fKud0P9qJzx9lu2Z9saVdIS9ZYFy/rt9g/U4nOI1QzAdFcwox2hWSx+AqoQsnp5VfCsvEeV+UFcm2C8ihRC+Pg0MnO1mC5sKVKmfdECgYA81WKxcKSIOtmbJWUKHWqOYxAzfq/vtUD+Eob8ntGJDHxuNXDN0XRV3Z8poIVIx0itsNM0Gg1kYmDTIQMo6J2h7qRIFLa8KscvOaPmc91LGCogauJNzNXuVpWN4X2/t6Qc7+MgQGY80twmDP2Phl0pykwnenH2NoWzwmgPb9COQQKBgBBgqV2KvBNKIIBz9OeyMWStEp5tueQaSuCT8tmgrbOMhiN29Nu/EeGPZcQymVaHxHsCEW1iJeLeZgnrym5gRqmPKPsf8LLvpMo2HTTmc5g1s1hXPumPz+zXK83k3C/TLInn5q2dFe7Hl573+2pTqjAJgc8u/2pQZWYlqdaM3vqBAoGAMHZG+9EphAPkttHmo2vP0xr/O7DPP01EFjdy7VSYYCLO3hVlu+82Q55jQJcIrWev53xPPARezDvHV9CkueI2FUDciQa6h0DgcTp1MJCI22Q+84H0B2SnK7kZn/FH5GWBIFt/8Tu6ImK87bsZnXhRmgRivb9G3Gw0XY3uQc4FsOE='),('3c3387e6-7d44-4c25-97f3-307507bda7fc','65a69851-b3a5-4113-944e-b6f39d8c3ee9','algorithm','HS256'),('3e8395f9-51aa-4849-9790-49f6ca496919','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','oidc-usermodel-attribute-mapper'),('3e97a3c2-7719-4151-b68f-51f036cdc694','65b05682-45a4-4262-a61f-bfca99fb7b91','host-sending-registration-request-must-match','true'),('3f8cb604-1954-4dee-91aa-b01ffd69d6ef','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','oidc-address-mapper'),('43626314-419a-4c53-adc0-dcaee0a93b02','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','oidc-full-name-mapper'),('462d0c3b-05c4-444c-8184-9701dcdbc54a','84e7b49e-11d4-42a8-9d14-682177d85cc7','priority','100'),('4ba25c0e-7315-47d5-9c00-88a82ac25fef','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','oidc-sha256-pairwise-sub-mapper'),('4dc6573d-ae88-41bd-9289-f79168dca784','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','oidc-address-mapper'),('50190f7d-d03c-4d25-a9bb-6f5f837a3a9e','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','oidc-usermodel-attribute-mapper'),('5098c401-c29a-47f7-b506-9d6d1d5184f0','008dd026-f500-49be-9a1b-bb3ce756b838','certificate','MIICpTCCAY0CBgF+3Q0ZNTANBgkqhkiG9w0BAQsFADAWMRQwEgYDVQQDDAtzcHRlay1jbG91ZDAeFw0yMjAyMDkwNTUzNTBaFw0zMjAyMDkwNTU1MzBaMBYxFDASBgNVBAMMC3NwdGVrLWNsb3VkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzPfGeo+FA4uoO/+pAzWFfO2sHtgeVas2O3LOvIjjay394n3uJzwPtLE64rbVyZ0++sD7WutxqSxMlFxWz521HdnElMiLz2xhvAqCUnfg3wBm4PkWtnTMRtYjnVM0GfpXoEEmeyOFmTQa/oKtUssM8+ObSqjujXlIN6SHMXJgJDvuo0Kpesse9xLe78tyt8TEsJZda/TNGRVSIMbcrEn+aJC958O7yYHmgX1SEn5ByCB2cUQRAKoyYQvJpCUtrPAKgg0SaftXHLjb+h4lAVzZKlUiIskCNCUuKQyJnLsbG6UnvIO06XSax6LOl62zren6ywUyjkt+j18YvtdrsL/l4wIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQBAXfGtvwrgDrA49eJ8AA0jUwroKdrDMz70kF8YMVOSNwAbNbRWlTRS21vNKPkXq3VVUtkQeIDXot6aBWeikf3qe/YyiSxxTDgoNwbM/kSghyhxJTzNswwXwgbr7DPT7S+lqrpMHVxet24fwRC/n6rCmCLFyzYqWnSTlbE934ErckVnLtDXPNbAowfmgx+lNaj40ZMtn71+5wFQ1Rfb1pIBOAbA09C2cqa0mjj8nFqQCr+c9h2oKoyuyDn4DPbleRHlr4bwRwQpr8D6wqsGX8M803oSV/QuVuTb96JJ69faPZOstSE4qhY1V9uFH7vSk8uqjYreUF/4OgcZ7GzSF3JA'),('50d78db2-71d3-4c8e-be4a-abb3e11b039e','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','oidc-usermodel-attribute-mapper'),('568a493b-f039-4f9c-ac5e-3a6492b3270a','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','saml-user-attribute-mapper'),('5dd3e16e-557b-4cc0-b564-8e18e109d700','008dd026-f500-49be-9a1b-bb3ce756b838','priority','100'),('5fb7fae5-1877-463e-acc1-326aa5873882','008dd026-f500-49be-9a1b-bb3ce756b838','keyUse','SIG'),('6169ec91-bf15-40c5-9be9-c0ad08265b34','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','oidc-full-name-mapper'),('63e0b2da-5370-4c7f-b6a4-a12ec401193b','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','oidc-address-mapper'),('678935f0-0407-4d7e-ba3e-52a8df4781cb','460e967b-4b14-4eb8-ab25-74bfdfb4d0b0','host-sending-registration-request-must-match','true'),('67a5a7cf-0f65-44fc-ad26-ddfeeabadc40','a5489f4b-c3c2-4434-a922-20c2a3375f46','keyUse','ENC'),('6b7bcc27-923f-44e5-886c-c5506e1c4741','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','saml-user-attribute-mapper'),('6bc0134d-45dc-4898-9940-09fc24f47db7','94489c6c-8781-4ece-be05-db1362452361','privateKey','MIIEogIBAAKCAQEAgedgBr6E5+F182O359iiWfnQVl+Q9YnSTZ9bvpDJa8/Q1D4FWstqLR070sqiu+DQCktGPiIih5u3CSreo/jXl6JKvlRdYiKmIY3HNfCa6eXURfDaPetqmRZSB8ynSjiYpQp3mVAyMukWValPUa7pU2uee3HwAakxuDCnhfR1BQkk5m/Q341Xyzm1DzS2/at76braLQSEWXndMIEi825CGd5SYDnlify2uMIi3PUoTPueor66njmgFmbl9b+kG4rpvjo5MFdlGWIJBnrbk8TAbMaYt5IPxqp7PxYmc4E/6pE0THQWeSw0H6RruTLXahOh/EjrFVjoJU4LIGwC6xFJqwIDAQABAoIBAAnKMR9w/j0nbacxDawIVHvpar0HqLoIb/PvnY9oA/X/DGLiZa7pA5Bw0pBZOxhjoeoUsC+AjQ+hOZMUSwEagMzpqy5a10rNB64hrDCxeEtGSdAXjar68ealmvsBfl4Gk+JkPo0sUQwrKsa74sFqKCVkR+ZPuOG3KYsTB5z9xk/huxIV5b0HCCfj3KDqdgAsvoUnmSYjdsAdahkcc4LqYUSammpYx1zUdVP2zKLa/W3OmTq6XousONbcBjZFtMyBXKMX4S8aLSzoBVjXunbiIWfd5mHEY895zXfKvovUlbT/tx3oSRE9iSeVyJnH5OLO9rxg59N7E/FyF0Wx9OW3b6kCgYEA7fLvDAnipl5934S3U0H+hEW0XP07IifTakOLXTGyXiBiOxF32fF5x/SOHgjPEgY31z1LxhwSW7k9jGrqkYcC1CGsHYhZALGCkrzfxTIgyuiYzph9fW3GJwxN6U/YDa3anhcPa5Nj84NIi0y0b99kEzDq5Wt/Zo3dBG0L0iniKI8CgYEAi8IpBp0hLB2wP6dAiu/vlnXWKl9JACYihkiffb4cfxwQR1vci/Pz+Ip0kiQ/x/ff3sF45tKOsqiwNr9XvP539QmIIghU1Y0umNCgDCODtE+1bALX8yANR4U8iHlH+5Na46po60xGftrserMQVG5XXNyyp1xC9ozQaY5bB0D8QyUCgYA8JLxhSZpdPOMJ/SytQbAIVOoK2wL4ViKs6FewfaA9BGhvz7GAh/WlId5OHyHfx2yT3e1qLKxDPOYeuOODVyHS5pPvfwgoEbYUSuMwn8QEJZiCF68YZgfA1O5Ye5L7mib48JE4Oq/33AYZF5E/+bH8289zn6E7Fb54oOts+qMsZwKBgEqOj/hZRuMUC3xJq2LzUStBPblpD7amoXBlucAI9YbR39VRqjx3LkX/BuUXtXbmQ2XAbNEmrr2cxDTQquvGDRSJuzf8ndU29g7L7TXMlGxSwYQXmEC5bpwi9gIQQVhyaeBefo5YKJtwCwwCDsob+zss+ZnS3F5PDLfjG6ipAGqJAoGAT6LY/mOKf+ygPv8GdgEWQn0ll7NpKCD+VvuO6EBN9BKDBfkWpTrp2ztjpHhpIQlPjvjAA91HKz5ZwBn46arHJYr5oa32/x5s5xYnRY6LLVz7+3TEjIE7m0UAMbddv9ytn8q58/0SgusPoZS/C4odJ9WTlEQql1SpnF0uVLNtNlA='),('71cef8c5-170e-4f0f-9762-271de273246c','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','saml-user-property-mapper'),('71fa09c9-c69f-4c82-a21e-5dfdfd707e6b','12be8e8b-5eae-43be-9c39-520ede4ec0cd','algorithm','HS256'),('743a5816-0afd-47b0-86dd-de2b38f36f7d','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','saml-user-attribute-mapper'),('7ca004a5-00f2-45e2-95c5-fc4cbcf7f641','7cf76ec4-9975-4e73-ad09-04c2e3869d6d','certificate','MIICmzCCAYMCBgF+3QCFwzANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZtYXN0ZXIwHhcNMjIwMjA5MDU0MDA2WhcNMzIwMjA5MDU0MTQ2WjARMQ8wDQYDVQQDDAZtYXN0ZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCpZ0HbXl1N4hrxbPtFEX0t0ckjcOxYzxlu6+OfMKHr6CLy8TQ1aGkb7msF8x2w2U5pg+dgQgF6bjlYL7H67HID6bZZTewvRODT1YxQFKupFIpgIz+v6VJbzaLUOu5TDQ6pefYy0hWIweq/agDv4aqI92XRehaHQy8fHUJVKOIkvNOOv685vPo9OLlQvQBJYx7FBI0M+I2z49cWOnfpLXM1EE0xvd1vNE42BYqp92t+EtTot45NOV42rqQQnGHh3RXlu9hHkZ1W+MLWkrYvvUUs3eNljgI6lG5aOPvqAC6bvweKvBDJyTjc5XyWW0bAdTGOMFrBHz1lGj9caVSaKhjlAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAGSCd1cDdB9wf2bzAy08OQG395FnbpdQUbmOkxI8U5ZoagmHdhROs60OU4ElKPY4BUXqv/wZInNm/hUnnQSO4tVtNuT4MZ3w4a95uSf30P0fW51v6xSlUl4MM5oUnKd9kRJERKiGzTzaEUBQpHF4UAwrRHijQ1xp7/fG238wQYTcFX0R3fejto8aocqzvg/mTydQW605AD653tLTfyHD/yQbiAnqEoSwPOVQ6vPCaHsTbWFa2y1fmFNeQ92uEUhtGBw9OZLVg7ODtED/Jtv9uaUYZaVizmLXMaHkrhwYVtmgsB25yMnHwAccX9DIRIum95JQAq7Bpz67tzw2NpCYo2s='),('809156e3-67c8-481f-8163-1e4915985612','84e7b49e-11d4-42a8-9d14-682177d85cc7','kid','bc94e850-41a9-4a12-9fa0-ef4b67fabaf1'),('8a427d8b-42fd-474d-8141-9038a6aa4ed3','0a65fc8c-78b6-4623-bf47-15e4196c4269','secret','kIeihhNc1sglWSQXHSH5oA'),('90575ea2-9c21-4860-8f5a-4fb109704ff6','f625e943-5668-40aa-9d44-b83b20758191','allow-default-scopes','true'),('910a23bd-9319-4622-b660-d7e905004f4b','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','oidc-usermodel-property-mapper'),('97cbd370-0322-4c2a-8626-9d72121ca130','ac92072c-45ec-4886-aa55-7b58daeb21dc','allow-default-scopes','true'),('98cf9d64-6334-40b4-856c-851f786d62f4','a5489f4b-c3c2-4434-a922-20c2a3375f46','algorithm','RSA-OAEP'),('9a270e2b-919e-435e-8f8f-d7daa4cae3ae','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','saml-role-list-mapper'),('9c3ec794-6b22-42d5-b398-0c050cc0a551','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','oidc-sha256-pairwise-sub-mapper'),('9e859124-e79b-4720-bebd-9cb8f84a65c7','a5489f4b-c3c2-4434-a922-20c2a3375f46','certificate','MIICpTCCAY0CBgF+3Q0Z+TANBgkqhkiG9w0BAQsFADAWMRQwEgYDVQQDDAtzcHRlay1jbG91ZDAeFw0yMjAyMDkwNTUzNTBaFw0zMjAyMDkwNTU1MzBaMBYxFDASBgNVBAMMC3NwdGVrLWNsb3VkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiIPrMOo05zqnMb2ArgcMMYVkASwN60y+yCelIelZFwNC8zHJXvRql+XSOHFNUfJ0pAVZfSRaMxLSRk6dIba3lnwgD+gRE6NTH9n77ON9Q0ugGxQ3jQiRrVtTqgcEXVyMPD8w3bSfjzO3s7z0z9GVdCdgrVpleBP1E/bqvRDmC//wvbjhuQ9/j44Ui2h2NjF4TeX5fTesAG8KLsy0wcfKM42aojL1kf66+NDqSDFQlq8mBZCiZJ23pnQ/RR+Id3Qzw5urHhv8k83v0AzJM1eYwLYuIMjX3VGrX72pwYDDaHViu6UyTgAI5U72kD+KBVRn+SfnWteVPqu+bGaeIAyoBwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQAtSbZG7SOUwQu07VQVg5e8aItxsydMUWhFn2Yhb1BUtPAURk6OUCqe3jtImqlrMHfWD6nzBqqDjSQuu9QPlLT6r5dYMo6XJWYfw7hegcnW9UqjFb/ZRo0NNIO9UYIljCVS7OugefBOmVBcauYE5KA+rh53Bb4aZQnXkjlyq3IMwTC2Hsx0rCE0d2OkHy/f4sV3kYJbUwg7J210qkerhZuhALnvGHhuiUuShuHyKKrjkfcKcYLnRn/rWEalzjUeGDlF3VYopkzKr+uF3ArQVcxcWp/Lb+S5Xb38v5I6UUrbgqfP/kXNnXwcaPopwaVo8ZF5QfTF7lR5g3wTIveuGUOE'),('9e8ac289-9ee2-4666-8f85-d5f675526d4d','7cf76ec4-9975-4e73-ad09-04c2e3869d6d','priority','100'),('a2f9c934-1ff2-42f8-a43f-6b9050d89aad','0a65fc8c-78b6-4623-bf47-15e4196c4269','kid','8486a59b-7c09-4876-8ffe-f91083f73956'),('a32b9f65-f045-4b8b-94f6-eafa174563d9','65b05682-45a4-4262-a61f-bfca99fb7b91','client-uris-must-match','true'),('a406ea02-1c14-43f7-8d54-562ffa6bc140','65a69851-b3a5-4113-944e-b6f39d8c3ee9','secret','thSrVSHwKnU9lo6LcIjBv3ujcZvZ8w7LjG982Q7FtNBYVvuKRETSnwm013QDyhMLVKgOAghgNq7zdJwpQ7-r5A'),('a444f5bc-0643-46ef-a2a2-593896e4789f','94489c6c-8781-4ece-be05-db1362452361','priority','100'),('ae1c6aac-c8c4-49d3-b5d1-bf24a12ee806','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','oidc-usermodel-attribute-mapper'),('b0135d88-b208-46ae-bb7f-a9db623e4348','12be8e8b-5eae-43be-9c39-520ede4ec0cd','secret','LVVvudvfCtgrIbTcPK4E3-hPEGbAXiIWmAJ9Ogl-tiC0ksX95-5YMybEE73su5A8rJPJ5h5DM0wCK7y-unw7LA'),('b8a0e822-1d7f-430e-a903-08879f1e9e62','e287dfad-a6f7-42dc-bcca-ee6cb792d528','allow-default-scopes','true'),('bb078772-953d-4395-9154-23399492319b','94489c6c-8781-4ece-be05-db1362452361','certificate','MIICmzCCAYMCBgF+3QCGOzANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZtYXN0ZXIwHhcNMjIwMjA5MDU0MDA2WhcNMzIwMjA5MDU0MTQ2WjARMQ8wDQYDVQQDDAZtYXN0ZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCB52AGvoTn4XXzY7fn2KJZ+dBWX5D1idJNn1u+kMlrz9DUPgVay2otHTvSyqK74NAKS0Y+IiKHm7cJKt6j+NeXokq+VF1iIqYhjcc18Jrp5dRF8No962qZFlIHzKdKOJilCneZUDIy6RZVqU9RrulTa557cfABqTG4MKeF9HUFCSTmb9DfjVfLObUPNLb9q3vputotBIRZed0wgSLzbkIZ3lJgOeWJ/La4wiLc9ShM+56ivrqeOaAWZuX1v6Qbium+OjkwV2UZYgkGetuTxMBsxpi3kg/Gqns/FiZzgT/qkTRMdBZ5LDQfpGu5MtdqE6H8SOsVWOglTgsgbALrEUmrAgMBAAEwDQYJKoZIhvcNAQELBQADggEBADp8KZLIg9c2bXWhLA6LJ9PfNBrWLBVZyH5/rvGv4MNcgSy0JkUbchxtU3s3KAUdIEi3wearFOz0AusgA2KQDiTQiHtY0E44AQ+UX9+cuH0mPwoo6MNKWr6J7IseQShlRCgLIIgE5Eu6t2r+PF7YicrwSGQgp3l0mRh7hnzBj04JEI3cw6pISKTQ7X3JyzEO8c363hp7uMPCPV5AOhlkTVQ/XtePHhjzad73xVbIdhTxNZPwG/xsxD0CZ5WrprepX393C+otKnJOld28PlwLbwzYRwivrNG2GEI2ppL9vxfFGGdIDgtq36zDXPKlj+J1RtAZc/IuiuALrMIl5C2lOY0='),('bff287a2-1892-4e81-819f-6c507f5965c4','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','saml-role-list-mapper'),('c55a68fc-6bf3-42b9-aead-ebd5fcb42894','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','oidc-full-name-mapper'),('c566bc1e-7ba3-4544-a04c-c1f22576e50c','65a69851-b3a5-4113-944e-b6f39d8c3ee9','kid','747ebc89-91a1-4e32-9de3-1c4d43d56b6b'),('cb9e4f25-5222-4dc1-a551-4df7b797c017','88394b39-a66e-41e5-b382-a525d19d713f','allowed-protocol-mapper-types','saml-role-list-mapper'),('d27f0c40-39ea-418c-bc88-2ad7921edcce','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','saml-user-property-mapper'),('d2e435cf-717f-4a96-a084-ea4a9055f8b4','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','saml-user-property-mapper'),('da791c37-dc5c-4793-a4b8-4d2097fe1ab7','0a65fc8c-78b6-4623-bf47-15e4196c4269','priority','100'),('dd5f5cef-02b5-4dd3-a5c3-38ed6c801eb5','460e967b-4b14-4eb8-ab25-74bfdfb4d0b0','client-uris-must-match','true'),('e1255868-2ed7-47d5-9512-fbb3cb21bcc6','343d9213-0aad-4184-adbe-14850336d7f8','allowed-protocol-mapper-types','oidc-sha256-pairwise-sub-mapper'),('e6063565-1eb8-4ab1-8497-f1ac9f2655a2','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','saml-user-attribute-mapper'),('f1297af5-f8cb-44e5-9a1f-d33eb2c4d946','ba2738fa-a187-421d-9047-3f496a6e55b2','allowed-protocol-mapper-types','oidc-usermodel-property-mapper'),('f315dd7b-56ef-4d34-8933-af07286b4315','84e7b49e-11d4-42a8-9d14-682177d85cc7','secret','UAbe4vD_ca40cbfWAcTJPw'),('f4068395-a303-4491-ab54-46111cf81e9a','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','oidc-address-mapper'),('f7886af4-ab9d-49f2-b872-f48280f98301','4d0315ae-0b72-4ef0-85f0-ae5841cc6063','max-clients','200'),('f8e2c76d-7ae8-450e-a438-14c5cdd285ac','bbfaed54-e32d-4fbf-8559-2781f26bf331','allowed-protocol-mapper-types','oidc-usermodel-property-mapper'),('f90b2104-3cc7-4aa8-9e83-27c8913785d2','008dd026-f500-49be-9a1b-bb3ce756b838','privateKey','MIIEpAIBAAKCAQEAzPfGeo+FA4uoO/+pAzWFfO2sHtgeVas2O3LOvIjjay394n3uJzwPtLE64rbVyZ0++sD7WutxqSxMlFxWz521HdnElMiLz2xhvAqCUnfg3wBm4PkWtnTMRtYjnVM0GfpXoEEmeyOFmTQa/oKtUssM8+ObSqjujXlIN6SHMXJgJDvuo0Kpesse9xLe78tyt8TEsJZda/TNGRVSIMbcrEn+aJC958O7yYHmgX1SEn5ByCB2cUQRAKoyYQvJpCUtrPAKgg0SaftXHLjb+h4lAVzZKlUiIskCNCUuKQyJnLsbG6UnvIO06XSax6LOl62zren6ywUyjkt+j18YvtdrsL/l4wIDAQABAoIBAQC9kJscVwgqE0vw0a66rRFcTWRW0oKbsNFjgfb6H5BVXB/nDZjo6H+OGivf6kEnSispNrpBiGeCktnwjMYtFiqgBPvurtu/ZvmtM/r70qtDE9gSUc1TygnzqbEenAkfzYylRhwU3vF8k9gzcyBh5jGAjwzcrAiVB4pnC9+NN777Qlrh2mbHoz0t0KtVtYn9Bey1h+yRS8P+AKBpAcyvyXJksyTYX6RZrzFAb/Axuv6e3SU8tlM4HmI6gg2nhb80eisRez23A3+YHzf4fkKs5KVOI1g5mU/yv4D51Tnkk5uVaX1Y4W8i8M0VzdaiBGfHGiEDed6Ndm5xj6z8cWSDBm9BAoGBAOeYZX4y9NWl/3buZjR/T3or32am2f3+FYKDwAnNA3TEjpYtW4HKItqa4nvD5T705j7SHP5zWgVpVxk1nZO2r21TVyaXd5sz1PGIBpgUkobT9d/Q5J14Bke/GuNyUQldAdjrHocvCea2Xxq6+IvBP8QHqae0XBfkJhVpKIdAU2ZJAoGBAOKRETNy6LtIEDVYnga2OFQ302wLmnlgR8VNChxoE7GAkxF/SrEyzLH9nCKlQJFSMHxXDKZmpvO1epJrtftU3y+sv+Rpv2TSLCOEF4l/vX1SXYrx12DZUQKFOBDc3OqYnwFL87MBjyT2GlfoJjA9EuyxCC1YDBrdK5rks12E53rLAoGAC3s5pNz/2Eunc4z0Eiwxk3vzUlJ5QidDh7niXloEjthjvTw4aiHMQowlKD+Y8NsRQxIFj/PGBbioMb6/th/7aG8WRfqCWnZiWysEMKvsa7S8XsErZEccAJiPy7DSJaWvvsJBPDBvzDlEU73rLnqBH8XFemPtoN2VqcAR20qsF8kCgYEA1ijB1Zv8mWqYx5Qoa7e6rwDF/A9LyQdVUA5uTEOkudgLLDLia3TVQDm1aJpD2hmSczXlYzUNAri8T8M4lYW4Idxs1n+OxsJTro6hoPr6JofAnCHVsFo61OMQCAB9YdM6GYOtq7sBj7KBXC64SkiwpsMAM7xSVOJS4WrkkLWcCNkCgYA2Q/pZhiOy0hgUobwnWSnWBk30fmL8DZf2UIgm/hh0PH3T42RVrwJaJ2UG590MKlJ0uxo85dtkYIBhpT3fzlhRvVQIMssEdWTB4RiWhseCZZJV3uq2RtBTGxIw3iMf6YptiQT+dcnmeFr5PbPzI0F+xxhTsQPI+/+msTWQ7WFzoQ=='),('fae84526-352e-419b-9648-d8e56ffedd3c','94489c6c-8781-4ece-be05-db1362452361','keyUse','ENC');
/*!40000 ALTER TABLE `component_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `composite_role`
--

DROP TABLE IF EXISTS `composite_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `composite_role` (
  `COMPOSITE` varchar(36) NOT NULL,
  `CHILD_ROLE` varchar(36) NOT NULL,
  PRIMARY KEY (`COMPOSITE`,`CHILD_ROLE`),
  KEY `IDX_COMPOSITE` (`COMPOSITE`),
  KEY `IDX_COMPOSITE_CHILD` (`CHILD_ROLE`),
  CONSTRAINT `FK_A63WVEKFTU8JO1PNJ81E7MCE2` FOREIGN KEY (`COMPOSITE`) REFERENCES `keycloak_role` (`ID`),
  CONSTRAINT `FK_GR7THLLB9LU8Q4VQA4524JJY8` FOREIGN KEY (`CHILD_ROLE`) REFERENCES `keycloak_role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `composite_role`
--

LOCK TABLES `composite_role` WRITE;
/*!40000 ALTER TABLE `composite_role` DISABLE KEYS */;
INSERT INTO `composite_role` VALUES ('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','81dd1228-a00c-4bfd-97b0-9512726a9885'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','a6ac2b20-2333-4cd5-9f5f-5db1ceba8bd0'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','e42b6717-1f17-49ed-92f6-5bb428ee0e37'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','ecd1fcb2-e5a7-4cc8-81c9-499e64e79014'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','08a3f504-63ee-428e-b54d-8d690a9b17fe'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','08d028c7-1a22-456b-937f-9dff083aedaf'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','0a0c2f23-a29f-4379-abc5-b90d04d2178b'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','116bf0bc-2d46-4c2b-8f6b-cf6af59469c5'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','123ef918-1c37-4b03-8630-da5632c344ff'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','2d7850b6-2c0e-499c-9765-b4c74241b26a'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','30328e06-9e6a-41e6-87cd-a5f46fa8ea89'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','3df8f447-9de8-44be-b5e8-08e12bce321e'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','4434128d-96b9-4b7c-be83-b5f90f369930'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','4b9a026d-d677-446b-9dd3-edfbd5a521ec'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','4d7389c1-0992-4445-8995-2618fdaf12a4'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','53dea1f2-1aff-463c-86c6-f1615a21b769'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','54a18124-892a-4dda-84f1-681433ef9cf7'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','5d6afc9e-ce92-44a9-96f2-0dff68c4907d'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','5f2570d6-5428-4141-83ad-85615db0d40b'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','6201ece4-b3fd-4168-804b-577b50b7d5c2'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','766bd181-d5df-425c-9f5f-32720c97e9b5'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','7f849712-2cf4-43e2-bab1-03500784699d'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','804389ac-a03c-4edb-9a7b-b5fcea9a782b'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','804675ce-e2f2-45ee-a312-65bc858eafb2'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','8293ee01-f6c0-414b-8f89-554c2964a30a'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','840e634b-8173-4fd6-b59c-43b88c69701a'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','8d3441d6-6d84-43cf-ad7c-e0108cb7778d'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','9061b134-77b1-43ef-b4bf-6bc3a02ab5e6'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','a4d42a54-eb0b-461b-ba7e-fae959da0e32'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','aa0830c8-a135-45e5-8175-cda42cb03c5b'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','adb0eb14-293c-4f5c-86e9-3b8de139fd14'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','b8a44e5a-3570-4e7a-8431-5439313d237d'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','c4ea0c88-afe8-46d8-81cc-7b6e6bfd1468'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','c816e2c5-479e-4baa-8359-02f641a2397a'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','c8345b16-9882-44f0-9812-84764ec84179'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','d4123ea2-cd39-401e-be92-d28d57fa6394'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','d589c7fe-2950-4bb5-90d2-7f640d2b5c83'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','d7e99464-f7e6-43fa-b579-6159e6ad48fc'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','efb693b3-f7a5-4f4d-98bd-910e6997d4bd'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','f88ef2c5-a588-43de-85a2-1177143a9f40'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','fae4d818-1d36-4397-bc23-0245019eb61d'),('4b9a026d-d677-446b-9dd3-edfbd5a521ec','c816e2c5-479e-4baa-8359-02f641a2397a'),('60a67a1d-6c98-49bb-acce-67f734c50430','7ea12a40-6b19-4a12-9749-5bdaed9c3637'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','06e47b09-9e28-4f58-8119-0ce07fc0d23c'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','0d91e0f3-4b4e-49fc-8bd4-08317ec2ede2'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','0fe55562-1bcb-467b-a24a-41ef12ed323b'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','1578f3d5-4e77-4ee4-b048-2240eda927b9'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','19dd2e6f-a0c1-488b-bef6-724df229412d'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','33931711-efbf-4bd0-89af-9669ad2a7304'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','415323d2-1d26-46be-b13d-946f66c9da26'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','569f5620-2457-4300-a1ae-5563664ea639'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','57811beb-f592-4813-b248-6e7768a18d0f'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','5ee1bb6d-9d83-418a-9f6b-0f87d7d2fce9'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','6dc04c00-02ca-4f85-aa92-3a823e9cb61d'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','75661740-9e63-4f7c-9cd2-25753fcc27c6'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','787262ef-53c8-48ad-a8bd-37ad6d630f98'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','7cff74ed-f014-4e38-af2e-54d0c39922d6'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','8b8de234-d376-4792-b4ce-fc0b1c7a619a'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','9b3d73f1-9923-41e4-9d79-8f539047c5f3'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','ebc5cf6d-a90b-4e7c-84b2-5bbac9d6ee26'),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','fb4d8eca-2086-4b32-ab5b-80a753319b10'),('840e634b-8173-4fd6-b59c-43b88c69701a','123ef918-1c37-4b03-8630-da5632c344ff'),('840e634b-8173-4fd6-b59c-43b88c69701a','d7e99464-f7e6-43fa-b579-6159e6ad48fc'),('9061b134-77b1-43ef-b4bf-6bc3a02ab5e6','4d7389c1-0992-4445-8995-2618fdaf12a4'),('9061b134-77b1-43ef-b4bf-6bc3a02ab5e6','aa0830c8-a135-45e5-8175-cda42cb03c5b'),('9b3d73f1-9923-41e4-9d79-8f539047c5f3','787262ef-53c8-48ad-a8bd-37ad6d630f98'),('c5e29044-0762-4c6a-858c-14d0f1249666','4dcb923b-ebe5-4deb-a5d3-6159f4bb1469'),('d4123ea2-cd39-401e-be92-d28d57fa6394','8d3441d6-6d84-43cf-ad7c-e0108cb7778d'),('e08b8d31-d520-4d12-8e4e-233a33257409','274dbf04-939f-44ef-a788-355fb03708dc'),('e08b8d31-d520-4d12-8e4e-233a33257409','a978c2b9-7e6c-4595-8f32-669996c5b871'),('e08b8d31-d520-4d12-8e4e-233a33257409','ae037b35-25ef-4de5-b4b6-9f0251cd0fed'),('e08b8d31-d520-4d12-8e4e-233a33257409','eda43363-b524-457e-8690-abd92be59289'),('e42b6717-1f17-49ed-92f6-5bb428ee0e37','bf16dbc9-620c-48d5-8809-219c5167ec33'),('ebc5cf6d-a90b-4e7c-84b2-5bbac9d6ee26','0d91e0f3-4b4e-49fc-8bd4-08317ec2ede2'),('ebc5cf6d-a90b-4e7c-84b2-5bbac9d6ee26','0fe55562-1bcb-467b-a24a-41ef12ed323b'),('eda43363-b524-457e-8690-abd92be59289','cc204501-ae67-4930-9bea-3b8cabde177e');
/*!40000 ALTER TABLE `composite_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credential`
--

DROP TABLE IF EXISTS `credential`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credential` (
  `ID` varchar(36) NOT NULL,
  `SALT` tinyblob DEFAULT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `USER_ID` varchar(36) DEFAULT NULL,
  `CREATED_DATE` bigint(20) DEFAULT NULL,
  `USER_LABEL` varchar(255) DEFAULT NULL,
  `SECRET_DATA` longtext DEFAULT NULL,
  `CREDENTIAL_DATA` longtext DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_USER_CREDENTIAL` (`USER_ID`),
  CONSTRAINT `FK_PFYR0GLASQYL0DEI3KL69R6V0` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credential`
--

LOCK TABLES `credential` WRITE;
/*!40000 ALTER TABLE `credential` DISABLE KEYS */;
INSERT INTO `credential` VALUES ('184adde9-f18c-48cc-96d8-d6a228d00a0d',NULL,'password','148eaaf2-53e5-4ef2-a8a6-52bb1c210933',1645600469776,NULL,'{\"value\":\"1PGdWUDvkNPcGmwyI2Gsg91DBojYpZzHT4gUGu+eeCcONLPCAFI5awqNFdHK4A6i1A+/59jBF0SQsOhwqli7qA==\",\"salt\":\"/r7O/zRd164HHDffo0dKKQ==\",\"additionalParameters\":{}}','{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}',10),('21eb6cc1-79e2-457d-a3c1-4a9e316fa00e',NULL,'password','2762b66d-e682-4ff1-a4f4-9429731e7a8d',1649991765714,NULL,'{\"value\":\"VG6kf8f17hY4HmBreYc+2OxOmqu/CgGnMeQZMcb9GaeaQlSpd40lCQPMr1e3H/fk+SNozLPwEOp6BPpLIcy/6w==\",\"salt\":\"9QfCIlm0JzErCKb/dvXavg==\",\"additionalParameters\":{}}','{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}',10),('3a0212c5-e0c8-44b0-94f9-baae824ab4b8',NULL,'password','3fc64047-176c-4b55-ac53-e8e383760104',1644385465930,NULL,'{\"value\":\"5nmlQw5OHQRfWtnCJPVCB4C540GvZNuodT8+34JBTbhVelKvjmi3mC+we55WuI0F/AQX1qu61Oagl+qZffutSw==\",\"salt\":\"ptFOPddl+UVO4pJQt360Ag==\",\"additionalParameters\":{}}','{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}',10),('44a3dd6f-1f5a-49a0-b255-12b63802c852',NULL,'password','1dc23092-e94c-4c93-be2c-3c96b1b0bb8f',1669793344215,NULL,'{\"value\":\"exqSf5hOd2XW2f5FHr1gxTguIozFLL8yAtQtMcC/DYXxS4nPbF7YSmokU8nfb26/9TM1LcbuDLUVLz9fPoe8gw==\",\"salt\":\"mt+4vrcZCzof/Se0KS9ouA==\",\"additionalParameters\":{}}','{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}',10),('5b660bb4-a507-4a53-935d-46d76a94e8ca',NULL,'password','9ac842e6-41ce-43de-a086-c64ea658fd26',1645765730419,NULL,'{\"value\":\"TPGUGDlPveSlyvNb2cZvQq2WAzYN9EEy7d65gMRdu1s4Qs/yptoJNRHFhzA0PEUjZU1ayN9TnmHBGDZHKTKFwg==\",\"salt\":\"BfDzlSfhslsztxw/MkGvTg==\",\"additionalParameters\":{}}','{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}',10);
/*!40000 ALTER TABLE `credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `databasechangelog`
--

DROP TABLE IF EXISTS `databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangelog` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangelog`
--

LOCK TABLES `databasechangelog` WRITE;
/*!40000 ALTER TABLE `databasechangelog` DISABLE KEYS */;
INSERT INTO `databasechangelog` VALUES ('1.0.0.Final-KEYCLOAK-5461','sthorger@redhat.com','META-INF/jpa-changelog-1.0.0.Final.xml','2022-02-09 14:40:43',1,'EXECUTED','7:4e70412f24a3f382c82183742ec79317','createTable tableName=APPLICATION_DEFAULT_ROLES; createTable tableName=CLIENT; createTable tableName=CLIENT_SESSION; createTable tableName=CLIENT_SESSION_ROLE; createTable tableName=COMPOSITE_ROLE; createTable tableName=CREDENTIAL; createTable tab...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.0.0.Final-KEYCLOAK-5461','sthorger@redhat.com','META-INF/db2-jpa-changelog-1.0.0.Final.xml','2022-02-09 14:40:43',2,'MARK_RAN','7:cb16724583e9675711801c6875114f28','createTable tableName=APPLICATION_DEFAULT_ROLES; createTable tableName=CLIENT; createTable tableName=CLIENT_SESSION; createTable tableName=CLIENT_SESSION_ROLE; createTable tableName=COMPOSITE_ROLE; createTable tableName=CREDENTIAL; createTable tab...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.1.0.Beta1','sthorger@redhat.com','META-INF/jpa-changelog-1.1.0.Beta1.xml','2022-02-09 14:40:45',3,'EXECUTED','7:0310eb8ba07cec616460794d42ade0fa','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=CLIENT_ATTRIBUTES; createTable tableName=CLIENT_SESSION_NOTE; createTable tableName=APP_NODE_REGISTRATIONS; addColumn table...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.1.0.Final','sthorger@redhat.com','META-INF/jpa-changelog-1.1.0.Final.xml','2022-02-09 14:40:45',4,'EXECUTED','7:5d25857e708c3233ef4439df1f93f012','renameColumn newColumnName=EVENT_TIME, oldColumnName=TIME, tableName=EVENT_ENTITY','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.2.0.Beta1','psilva@redhat.com','META-INF/jpa-changelog-1.2.0.Beta1.xml','2022-02-09 14:40:49',5,'EXECUTED','7:c7a54a1041d58eb3817a4a883b4d4e84','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=PROTOCOL_MAPPER; createTable tableName=PROTOCOL_MAPPER_CONFIG; createTable tableName=...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.2.0.Beta1','psilva@redhat.com','META-INF/db2-jpa-changelog-1.2.0.Beta1.xml','2022-02-09 14:40:49',6,'MARK_RAN','7:2e01012df20974c1c2a605ef8afe25b7','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=PROTOCOL_MAPPER; createTable tableName=PROTOCOL_MAPPER_CONFIG; createTable tableName=...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.2.0.RC1','bburke@redhat.com','META-INF/jpa-changelog-1.2.0.CR1.xml','2022-02-09 14:40:54',7,'EXECUTED','7:0f08df48468428e0f30ee59a8ec01a41','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=MIGRATION_MODEL; createTable tableName=IDENTITY_P...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.2.0.RC1','bburke@redhat.com','META-INF/db2-jpa-changelog-1.2.0.CR1.xml','2022-02-09 14:40:54',8,'MARK_RAN','7:a77ea2ad226b345e7d689d366f185c8c','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=MIGRATION_MODEL; createTable tableName=IDENTITY_P...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.2.0.Final','keycloak','META-INF/jpa-changelog-1.2.0.Final.xml','2022-02-09 14:40:54',9,'EXECUTED','7:a3377a2059aefbf3b90ebb4c4cc8e2ab','update tableName=CLIENT; update tableName=CLIENT; update tableName=CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.3.0','bburke@redhat.com','META-INF/jpa-changelog-1.3.0.xml','2022-02-09 14:40:58',10,'EXECUTED','7:04c1dbedc2aa3e9756d1a1668e003451','delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=ADMI...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.4.0','bburke@redhat.com','META-INF/jpa-changelog-1.4.0.xml','2022-02-09 14:41:00',11,'EXECUTED','7:36ef39ed560ad07062d956db861042ba','delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.4.0','bburke@redhat.com','META-INF/db2-jpa-changelog-1.4.0.xml','2022-02-09 14:41:00',12,'MARK_RAN','7:d909180b2530479a716d3f9c9eaea3d7','delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.5.0','bburke@redhat.com','META-INF/jpa-changelog-1.5.0.xml','2022-02-09 14:41:00',13,'EXECUTED','7:cf12b04b79bea5152f165eb41f3955f6','delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.6.1_from15','mposolda@redhat.com','META-INF/jpa-changelog-1.6.1.xml','2022-02-09 14:41:02',14,'EXECUTED','7:7e32c8f05c755e8675764e7d5f514509','addColumn tableName=REALM; addColumn tableName=KEYCLOAK_ROLE; addColumn tableName=CLIENT; createTable tableName=OFFLINE_USER_SESSION; createTable tableName=OFFLINE_CLIENT_SESSION; addPrimaryKey constraintName=CONSTRAINT_OFFL_US_SES_PK2, tableName=...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.6.1_from16-pre','mposolda@redhat.com','META-INF/jpa-changelog-1.6.1.xml','2022-02-09 14:41:02',15,'MARK_RAN','7:980ba23cc0ec39cab731ce903dd01291','delete tableName=OFFLINE_CLIENT_SESSION; delete tableName=OFFLINE_USER_SESSION','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.6.1_from16','mposolda@redhat.com','META-INF/jpa-changelog-1.6.1.xml','2022-02-09 14:41:02',16,'MARK_RAN','7:2fa220758991285312eb84f3b4ff5336','dropPrimaryKey constraintName=CONSTRAINT_OFFLINE_US_SES_PK, tableName=OFFLINE_USER_SESSION; dropPrimaryKey constraintName=CONSTRAINT_OFFLINE_CL_SES_PK, tableName=OFFLINE_CLIENT_SESSION; addColumn tableName=OFFLINE_USER_SESSION; update tableName=OF...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.6.1','mposolda@redhat.com','META-INF/jpa-changelog-1.6.1.xml','2022-02-09 14:41:02',17,'EXECUTED','7:d41d8cd98f00b204e9800998ecf8427e','empty','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.7.0','bburke@redhat.com','META-INF/jpa-changelog-1.7.0.xml','2022-02-09 14:41:05',18,'EXECUTED','7:91ace540896df890cc00a0490ee52bbc','createTable tableName=KEYCLOAK_GROUP; createTable tableName=GROUP_ROLE_MAPPING; createTable tableName=GROUP_ATTRIBUTE; createTable tableName=USER_GROUP_MEMBERSHIP; createTable tableName=REALM_DEFAULT_GROUPS; addColumn tableName=IDENTITY_PROVIDER; ...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.8.0','mposolda@redhat.com','META-INF/jpa-changelog-1.8.0.xml','2022-02-09 14:41:08',19,'EXECUTED','7:c31d1646dfa2618a9335c00e07f89f24','addColumn tableName=IDENTITY_PROVIDER; createTable tableName=CLIENT_TEMPLATE; createTable tableName=CLIENT_TEMPLATE_ATTRIBUTES; createTable tableName=TEMPLATE_SCOPE_MAPPING; dropNotNullConstraint columnName=CLIENT_ID, tableName=PROTOCOL_MAPPER; ad...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.8.0-2','keycloak','META-INF/jpa-changelog-1.8.0.xml','2022-02-09 14:41:08',20,'EXECUTED','7:df8bc21027a4f7cbbb01f6344e89ce07','dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; update tableName=CREDENTIAL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.8.0','mposolda@redhat.com','META-INF/db2-jpa-changelog-1.8.0.xml','2022-02-09 14:41:08',21,'MARK_RAN','7:f987971fe6b37d963bc95fee2b27f8df','addColumn tableName=IDENTITY_PROVIDER; createTable tableName=CLIENT_TEMPLATE; createTable tableName=CLIENT_TEMPLATE_ATTRIBUTES; createTable tableName=TEMPLATE_SCOPE_MAPPING; dropNotNullConstraint columnName=CLIENT_ID, tableName=PROTOCOL_MAPPER; ad...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.8.0-2','keycloak','META-INF/db2-jpa-changelog-1.8.0.xml','2022-02-09 14:41:08',22,'MARK_RAN','7:df8bc21027a4f7cbbb01f6344e89ce07','dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; update tableName=CREDENTIAL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.9.0','mposolda@redhat.com','META-INF/jpa-changelog-1.9.0.xml','2022-02-09 14:41:09',23,'EXECUTED','7:ed2dc7f799d19ac452cbcda56c929e47','update tableName=REALM; update tableName=REALM; update tableName=REALM; update tableName=REALM; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=REALM; update tableName=REALM; customChange; dr...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.9.1','keycloak','META-INF/jpa-changelog-1.9.1.xml','2022-02-09 14:41:09',24,'EXECUTED','7:80b5db88a5dda36ece5f235be8757615','modifyDataType columnName=PRIVATE_KEY, tableName=REALM; modifyDataType columnName=PUBLIC_KEY, tableName=REALM; modifyDataType columnName=CERTIFICATE, tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.9.1','keycloak','META-INF/db2-jpa-changelog-1.9.1.xml','2022-02-09 14:41:09',25,'MARK_RAN','7:1437310ed1305a9b93f8848f301726ce','modifyDataType columnName=PRIVATE_KEY, tableName=REALM; modifyDataType columnName=CERTIFICATE, tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('1.9.2','keycloak','META-INF/jpa-changelog-1.9.2.xml','2022-02-09 14:41:10',26,'EXECUTED','7:b82ffb34850fa0836be16deefc6a87c4','createIndex indexName=IDX_USER_EMAIL, tableName=USER_ENTITY; createIndex indexName=IDX_USER_ROLE_MAPPING, tableName=USER_ROLE_MAPPING; createIndex indexName=IDX_USER_GROUP_MAPPING, tableName=USER_GROUP_MEMBERSHIP; createIndex indexName=IDX_USER_CO...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-2.0.0','psilva@redhat.com','META-INF/jpa-changelog-authz-2.0.0.xml','2022-02-09 14:41:14',27,'EXECUTED','7:9cc98082921330d8d9266decdd4bd658','createTable tableName=RESOURCE_SERVER; addPrimaryKey constraintName=CONSTRAINT_FARS, tableName=RESOURCE_SERVER; addUniqueConstraint constraintName=UK_AU8TT6T700S9V50BU18WS5HA6, tableName=RESOURCE_SERVER; createTable tableName=RESOURCE_SERVER_RESOU...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-2.5.1','psilva@redhat.com','META-INF/jpa-changelog-authz-2.5.1.xml','2022-02-09 14:41:14',28,'EXECUTED','7:03d64aeed9cb52b969bd30a7ac0db57e','update tableName=RESOURCE_SERVER_POLICY','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.1.0-KEYCLOAK-5461','bburke@redhat.com','META-INF/jpa-changelog-2.1.0.xml','2022-02-09 14:41:16',29,'EXECUTED','7:f1f9fd8710399d725b780f463c6b21cd','createTable tableName=BROKER_LINK; createTable tableName=FED_USER_ATTRIBUTE; createTable tableName=FED_USER_CONSENT; createTable tableName=FED_USER_CONSENT_ROLE; createTable tableName=FED_USER_CONSENT_PROT_MAPPER; createTable tableName=FED_USER_CR...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.2.0','bburke@redhat.com','META-INF/jpa-changelog-2.2.0.xml','2022-02-09 14:41:17',30,'EXECUTED','7:53188c3eb1107546e6f765835705b6c1','addColumn tableName=ADMIN_EVENT_ENTITY; createTable tableName=CREDENTIAL_ATTRIBUTE; createTable tableName=FED_CREDENTIAL_ATTRIBUTE; modifyDataType columnName=VALUE, tableName=CREDENTIAL; addForeignKeyConstraint baseTableName=FED_CREDENTIAL_ATTRIBU...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.3.0','bburke@redhat.com','META-INF/jpa-changelog-2.3.0.xml','2022-02-09 14:41:17',31,'EXECUTED','7:d6e6f3bc57a0c5586737d1351725d4d4','createTable tableName=FEDERATED_USER; addPrimaryKey constraintName=CONSTR_FEDERATED_USER, tableName=FEDERATED_USER; dropDefaultValue columnName=TOTP, tableName=USER_ENTITY; dropColumn columnName=TOTP, tableName=USER_ENTITY; addColumn tableName=IDE...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.4.0','bburke@redhat.com','META-INF/jpa-changelog-2.4.0.xml','2022-02-09 14:41:17',32,'EXECUTED','7:454d604fbd755d9df3fd9c6329043aa5','customChange','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.0','bburke@redhat.com','META-INF/jpa-changelog-2.5.0.xml','2022-02-09 14:41:17',33,'EXECUTED','7:57e98a3077e29caf562f7dbf80c72600','customChange; modifyDataType columnName=USER_ID, tableName=OFFLINE_USER_SESSION','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.0-unicode-oracle','hmlnarik@redhat.com','META-INF/jpa-changelog-2.5.0.xml','2022-02-09 14:41:17',34,'MARK_RAN','7:e4c7e8f2256210aee71ddc42f538b57a','modifyDataType columnName=DESCRIPTION, tableName=AUTHENTICATION_FLOW; modifyDataType columnName=DESCRIPTION, tableName=CLIENT_TEMPLATE; modifyDataType columnName=DESCRIPTION, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=DESCRIPTION,...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.0-unicode-other-dbs','hmlnarik@redhat.com','META-INF/jpa-changelog-2.5.0.xml','2022-02-09 14:41:18',35,'EXECUTED','7:09a43c97e49bc626460480aa1379b522','modifyDataType columnName=DESCRIPTION, tableName=AUTHENTICATION_FLOW; modifyDataType columnName=DESCRIPTION, tableName=CLIENT_TEMPLATE; modifyDataType columnName=DESCRIPTION, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=DESCRIPTION,...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.0-duplicate-email-support','slawomir@dabek.name','META-INF/jpa-changelog-2.5.0.xml','2022-02-09 14:41:19',36,'EXECUTED','7:26bfc7c74fefa9126f2ce702fb775553','addColumn tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.0-unique-group-names','hmlnarik@redhat.com','META-INF/jpa-changelog-2.5.0.xml','2022-02-09 14:41:19',37,'EXECUTED','7:a161e2ae671a9020fff61e996a207377','addUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP','',NULL,'3.5.4',NULL,NULL,'4385232740'),('2.5.1','bburke@redhat.com','META-INF/jpa-changelog-2.5.1.xml','2022-02-09 14:41:19',38,'EXECUTED','7:37fc1781855ac5388c494f1442b3f717','addColumn tableName=FED_USER_CONSENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.0.0','bburke@redhat.com','META-INF/jpa-changelog-3.0.0.xml','2022-02-09 14:41:19',39,'EXECUTED','7:13a27db0dae6049541136adad7261d27','addColumn tableName=IDENTITY_PROVIDER','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.2.0-fix','keycloak','META-INF/jpa-changelog-3.2.0.xml','2022-02-09 14:41:19',40,'MARK_RAN','7:550300617e3b59e8af3a6294df8248a3','addNotNullConstraint columnName=REALM_ID, tableName=CLIENT_INITIAL_ACCESS','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.2.0-fix-with-keycloak-5416','keycloak','META-INF/jpa-changelog-3.2.0.xml','2022-02-09 14:41:19',41,'MARK_RAN','7:e3a9482b8931481dc2772a5c07c44f17','dropIndex indexName=IDX_CLIENT_INIT_ACC_REALM, tableName=CLIENT_INITIAL_ACCESS; addNotNullConstraint columnName=REALM_ID, tableName=CLIENT_INITIAL_ACCESS; createIndex indexName=IDX_CLIENT_INIT_ACC_REALM, tableName=CLIENT_INITIAL_ACCESS','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.2.0-fix-offline-sessions','hmlnarik','META-INF/jpa-changelog-3.2.0.xml','2022-02-09 14:41:19',42,'EXECUTED','7:72b07d85a2677cb257edb02b408f332d','customChange','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.2.0-fixed','keycloak','META-INF/jpa-changelog-3.2.0.xml','2022-02-09 14:41:23',43,'EXECUTED','7:a72a7858967bd414835d19e04d880312','addColumn tableName=REALM; dropPrimaryKey constraintName=CONSTRAINT_OFFL_CL_SES_PK2, tableName=OFFLINE_CLIENT_SESSION; dropColumn columnName=CLIENT_SESSION_ID, tableName=OFFLINE_CLIENT_SESSION; addPrimaryKey constraintName=CONSTRAINT_OFFL_CL_SES_P...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.3.0','keycloak','META-INF/jpa-changelog-3.3.0.xml','2022-02-09 14:41:23',44,'EXECUTED','7:94edff7cf9ce179e7e85f0cd78a3cf2c','addColumn tableName=USER_ENTITY','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-3.4.0.CR1-resource-server-pk-change-part1','glavoie@gmail.com','META-INF/jpa-changelog-authz-3.4.0.CR1.xml','2022-02-09 14:41:23',45,'EXECUTED','7:6a48ce645a3525488a90fbf76adf3bb3','addColumn tableName=RESOURCE_SERVER_POLICY; addColumn tableName=RESOURCE_SERVER_RESOURCE; addColumn tableName=RESOURCE_SERVER_SCOPE','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-3.4.0.CR1-resource-server-pk-change-part2-KEYCLOAK-6095','hmlnarik@redhat.com','META-INF/jpa-changelog-authz-3.4.0.CR1.xml','2022-02-09 14:41:23',46,'EXECUTED','7:e64b5dcea7db06077c6e57d3b9e5ca14','customChange','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-3.4.0.CR1-resource-server-pk-change-part3-fixed','glavoie@gmail.com','META-INF/jpa-changelog-authz-3.4.0.CR1.xml','2022-02-09 14:41:23',47,'MARK_RAN','7:fd8cf02498f8b1e72496a20afc75178c','dropIndex indexName=IDX_RES_SERV_POL_RES_SERV, tableName=RESOURCE_SERVER_POLICY; dropIndex indexName=IDX_RES_SRV_RES_RES_SRV, tableName=RESOURCE_SERVER_RESOURCE; dropIndex indexName=IDX_RES_SRV_SCOPE_RES_SRV, tableName=RESOURCE_SERVER_SCOPE','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-3.4.0.CR1-resource-server-pk-change-part3-fixed-nodropindex','glavoie@gmail.com','META-INF/jpa-changelog-authz-3.4.0.CR1.xml','2022-02-09 14:41:26',48,'EXECUTED','7:542794f25aa2b1fbabb7e577d6646319','addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, tableName=RESOURCE_SERVER_POLICY; addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, tableName=RESOURCE_SERVER_RESOURCE; addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, ...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authn-3.4.0.CR1-refresh-token-max-reuse','glavoie@gmail.com','META-INF/jpa-changelog-authz-3.4.0.CR1.xml','2022-02-09 14:41:26',49,'EXECUTED','7:edad604c882df12f74941dac3cc6d650','addColumn tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.4.0','keycloak','META-INF/jpa-changelog-3.4.0.xml','2022-02-09 14:41:29',50,'EXECUTED','7:0f88b78b7b46480eb92690cbf5e44900','addPrimaryKey constraintName=CONSTRAINT_REALM_DEFAULT_ROLES, tableName=REALM_DEFAULT_ROLES; addPrimaryKey constraintName=CONSTRAINT_COMPOSITE_ROLE, tableName=COMPOSITE_ROLE; addPrimaryKey constraintName=CONSTR_REALM_DEFAULT_GROUPS, tableName=REALM...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.4.0-KEYCLOAK-5230','hmlnarik@redhat.com','META-INF/jpa-changelog-3.4.0.xml','2022-02-09 14:41:30',51,'EXECUTED','7:d560e43982611d936457c327f872dd59','createIndex indexName=IDX_FU_ATTRIBUTE, tableName=FED_USER_ATTRIBUTE; createIndex indexName=IDX_FU_CONSENT, tableName=FED_USER_CONSENT; createIndex indexName=IDX_FU_CONSENT_RU, tableName=FED_USER_CONSENT; createIndex indexName=IDX_FU_CREDENTIAL, t...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.4.1','psilva@redhat.com','META-INF/jpa-changelog-3.4.1.xml','2022-02-09 14:41:30',52,'EXECUTED','7:c155566c42b4d14ef07059ec3b3bbd8e','modifyDataType columnName=VALUE, tableName=CLIENT_ATTRIBUTES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.4.2','keycloak','META-INF/jpa-changelog-3.4.2.xml','2022-02-09 14:41:30',53,'EXECUTED','7:b40376581f12d70f3c89ba8ddf5b7dea','update tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('3.4.2-KEYCLOAK-5172','mkanis@redhat.com','META-INF/jpa-changelog-3.4.2.xml','2022-02-09 14:41:30',54,'EXECUTED','7:a1132cc395f7b95b3646146c2e38f168','update tableName=CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.0.0-KEYCLOAK-6335','bburke@redhat.com','META-INF/jpa-changelog-4.0.0.xml','2022-02-09 14:41:30',55,'EXECUTED','7:d8dc5d89c789105cfa7ca0e82cba60af','createTable tableName=CLIENT_AUTH_FLOW_BINDINGS; addPrimaryKey constraintName=C_CLI_FLOW_BIND, tableName=CLIENT_AUTH_FLOW_BINDINGS','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.0.0-CLEANUP-UNUSED-TABLE','bburke@redhat.com','META-INF/jpa-changelog-4.0.0.xml','2022-02-09 14:41:30',56,'EXECUTED','7:7822e0165097182e8f653c35517656a3','dropTable tableName=CLIENT_IDENTITY_PROV_MAPPING','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.0.0-KEYCLOAK-6228','bburke@redhat.com','META-INF/jpa-changelog-4.0.0.xml','2022-02-09 14:41:31',57,'EXECUTED','7:c6538c29b9c9a08f9e9ea2de5c2b6375','dropUniqueConstraint constraintName=UK_JKUWUVD56ONTGSUHOGM8UEWRT, tableName=USER_CONSENT; dropNotNullConstraint columnName=CLIENT_ID, tableName=USER_CONSENT; addColumn tableName=USER_CONSENT; addUniqueConstraint constraintName=UK_JKUWUVD56ONTGSUHO...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.0.0-KEYCLOAK-5579-fixed','mposolda@redhat.com','META-INF/jpa-changelog-4.0.0.xml','2022-02-09 14:41:36',58,'EXECUTED','7:6d4893e36de22369cf73bcb051ded875','dropForeignKeyConstraint baseTableName=CLIENT_TEMPLATE_ATTRIBUTES, constraintName=FK_CL_TEMPL_ATTR_TEMPL; renameTable newTableName=CLIENT_SCOPE_ATTRIBUTES, oldTableName=CLIENT_TEMPLATE_ATTRIBUTES; renameColumn newColumnName=SCOPE_ID, oldColumnName...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-4.0.0.CR1','psilva@redhat.com','META-INF/jpa-changelog-authz-4.0.0.CR1.xml','2022-02-09 14:41:37',59,'EXECUTED','7:57960fc0b0f0dd0563ea6f8b2e4a1707','createTable tableName=RESOURCE_SERVER_PERM_TICKET; addPrimaryKey constraintName=CONSTRAINT_FAPMT, tableName=RESOURCE_SERVER_PERM_TICKET; addForeignKeyConstraint baseTableName=RESOURCE_SERVER_PERM_TICKET, constraintName=FK_FRSRHO213XCX4WNKOG82SSPMT...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-4.0.0.Beta3','psilva@redhat.com','META-INF/jpa-changelog-authz-4.0.0.Beta3.xml','2022-02-09 14:41:37',60,'EXECUTED','7:2b4b8bff39944c7097977cc18dbceb3b','addColumn tableName=RESOURCE_SERVER_POLICY; addColumn tableName=RESOURCE_SERVER_PERM_TICKET; addForeignKeyConstraint baseTableName=RESOURCE_SERVER_PERM_TICKET, constraintName=FK_FRSRPO2128CX4WNKOG82SSRFY, referencedTableName=RESOURCE_SERVER_POLICY','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-4.2.0.Final','mhajas@redhat.com','META-INF/jpa-changelog-authz-4.2.0.Final.xml','2022-02-09 14:41:38',61,'EXECUTED','7:2aa42a964c59cd5b8ca9822340ba33a8','createTable tableName=RESOURCE_URIS; addForeignKeyConstraint baseTableName=RESOURCE_URIS, constraintName=FK_RESOURCE_SERVER_URIS, referencedTableName=RESOURCE_SERVER_RESOURCE; customChange; dropColumn columnName=URI, tableName=RESOURCE_SERVER_RESO...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-4.2.0.Final-KEYCLOAK-9944','hmlnarik@redhat.com','META-INF/jpa-changelog-authz-4.2.0.Final.xml','2022-02-09 14:41:38',62,'EXECUTED','7:9ac9e58545479929ba23f4a3087a0346','addPrimaryKey constraintName=CONSTRAINT_RESOUR_URIS_PK, tableName=RESOURCE_URIS','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.2.0-KEYCLOAK-6313','wadahiro@gmail.com','META-INF/jpa-changelog-4.2.0.xml','2022-02-09 14:41:38',63,'EXECUTED','7:14d407c35bc4fe1976867756bcea0c36','addColumn tableName=REQUIRED_ACTION_PROVIDER','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.3.0-KEYCLOAK-7984','wadahiro@gmail.com','META-INF/jpa-changelog-4.3.0.xml','2022-02-09 14:41:38',64,'EXECUTED','7:241a8030c748c8548e346adee548fa93','update tableName=REQUIRED_ACTION_PROVIDER','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.6.0-KEYCLOAK-7950','psilva@redhat.com','META-INF/jpa-changelog-4.6.0.xml','2022-02-09 14:41:38',65,'EXECUTED','7:7d3182f65a34fcc61e8d23def037dc3f','update tableName=RESOURCE_SERVER_RESOURCE','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.6.0-KEYCLOAK-8377','keycloak','META-INF/jpa-changelog-4.6.0.xml','2022-02-09 14:41:39',66,'EXECUTED','7:b30039e00a0b9715d430d1b0636728fa','createTable tableName=ROLE_ATTRIBUTE; addPrimaryKey constraintName=CONSTRAINT_ROLE_ATTRIBUTE_PK, tableName=ROLE_ATTRIBUTE; addForeignKeyConstraint baseTableName=ROLE_ATTRIBUTE, constraintName=FK_ROLE_ATTRIBUTE_ID, referencedTableName=KEYCLOAK_ROLE...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.6.0-KEYCLOAK-8555','gideonray@gmail.com','META-INF/jpa-changelog-4.6.0.xml','2022-02-09 14:41:39',67,'EXECUTED','7:3797315ca61d531780f8e6f82f258159','createIndex indexName=IDX_COMPONENT_PROVIDER_TYPE, tableName=COMPONENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.7.0-KEYCLOAK-1267','sguilhen@redhat.com','META-INF/jpa-changelog-4.7.0.xml','2022-02-09 14:41:39',68,'EXECUTED','7:c7aa4c8d9573500c2d347c1941ff0301','addColumn tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.7.0-KEYCLOAK-7275','keycloak','META-INF/jpa-changelog-4.7.0.xml','2022-02-09 14:41:39',69,'EXECUTED','7:b207faee394fc074a442ecd42185a5dd','renameColumn newColumnName=CREATED_ON, oldColumnName=LAST_SESSION_REFRESH, tableName=OFFLINE_USER_SESSION; addNotNullConstraint columnName=CREATED_ON, tableName=OFFLINE_USER_SESSION; addColumn tableName=OFFLINE_USER_SESSION; customChange; createIn...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('4.8.0-KEYCLOAK-8835','sguilhen@redhat.com','META-INF/jpa-changelog-4.8.0.xml','2022-02-09 14:41:39',70,'EXECUTED','7:ab9a9762faaba4ddfa35514b212c4922','addNotNullConstraint columnName=SSO_MAX_LIFESPAN_REMEMBER_ME, tableName=REALM; addNotNullConstraint columnName=SSO_IDLE_TIMEOUT_REMEMBER_ME, tableName=REALM','',NULL,'3.5.4',NULL,NULL,'4385232740'),('authz-7.0.0-KEYCLOAK-10443','psilva@redhat.com','META-INF/jpa-changelog-authz-7.0.0.xml','2022-02-09 14:41:39',71,'EXECUTED','7:b9710f74515a6ccb51b72dc0d19df8c4','addColumn tableName=RESOURCE_SERVER','',NULL,'3.5.4',NULL,NULL,'4385232740'),('8.0.0-adding-credential-columns','keycloak','META-INF/jpa-changelog-8.0.0.xml','2022-02-09 14:41:39',72,'EXECUTED','7:ec9707ae4d4f0b7452fee20128083879','addColumn tableName=CREDENTIAL; addColumn tableName=FED_USER_CREDENTIAL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('8.0.0-updating-credential-data-not-oracle-fixed','keycloak','META-INF/jpa-changelog-8.0.0.xml','2022-02-09 14:41:39',73,'EXECUTED','7:3979a0ae07ac465e920ca696532fc736','update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('8.0.0-updating-credential-data-oracle-fixed','keycloak','META-INF/jpa-changelog-8.0.0.xml','2022-02-09 14:41:39',74,'MARK_RAN','7:5abfde4c259119d143bd2fbf49ac2bca','update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('8.0.0-credential-cleanup-fixed','keycloak','META-INF/jpa-changelog-8.0.0.xml','2022-02-09 14:41:40',75,'EXECUTED','7:b48da8c11a3d83ddd6b7d0c8c2219345','dropDefaultValue columnName=COUNTER, tableName=CREDENTIAL; dropDefaultValue columnName=DIGITS, tableName=CREDENTIAL; dropDefaultValue columnName=PERIOD, tableName=CREDENTIAL; dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; dropColumn ...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('8.0.0-resource-tag-support','keycloak','META-INF/jpa-changelog-8.0.0.xml','2022-02-09 14:41:40',76,'EXECUTED','7:a73379915c23bfad3e8f5c6d5c0aa4bd','addColumn tableName=MIGRATION_MODEL; createIndex indexName=IDX_UPDATE_TIME, tableName=MIGRATION_MODEL','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.0-always-display-client','keycloak','META-INF/jpa-changelog-9.0.0.xml','2022-02-09 14:41:40',77,'EXECUTED','7:39e0073779aba192646291aa2332493d','addColumn tableName=CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.0-drop-constraints-for-column-increase','keycloak','META-INF/jpa-changelog-9.0.0.xml','2022-02-09 14:41:40',78,'MARK_RAN','7:81f87368f00450799b4bf42ea0b3ec34','dropUniqueConstraint constraintName=UK_FRSR6T700S9V50BU18WS5PMT, tableName=RESOURCE_SERVER_PERM_TICKET; dropUniqueConstraint constraintName=UK_FRSR6T700S9V50BU18WS5HA6, tableName=RESOURCE_SERVER_RESOURCE; dropPrimaryKey constraintName=CONSTRAINT_O...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.0-increase-column-size-federated-fk','keycloak','META-INF/jpa-changelog-9.0.0.xml','2022-02-09 14:41:40',79,'EXECUTED','7:20b37422abb9fb6571c618148f013a15','modifyDataType columnName=CLIENT_ID, tableName=FED_USER_CONSENT; modifyDataType columnName=CLIENT_REALM_CONSTRAINT, tableName=KEYCLOAK_ROLE; modifyDataType columnName=OWNER, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=CLIENT_ID, ta...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.0-recreate-constraints-after-column-increase','keycloak','META-INF/jpa-changelog-9.0.0.xml','2022-02-09 14:41:40',80,'MARK_RAN','7:1970bb6cfb5ee800736b95ad3fb3c78a','addNotNullConstraint columnName=CLIENT_ID, tableName=OFFLINE_CLIENT_SESSION; addNotNullConstraint columnName=OWNER, tableName=RESOURCE_SERVER_PERM_TICKET; addNotNullConstraint columnName=REQUESTER, tableName=RESOURCE_SERVER_PERM_TICKET; addNotNull...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.1-add-index-to-client.client_id','keycloak','META-INF/jpa-changelog-9.0.1.xml','2022-02-09 14:41:41',81,'EXECUTED','7:45d9b25fc3b455d522d8dcc10a0f4c80','createIndex indexName=IDX_CLIENT_ID, tableName=CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.1-KEYCLOAK-12579-drop-constraints','keycloak','META-INF/jpa-changelog-9.0.1.xml','2022-02-09 14:41:41',82,'MARK_RAN','7:890ae73712bc187a66c2813a724d037f','dropUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.1-KEYCLOAK-12579-add-not-null-constraint','keycloak','META-INF/jpa-changelog-9.0.1.xml','2022-02-09 14:41:41',83,'EXECUTED','7:0a211980d27fafe3ff50d19a3a29b538','addNotNullConstraint columnName=PARENT_GROUP, tableName=KEYCLOAK_GROUP','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.1-KEYCLOAK-12579-recreate-constraints','keycloak','META-INF/jpa-changelog-9.0.1.xml','2022-02-09 14:41:41',84,'MARK_RAN','7:a161e2ae671a9020fff61e996a207377','addUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP','',NULL,'3.5.4',NULL,NULL,'4385232740'),('9.0.1-add-index-to-events','keycloak','META-INF/jpa-changelog-9.0.1.xml','2022-02-09 14:41:41',85,'EXECUTED','7:01c49302201bdf815b0a18d1f98a55dc','createIndex indexName=IDX_EVENT_TIME, tableName=EVENT_ENTITY','',NULL,'3.5.4',NULL,NULL,'4385232740'),('map-remove-ri','keycloak','META-INF/jpa-changelog-11.0.0.xml','2022-02-09 14:41:41',86,'EXECUTED','7:3dace6b144c11f53f1ad2c0361279b86','dropForeignKeyConstraint baseTableName=REALM, constraintName=FK_TRAF444KK6QRKMS7N56AIWQ5Y; dropForeignKeyConstraint baseTableName=KEYCLOAK_ROLE, constraintName=FK_KJHO5LE2C0RAL09FL8CM9WFW9','',NULL,'3.5.4',NULL,NULL,'4385232740'),('map-remove-ri','keycloak','META-INF/jpa-changelog-12.0.0.xml','2022-02-09 14:41:41',87,'EXECUTED','7:578d0b92077eaf2ab95ad0ec087aa903','dropForeignKeyConstraint baseTableName=REALM_DEFAULT_GROUPS, constraintName=FK_DEF_GROUPS_GROUP; dropForeignKeyConstraint baseTableName=REALM_DEFAULT_ROLES, constraintName=FK_H4WPD7W4HSOOLNI3H0SW7BTJE; dropForeignKeyConstraint baseTableName=CLIENT...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('12.1.0-add-realm-localization-table','keycloak','META-INF/jpa-changelog-12.0.0.xml','2022-02-09 14:41:41',88,'EXECUTED','7:c95abe90d962c57a09ecaee57972835d','createTable tableName=REALM_LOCALIZATIONS; addPrimaryKey tableName=REALM_LOCALIZATIONS','',NULL,'3.5.4',NULL,NULL,'4385232740'),('default-roles','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:41',89,'EXECUTED','7:f1313bcc2994a5c4dc1062ed6d8282d3','addColumn tableName=REALM; customChange','',NULL,'3.5.4',NULL,NULL,'4385232740'),('default-roles-cleanup','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:41',90,'EXECUTED','7:90d763b52eaffebefbcbde55f269508b','dropTable tableName=REALM_DEFAULT_ROLES; dropTable tableName=CLIENT_DEFAULT_ROLES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('13.0.0-KEYCLOAK-16844','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:41',91,'EXECUTED','7:d554f0cb92b764470dccfa5e0014a7dd','createIndex indexName=IDX_OFFLINE_USS_PRELOAD, tableName=OFFLINE_USER_SESSION','',NULL,'3.5.4',NULL,NULL,'4385232740'),('map-remove-ri-13.0.0','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:41',92,'EXECUTED','7:73193e3ab3c35cf0f37ccea3bf783764','dropForeignKeyConstraint baseTableName=DEFAULT_CLIENT_SCOPE, constraintName=FK_R_DEF_CLI_SCOPE_SCOPE; dropForeignKeyConstraint baseTableName=CLIENT_SCOPE_CLIENT, constraintName=FK_C_CLI_SCOPE_SCOPE; dropForeignKeyConstraint baseTableName=CLIENT_SC...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('13.0.0-KEYCLOAK-17992-drop-constraints','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:41',93,'MARK_RAN','7:90a1e74f92e9cbaa0c5eab80b8a037f3','dropPrimaryKey constraintName=C_CLI_SCOPE_BIND, tableName=CLIENT_SCOPE_CLIENT; dropIndex indexName=IDX_CLSCOPE_CL, tableName=CLIENT_SCOPE_CLIENT; dropIndex indexName=IDX_CL_CLSCOPE, tableName=CLIENT_SCOPE_CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('13.0.0-increase-column-size-federated','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:42',94,'EXECUTED','7:5b9248f29cd047c200083cc6d8388b16','modifyDataType columnName=CLIENT_ID, tableName=CLIENT_SCOPE_CLIENT; modifyDataType columnName=SCOPE_ID, tableName=CLIENT_SCOPE_CLIENT','',NULL,'3.5.4',NULL,NULL,'4385232740'),('13.0.0-KEYCLOAK-17992-recreate-constraints','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:42',95,'MARK_RAN','7:64db59e44c374f13955489e8990d17a1','addNotNullConstraint columnName=CLIENT_ID, tableName=CLIENT_SCOPE_CLIENT; addNotNullConstraint columnName=SCOPE_ID, tableName=CLIENT_SCOPE_CLIENT; addPrimaryKey constraintName=C_CLI_SCOPE_BIND, tableName=CLIENT_SCOPE_CLIENT; createIndex indexName=...','',NULL,'3.5.4',NULL,NULL,'4385232740'),('json-string-accomodation-fixed','keycloak','META-INF/jpa-changelog-13.0.0.xml','2022-02-09 14:41:42',96,'EXECUTED','7:329a578cdb43262fff975f0a7f6cda60','addColumn tableName=REALM_ATTRIBUTE; update tableName=REALM_ATTRIBUTE; dropColumn columnName=VALUE, tableName=REALM_ATTRIBUTE; renameColumn newColumnName=VALUE, oldColumnName=VALUE_NEW, tableName=REALM_ATTRIBUTE','',NULL,'3.5.4',NULL,NULL,'4385232740'),('14.0.0-KEYCLOAK-11019','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',97,'EXECUTED','7:fae0de241ac0fd0bbc2b380b85e4f567','createIndex indexName=IDX_OFFLINE_CSS_PRELOAD, tableName=OFFLINE_CLIENT_SESSION; createIndex indexName=IDX_OFFLINE_USS_BY_USER, tableName=OFFLINE_USER_SESSION; createIndex indexName=IDX_OFFLINE_USS_BY_USERSESS, tableName=OFFLINE_USER_SESSION','',NULL,'3.5.4',NULL,NULL,'4385232740'),('14.0.0-KEYCLOAK-18286','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',98,'MARK_RAN','7:075d54e9180f49bb0c64ca4218936e81','createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('14.0.0-KEYCLOAK-18286-revert','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',99,'MARK_RAN','7:06499836520f4f6b3d05e35a59324910','dropIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('14.0.0-KEYCLOAK-18286-supported-dbs','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',100,'EXECUTED','7:b558ad47ea0e4d3c3514225a49cc0d65','createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('14.0.0-KEYCLOAK-18286-unsupported-dbs','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',101,'MARK_RAN','7:3d2b23076e59c6f70bae703aa01be35b','createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES','',NULL,'3.5.4',NULL,NULL,'4385232740'),('KEYCLOAK-17267-add-index-to-user-attributes','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',102,'EXECUTED','7:1a7f28ff8d9e53aeb879d76ea3d9341a','createIndex indexName=IDX_USER_ATTRIBUTE_NAME, tableName=USER_ATTRIBUTE','',NULL,'3.5.4',NULL,NULL,'4385232740'),('KEYCLOAK-18146-add-saml-art-binding-identifier','keycloak','META-INF/jpa-changelog-14.0.0.xml','2022-02-09 14:41:42',103,'EXECUTED','7:2fd554456fed4a82c698c555c5b751b6','customChange','',NULL,'3.5.4',NULL,NULL,'4385232740'),('15.0.0-KEYCLOAK-18467','keycloak','META-INF/jpa-changelog-15.0.0.xml','2022-02-09 14:41:42',104,'EXECUTED','7:b06356d66c2790ecc2ae54ba0458397a','addColumn tableName=REALM_LOCALIZATIONS; update tableName=REALM_LOCALIZATIONS; dropColumn columnName=TEXTS, tableName=REALM_LOCALIZATIONS; renameColumn newColumnName=TEXTS, oldColumnName=TEXTS_NEW, tableName=REALM_LOCALIZATIONS; addNotNullConstrai...','',NULL,'3.5.4',NULL,NULL,'4385232740');
/*!40000 ALTER TABLE `databasechangelog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `databasechangeloglock`
--

DROP TABLE IF EXISTS `databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangeloglock`
--

LOCK TABLES `databasechangeloglock` WRITE;
/*!40000 ALTER TABLE `databasechangeloglock` DISABLE KEYS */;
INSERT INTO `databasechangeloglock` VALUES (1,'\0',NULL,NULL),(1000,'\0',NULL,NULL),(1001,'\0',NULL,NULL);
/*!40000 ALTER TABLE `databasechangeloglock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `default_client_scope`
--

DROP TABLE IF EXISTS `default_client_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `default_client_scope` (
  `REALM_ID` varchar(36) NOT NULL,
  `SCOPE_ID` varchar(36) NOT NULL,
  `DEFAULT_SCOPE` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`REALM_ID`,`SCOPE_ID`),
  KEY `IDX_DEFCLS_REALM` (`REALM_ID`),
  KEY `IDX_DEFCLS_SCOPE` (`SCOPE_ID`),
  CONSTRAINT `FK_R_DEF_CLI_SCOPE_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `default_client_scope`
--

LOCK TABLES `default_client_scope` WRITE;
/*!40000 ALTER TABLE `default_client_scope` DISABLE KEYS */;
INSERT INTO `default_client_scope` VALUES ('master','0c5a33f2-15c0-44b9-a387-190556db824d',''),('master','138be55b-dbeb-4e9f-951e-ea982198a1bd',''),('master','2f5372a0-234e-45ed-8bd3-e6f4047fe25f','\0'),('master','903b4c80-e49d-4e5c-82ad-291d6fca7046',''),('master','d041a31f-8104-448a-85dc-3fc0c7bb7c88','\0'),('master','d05cae1b-d92b-4abe-b7db-a201046a6662','\0'),('master','d846f2ff-0580-441a-8cde-3ed2a38d0b23','\0'),('master','e0e3ea89-7e94-4170-b3f8-394c82b7d95d',''),('master','eac1edda-84b5-45fd-9845-4fd7421c8d6b',''),('sptek-cloud','04297489-3e8c-4dc8-9ca0-bf26e5ec3f77',''),('sptek-cloud','3b2dfded-98d7-4c9b-bee2-b7c65e98094c',''),('sptek-cloud','425bbf94-3e2a-4d4a-b66a-e68d5b47e38e','\0'),('sptek-cloud','70b4c562-d08a-42e3-a106-d75e23822b20',''),('sptek-cloud','a954b1f3-be84-4af2-9ed4-869079942000','\0'),('sptek-cloud','b2d2c539-8634-42a6-be33-f82ee1053d1a',''),('sptek-cloud','bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc','\0'),('sptek-cloud','d7812a53-c68c-411d-aff2-61896320f4ae','\0'),('sptek-cloud','e2d2d02e-8d54-4fbf-bed3-f26df4f5d8ab','');
/*!40000 ALTER TABLE `default_client_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_entity`
--

DROP TABLE IF EXISTS `event_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_entity` (
  `ID` varchar(36) NOT NULL,
  `CLIENT_ID` varchar(255) DEFAULT NULL,
  `DETAILS_JSON` text DEFAULT NULL,
  `ERROR` varchar(255) DEFAULT NULL,
  `IP_ADDRESS` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `SESSION_ID` varchar(255) DEFAULT NULL,
  `EVENT_TIME` bigint(20) DEFAULT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_EVENT_TIME` (`REALM_ID`,`EVENT_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_entity`
--

LOCK TABLES `event_entity` WRITE;
/*!40000 ALTER TABLE `event_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_attribute`
--

DROP TABLE IF EXISTS `fed_user_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_attribute` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  `VALUE` text DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_FU_ATTRIBUTE` (`USER_ID`,`REALM_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_attribute`
--

LOCK TABLES `fed_user_attribute` WRITE;
/*!40000 ALTER TABLE `fed_user_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_consent`
--

DROP TABLE IF EXISTS `fed_user_consent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_consent` (
  `ID` varchar(36) NOT NULL,
  `CLIENT_ID` varchar(255) DEFAULT NULL,
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  `CREATED_DATE` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_DATE` bigint(20) DEFAULT NULL,
  `CLIENT_STORAGE_PROVIDER` varchar(36) DEFAULT NULL,
  `EXTERNAL_CLIENT_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_FU_CONSENT` (`USER_ID`,`CLIENT_ID`),
  KEY `IDX_FU_CONSENT_RU` (`REALM_ID`,`USER_ID`),
  KEY `IDX_FU_CNSNT_EXT` (`USER_ID`,`CLIENT_STORAGE_PROVIDER`,`EXTERNAL_CLIENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_consent`
--

LOCK TABLES `fed_user_consent` WRITE;
/*!40000 ALTER TABLE `fed_user_consent` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_consent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_consent_cl_scope`
--

DROP TABLE IF EXISTS `fed_user_consent_cl_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_consent_cl_scope` (
  `USER_CONSENT_ID` varchar(36) NOT NULL,
  `SCOPE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`USER_CONSENT_ID`,`SCOPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_consent_cl_scope`
--

LOCK TABLES `fed_user_consent_cl_scope` WRITE;
/*!40000 ALTER TABLE `fed_user_consent_cl_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_consent_cl_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_credential`
--

DROP TABLE IF EXISTS `fed_user_credential`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_credential` (
  `ID` varchar(36) NOT NULL,
  `SALT` tinyblob DEFAULT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `CREATED_DATE` bigint(20) DEFAULT NULL,
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  `USER_LABEL` varchar(255) DEFAULT NULL,
  `SECRET_DATA` longtext DEFAULT NULL,
  `CREDENTIAL_DATA` longtext DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_FU_CREDENTIAL` (`USER_ID`,`TYPE`),
  KEY `IDX_FU_CREDENTIAL_RU` (`REALM_ID`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_credential`
--

LOCK TABLES `fed_user_credential` WRITE;
/*!40000 ALTER TABLE `fed_user_credential` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_group_membership`
--

DROP TABLE IF EXISTS `fed_user_group_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_group_membership` (
  `GROUP_ID` varchar(36) NOT NULL,
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `IDX_FU_GROUP_MEMBERSHIP` (`USER_ID`,`GROUP_ID`),
  KEY `IDX_FU_GROUP_MEMBERSHIP_RU` (`REALM_ID`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_group_membership`
--

LOCK TABLES `fed_user_group_membership` WRITE;
/*!40000 ALTER TABLE `fed_user_group_membership` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_group_membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_required_action`
--

DROP TABLE IF EXISTS `fed_user_required_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_required_action` (
  `REQUIRED_ACTION` varchar(255) NOT NULL DEFAULT ' ',
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`REQUIRED_ACTION`,`USER_ID`),
  KEY `IDX_FU_REQUIRED_ACTION` (`USER_ID`,`REQUIRED_ACTION`),
  KEY `IDX_FU_REQUIRED_ACTION_RU` (`REALM_ID`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_required_action`
--

LOCK TABLES `fed_user_required_action` WRITE;
/*!40000 ALTER TABLE `fed_user_required_action` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_required_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fed_user_role_mapping`
--

DROP TABLE IF EXISTS `fed_user_role_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fed_user_role_mapping` (
  `ROLE_ID` varchar(36) NOT NULL,
  `USER_ID` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ROLE_ID`,`USER_ID`),
  KEY `IDX_FU_ROLE_MAPPING` (`USER_ID`,`ROLE_ID`),
  KEY `IDX_FU_ROLE_MAPPING_RU` (`REALM_ID`,`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fed_user_role_mapping`
--

LOCK TABLES `fed_user_role_mapping` WRITE;
/*!40000 ALTER TABLE `fed_user_role_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `fed_user_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `federated_identity`
--

DROP TABLE IF EXISTS `federated_identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `federated_identity` (
  `IDENTITY_PROVIDER` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `FEDERATED_USER_ID` varchar(255) DEFAULT NULL,
  `FEDERATED_USERNAME` varchar(255) DEFAULT NULL,
  `TOKEN` text DEFAULT NULL,
  `USER_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`IDENTITY_PROVIDER`,`USER_ID`),
  KEY `IDX_FEDIDENTITY_USER` (`USER_ID`),
  KEY `IDX_FEDIDENTITY_FEDUSER` (`FEDERATED_USER_ID`),
  CONSTRAINT `FK404288B92EF007A6` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `federated_identity`
--

LOCK TABLES `federated_identity` WRITE;
/*!40000 ALTER TABLE `federated_identity` DISABLE KEYS */;
/*!40000 ALTER TABLE `federated_identity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `federated_user`
--

DROP TABLE IF EXISTS `federated_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `federated_user` (
  `ID` varchar(255) NOT NULL,
  `STORAGE_PROVIDER_ID` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `federated_user`
--

LOCK TABLES `federated_user` WRITE;
/*!40000 ALTER TABLE `federated_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `federated_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_attribute`
--

DROP TABLE IF EXISTS `group_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_attribute` (
  `ID` varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here',
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `GROUP_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_GROUP_ATTR_GROUP` (`GROUP_ID`),
  CONSTRAINT `FK_GROUP_ATTRIBUTE_GROUP` FOREIGN KEY (`GROUP_ID`) REFERENCES `keycloak_group` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_attribute`
--

LOCK TABLES `group_attribute` WRITE;
/*!40000 ALTER TABLE `group_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_role_mapping`
--

DROP TABLE IF EXISTS `group_role_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_role_mapping` (
  `ROLE_ID` varchar(36) NOT NULL,
  `GROUP_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ROLE_ID`,`GROUP_ID`),
  KEY `IDX_GROUP_ROLE_MAPP_GROUP` (`GROUP_ID`),
  CONSTRAINT `FK_GROUP_ROLE_GROUP` FOREIGN KEY (`GROUP_ID`) REFERENCES `keycloak_group` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_role_mapping`
--

LOCK TABLES `group_role_mapping` WRITE;
/*!40000 ALTER TABLE `group_role_mapping` DISABLE KEYS */;
INSERT INTO `group_role_mapping` VALUES ('11a1c224-db64-4f7f-9a6d-12bf6bb29d4d','a6e31a93-1b89-48cd-aca6-a73c0e336088'),('e86e5a4d-8893-4664-aa99-03ee63cb8b7b','a6e31a93-1b89-48cd-aca6-a73c0e336088');
/*!40000 ALTER TABLE `group_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identity_provider`
--

DROP TABLE IF EXISTS `identity_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity_provider` (
  `INTERNAL_ID` varchar(36) NOT NULL,
  `ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `PROVIDER_ALIAS` varchar(255) DEFAULT NULL,
  `PROVIDER_ID` varchar(255) DEFAULT NULL,
  `STORE_TOKEN` bit(1) NOT NULL DEFAULT b'0',
  `AUTHENTICATE_BY_DEFAULT` bit(1) NOT NULL DEFAULT b'0',
  `REALM_ID` varchar(36) DEFAULT NULL,
  `ADD_TOKEN_ROLE` bit(1) NOT NULL DEFAULT b'1',
  `TRUST_EMAIL` bit(1) NOT NULL DEFAULT b'0',
  `FIRST_BROKER_LOGIN_FLOW_ID` varchar(36) DEFAULT NULL,
  `POST_BROKER_LOGIN_FLOW_ID` varchar(36) DEFAULT NULL,
  `PROVIDER_DISPLAY_NAME` varchar(255) DEFAULT NULL,
  `LINK_ONLY` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`INTERNAL_ID`),
  UNIQUE KEY `UK_2DAELWNIBJI49AVXSRTUF6XJ33` (`PROVIDER_ALIAS`,`REALM_ID`),
  KEY `IDX_IDENT_PROV_REALM` (`REALM_ID`),
  CONSTRAINT `FK2B4EBC52AE5C3B34` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identity_provider`
--

LOCK TABLES `identity_provider` WRITE;
/*!40000 ALTER TABLE `identity_provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identity_provider_config`
--

DROP TABLE IF EXISTS `identity_provider_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity_provider_config` (
  `IDENTITY_PROVIDER_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`IDENTITY_PROVIDER_ID`,`NAME`),
  CONSTRAINT `FKDC4897CF864C4E43` FOREIGN KEY (`IDENTITY_PROVIDER_ID`) REFERENCES `identity_provider` (`INTERNAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identity_provider_config`
--

LOCK TABLES `identity_provider_config` WRITE;
/*!40000 ALTER TABLE `identity_provider_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_provider_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identity_provider_mapper`
--

DROP TABLE IF EXISTS `identity_provider_mapper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity_provider_mapper` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `IDP_ALIAS` varchar(255) NOT NULL,
  `IDP_MAPPER_NAME` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_ID_PROV_MAPP_REALM` (`REALM_ID`),
  CONSTRAINT `FK_IDPM_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identity_provider_mapper`
--

LOCK TABLES `identity_provider_mapper` WRITE;
/*!40000 ALTER TABLE `identity_provider_mapper` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_provider_mapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `idp_mapper_config`
--

DROP TABLE IF EXISTS `idp_mapper_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idp_mapper_config` (
  `IDP_MAPPER_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`IDP_MAPPER_ID`,`NAME`),
  CONSTRAINT `FK_IDPMCONFIG` FOREIGN KEY (`IDP_MAPPER_ID`) REFERENCES `identity_provider_mapper` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `idp_mapper_config`
--

LOCK TABLES `idp_mapper_config` WRITE;
/*!40000 ALTER TABLE `idp_mapper_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `idp_mapper_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keycloak_group`
--

DROP TABLE IF EXISTS `keycloak_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keycloak_group` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `PARENT_GROUP` varchar(36) NOT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SIBLING_NAMES` (`REALM_ID`,`PARENT_GROUP`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keycloak_group`
--

LOCK TABLES `keycloak_group` WRITE;
/*!40000 ALTER TABLE `keycloak_group` DISABLE KEYS */;
INSERT INTO `keycloak_group` VALUES ('8ace60b0-9b66-4072-b37d-a6c896441df1','GROUP1',' ','sptek-cloud'),('adbe7ee1-8d33-450d-bd96-0416d5f77919','GROUP2',' ','sptek-cloud'),('a6e31a93-1b89-48cd-aca6-a73c0e336088','paas_group',' ','sptek-cloud'),('e761632f-755a-4046-af77-6e7ec3aeabc8','strato_paasportal_group',' ','sptek-cloud');
/*!40000 ALTER TABLE `keycloak_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keycloak_role`
--

DROP TABLE IF EXISTS `keycloak_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keycloak_role` (
  `ID` varchar(36) NOT NULL,
  `CLIENT_REALM_CONSTRAINT` varchar(255) DEFAULT NULL,
  `CLIENT_ROLE` bit(1) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `CLIENT` varchar(36) DEFAULT NULL,
  `REALM` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_J3RWUVD56ONTGSUHOGM184WW2-2` (`NAME`,`CLIENT_REALM_CONSTRAINT`),
  KEY `IDX_KEYCLOAK_ROLE_CLIENT` (`CLIENT`),
  KEY `IDX_KEYCLOAK_ROLE_REALM` (`REALM`),
  CONSTRAINT `FK_6VYQFE4CN4WLQ8R6KT5VDSJ5C` FOREIGN KEY (`REALM`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keycloak_role`
--

LOCK TABLES `keycloak_role` WRITE;
/*!40000 ALTER TABLE `keycloak_role` DISABLE KEYS */;
INSERT INTO `keycloak_role` VALUES ('06e47b09-9e28-4f58-8119-0ce07fc0d23c','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-authorization}','manage-authorization','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('08a3f504-63ee-428e-b54d-8d690a9b17fe','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-authorization}','view-authorization','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('08d028c7-1a22-456b-937f-9dff083aedaf','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-clients}','manage-clients','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('0a0c2f23-a29f-4379-abc5-b90d04d2178b','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_create-client}','create-client','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','sptek-cloud','\0','${role_default-roles}','default-roles-sptek-cloud','sptek-cloud',NULL,NULL),('0d91e0f3-4b4e-49fc-8bd4-08317ec2ede2','99f70571-5263-4b31-bbea-8b85911746b2','','${role_query-groups}','query-groups','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('0fe55562-1bcb-467b-a24a-41ef12ed323b','99f70571-5263-4b31-bbea-8b85911746b2','','${role_query-users}','query-users','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('116bf0bc-2d46-4c2b-8f6b-cf6af59469c5','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-clients}','manage-clients','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('11a1c224-db64-4f7f-9a6d-12bf6bb29d4d','sptek-cloud','\0',NULL,'paas_menu_02','sptek-cloud',NULL,NULL),('123ef918-1c37-4b03-8630-da5632c344ff','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_query-groups}','query-groups','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('1578f3d5-4e77-4ee4-b048-2240eda927b9','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-authorization}','view-authorization','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('19d650fb-4f0b-4a1b-a495-c7f2fa5d7292','12d7e401-eb29-4e31-866e-1efdfcd53787','','${role_read-token}','read-token','sptek-cloud','12d7e401-eb29-4e31-866e-1efdfcd53787',NULL),('19dd2e6f-a0c1-488b-bef6-724df229412d','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-identity-providers}','manage-identity-providers','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('274dbf04-939f-44ef-a788-355fb03708dc','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_view-profile}','view-profile','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('2d7850b6-2c0e-499c-9765-b4c74241b26a','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-events}','view-events','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','master','\0','${role_admin}','admin','master',NULL,NULL),('30328e06-9e6a-41e6-87cd-a5f46fa8ea89','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-authorization}','view-authorization','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('32217aa8-8c82-4540-8b35-ff966ab7e29b','291690ba-3798-45f3-8b01-f11293902cce','','${role_delete-account}','delete-account','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('33931711-efbf-4bd0-89af-9669ad2a7304','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-identity-providers}','view-identity-providers','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('3df8f447-9de8-44be-b5e8-08e12bce321e','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-events}','view-events','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('3ec99580-21ef-40c8-9ac0-0872d3c31a8c','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_delete-account}','delete-account','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('415323d2-1d26-46be-b13d-946f66c9da26','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-events}','view-events','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('4434128d-96b9-4b7c-be83-b5f90f369930','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_query-realms}','query-realms','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('4b9a026d-d677-446b-9dd3-edfbd5a521ec','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-clients}','view-clients','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('4d7389c1-0992-4445-8995-2618fdaf12a4','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_query-groups}','query-groups','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('4dcb923b-ebe5-4deb-a5d3-6159f4bb1469','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_view-consent}','view-consent','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('53dea1f2-1aff-463c-86c6-f1615a21b769','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-events}','manage-events','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('54a18124-892a-4dda-84f1-681433ef9cf7','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-realm}','view-realm','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('569f5620-2457-4300-a1ae-5563664ea639','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-events}','manage-events','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('57811beb-f592-4813-b248-6e7768a18d0f','99f70571-5263-4b31-bbea-8b85911746b2','','${role_query-realms}','query-realms','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('5d6afc9e-ce92-44a9-96f2-0dff68c4907d','master','\0','${role_create-realm}','create-realm','master',NULL,NULL),('5ee1bb6d-9d83-418a-9f6b-0f87d7d2fce9','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-clients}','manage-clients','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('5f2570d6-5428-4141-83ad-85615db0d40b','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-events}','manage-events','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('60a67a1d-6c98-49bb-acce-67f734c50430','291690ba-3798-45f3-8b01-f11293902cce','','${role_manage-consent}','manage-consent','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('6201ece4-b3fd-4168-804b-577b50b7d5c2','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_impersonation}','impersonation','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('6cf5d8ef-fad0-4405-9965-d80ba101b8bc','99f70571-5263-4b31-bbea-8b85911746b2','','${role_realm-admin}','realm-admin','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('6dc04c00-02ca-4f85-aa92-3a823e9cb61d','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-realm}','manage-realm','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('75661740-9e63-4f7c-9cd2-25753fcc27c6','99f70571-5263-4b31-bbea-8b85911746b2','','${role_manage-users}','manage-users','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('766bd181-d5df-425c-9f5f-32720c97e9b5','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-realm}','manage-realm','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('787262ef-53c8-48ad-a8bd-37ad6d630f98','99f70571-5263-4b31-bbea-8b85911746b2','','${role_query-clients}','query-clients','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('7cff74ed-f014-4e38-af2e-54d0c39922d6','99f70571-5263-4b31-bbea-8b85911746b2','','${role_impersonation}','impersonation','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('7ea12a40-6b19-4a12-9749-5bdaed9c3637','291690ba-3798-45f3-8b01-f11293902cce','','${role_view-consent}','view-consent','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('7f849712-2cf4-43e2-bab1-03500784699d','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-realm}','manage-realm','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('804389ac-a03c-4edb-9a7b-b5fcea9a782b','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_create-client}','create-client','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('804675ce-e2f2-45ee-a312-65bc858eafb2','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-users}','manage-users','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('81dd1228-a00c-4bfd-97b0-9512726a9885','sptek-cloud','\0','${role_offline-access}','offline_access','sptek-cloud',NULL,NULL),('8293ee01-f6c0-414b-8f89-554c2964a30a','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-users}','manage-users','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('840e634b-8173-4fd6-b59c-43b88c69701a','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-users}','view-users','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('8b8de234-d376-4792-b4ce-fc0b1c7a619a','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-realm}','view-realm','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('8d3441d6-6d84-43cf-ad7c-e0108cb7778d','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_query-clients}','query-clients','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('9061b134-77b1-43ef-b4bf-6bc3a02ab5e6','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-users}','view-users','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('918ad3bb-bd63-42bd-8fae-5ee9978ddbca','sptek-cloud','\0','포탈 관리자','PORTAL_ADMIN','sptek-cloud',NULL,NULL),('959c6c6d-238a-4de6-8681-f7d0cde3a088','sptek-cloud','\0','프로젝트 맴버','PROJECT_MEMBER','sptek-cloud',NULL,NULL),('9b3d73f1-9923-41e4-9d79-8f539047c5f3','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-clients}','view-clients','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('9df334ac-fbfe-47aa-9039-5613b7bd124d','291690ba-3798-45f3-8b01-f11293902cce','','${role_view-applications}','view-applications','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('a01d71e8-3719-4b55-9f70-b685f7ff0ff8','955e906d-aa0f-4d49-82f0-404d19080454','',NULL,'uma_protection','master','955e906d-aa0f-4d49-82f0-404d19080454',NULL),('a22609c6-f451-49b3-a4f0-93d8487090fc','sptek-cloud','\0','시스템 매니저','SYSTEM_ADMIN','sptek-cloud',NULL,NULL),('a4d42a54-eb0b-461b-ba7e-fae959da0e32','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_impersonation}','impersonation','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('a6ac2b20-2333-4cd5-9f5f-5db1ceba8bd0','291690ba-3798-45f3-8b01-f11293902cce','','${role_view-profile}','view-profile','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('a978c2b9-7e6c-4595-8f32-669996c5b871','master','\0','${role_offline-access}','offline_access','master',NULL,NULL),('aa0830c8-a135-45e5-8175-cda42cb03c5b','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_query-users}','query-users','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('adb0eb14-293c-4f5c-86e9-3b8de139fd14','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-realm}','view-realm','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('ae037b35-25ef-4de5-b4b6-9f0251cd0fed','master','\0','${role_uma_authorization}','uma_authorization','master',NULL,NULL),('b8a44e5a-3570-4e7a-8431-5439313d237d','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-identity-providers}','view-identity-providers','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('bf16dbc9-620c-48d5-8809-219c5167ec33','291690ba-3798-45f3-8b01-f11293902cce','','${role_manage-account-links}','manage-account-links','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('c4ea0c88-afe8-46d8-81cc-7b6e6bfd1468','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-authorization}','manage-authorization','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('c5e29044-0762-4c6a-858c-14d0f1249666','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_manage-consent}','manage-consent','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('c816e2c5-479e-4baa-8359-02f641a2397a','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_query-clients}','query-clients','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('c8345b16-9882-44f0-9812-84764ec84179','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-identity-providers}','manage-identity-providers','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('cc204501-ae67-4930-9bea-3b8cabde177e','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_manage-account-links}','manage-account-links','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('d4123ea2-cd39-401e-be92-d28d57fa6394','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_view-clients}','view-clients','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('d589c7fe-2950-4bb5-90d2-7f640d2b5c83','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_view-identity-providers}','view-identity-providers','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('d7e99464-f7e6-43fa-b579-6159e6ad48fc','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_query-users}','query-users','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('e08b8d31-d520-4d12-8e4e-233a33257409','master','\0','${role_default-roles}','default-roles-master','master',NULL,NULL),('e3d01ac5-fcd4-4616-9481-4781b7f215ae','sptek-cloud','\0','프로젝트 매니저','PROJECT_MANAGER','sptek-cloud',NULL,NULL),('e42b6717-1f17-49ed-92f6-5bb428ee0e37','291690ba-3798-45f3-8b01-f11293902cce','','${role_manage-account}','manage-account','sptek-cloud','291690ba-3798-45f3-8b01-f11293902cce',NULL),('e7e6b7aa-c395-4732-885e-4b04e5761564','5d5a2769-c432-4df2-b1f9-4ac13ac0463b','','${role_read-token}','read-token','master','5d5a2769-c432-4df2-b1f9-4ac13ac0463b',NULL),('e86e5a4d-8893-4664-aa99-03ee63cb8b7b','sptek-cloud','\0',NULL,'paas_menu_01','sptek-cloud',NULL,NULL),('ebc5cf6d-a90b-4e7c-84b2-5bbac9d6ee26','99f70571-5263-4b31-bbea-8b85911746b2','','${role_view-users}','view-users','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL),('ecd1fcb2-e5a7-4cc8-81c9-499e64e79014','sptek-cloud','\0','${role_uma_authorization}','uma_authorization','sptek-cloud',NULL,NULL),('eda43363-b524-457e-8690-abd92be59289','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_manage-account}','manage-account','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('efb693b3-f7a5-4f4d-98bd-910e6997d4bd','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_query-realms}','query-realms','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('f35cdf56-3e3d-44e6-816e-b88c09c8dfc5','41faae5b-259c-4c66-ad6d-9afada387cee','','${role_view-applications}','view-applications','master','41faae5b-259c-4c66-ad6d-9afada387cee',NULL),('f88ef2c5-a588-43de-85a2-1177143a9f40','c09cfaf6-b8eb-40f1-847b-03b534d85bbb','','${role_manage-identity-providers}','manage-identity-providers','master','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',NULL),('fae4d818-1d36-4397-bc23-0245019eb61d','fad7c87e-69c6-4163-a0dd-825c5276a923','','${role_manage-authorization}','manage-authorization','master','fad7c87e-69c6-4163-a0dd-825c5276a923',NULL),('fb4d8eca-2086-4b32-ab5b-80a753319b10','99f70571-5263-4b31-bbea-8b85911746b2','','${role_create-client}','create-client','sptek-cloud','99f70571-5263-4b31-bbea-8b85911746b2',NULL);
/*!40000 ALTER TABLE `keycloak_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `migration_model`
--

DROP TABLE IF EXISTS `migration_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `migration_model` (
  `ID` varchar(36) NOT NULL,
  `VERSION` varchar(36) DEFAULT NULL,
  `UPDATE_TIME` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID`),
  KEY `IDX_UPDATE_TIME` (`UPDATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migration_model`
--

LOCK TABLES `migration_model` WRITE;
/*!40000 ALTER TABLE `migration_model` DISABLE KEYS */;
INSERT INTO `migration_model` VALUES ('8753p','16.1.0',1644385304);
/*!40000 ALTER TABLE `migration_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offline_client_session`
--

DROP TABLE IF EXISTS `offline_client_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_client_session` (
  `USER_SESSION_ID` varchar(36) NOT NULL,
  `CLIENT_ID` varchar(255) NOT NULL,
  `OFFLINE_FLAG` varchar(4) NOT NULL,
  `TIMESTAMP` int(11) DEFAULT NULL,
  `DATA` longtext DEFAULT NULL,
  `CLIENT_STORAGE_PROVIDER` varchar(36) NOT NULL DEFAULT 'local',
  `EXTERNAL_CLIENT_ID` varchar(255) NOT NULL DEFAULT 'local',
  PRIMARY KEY (`USER_SESSION_ID`,`CLIENT_ID`,`CLIENT_STORAGE_PROVIDER`,`EXTERNAL_CLIENT_ID`,`OFFLINE_FLAG`),
  KEY `IDX_US_SESS_ID_ON_CL_SESS` (`USER_SESSION_ID`),
  KEY `IDX_OFFLINE_CSS_PRELOAD` (`CLIENT_ID`,`OFFLINE_FLAG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offline_client_session`
--

LOCK TABLES `offline_client_session` WRITE;
/*!40000 ALTER TABLE `offline_client_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `offline_client_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offline_user_session`
--

DROP TABLE IF EXISTS `offline_user_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_user_session` (
  `USER_SESSION_ID` varchar(36) NOT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `CREATED_ON` int(11) NOT NULL,
  `OFFLINE_FLAG` varchar(4) NOT NULL,
  `DATA` longtext DEFAULT NULL,
  `LAST_SESSION_REFRESH` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`USER_SESSION_ID`,`OFFLINE_FLAG`),
  KEY `IDX_OFFLINE_USS_CREATEDON` (`CREATED_ON`),
  KEY `IDX_OFFLINE_USS_PRELOAD` (`OFFLINE_FLAG`,`CREATED_ON`,`USER_SESSION_ID`),
  KEY `IDX_OFFLINE_USS_BY_USER` (`USER_ID`,`REALM_ID`,`OFFLINE_FLAG`),
  KEY `IDX_OFFLINE_USS_BY_USERSESS` (`REALM_ID`,`OFFLINE_FLAG`,`USER_SESSION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offline_user_session`
--

LOCK TABLES `offline_user_session` WRITE;
/*!40000 ALTER TABLE `offline_user_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `offline_user_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `policy_config`
--

DROP TABLE IF EXISTS `policy_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy_config` (
  `POLICY_ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  PRIMARY KEY (`POLICY_ID`,`NAME`),
  CONSTRAINT `FKDC34197CF864C4E43` FOREIGN KEY (`POLICY_ID`) REFERENCES `resource_server_policy` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_config`
--

LOCK TABLES `policy_config` WRITE;
/*!40000 ALTER TABLE `policy_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `policy_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `protocol_mapper`
--

DROP TABLE IF EXISTS `protocol_mapper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol_mapper` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `PROTOCOL` varchar(255) NOT NULL,
  `PROTOCOL_MAPPER_NAME` varchar(255) NOT NULL,
  `CLIENT_ID` varchar(36) DEFAULT NULL,
  `CLIENT_SCOPE_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_PROTOCOL_MAPPER_CLIENT` (`CLIENT_ID`),
  KEY `IDX_CLSCOPE_PROTMAP` (`CLIENT_SCOPE_ID`),
  CONSTRAINT `FK_CLI_SCOPE_MAPPER` FOREIGN KEY (`CLIENT_SCOPE_ID`) REFERENCES `client_scope` (`ID`),
  CONSTRAINT `FK_PCM_REALM` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `protocol_mapper`
--

LOCK TABLES `protocol_mapper` WRITE;
/*!40000 ALTER TABLE `protocol_mapper` DISABLE KEYS */;
INSERT INTO `protocol_mapper` VALUES ('001c0995-0068-46c2-8864-cc4b7f7e71c5','locale','openid-connect','oidc-usermodel-attribute-mapper','470a4a8a-3268-49ff-81b1-bb2c6dfda5ff',NULL),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','phone number verified','openid-connect','oidc-usermodel-attribute-mapper',NULL,'d05cae1b-d92b-4abe-b7db-a201046a6662'),('04bbdb2d-a48f-4f64-be82-801dfe757234','Client IP Address','openid-connect','oidc-usersessionmodel-note-mapper','955e906d-aa0f-4d49-82f0-404d19080454',NULL),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','Client ID','openid-connect','oidc-usersessionmodel-note-mapper','955e906d-aa0f-4d49-82f0-404d19080454',NULL),('12fa625e-05c5-4887-aa90-eada447677de','groups','openid-connect','oidc-usermodel-realm-role-mapper',NULL,'425bbf94-3e2a-4d4a-b66a-e68d5b47e38e'),('175e0935-e09b-4b56-a434-f3b1f0aeb975','allowed web origins','openid-connect','oidc-allowed-origins-mapper',NULL,'138be55b-dbeb-4e9f-951e-ea982198a1bd'),('18b365d5-2e81-493e-aa01-a699cc86120d','Client Host','openid-connect','oidc-usersessionmodel-note-mapper','955e906d-aa0f-4d49-82f0-404d19080454',NULL),('19e0d401-002e-4137-9904-3c174bcaf765','email','openid-connect','oidc-usermodel-property-mapper',NULL,'eac1edda-84b5-45fd-9845-4fd7421c8d6b'),('1a761443-d467-4de2-800d-14e765739ebf','gender','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('28f67292-468d-4294-8616-cdee6c5f9a71','family name','openid-connect','oidc-usermodel-property-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','zoneinfo','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('323f2d16-f3ed-44bc-8929-3be3d253522f','birthdate','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','picture','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('3dbeb18a-9d35-4ff2-a596-2b6e45ae7c66','audience resolve','openid-connect','oidc-audience-resolve-mapper','0fcab411-7988-4552-aa1a-bf8d87f77a81',NULL),('486ddab7-ed4e-4083-87f8-f960d5fcf214','role list','saml','saml-role-list-mapper',NULL,'e0e3ea89-7e94-4170-b3f8-394c82b7d95d'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','realm roles','openid-connect','oidc-usermodel-realm-role-mapper',NULL,'70b4c562-d08a-42e3-a106-d75e23822b20'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','email verified','openid-connect','oidc-usermodel-property-mapper',NULL,'eac1edda-84b5-45fd-9845-4fd7421c8d6b'),('53a5dc2b-3e26-424b-a685-b063c3cce161','middle name','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','website','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('5acc7456-99b3-4e53-a61d-277178792e90','updated at','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('5ca9dbd6-fc94-464f-9278-db07656f85c0','full name','openid-connect','oidc-full-name-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('5d3b692d-9285-49bd-a453-d0765276bb23','phone number','openid-connect','oidc-usermodel-attribute-mapper',NULL,'bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc'),('5ee78917-d800-4735-968a-a66fc24e97b0','full name','openid-connect','oidc-full-name-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('64827743-a70d-4833-a7d8-904ed422b24a','middle name','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('68de005e-537e-45ee-9e1b-5bc6b0cbc027','audience resolve','openid-connect','oidc-audience-resolve-mapper','da1e37ec-b140-4527-afcd-50cd0eceaed7',NULL),('69fa8ea6-5593-40fa-bb26-d77712934252','updated at','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','upn','openid-connect','oidc-usermodel-property-mapper',NULL,'2f5372a0-234e-45ed-8bd3-e6f4047fe25f'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','locale','openid-connect','oidc-usermodel-attribute-mapper','5db6df3c-92fb-40c0-a60d-ecdaa5827c59',NULL),('701057b5-4e77-4331-b6b0-9d7059b023ed','profile','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('7037762d-79c1-4d07-8764-9ed8396ae555','realm roles','openid-connect','oidc-usermodel-realm-role-mapper',NULL,'0c5a33f2-15c0-44b9-a387-190556db824d'),('72d9add0-3318-4ff2-bc1c-82c2c439c34d','role list','saml','saml-role-list-mapper',NULL,'e2d2d02e-8d54-4fbf-bed3-f26df4f5d8ab'),('7834d457-048d-47ae-98c5-f1f717309942','given name','openid-connect','oidc-usermodel-property-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','client roles','openid-connect','oidc-usermodel-client-role-mapper',NULL,'70b4c562-d08a-42e3-a106-d75e23822b20'),('7e778f1d-0589-4114-b1ba-c59027c3823f','email verified','openid-connect','oidc-usermodel-property-mapper',NULL,'3b2dfded-98d7-4c9b-bee2-b7c65e98094c'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','nickname','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('95444849-2a41-40a8-9a87-03e82e823861','website','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('96937063-d091-4e9f-a036-23d19f9b31ff','username','openid-connect','oidc-usermodel-property-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('9be3a894-f467-4443-abb0-8f1df1970059','username','openid-connect','oidc-usermodel-property-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('a0e75ae7-c4fd-43fe-afb8-a2be6f5cef4d','allowed web origins','openid-connect','oidc-allowed-origins-mapper',NULL,'b2d2c539-8634-42a6-be33-f82ee1053d1a'),('a22dd159-3992-459e-b6db-6c9786244e1b','upn','openid-connect','oidc-usermodel-property-mapper',NULL,'425bbf94-3e2a-4d4a-b66a-e68d5b47e38e'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','address','openid-connect','oidc-address-mapper',NULL,'a954b1f3-be84-4af2-9ed4-869079942000'),('a978154e-1b22-466c-aadd-1ea1fb286e98','zoneinfo','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('abdf253f-906d-49da-a16b-97e5271dcaea','birthdate','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','client roles','openid-connect','oidc-usermodel-client-role-mapper',NULL,'0c5a33f2-15c0-44b9-a387-190556db824d'),('afed58ca-98d8-4395-886d-4132858f6c2b','audience resolve','openid-connect','oidc-audience-resolve-mapper',NULL,'70b4c562-d08a-42e3-a106-d75e23822b20'),('b42c839d-456f-4d20-b60b-ad408940bd9f','picture','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('bbdb4fc4-1db8-4536-bc84-04b05bede6e7','audience resolve','openid-connect','oidc-audience-resolve-mapper',NULL,'0c5a33f2-15c0-44b9-a387-190556db824d'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','family name','openid-connect','oidc-usermodel-property-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('c7e339f1-9096-4e1f-b715-4b306c209c06','given name','openid-connect','oidc-usermodel-property-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','locale','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','phone number verified','openid-connect','oidc-usermodel-attribute-mapper',NULL,'bae0e7eb-5ef3-4596-9d77-e87c36d0a0cc'),('da990979-d54f-4d43-b0bc-a97c7e33e803','phone number','openid-connect','oidc-usermodel-attribute-mapper',NULL,'d05cae1b-d92b-4abe-b7db-a201046a6662'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','nickname','openid-connect','oidc-usermodel-attribute-mapper',NULL,'04297489-3e8c-4dc8-9ca0-bf26e5ec3f77'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','locale','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','gender','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','address','openid-connect','oidc-address-mapper',NULL,'d041a31f-8104-448a-85dc-3fc0c7bb7c88'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','groups','openid-connect','oidc-usermodel-realm-role-mapper',NULL,'2f5372a0-234e-45ed-8bd3-e6f4047fe25f'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','profile','openid-connect','oidc-usermodel-attribute-mapper',NULL,'903b4c80-e49d-4e5c-82ad-291d6fca7046'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','email','openid-connect','oidc-usermodel-property-mapper',NULL,'3b2dfded-98d7-4c9b-bee2-b7c65e98094c');
/*!40000 ALTER TABLE `protocol_mapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `protocol_mapper_config`
--

DROP TABLE IF EXISTS `protocol_mapper_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol_mapper_config` (
  `PROTOCOL_MAPPER_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`PROTOCOL_MAPPER_ID`,`NAME`),
  CONSTRAINT `FK_PMCONFIG` FOREIGN KEY (`PROTOCOL_MAPPER_ID`) REFERENCES `protocol_mapper` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `protocol_mapper_config`
--

LOCK TABLES `protocol_mapper_config` WRITE;
/*!40000 ALTER TABLE `protocol_mapper_config` DISABLE KEYS */;
INSERT INTO `protocol_mapper_config` VALUES ('001c0995-0068-46c2-8864-cc4b7f7e71c5','true','access.token.claim'),('001c0995-0068-46c2-8864-cc4b7f7e71c5','locale','claim.name'),('001c0995-0068-46c2-8864-cc4b7f7e71c5','true','id.token.claim'),('001c0995-0068-46c2-8864-cc4b7f7e71c5','String','jsonType.label'),('001c0995-0068-46c2-8864-cc4b7f7e71c5','locale','user.attribute'),('001c0995-0068-46c2-8864-cc4b7f7e71c5','true','userinfo.token.claim'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','true','access.token.claim'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','phone_number_verified','claim.name'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','true','id.token.claim'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','boolean','jsonType.label'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','phoneNumberVerified','user.attribute'),('0408cb53-9a07-44fb-9e0a-0c2d160629a0','true','userinfo.token.claim'),('04bbdb2d-a48f-4f64-be82-801dfe757234','true','access.token.claim'),('04bbdb2d-a48f-4f64-be82-801dfe757234','clientAddress','claim.name'),('04bbdb2d-a48f-4f64-be82-801dfe757234','true','id.token.claim'),('04bbdb2d-a48f-4f64-be82-801dfe757234','String','jsonType.label'),('04bbdb2d-a48f-4f64-be82-801dfe757234','clientAddress','user.session.note'),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','true','access.token.claim'),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','clientId','claim.name'),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','true','id.token.claim'),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','String','jsonType.label'),('0c8d9548-3c19-441f-a45b-3a9c3c574cd5','clientId','user.session.note'),('12fa625e-05c5-4887-aa90-eada447677de','true','access.token.claim'),('12fa625e-05c5-4887-aa90-eada447677de','groups','claim.name'),('12fa625e-05c5-4887-aa90-eada447677de','true','id.token.claim'),('12fa625e-05c5-4887-aa90-eada447677de','String','jsonType.label'),('12fa625e-05c5-4887-aa90-eada447677de','true','multivalued'),('12fa625e-05c5-4887-aa90-eada447677de','foo','user.attribute'),('18b365d5-2e81-493e-aa01-a699cc86120d','true','access.token.claim'),('18b365d5-2e81-493e-aa01-a699cc86120d','clientHost','claim.name'),('18b365d5-2e81-493e-aa01-a699cc86120d','true','id.token.claim'),('18b365d5-2e81-493e-aa01-a699cc86120d','String','jsonType.label'),('18b365d5-2e81-493e-aa01-a699cc86120d','clientHost','user.session.note'),('19e0d401-002e-4137-9904-3c174bcaf765','true','access.token.claim'),('19e0d401-002e-4137-9904-3c174bcaf765','email','claim.name'),('19e0d401-002e-4137-9904-3c174bcaf765','true','id.token.claim'),('19e0d401-002e-4137-9904-3c174bcaf765','String','jsonType.label'),('19e0d401-002e-4137-9904-3c174bcaf765','email','user.attribute'),('19e0d401-002e-4137-9904-3c174bcaf765','true','userinfo.token.claim'),('1a761443-d467-4de2-800d-14e765739ebf','true','access.token.claim'),('1a761443-d467-4de2-800d-14e765739ebf','gender','claim.name'),('1a761443-d467-4de2-800d-14e765739ebf','true','id.token.claim'),('1a761443-d467-4de2-800d-14e765739ebf','String','jsonType.label'),('1a761443-d467-4de2-800d-14e765739ebf','gender','user.attribute'),('1a761443-d467-4de2-800d-14e765739ebf','true','userinfo.token.claim'),('28f67292-468d-4294-8616-cdee6c5f9a71','true','access.token.claim'),('28f67292-468d-4294-8616-cdee6c5f9a71','family_name','claim.name'),('28f67292-468d-4294-8616-cdee6c5f9a71','true','id.token.claim'),('28f67292-468d-4294-8616-cdee6c5f9a71','String','jsonType.label'),('28f67292-468d-4294-8616-cdee6c5f9a71','lastName','user.attribute'),('28f67292-468d-4294-8616-cdee6c5f9a71','true','userinfo.token.claim'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','true','access.token.claim'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','zoneinfo','claim.name'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','true','id.token.claim'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','String','jsonType.label'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','zoneinfo','user.attribute'),('2b9c58dc-ead8-4540-b610-aeedcbe2f746','true','userinfo.token.claim'),('323f2d16-f3ed-44bc-8929-3be3d253522f','true','access.token.claim'),('323f2d16-f3ed-44bc-8929-3be3d253522f','birthdate','claim.name'),('323f2d16-f3ed-44bc-8929-3be3d253522f','true','id.token.claim'),('323f2d16-f3ed-44bc-8929-3be3d253522f','String','jsonType.label'),('323f2d16-f3ed-44bc-8929-3be3d253522f','birthdate','user.attribute'),('323f2d16-f3ed-44bc-8929-3be3d253522f','true','userinfo.token.claim'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','true','access.token.claim'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','picture','claim.name'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','true','id.token.claim'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','String','jsonType.label'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','picture','user.attribute'),('3c4771a0-0c28-460e-acbb-d93ab81ab0bc','true','userinfo.token.claim'),('486ddab7-ed4e-4083-87f8-f960d5fcf214','Role','attribute.name'),('486ddab7-ed4e-4083-87f8-f960d5fcf214','Basic','attribute.nameformat'),('486ddab7-ed4e-4083-87f8-f960d5fcf214','false','single'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','true','access.token.claim'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','realm_access.roles','claim.name'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','String','jsonType.label'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','true','multivalued'),('492e3e3f-254e-4c1f-8318-0f017cc4595c','foo','user.attribute'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','true','access.token.claim'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','email_verified','claim.name'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','true','id.token.claim'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','boolean','jsonType.label'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','emailVerified','user.attribute'),('4ce0ff8a-2ab4-49df-a152-42ede26b5de1','true','userinfo.token.claim'),('53a5dc2b-3e26-424b-a685-b063c3cce161','true','access.token.claim'),('53a5dc2b-3e26-424b-a685-b063c3cce161','middle_name','claim.name'),('53a5dc2b-3e26-424b-a685-b063c3cce161','true','id.token.claim'),('53a5dc2b-3e26-424b-a685-b063c3cce161','String','jsonType.label'),('53a5dc2b-3e26-424b-a685-b063c3cce161','middleName','user.attribute'),('53a5dc2b-3e26-424b-a685-b063c3cce161','true','userinfo.token.claim'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','true','access.token.claim'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','website','claim.name'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','true','id.token.claim'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','String','jsonType.label'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','website','user.attribute'),('5995ab0a-f28b-43f3-b96d-0995aa2dbe51','true','userinfo.token.claim'),('5acc7456-99b3-4e53-a61d-277178792e90','true','access.token.claim'),('5acc7456-99b3-4e53-a61d-277178792e90','updated_at','claim.name'),('5acc7456-99b3-4e53-a61d-277178792e90','true','id.token.claim'),('5acc7456-99b3-4e53-a61d-277178792e90','String','jsonType.label'),('5acc7456-99b3-4e53-a61d-277178792e90','updatedAt','user.attribute'),('5acc7456-99b3-4e53-a61d-277178792e90','true','userinfo.token.claim'),('5ca9dbd6-fc94-464f-9278-db07656f85c0','true','access.token.claim'),('5ca9dbd6-fc94-464f-9278-db07656f85c0','true','id.token.claim'),('5ca9dbd6-fc94-464f-9278-db07656f85c0','true','userinfo.token.claim'),('5d3b692d-9285-49bd-a453-d0765276bb23','true','access.token.claim'),('5d3b692d-9285-49bd-a453-d0765276bb23','phone_number','claim.name'),('5d3b692d-9285-49bd-a453-d0765276bb23','true','id.token.claim'),('5d3b692d-9285-49bd-a453-d0765276bb23','String','jsonType.label'),('5d3b692d-9285-49bd-a453-d0765276bb23','phoneNumber','user.attribute'),('5d3b692d-9285-49bd-a453-d0765276bb23','true','userinfo.token.claim'),('5ee78917-d800-4735-968a-a66fc24e97b0','true','access.token.claim'),('5ee78917-d800-4735-968a-a66fc24e97b0','true','id.token.claim'),('5ee78917-d800-4735-968a-a66fc24e97b0','true','userinfo.token.claim'),('64827743-a70d-4833-a7d8-904ed422b24a','true','access.token.claim'),('64827743-a70d-4833-a7d8-904ed422b24a','middle_name','claim.name'),('64827743-a70d-4833-a7d8-904ed422b24a','true','id.token.claim'),('64827743-a70d-4833-a7d8-904ed422b24a','String','jsonType.label'),('64827743-a70d-4833-a7d8-904ed422b24a','middleName','user.attribute'),('64827743-a70d-4833-a7d8-904ed422b24a','true','userinfo.token.claim'),('69fa8ea6-5593-40fa-bb26-d77712934252','true','access.token.claim'),('69fa8ea6-5593-40fa-bb26-d77712934252','updated_at','claim.name'),('69fa8ea6-5593-40fa-bb26-d77712934252','true','id.token.claim'),('69fa8ea6-5593-40fa-bb26-d77712934252','String','jsonType.label'),('69fa8ea6-5593-40fa-bb26-d77712934252','updatedAt','user.attribute'),('69fa8ea6-5593-40fa-bb26-d77712934252','true','userinfo.token.claim'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','true','access.token.claim'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','upn','claim.name'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','true','id.token.claim'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','String','jsonType.label'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','username','user.attribute'),('6ce59d8a-05d5-4e1b-a625-a80fec19bd47','true','userinfo.token.claim'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','true','access.token.claim'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','locale','claim.name'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','true','id.token.claim'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','String','jsonType.label'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','locale','user.attribute'),('6df9a7be-a973-4eec-b38d-f788a1ec7d95','true','userinfo.token.claim'),('701057b5-4e77-4331-b6b0-9d7059b023ed','true','access.token.claim'),('701057b5-4e77-4331-b6b0-9d7059b023ed','profile','claim.name'),('701057b5-4e77-4331-b6b0-9d7059b023ed','true','id.token.claim'),('701057b5-4e77-4331-b6b0-9d7059b023ed','String','jsonType.label'),('701057b5-4e77-4331-b6b0-9d7059b023ed','profile','user.attribute'),('701057b5-4e77-4331-b6b0-9d7059b023ed','true','userinfo.token.claim'),('7037762d-79c1-4d07-8764-9ed8396ae555','true','access.token.claim'),('7037762d-79c1-4d07-8764-9ed8396ae555','realm_access.roles','claim.name'),('7037762d-79c1-4d07-8764-9ed8396ae555','String','jsonType.label'),('7037762d-79c1-4d07-8764-9ed8396ae555','true','multivalued'),('7037762d-79c1-4d07-8764-9ed8396ae555','foo','user.attribute'),('72d9add0-3318-4ff2-bc1c-82c2c439c34d','Role','attribute.name'),('72d9add0-3318-4ff2-bc1c-82c2c439c34d','Basic','attribute.nameformat'),('72d9add0-3318-4ff2-bc1c-82c2c439c34d','false','single'),('7834d457-048d-47ae-98c5-f1f717309942','true','access.token.claim'),('7834d457-048d-47ae-98c5-f1f717309942','given_name','claim.name'),('7834d457-048d-47ae-98c5-f1f717309942','true','id.token.claim'),('7834d457-048d-47ae-98c5-f1f717309942','String','jsonType.label'),('7834d457-048d-47ae-98c5-f1f717309942','firstName','user.attribute'),('7834d457-048d-47ae-98c5-f1f717309942','true','userinfo.token.claim'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','true','access.token.claim'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','resource_access.${client_id}.roles','claim.name'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','String','jsonType.label'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','true','multivalued'),('7b644f33-6c0a-49d7-9bd7-f921ec5ed061','foo','user.attribute'),('7e778f1d-0589-4114-b1ba-c59027c3823f','true','access.token.claim'),('7e778f1d-0589-4114-b1ba-c59027c3823f','email_verified','claim.name'),('7e778f1d-0589-4114-b1ba-c59027c3823f','true','id.token.claim'),('7e778f1d-0589-4114-b1ba-c59027c3823f','boolean','jsonType.label'),('7e778f1d-0589-4114-b1ba-c59027c3823f','emailVerified','user.attribute'),('7e778f1d-0589-4114-b1ba-c59027c3823f','true','userinfo.token.claim'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','true','access.token.claim'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','nickname','claim.name'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','true','id.token.claim'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','String','jsonType.label'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','nickname','user.attribute'),('918623a1-6401-4e33-bcf1-de1bd1fa5fd8','true','userinfo.token.claim'),('95444849-2a41-40a8-9a87-03e82e823861','true','access.token.claim'),('95444849-2a41-40a8-9a87-03e82e823861','website','claim.name'),('95444849-2a41-40a8-9a87-03e82e823861','true','id.token.claim'),('95444849-2a41-40a8-9a87-03e82e823861','String','jsonType.label'),('95444849-2a41-40a8-9a87-03e82e823861','website','user.attribute'),('95444849-2a41-40a8-9a87-03e82e823861','true','userinfo.token.claim'),('96937063-d091-4e9f-a036-23d19f9b31ff','true','access.token.claim'),('96937063-d091-4e9f-a036-23d19f9b31ff','preferred_username','claim.name'),('96937063-d091-4e9f-a036-23d19f9b31ff','true','id.token.claim'),('96937063-d091-4e9f-a036-23d19f9b31ff','String','jsonType.label'),('96937063-d091-4e9f-a036-23d19f9b31ff','username','user.attribute'),('96937063-d091-4e9f-a036-23d19f9b31ff','true','userinfo.token.claim'),('9be3a894-f467-4443-abb0-8f1df1970059','true','access.token.claim'),('9be3a894-f467-4443-abb0-8f1df1970059','preferred_username','claim.name'),('9be3a894-f467-4443-abb0-8f1df1970059','true','id.token.claim'),('9be3a894-f467-4443-abb0-8f1df1970059','String','jsonType.label'),('9be3a894-f467-4443-abb0-8f1df1970059','username','user.attribute'),('9be3a894-f467-4443-abb0-8f1df1970059','true','userinfo.token.claim'),('a22dd159-3992-459e-b6db-6c9786244e1b','true','access.token.claim'),('a22dd159-3992-459e-b6db-6c9786244e1b','upn','claim.name'),('a22dd159-3992-459e-b6db-6c9786244e1b','true','id.token.claim'),('a22dd159-3992-459e-b6db-6c9786244e1b','String','jsonType.label'),('a22dd159-3992-459e-b6db-6c9786244e1b','username','user.attribute'),('a22dd159-3992-459e-b6db-6c9786244e1b','true','userinfo.token.claim'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','true','access.token.claim'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','true','id.token.claim'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','country','user.attribute.country'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','formatted','user.attribute.formatted'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','locality','user.attribute.locality'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','postal_code','user.attribute.postal_code'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','region','user.attribute.region'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','street','user.attribute.street'),('a2cb6a3d-5fc5-4365-90c2-397244552d1c','true','userinfo.token.claim'),('a978154e-1b22-466c-aadd-1ea1fb286e98','true','access.token.claim'),('a978154e-1b22-466c-aadd-1ea1fb286e98','zoneinfo','claim.name'),('a978154e-1b22-466c-aadd-1ea1fb286e98','true','id.token.claim'),('a978154e-1b22-466c-aadd-1ea1fb286e98','String','jsonType.label'),('a978154e-1b22-466c-aadd-1ea1fb286e98','zoneinfo','user.attribute'),('a978154e-1b22-466c-aadd-1ea1fb286e98','true','userinfo.token.claim'),('abdf253f-906d-49da-a16b-97e5271dcaea','true','access.token.claim'),('abdf253f-906d-49da-a16b-97e5271dcaea','birthdate','claim.name'),('abdf253f-906d-49da-a16b-97e5271dcaea','true','id.token.claim'),('abdf253f-906d-49da-a16b-97e5271dcaea','String','jsonType.label'),('abdf253f-906d-49da-a16b-97e5271dcaea','birthdate','user.attribute'),('abdf253f-906d-49da-a16b-97e5271dcaea','true','userinfo.token.claim'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','true','access.token.claim'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','resource_access.${client_id}.roles','claim.name'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','String','jsonType.label'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','true','multivalued'),('ad8968a4-f478-4a9e-a8fb-938aa08d1d89','foo','user.attribute'),('b42c839d-456f-4d20-b60b-ad408940bd9f','true','access.token.claim'),('b42c839d-456f-4d20-b60b-ad408940bd9f','picture','claim.name'),('b42c839d-456f-4d20-b60b-ad408940bd9f','true','id.token.claim'),('b42c839d-456f-4d20-b60b-ad408940bd9f','String','jsonType.label'),('b42c839d-456f-4d20-b60b-ad408940bd9f','picture','user.attribute'),('b42c839d-456f-4d20-b60b-ad408940bd9f','true','userinfo.token.claim'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','true','access.token.claim'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','family_name','claim.name'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','true','id.token.claim'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','String','jsonType.label'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','lastName','user.attribute'),('c55292eb-9de9-4f4d-805d-916b69f8c66c','true','userinfo.token.claim'),('c7e339f1-9096-4e1f-b715-4b306c209c06','true','access.token.claim'),('c7e339f1-9096-4e1f-b715-4b306c209c06','given_name','claim.name'),('c7e339f1-9096-4e1f-b715-4b306c209c06','true','id.token.claim'),('c7e339f1-9096-4e1f-b715-4b306c209c06','String','jsonType.label'),('c7e339f1-9096-4e1f-b715-4b306c209c06','firstName','user.attribute'),('c7e339f1-9096-4e1f-b715-4b306c209c06','true','userinfo.token.claim'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','true','access.token.claim'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','locale','claim.name'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','true','id.token.claim'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','String','jsonType.label'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','locale','user.attribute'),('c8ae47a1-6259-4e66-8178-7def0a63eb34','true','userinfo.token.claim'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','true','access.token.claim'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','phone_number_verified','claim.name'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','true','id.token.claim'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','boolean','jsonType.label'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','phoneNumberVerified','user.attribute'),('d4bf6c76-0cbf-4d35-9f39-46edde44728c','true','userinfo.token.claim'),('da990979-d54f-4d43-b0bc-a97c7e33e803','true','access.token.claim'),('da990979-d54f-4d43-b0bc-a97c7e33e803','phone_number','claim.name'),('da990979-d54f-4d43-b0bc-a97c7e33e803','true','id.token.claim'),('da990979-d54f-4d43-b0bc-a97c7e33e803','String','jsonType.label'),('da990979-d54f-4d43-b0bc-a97c7e33e803','phoneNumber','user.attribute'),('da990979-d54f-4d43-b0bc-a97c7e33e803','true','userinfo.token.claim'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','true','access.token.claim'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','nickname','claim.name'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','true','id.token.claim'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','String','jsonType.label'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','nickname','user.attribute'),('e0305d7f-61cf-4d72-952a-dc9f31b9d322','true','userinfo.token.claim'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','true','access.token.claim'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','locale','claim.name'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','true','id.token.claim'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','String','jsonType.label'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','locale','user.attribute'),('ecccbe94-5610-4d3a-b3e9-b16014de4bdd','true','userinfo.token.claim'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','true','access.token.claim'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','gender','claim.name'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','true','id.token.claim'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','String','jsonType.label'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','gender','user.attribute'),('edefe5a6-8d12-4509-93c9-de35a4ce3985','true','userinfo.token.claim'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','true','access.token.claim'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','true','id.token.claim'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','country','user.attribute.country'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','formatted','user.attribute.formatted'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','locality','user.attribute.locality'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','postal_code','user.attribute.postal_code'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','region','user.attribute.region'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','street','user.attribute.street'),('f24012f7-2081-45c4-91ab-9301f2ac3fa4','true','userinfo.token.claim'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','true','access.token.claim'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','groups','claim.name'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','true','id.token.claim'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','String','jsonType.label'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','true','multivalued'),('f3002b43-f456-43c5-b53e-b6d7b6484b18','foo','user.attribute'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','true','access.token.claim'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','profile','claim.name'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','true','id.token.claim'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','String','jsonType.label'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','profile','user.attribute'),('f5db85ca-037b-4ba3-95bf-0508d35cbb5a','true','userinfo.token.claim'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','true','access.token.claim'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','email','claim.name'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','true','id.token.claim'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','String','jsonType.label'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','email','user.attribute'),('fdb0cec1-68ff-4ad2-95fb-0899f0079dfb','true','userinfo.token.claim');
/*!40000 ALTER TABLE `protocol_mapper_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm`
--

DROP TABLE IF EXISTS `realm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm` (
  `ID` varchar(36) NOT NULL,
  `ACCESS_CODE_LIFESPAN` int(11) DEFAULT NULL,
  `USER_ACTION_LIFESPAN` int(11) DEFAULT NULL,
  `ACCESS_TOKEN_LIFESPAN` int(11) DEFAULT NULL,
  `ACCOUNT_THEME` varchar(255) DEFAULT NULL,
  `ADMIN_THEME` varchar(255) DEFAULT NULL,
  `EMAIL_THEME` varchar(255) DEFAULT NULL,
  `ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `EVENTS_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `EVENTS_EXPIRATION` bigint(20) DEFAULT NULL,
  `LOGIN_THEME` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `NOT_BEFORE` int(11) DEFAULT NULL,
  `PASSWORD_POLICY` text DEFAULT NULL,
  `REGISTRATION_ALLOWED` bit(1) NOT NULL DEFAULT b'0',
  `REMEMBER_ME` bit(1) NOT NULL DEFAULT b'0',
  `RESET_PASSWORD_ALLOWED` bit(1) NOT NULL DEFAULT b'0',
  `SOCIAL` bit(1) NOT NULL DEFAULT b'0',
  `SSL_REQUIRED` varchar(255) DEFAULT NULL,
  `SSO_IDLE_TIMEOUT` int(11) DEFAULT NULL,
  `SSO_MAX_LIFESPAN` int(11) DEFAULT NULL,
  `UPDATE_PROFILE_ON_SOC_LOGIN` bit(1) NOT NULL DEFAULT b'0',
  `VERIFY_EMAIL` bit(1) NOT NULL DEFAULT b'0',
  `MASTER_ADMIN_CLIENT` varchar(36) DEFAULT NULL,
  `LOGIN_LIFESPAN` int(11) DEFAULT NULL,
  `INTERNATIONALIZATION_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `DEFAULT_LOCALE` varchar(255) DEFAULT NULL,
  `REG_EMAIL_AS_USERNAME` bit(1) NOT NULL DEFAULT b'0',
  `ADMIN_EVENTS_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `ADMIN_EVENTS_DETAILS_ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `EDIT_USERNAME_ALLOWED` bit(1) NOT NULL DEFAULT b'0',
  `OTP_POLICY_COUNTER` int(11) DEFAULT 0,
  `OTP_POLICY_WINDOW` int(11) DEFAULT 1,
  `OTP_POLICY_PERIOD` int(11) DEFAULT 30,
  `OTP_POLICY_DIGITS` int(11) DEFAULT 6,
  `OTP_POLICY_ALG` varchar(36) DEFAULT 'HmacSHA1',
  `OTP_POLICY_TYPE` varchar(36) DEFAULT 'totp',
  `BROWSER_FLOW` varchar(36) DEFAULT NULL,
  `REGISTRATION_FLOW` varchar(36) DEFAULT NULL,
  `DIRECT_GRANT_FLOW` varchar(36) DEFAULT NULL,
  `RESET_CREDENTIALS_FLOW` varchar(36) DEFAULT NULL,
  `CLIENT_AUTH_FLOW` varchar(36) DEFAULT NULL,
  `OFFLINE_SESSION_IDLE_TIMEOUT` int(11) DEFAULT 0,
  `REVOKE_REFRESH_TOKEN` bit(1) NOT NULL DEFAULT b'0',
  `ACCESS_TOKEN_LIFE_IMPLICIT` int(11) DEFAULT 0,
  `LOGIN_WITH_EMAIL_ALLOWED` bit(1) NOT NULL DEFAULT b'1',
  `DUPLICATE_EMAILS_ALLOWED` bit(1) NOT NULL DEFAULT b'0',
  `DOCKER_AUTH_FLOW` varchar(36) DEFAULT NULL,
  `REFRESH_TOKEN_MAX_REUSE` int(11) DEFAULT 0,
  `ALLOW_USER_MANAGED_ACCESS` bit(1) NOT NULL DEFAULT b'0',
  `SSO_MAX_LIFESPAN_REMEMBER_ME` int(11) NOT NULL,
  `SSO_IDLE_TIMEOUT_REMEMBER_ME` int(11) NOT NULL,
  `DEFAULT_ROLE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_ORVSDMLA56612EAEFIQ6WL5OI` (`NAME`),
  KEY `IDX_REALM_MASTER_ADM_CLI` (`MASTER_ADMIN_CLIENT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm`
--

LOCK TABLES `realm` WRITE;
/*!40000 ALTER TABLE `realm` DISABLE KEYS */;
INSERT INTO `realm` VALUES ('master',60,300,60,NULL,NULL,NULL,'','\0',0,NULL,'master',0,NULL,'\0','\0','\0','\0','EXTERNAL',1800,36000,'\0','\0','c09cfaf6-b8eb-40f1-847b-03b534d85bbb',1800,'\0',NULL,'\0','\0','\0','\0',0,1,30,6,'HmacSHA1','totp','6d9e9bf3-4f51-42b1-aae2-bc47ecbc8238','048e3586-22d3-40b3-b6b8-08e7c71bc0ee','9be080e3-a2cc-4f20-bcfd-40c3faffbe8f','ffca34a6-ab7d-4f73-8a5f-59d920f35ecf','7b327e66-e100-433d-a3b7-9bb8c179aec9',2592000,'\0',900,'','\0','187056d3-10a2-40a1-b4dc-252e3d361487',0,'\0',0,0,'e08b8d31-d520-4d12-8e4e-233a33257409'),('sptek-cloud',60,300,300,NULL,NULL,NULL,'','\0',0,NULL,'strato-cloud',1645150847,NULL,'\0','\0','\0','\0','EXTERNAL',1800,36000,'\0','\0','fad7c87e-69c6-4163-a0dd-825c5276a923',1800,'\0',NULL,'\0','\0','\0','\0',0,1,30,6,'HmacSHA1','totp','3d00e676-d69b-4f7e-8a3d-b971f94db960','1e0b7c25-f5d5-445d-ae80-c8fc5000413e','9d4cdb6d-1843-4c51-8875-bacd66baa321','577e637e-7555-4ceb-819c-2c2ab1c5468f','0b04cdbc-d680-41a9-9b03-c1d4a6ac3fb0',2592000,'\0',900,'','\0','39d4a2d5-1496-4c9e-ad55-6d4d2230bc2d',0,'\0',0,0,'0b2ccb5a-1388-4bcc-84fa-8069c9d0f781');
/*!40000 ALTER TABLE `realm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_attribute`
--

DROP TABLE IF EXISTS `realm_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_attribute` (
  `NAME` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  PRIMARY KEY (`NAME`,`REALM_ID`),
  KEY `IDX_REALM_ATTR_REALM` (`REALM_ID`),
  CONSTRAINT `FK_8SHXD6L3E9ATQUKACXGPFFPTW` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_attribute`
--

LOCK TABLES `realm_attribute` WRITE;
/*!40000 ALTER TABLE `realm_attribute` DISABLE KEYS */;
INSERT INTO `realm_attribute` VALUES ('actionTokenGeneratedByAdminLifespan','sptek-cloud','43200'),('actionTokenGeneratedByUserLifespan','sptek-cloud','300'),('bruteForceProtected','master','false'),('bruteForceProtected','sptek-cloud','false'),('cibaAuthRequestedUserHint','sptek-cloud','login_hint'),('cibaBackchannelTokenDeliveryMode','sptek-cloud','poll'),('cibaExpiresIn','sptek-cloud','120'),('cibaInterval','sptek-cloud','5'),('client-policies.policies','sptek-cloud','{\"policies\":[]}'),('client-policies.profiles','sptek-cloud','{\"profiles\":[]}'),('clientOfflineSessionIdleTimeout','sptek-cloud','0'),('clientOfflineSessionMaxLifespan','sptek-cloud','0'),('clientSessionIdleTimeout','sptek-cloud','0'),('clientSessionMaxLifespan','sptek-cloud','0'),('defaultSignatureAlgorithm','master','RS256'),('defaultSignatureAlgorithm','sptek-cloud','RS256'),('displayName','master','Keycloak'),('displayNameHtml','master','<div class=\"kc-logo-text\"><span>Keycloak</span></div>'),('failureFactor','master','30'),('failureFactor','sptek-cloud','30'),('maxDeltaTimeSeconds','master','43200'),('maxDeltaTimeSeconds','sptek-cloud','43200'),('maxFailureWaitSeconds','master','900'),('maxFailureWaitSeconds','sptek-cloud','900'),('minimumQuickLoginWaitSeconds','master','60'),('minimumQuickLoginWaitSeconds','sptek-cloud','60'),('oauth2DeviceCodeLifespan','sptek-cloud','600'),('oauth2DevicePollingInterval','sptek-cloud','5'),('offlineSessionMaxLifespan','master','5184000'),('offlineSessionMaxLifespan','sptek-cloud','5184000'),('offlineSessionMaxLifespanEnabled','master','false'),('offlineSessionMaxLifespanEnabled','sptek-cloud','false'),('parRequestUriLifespan','sptek-cloud','60'),('permanentLockout','master','false'),('permanentLockout','sptek-cloud','false'),('quickLoginCheckMilliSeconds','master','1000'),('quickLoginCheckMilliSeconds','sptek-cloud','1000'),('userProfileEnabled','sptek-cloud','false'),('waitIncrementSeconds','master','60'),('waitIncrementSeconds','sptek-cloud','60'),('webAuthnPolicyAttestationConveyancePreference','sptek-cloud','not specified'),('webAuthnPolicyAttestationConveyancePreferencePasswordless','sptek-cloud','not specified'),('webAuthnPolicyAuthenticatorAttachment','sptek-cloud','not specified'),('webAuthnPolicyAuthenticatorAttachmentPasswordless','sptek-cloud','not specified'),('webAuthnPolicyAvoidSameAuthenticatorRegister','sptek-cloud','false'),('webAuthnPolicyAvoidSameAuthenticatorRegisterPasswordless','sptek-cloud','false'),('webAuthnPolicyCreateTimeout','sptek-cloud','0'),('webAuthnPolicyCreateTimeoutPasswordless','sptek-cloud','0'),('webAuthnPolicyRequireResidentKey','sptek-cloud','not specified'),('webAuthnPolicyRequireResidentKeyPasswordless','sptek-cloud','not specified'),('webAuthnPolicyRpEntityName','sptek-cloud','keycloak'),('webAuthnPolicyRpEntityNamePasswordless','sptek-cloud','keycloak'),('webAuthnPolicyRpId','sptek-cloud',''),('webAuthnPolicyRpIdPasswordless','sptek-cloud',''),('webAuthnPolicySignatureAlgorithms','sptek-cloud','ES256'),('webAuthnPolicySignatureAlgorithmsPasswordless','sptek-cloud','ES256'),('webAuthnPolicyUserVerificationRequirement','sptek-cloud','not specified'),('webAuthnPolicyUserVerificationRequirementPasswordless','sptek-cloud','not specified'),('_browser_header.contentSecurityPolicy','master','frame-src \'self\'; frame-ancestors \'self\'; object-src \'none\';'),('_browser_header.contentSecurityPolicy','sptek-cloud','frame-src \'self\'; frame-ancestors \'self\'; object-src \'none\';'),('_browser_header.contentSecurityPolicyReportOnly','master',''),('_browser_header.contentSecurityPolicyReportOnly','sptek-cloud',''),('_browser_header.strictTransportSecurity','master','max-age=31536000; includeSubDomains'),('_browser_header.strictTransportSecurity','sptek-cloud','max-age=31536000; includeSubDomains'),('_browser_header.xContentTypeOptions','master','nosniff'),('_browser_header.xContentTypeOptions','sptek-cloud','nosniff'),('_browser_header.xFrameOptions','master','SAMEORIGIN'),('_browser_header.xFrameOptions','sptek-cloud','SAMEORIGIN'),('_browser_header.xRobotsTag','master','none'),('_browser_header.xRobotsTag','sptek-cloud','none'),('_browser_header.xXSSProtection','master','1; mode=block'),('_browser_header.xXSSProtection','sptek-cloud','1; mode=block');
/*!40000 ALTER TABLE `realm_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_default_groups`
--

DROP TABLE IF EXISTS `realm_default_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_default_groups` (
  `REALM_ID` varchar(36) NOT NULL,
  `GROUP_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`GROUP_ID`),
  UNIQUE KEY `CON_GROUP_ID_DEF_GROUPS` (`GROUP_ID`),
  KEY `IDX_REALM_DEF_GRP_REALM` (`REALM_ID`),
  CONSTRAINT `FK_DEF_GROUPS_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_default_groups`
--

LOCK TABLES `realm_default_groups` WRITE;
/*!40000 ALTER TABLE `realm_default_groups` DISABLE KEYS */;
INSERT INTO `realm_default_groups` VALUES ('sptek-cloud','e761632f-755a-4046-af77-6e7ec3aeabc8');
/*!40000 ALTER TABLE `realm_default_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_enabled_event_types`
--

DROP TABLE IF EXISTS `realm_enabled_event_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_enabled_event_types` (
  `REALM_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`VALUE`),
  KEY `IDX_REALM_EVT_TYPES_REALM` (`REALM_ID`),
  CONSTRAINT `FK_H846O4H0W8EPX5NWEDRF5Y69J` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_enabled_event_types`
--

LOCK TABLES `realm_enabled_event_types` WRITE;
/*!40000 ALTER TABLE `realm_enabled_event_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `realm_enabled_event_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_events_listeners`
--

DROP TABLE IF EXISTS `realm_events_listeners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_events_listeners` (
  `REALM_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`VALUE`),
  KEY `IDX_REALM_EVT_LIST_REALM` (`REALM_ID`),
  CONSTRAINT `FK_H846O4H0W8EPX5NXEV9F5Y69J` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_events_listeners`
--

LOCK TABLES `realm_events_listeners` WRITE;
/*!40000 ALTER TABLE `realm_events_listeners` DISABLE KEYS */;
INSERT INTO `realm_events_listeners` VALUES ('master','jboss-logging'),('sptek-cloud','jboss-logging');
/*!40000 ALTER TABLE `realm_events_listeners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_localizations`
--

DROP TABLE IF EXISTS `realm_localizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_localizations` (
  `REALM_ID` varchar(255) NOT NULL,
  `LOCALE` varchar(255) NOT NULL,
  `TEXTS` longtext NOT NULL,
  PRIMARY KEY (`REALM_ID`,`LOCALE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_localizations`
--

LOCK TABLES `realm_localizations` WRITE;
/*!40000 ALTER TABLE `realm_localizations` DISABLE KEYS */;
/*!40000 ALTER TABLE `realm_localizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_required_credential`
--

DROP TABLE IF EXISTS `realm_required_credential`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_required_credential` (
  `TYPE` varchar(255) NOT NULL,
  `FORM_LABEL` varchar(255) DEFAULT NULL,
  `INPUT` bit(1) NOT NULL DEFAULT b'0',
  `SECRET` bit(1) NOT NULL DEFAULT b'0',
  `REALM_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`TYPE`),
  CONSTRAINT `FK_5HG65LYBEVAVKQFKI3KPONH9V` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_required_credential`
--

LOCK TABLES `realm_required_credential` WRITE;
/*!40000 ALTER TABLE `realm_required_credential` DISABLE KEYS */;
INSERT INTO `realm_required_credential` VALUES ('password','password','','','master'),('password','password','','','sptek-cloud');
/*!40000 ALTER TABLE `realm_required_credential` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_smtp_config`
--

DROP TABLE IF EXISTS `realm_smtp_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_smtp_config` (
  `REALM_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`NAME`),
  CONSTRAINT `FK_70EJ8XDXGXD0B9HH6180IRR0O` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_smtp_config`
--

LOCK TABLES `realm_smtp_config` WRITE;
/*!40000 ALTER TABLE `realm_smtp_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `realm_smtp_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `realm_supported_locales`
--

DROP TABLE IF EXISTS `realm_supported_locales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realm_supported_locales` (
  `REALM_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`REALM_ID`,`VALUE`),
  KEY `IDX_REALM_SUPP_LOCAL_REALM` (`REALM_ID`),
  CONSTRAINT `FK_SUPPORTED_LOCALES_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `realm_supported_locales`
--

LOCK TABLES `realm_supported_locales` WRITE;
/*!40000 ALTER TABLE `realm_supported_locales` DISABLE KEYS */;
/*!40000 ALTER TABLE `realm_supported_locales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `redirect_uris`
--

DROP TABLE IF EXISTS `redirect_uris`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `redirect_uris` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`VALUE`),
  KEY `IDX_REDIR_URI_CLIENT` (`CLIENT_ID`),
  CONSTRAINT `FK_1BURS8PB4OUJ97H5WUPPAHV9F` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redirect_uris`
--

LOCK TABLES `redirect_uris` WRITE;
/*!40000 ALTER TABLE `redirect_uris` DISABLE KEYS */;
INSERT INTO `redirect_uris` VALUES ('0fcab411-7988-4552-aa1a-bf8d87f77a81','/realms/sptek-cloud/account/*'),('291690ba-3798-45f3-8b01-f11293902cce','/realms/strato-cloud/account/*'),('2e5f8397-470b-4516-8cc3-aa92e3bda308','http://210.219.181.78:18080/*'),('41faae5b-259c-4c66-ad6d-9afada387cee','/realms/master/account/*'),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','/admin/strato-cloud/console/*'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','/admin/master/console/*'),('775ead1f-db83-43ad-a74d-b856153f9e51','http://kafka-bmt02:8081/*'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','http://kafka-bmt01:8081/*'),('97e593c5-a1ee-4253-b279-bcddf129f69a','http://paasportal.strato.com/*'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','http://localhost:18080/*'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','/realms/master/account/*'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','http://paasportal.strato.com:18080/*');
/*!40000 ALTER TABLE `redirect_uris` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `required_action_config`
--

DROP TABLE IF EXISTS `required_action_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `required_action_config` (
  `REQUIRED_ACTION_ID` varchar(36) NOT NULL,
  `VALUE` longtext DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`REQUIRED_ACTION_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `required_action_config`
--

LOCK TABLES `required_action_config` WRITE;
/*!40000 ALTER TABLE `required_action_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `required_action_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `required_action_provider`
--

DROP TABLE IF EXISTS `required_action_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `required_action_provider` (
  `ID` varchar(36) NOT NULL,
  `ALIAS` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  `ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `DEFAULT_ACTION` bit(1) NOT NULL DEFAULT b'0',
  `PROVIDER_ID` varchar(255) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_REQ_ACT_PROV_REALM` (`REALM_ID`),
  CONSTRAINT `FK_REQ_ACT_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `required_action_provider`
--

LOCK TABLES `required_action_provider` WRITE;
/*!40000 ALTER TABLE `required_action_provider` DISABLE KEYS */;
INSERT INTO `required_action_provider` VALUES ('03122a0a-3fe8-471f-a5d9-c1bd9287f60c','UPDATE_PASSWORD','Update Password','master','','\0','UPDATE_PASSWORD',30),('0a56ebf6-5cc3-4708-af3f-120843f2cefc','terms_and_conditions','Terms and Conditions','sptek-cloud','\0','\0','terms_and_conditions',20),('11bef9bc-276f-4408-b170-e4244354d202','update_user_locale','Update User Locale','master','','\0','update_user_locale',1000),('1a2d6d0a-a40d-4dc3-8299-a4e0a6028e81','terms_and_conditions','Terms and Conditions','master','\0','\0','terms_and_conditions',20),('40caab91-b23a-4c55-b381-d18519a531a7','UPDATE_PROFILE','Update Profile','sptek-cloud','','\0','UPDATE_PROFILE',40),('665938ab-6d67-4bca-80ed-75dbc65ab6bb','VERIFY_EMAIL','Verify Email','master','','\0','VERIFY_EMAIL',50),('a622f910-a3c7-4d76-9e2c-fb14c111ea88','CONFIGURE_TOTP','Configure OTP','sptek-cloud','','\0','CONFIGURE_TOTP',10),('af120a67-6e7e-4545-ba57-0b5f161379e9','update_user_locale','Update User Locale','sptek-cloud','','\0','update_user_locale',1000),('cf9bd0ea-08b8-4d20-98ba-5cead3687f53','delete_account','Delete Account','sptek-cloud','\0','\0','delete_account',60),('d2b53159-25fa-47ef-85f1-e6618244ac5f','CONFIGURE_TOTP','Configure OTP','master','','\0','CONFIGURE_TOTP',10),('e37bc311-4661-4655-87ff-7ee98ee2a99d','UPDATE_PASSWORD','Update Password','sptek-cloud','','\0','UPDATE_PASSWORD',30),('e8e186e5-9f31-4007-aada-c756e8feee7a','VERIFY_EMAIL','Verify Email','sptek-cloud','','\0','VERIFY_EMAIL',50),('f8c9bf65-5f24-4297-b2ba-253aafc36f24','UPDATE_PROFILE','Update Profile','master','','\0','UPDATE_PROFILE',40),('fa2c4c94-1ee2-4634-8f4f-1568c66f979b','delete_account','Delete Account','master','\0','\0','delete_account',60);
/*!40000 ALTER TABLE `required_action_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_attribute`
--

DROP TABLE IF EXISTS `resource_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_attribute` (
  `ID` varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here',
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `RESOURCE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_5HRM2VLF9QL5FU022KQEPOVBR` (`RESOURCE_ID`),
  CONSTRAINT `FK_5HRM2VLF9QL5FU022KQEPOVBR` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `resource_server_resource` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_attribute`
--

LOCK TABLES `resource_attribute` WRITE;
/*!40000 ALTER TABLE `resource_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_policy`
--

DROP TABLE IF EXISTS `resource_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_policy` (
  `RESOURCE_ID` varchar(36) NOT NULL,
  `POLICY_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`RESOURCE_ID`,`POLICY_ID`),
  KEY `IDX_RES_POLICY_POLICY` (`POLICY_ID`),
  CONSTRAINT `FK_FRSRPOS53XCX4WNKOG82SSRFY` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `resource_server_resource` (`ID`),
  CONSTRAINT `FK_FRSRPP213XCX4WNKOG82SSRFY` FOREIGN KEY (`POLICY_ID`) REFERENCES `resource_server_policy` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_policy`
--

LOCK TABLES `resource_policy` WRITE;
/*!40000 ALTER TABLE `resource_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_scope`
--

DROP TABLE IF EXISTS `resource_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_scope` (
  `RESOURCE_ID` varchar(36) NOT NULL,
  `SCOPE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`RESOURCE_ID`,`SCOPE_ID`),
  KEY `IDX_RES_SCOPE_SCOPE` (`SCOPE_ID`),
  CONSTRAINT `FK_FRSRPOS13XCX4WNKOG82SSRFY` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `resource_server_resource` (`ID`),
  CONSTRAINT `FK_FRSRPS213XCX4WNKOG82SSRFY` FOREIGN KEY (`SCOPE_ID`) REFERENCES `resource_server_scope` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_scope`
--

LOCK TABLES `resource_scope` WRITE;
/*!40000 ALTER TABLE `resource_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_server`
--

DROP TABLE IF EXISTS `resource_server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_server` (
  `ID` varchar(36) NOT NULL,
  `ALLOW_RS_REMOTE_MGMT` bit(1) NOT NULL DEFAULT b'0',
  `POLICY_ENFORCE_MODE` varchar(15) NOT NULL,
  `DECISION_STRATEGY` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_server`
--

LOCK TABLES `resource_server` WRITE;
/*!40000 ALTER TABLE `resource_server` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_server` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_server_perm_ticket`
--

DROP TABLE IF EXISTS `resource_server_perm_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_server_perm_ticket` (
  `ID` varchar(36) NOT NULL,
  `OWNER` varchar(255) DEFAULT NULL,
  `REQUESTER` varchar(255) DEFAULT NULL,
  `CREATED_TIMESTAMP` bigint(20) NOT NULL,
  `GRANTED_TIMESTAMP` bigint(20) DEFAULT NULL,
  `RESOURCE_ID` varchar(36) NOT NULL,
  `SCOPE_ID` varchar(36) DEFAULT NULL,
  `RESOURCE_SERVER_ID` varchar(36) NOT NULL,
  `POLICY_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_FRSR6T700S9V50BU18WS5PMT` (`OWNER`,`REQUESTER`,`RESOURCE_SERVER_ID`,`RESOURCE_ID`,`SCOPE_ID`),
  KEY `FK_FRSRHO213XCX4WNKOG82SSPMT` (`RESOURCE_SERVER_ID`),
  KEY `FK_FRSRHO213XCX4WNKOG83SSPMT` (`RESOURCE_ID`),
  KEY `FK_FRSRHO213XCX4WNKOG84SSPMT` (`SCOPE_ID`),
  KEY `FK_FRSRPO2128CX4WNKOG82SSRFY` (`POLICY_ID`),
  CONSTRAINT `FK_FRSRHO213XCX4WNKOG82SSPMT` FOREIGN KEY (`RESOURCE_SERVER_ID`) REFERENCES `resource_server` (`ID`),
  CONSTRAINT `FK_FRSRHO213XCX4WNKOG83SSPMT` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `resource_server_resource` (`ID`),
  CONSTRAINT `FK_FRSRHO213XCX4WNKOG84SSPMT` FOREIGN KEY (`SCOPE_ID`) REFERENCES `resource_server_scope` (`ID`),
  CONSTRAINT `FK_FRSRPO2128CX4WNKOG82SSRFY` FOREIGN KEY (`POLICY_ID`) REFERENCES `resource_server_policy` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_server_perm_ticket`
--

LOCK TABLES `resource_server_perm_ticket` WRITE;
/*!40000 ALTER TABLE `resource_server_perm_ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_server_perm_ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_server_policy`
--

DROP TABLE IF EXISTS `resource_server_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_server_policy` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `TYPE` varchar(255) NOT NULL,
  `DECISION_STRATEGY` varchar(20) DEFAULT NULL,
  `LOGIC` varchar(20) DEFAULT NULL,
  `RESOURCE_SERVER_ID` varchar(36) DEFAULT NULL,
  `OWNER` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_FRSRPT700S9V50BU18WS5HA6` (`NAME`,`RESOURCE_SERVER_ID`),
  KEY `IDX_RES_SERV_POL_RES_SERV` (`RESOURCE_SERVER_ID`),
  CONSTRAINT `FK_FRSRPO213XCX4WNKOG82SSRFY` FOREIGN KEY (`RESOURCE_SERVER_ID`) REFERENCES `resource_server` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_server_policy`
--

LOCK TABLES `resource_server_policy` WRITE;
/*!40000 ALTER TABLE `resource_server_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_server_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_server_resource`
--

DROP TABLE IF EXISTS `resource_server_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_server_resource` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `ICON_URI` varchar(255) DEFAULT NULL,
  `OWNER` varchar(255) DEFAULT NULL,
  `RESOURCE_SERVER_ID` varchar(36) DEFAULT NULL,
  `OWNER_MANAGED_ACCESS` bit(1) NOT NULL DEFAULT b'0',
  `DISPLAY_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_FRSR6T700S9V50BU18WS5HA6` (`NAME`,`OWNER`,`RESOURCE_SERVER_ID`),
  KEY `IDX_RES_SRV_RES_RES_SRV` (`RESOURCE_SERVER_ID`),
  CONSTRAINT `FK_FRSRHO213XCX4WNKOG82SSRFY` FOREIGN KEY (`RESOURCE_SERVER_ID`) REFERENCES `resource_server` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_server_resource`
--

LOCK TABLES `resource_server_resource` WRITE;
/*!40000 ALTER TABLE `resource_server_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_server_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_server_scope`
--

DROP TABLE IF EXISTS `resource_server_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_server_scope` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `ICON_URI` varchar(255) DEFAULT NULL,
  `RESOURCE_SERVER_ID` varchar(36) DEFAULT NULL,
  `DISPLAY_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_FRSRST700S9V50BU18WS5HA6` (`NAME`,`RESOURCE_SERVER_ID`),
  KEY `IDX_RES_SRV_SCOPE_RES_SRV` (`RESOURCE_SERVER_ID`),
  CONSTRAINT `FK_FRSRSO213XCX4WNKOG82SSRFY` FOREIGN KEY (`RESOURCE_SERVER_ID`) REFERENCES `resource_server` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_server_scope`
--

LOCK TABLES `resource_server_scope` WRITE;
/*!40000 ALTER TABLE `resource_server_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_server_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_uris`
--

DROP TABLE IF EXISTS `resource_uris`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_uris` (
  `RESOURCE_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`RESOURCE_ID`,`VALUE`),
  CONSTRAINT `FK_RESOURCE_SERVER_URIS` FOREIGN KEY (`RESOURCE_ID`) REFERENCES `resource_server_resource` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_uris`
--

LOCK TABLES `resource_uris` WRITE;
/*!40000 ALTER TABLE `resource_uris` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_uris` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_attribute`
--

DROP TABLE IF EXISTS `role_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_attribute` (
  `ID` varchar(36) NOT NULL,
  `ROLE_ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_ROLE_ATTRIBUTE` (`ROLE_ID`),
  CONSTRAINT `FK_ROLE_ATTRIBUTE_ID` FOREIGN KEY (`ROLE_ID`) REFERENCES `keycloak_role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_attribute`
--

LOCK TABLES `role_attribute` WRITE;
/*!40000 ALTER TABLE `role_attribute` DISABLE KEYS */;
INSERT INTO `role_attribute` VALUES ('a02c78d1-f4a5-4645-b3cb-c105f2f3655e','11a1c224-db64-4f7f-9a6d-12bf6bb29d4d','menu04','true'),('bb9a3bdf-d313-4351-aa6d-a28e85e72a67','e86e5a4d-8893-4664-aa99-03ee63cb8b7b','menu02','true'),('fa3cbef7-764b-4809-a9c4-4a58e2dbc20e','e86e5a4d-8893-4664-aa99-03ee63cb8b7b','menu01','true');
/*!40000 ALTER TABLE `role_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scope_mapping`
--

DROP TABLE IF EXISTS `scope_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scope_mapping` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `ROLE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`ROLE_ID`),
  KEY `IDX_SCOPE_MAPPING_ROLE` (`ROLE_ID`),
  CONSTRAINT `FK_OUSE064PLMLR732LXJCN1Q5F1` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scope_mapping`
--

LOCK TABLES `scope_mapping` WRITE;
/*!40000 ALTER TABLE `scope_mapping` DISABLE KEYS */;
INSERT INTO `scope_mapping` VALUES ('0fcab411-7988-4552-aa1a-bf8d87f77a81','e42b6717-1f17-49ed-92f6-5bb428ee0e37'),('da1e37ec-b140-4527-afcd-50cd0eceaed7','eda43363-b524-457e-8690-abd92be59289');
/*!40000 ALTER TABLE `scope_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scope_policy`
--

DROP TABLE IF EXISTS `scope_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scope_policy` (
  `SCOPE_ID` varchar(36) NOT NULL,
  `POLICY_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`SCOPE_ID`,`POLICY_ID`),
  KEY `IDX_SCOPE_POLICY_POLICY` (`POLICY_ID`),
  CONSTRAINT `FK_FRSRASP13XCX4WNKOG82SSRFY` FOREIGN KEY (`POLICY_ID`) REFERENCES `resource_server_policy` (`ID`),
  CONSTRAINT `FK_FRSRPASS3XCX4WNKOG82SSRFY` FOREIGN KEY (`SCOPE_ID`) REFERENCES `resource_server_scope` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scope_policy`
--

LOCK TABLES `scope_policy` WRITE;
/*!40000 ALTER TABLE `scope_policy` DISABLE KEYS */;
/*!40000 ALTER TABLE `scope_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_attribute`
--

DROP TABLE IF EXISTS `user_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_attribute` (
  `NAME` varchar(255) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `ID` varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here',
  PRIMARY KEY (`ID`),
  KEY `IDX_USER_ATTRIBUTE` (`USER_ID`),
  KEY `IDX_USER_ATTRIBUTE_NAME` (`NAME`,`VALUE`),
  CONSTRAINT `FK_5HRM2VLF9QL5FU043KQEPOVBR` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_attribute`
--

LOCK TABLES `user_attribute` WRITE;
/*!40000 ALTER TABLE `user_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_consent`
--

DROP TABLE IF EXISTS `user_consent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_consent` (
  `ID` varchar(36) NOT NULL,
  `CLIENT_ID` varchar(255) DEFAULT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `CREATED_DATE` bigint(20) DEFAULT NULL,
  `LAST_UPDATED_DATE` bigint(20) DEFAULT NULL,
  `CLIENT_STORAGE_PROVIDER` varchar(36) DEFAULT NULL,
  `EXTERNAL_CLIENT_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_JKUWUVD56ONTGSUHOGM8UEWRT` (`CLIENT_ID`,`CLIENT_STORAGE_PROVIDER`,`EXTERNAL_CLIENT_ID`,`USER_ID`),
  KEY `IDX_USER_CONSENT` (`USER_ID`),
  CONSTRAINT `FK_GRNTCSNT_USER` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_consent`
--

LOCK TABLES `user_consent` WRITE;
/*!40000 ALTER TABLE `user_consent` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_consent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_consent_client_scope`
--

DROP TABLE IF EXISTS `user_consent_client_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_consent_client_scope` (
  `USER_CONSENT_ID` varchar(36) NOT NULL,
  `SCOPE_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`USER_CONSENT_ID`,`SCOPE_ID`),
  KEY `IDX_USCONSENT_CLSCOPE` (`USER_CONSENT_ID`),
  CONSTRAINT `FK_GRNTCSNT_CLSC_USC` FOREIGN KEY (`USER_CONSENT_ID`) REFERENCES `user_consent` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_consent_client_scope`
--

LOCK TABLES `user_consent_client_scope` WRITE;
/*!40000 ALTER TABLE `user_consent_client_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_consent_client_scope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_entity`
--

DROP TABLE IF EXISTS `user_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_entity` (
  `ID` varchar(36) NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `EMAIL_CONSTRAINT` varchar(255) DEFAULT NULL,
  `EMAIL_VERIFIED` bit(1) NOT NULL DEFAULT b'0',
  `ENABLED` bit(1) NOT NULL DEFAULT b'0',
  `FEDERATION_LINK` varchar(255) DEFAULT NULL,
  `FIRST_NAME` varchar(255) DEFAULT NULL,
  `LAST_NAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `CREATED_TIMESTAMP` bigint(20) DEFAULT NULL,
  `SERVICE_ACCOUNT_CLIENT_LINK` varchar(255) DEFAULT NULL,
  `NOT_BEFORE` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_DYKN684SL8UP1CRFEI6ECKHD7` (`REALM_ID`,`EMAIL_CONSTRAINT`),
  UNIQUE KEY `UK_RU8TT6T700S9V50BU18WS5HA6` (`REALM_ID`,`USERNAME`),
  KEY `IDX_USER_EMAIL` (`EMAIL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_entity`
--

LOCK TABLES `user_entity` WRITE;
/*!40000 ALTER TABLE `user_entity` DISABLE KEYS */;
INSERT INTO `user_entity` VALUES ('1dc23092-e94c-4c93-be2c-3c96b1b0bb8f','paasportal@strato.co.kr','paasportal@strato.co.kr','\0','',NULL,'관리자',NULL,'sptek-cloud','paasportal@strato.co.kr',1652415929013,NULL,1669768564),('2762b66d-e682-4ff1-a4f4-9429731e7a8d','paas-admin@strato.co.kr','paas-admin@strato.co.kr','\0','',NULL,'paas','paas','sptek-cloud','paas-admin@strato.co.kr',1649991754228,NULL,1652432010),('3fc64047-176c-4b55-ac53-e8e383760104',NULL,'3c26adff-c0ad-4a38-a7c4-d7b36a8dfabb','\0','',NULL,NULL,NULL,'master','admin',1644385465889,NULL,0),('6ffb0544-feaf-4d6d-9cc4-0277687893a4',NULL,'ef23e94b-3106-4f13-91c3-fca5e0224143','\0','',NULL,NULL,NULL,'master','service-account-admin-cli',1644393519862,'955e906d-aa0f-4d49-82f0-404d19080454',0);
/*!40000 ALTER TABLE `user_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_federation_config`
--

DROP TABLE IF EXISTS `user_federation_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_federation_config` (
  `USER_FEDERATION_PROVIDER_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`USER_FEDERATION_PROVIDER_ID`,`NAME`),
  CONSTRAINT `FK_T13HPU1J94R2EBPEKR39X5EU5` FOREIGN KEY (`USER_FEDERATION_PROVIDER_ID`) REFERENCES `user_federation_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_federation_config`
--

LOCK TABLES `user_federation_config` WRITE;
/*!40000 ALTER TABLE `user_federation_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_federation_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_federation_mapper`
--

DROP TABLE IF EXISTS `user_federation_mapper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_federation_mapper` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `FEDERATION_PROVIDER_ID` varchar(36) NOT NULL,
  `FEDERATION_MAPPER_TYPE` varchar(255) NOT NULL,
  `REALM_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_USR_FED_MAP_FED_PRV` (`FEDERATION_PROVIDER_ID`),
  KEY `IDX_USR_FED_MAP_REALM` (`REALM_ID`),
  CONSTRAINT `FK_FEDMAPPERPM_FEDPRV` FOREIGN KEY (`FEDERATION_PROVIDER_ID`) REFERENCES `user_federation_provider` (`ID`),
  CONSTRAINT `FK_FEDMAPPERPM_REALM` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_federation_mapper`
--

LOCK TABLES `user_federation_mapper` WRITE;
/*!40000 ALTER TABLE `user_federation_mapper` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_federation_mapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_federation_mapper_config`
--

DROP TABLE IF EXISTS `user_federation_mapper_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_federation_mapper_config` (
  `USER_FEDERATION_MAPPER_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`USER_FEDERATION_MAPPER_ID`,`NAME`),
  CONSTRAINT `FK_FEDMAPPER_CFG` FOREIGN KEY (`USER_FEDERATION_MAPPER_ID`) REFERENCES `user_federation_mapper` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_federation_mapper_config`
--

LOCK TABLES `user_federation_mapper_config` WRITE;
/*!40000 ALTER TABLE `user_federation_mapper_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_federation_mapper_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_federation_provider`
--

DROP TABLE IF EXISTS `user_federation_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_federation_provider` (
  `ID` varchar(36) NOT NULL,
  `CHANGED_SYNC_PERIOD` int(11) DEFAULT NULL,
  `DISPLAY_NAME` varchar(255) DEFAULT NULL,
  `FULL_SYNC_PERIOD` int(11) DEFAULT NULL,
  `LAST_SYNC` int(11) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `PROVIDER_NAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_USR_FED_PRV_REALM` (`REALM_ID`),
  CONSTRAINT `FK_1FJ32F6PTOLW2QY60CD8N01E8` FOREIGN KEY (`REALM_ID`) REFERENCES `realm` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_federation_provider`
--

LOCK TABLES `user_federation_provider` WRITE;
/*!40000 ALTER TABLE `user_federation_provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_federation_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group_membership`
--

DROP TABLE IF EXISTS `user_group_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_membership` (
  `GROUP_ID` varchar(36) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `IDX_USER_GROUP_MAPPING` (`USER_ID`),
  CONSTRAINT `FK_USER_GROUP_USER` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group_membership`
--

LOCK TABLES `user_group_membership` WRITE;
/*!40000 ALTER TABLE `user_group_membership` DISABLE KEYS */;
INSERT INTO `user_group_membership` VALUES ('8ace60b0-9b66-4072-b37d-a6c896441df1','148eaaf2-53e5-4ef2-a8a6-52bb1c210933'),('8ace60b0-9b66-4072-b37d-a6c896441df1','8f6a9f96-7618-4b17-95d2-3fda0e4531d3'),('adbe7ee1-8d33-450d-bd96-0416d5f77919','148eaaf2-53e5-4ef2-a8a6-52bb1c210933'),('e761632f-755a-4046-af77-6e7ec3aeabc8','1dc23092-e94c-4c93-be2c-3c96b1b0bb8f'),('e761632f-755a-4046-af77-6e7ec3aeabc8','2762b66d-e682-4ff1-a4f4-9429731e7a8d'),('e761632f-755a-4046-af77-6e7ec3aeabc8','9ac842e6-41ce-43de-a086-c64ea658fd26');
/*!40000 ALTER TABLE `user_group_membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_required_action`
--

DROP TABLE IF EXISTS `user_required_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_required_action` (
  `USER_ID` varchar(36) NOT NULL,
  `REQUIRED_ACTION` varchar(255) NOT NULL DEFAULT ' ',
  PRIMARY KEY (`REQUIRED_ACTION`,`USER_ID`),
  KEY `IDX_USER_REQACTIONS` (`USER_ID`),
  CONSTRAINT `FK_6QJ3W1JW9CVAFHE19BWSIUVMD` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_required_action`
--

LOCK TABLES `user_required_action` WRITE;
/*!40000 ALTER TABLE `user_required_action` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_required_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_mapping`
--

DROP TABLE IF EXISTS `user_role_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_mapping` (
  `ROLE_ID` varchar(255) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ROLE_ID`,`USER_ID`),
  KEY `IDX_USER_ROLE_MAPPING` (`USER_ID`),
  CONSTRAINT `FK_C4FQV34P1MBYLLOXANG7B1Q3L` FOREIGN KEY (`USER_ID`) REFERENCES `user_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_mapping`
--

LOCK TABLES `user_role_mapping` WRITE;
/*!40000 ALTER TABLE `user_role_mapping` DISABLE KEYS */;
INSERT INTO `user_role_mapping` VALUES ('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','148eaaf2-53e5-4ef2-a8a6-52bb1c210933'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','1dc23092-e94c-4c93-be2c-3c96b1b0bb8f'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','2762b66d-e682-4ff1-a4f4-9429731e7a8d'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','8f6a9f96-7618-4b17-95d2-3fda0e4531d3'),('0b2ccb5a-1388-4bcc-84fa-8069c9d0f781','9ac842e6-41ce-43de-a086-c64ea658fd26'),('116bf0bc-2d46-4c2b-8f6b-cf6af59469c5','3fc64047-176c-4b55-ac53-e8e383760104'),('123ef918-1c37-4b03-8630-da5632c344ff','3fc64047-176c-4b55-ac53-e8e383760104'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','3fc64047-176c-4b55-ac53-e8e383760104'),('2e346fa6-63fa-4f38-b307-ef4fdf5bb901','6ffb0544-feaf-4d6d-9cc4-0277687893a4'),('30328e06-9e6a-41e6-87cd-a5f46fa8ea89','3fc64047-176c-4b55-ac53-e8e383760104'),('3df8f447-9de8-44be-b5e8-08e12bce321e','3fc64047-176c-4b55-ac53-e8e383760104'),('4434128d-96b9-4b7c-be83-b5f90f369930','3fc64047-176c-4b55-ac53-e8e383760104'),('4b9a026d-d677-446b-9dd3-edfbd5a521ec','3fc64047-176c-4b55-ac53-e8e383760104'),('53dea1f2-1aff-463c-86c6-f1615a21b769','3fc64047-176c-4b55-ac53-e8e383760104'),('54a18124-892a-4dda-84f1-681433ef9cf7','3fc64047-176c-4b55-ac53-e8e383760104'),('766bd181-d5df-425c-9f5f-32720c97e9b5','3fc64047-176c-4b55-ac53-e8e383760104'),('804389ac-a03c-4edb-9a7b-b5fcea9a782b','3fc64047-176c-4b55-ac53-e8e383760104'),('8293ee01-f6c0-414b-8f89-554c2964a30a','3fc64047-176c-4b55-ac53-e8e383760104'),('840e634b-8173-4fd6-b59c-43b88c69701a','3fc64047-176c-4b55-ac53-e8e383760104'),('a01d71e8-3719-4b55-9f70-b685f7ff0ff8','6ffb0544-feaf-4d6d-9cc4-0277687893a4'),('c816e2c5-479e-4baa-8359-02f641a2397a','3fc64047-176c-4b55-ac53-e8e383760104'),('c8345b16-9882-44f0-9812-84764ec84179','3fc64047-176c-4b55-ac53-e8e383760104'),('d589c7fe-2950-4bb5-90d2-7f640d2b5c83','3fc64047-176c-4b55-ac53-e8e383760104'),('d7e99464-f7e6-43fa-b579-6159e6ad48fc','3fc64047-176c-4b55-ac53-e8e383760104'),('e08b8d31-d520-4d12-8e4e-233a33257409','3fc64047-176c-4b55-ac53-e8e383760104'),('e08b8d31-d520-4d12-8e4e-233a33257409','6ffb0544-feaf-4d6d-9cc4-0277687893a4'),('fae4d818-1d36-4397-bc23-0245019eb61d','3fc64047-176c-4b55-ac53-e8e383760104');
/*!40000 ALTER TABLE `user_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_session`
--

DROP TABLE IF EXISTS `user_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_session` (
  `ID` varchar(36) NOT NULL,
  `AUTH_METHOD` varchar(255) DEFAULT NULL,
  `IP_ADDRESS` varchar(255) DEFAULT NULL,
  `LAST_SESSION_REFRESH` int(11) DEFAULT NULL,
  `LOGIN_USERNAME` varchar(255) DEFAULT NULL,
  `REALM_ID` varchar(255) DEFAULT NULL,
  `REMEMBER_ME` bit(1) NOT NULL DEFAULT b'0',
  `STARTED` int(11) DEFAULT NULL,
  `USER_ID` varchar(255) DEFAULT NULL,
  `USER_SESSION_STATE` int(11) DEFAULT NULL,
  `BROKER_SESSION_ID` varchar(255) DEFAULT NULL,
  `BROKER_USER_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session`
--

LOCK TABLES `user_session` WRITE;
/*!40000 ALTER TABLE `user_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_session_note`
--

DROP TABLE IF EXISTS `user_session_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_session_note` (
  `USER_SESSION` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `VALUE` text DEFAULT NULL,
  PRIMARY KEY (`USER_SESSION`,`NAME`),
  CONSTRAINT `FK5EDFB00FF51D3472` FOREIGN KEY (`USER_SESSION`) REFERENCES `user_session` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session_note`
--

LOCK TABLES `user_session_note` WRITE;
/*!40000 ALTER TABLE `user_session_note` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_session_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `username_login_failure`
--

DROP TABLE IF EXISTS `username_login_failure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `username_login_failure` (
  `REALM_ID` varchar(36) NOT NULL,
  `USERNAME` varchar(255) NOT NULL,
  `FAILED_LOGIN_NOT_BEFORE` int(11) DEFAULT NULL,
  `LAST_FAILURE` bigint(20) DEFAULT NULL,
  `LAST_IP_FAILURE` varchar(255) DEFAULT NULL,
  `NUM_FAILURES` int(11) DEFAULT NULL,
  PRIMARY KEY (`REALM_ID`,`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `username_login_failure`
--

LOCK TABLES `username_login_failure` WRITE;
/*!40000 ALTER TABLE `username_login_failure` DISABLE KEYS */;
/*!40000 ALTER TABLE `username_login_failure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_origins`
--

DROP TABLE IF EXISTS `web_origins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_origins` (
  `CLIENT_ID` varchar(36) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  PRIMARY KEY (`CLIENT_ID`,`VALUE`),
  KEY `IDX_WEB_ORIG_CLIENT` (`CLIENT_ID`),
  CONSTRAINT `FK_LOJPHO213XCX4WNKOG82SSRFY` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_origins`
--

LOCK TABLES `web_origins` WRITE;
/*!40000 ALTER TABLE `web_origins` DISABLE KEYS */;
INSERT INTO `web_origins` VALUES ('2e5f8397-470b-4516-8cc3-aa92e3bda308',''),('470a4a8a-3268-49ff-81b1-bb2c6dfda5ff','+'),('5db6df3c-92fb-40c0-a60d-ecdaa5827c59','+'),('775ead1f-db83-43ad-a74d-b856153f9e51','http://kafka-bmt02:8081'),('77d2f780-3bec-4dcc-84d8-1862aa357c84','http://kafka-bmt01:8081'),('c037ec42-3ac7-4c21-a9c2-a0ef7749a697','http://localhost:18080'),('e0dfbdd0-31d3-470e-b0dc-5a8bc09586fe','http://paasportal.strato.com');
/*!40000 ALTER TABLE `web_origins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'keycloak'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-30 16:30:06
