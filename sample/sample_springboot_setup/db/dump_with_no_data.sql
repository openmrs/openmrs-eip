-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: openmrs
-- ------------------------------------------------------
-- Server version	5.6.44

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
-- Table structure for table `address_hierarchy_address_to_entry_map`
--

DROP TABLE IF EXISTS `address_hierarchy_address_to_entry_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address_hierarchy_address_to_entry_map` (
  `address_to_entry_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `address_id` int(11) NOT NULL,
  `entry_id` int(11) NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`address_to_entry_map_id`),
  KEY `address_id_to_person_address_table` (`address_id`),
  KEY `entry_id_to_address_hierarchy_table` (`entry_id`),
  CONSTRAINT `address_id_to_person_address_table` FOREIGN KEY (`address_id`) REFERENCES `person_address` (`person_address_id`),
  CONSTRAINT `entry_id_to_address_hierarchy_table` FOREIGN KEY (`entry_id`) REFERENCES `address_hierarchy_entry` (`address_hierarchy_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `address_hierarchy_entry`
--

DROP TABLE IF EXISTS `address_hierarchy_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address_hierarchy_entry` (
  `address_hierarchy_entry_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(160) DEFAULT NULL,
  `level_id` int(11) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `user_generated_id` varchar(11) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `elevation` double DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`address_hierarchy_entry_id`),
  KEY `parent_name` (`parent_id`,`name`(20)),
  KEY `level_name` (`level_id`,`name`(20)),
  KEY `address_hierarchy_entry_name_idx` (`name`(10)),
  CONSTRAINT `level_to_level` FOREIGN KEY (`level_id`) REFERENCES `address_hierarchy_level` (`address_hierarchy_level_id`),
  CONSTRAINT `parent-to-parent` FOREIGN KEY (`parent_id`) REFERENCES `address_hierarchy_entry` (`address_hierarchy_entry_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `address_hierarchy_level`
--

DROP TABLE IF EXISTS `address_hierarchy_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address_hierarchy_level` (
  `address_hierarchy_level_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(160) DEFAULT NULL,
  `parent_level_id` int(11) DEFAULT NULL,
  `address_field` varchar(50) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `required` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address_hierarchy_level_id`),
  UNIQUE KEY `parent_level_id_unique` (`parent_level_id`),
  KEY `address_field_unique` (`address_field`),
  CONSTRAINT `parent_level` FOREIGN KEY (`parent_level_id`) REFERENCES `address_hierarchy_level` (`address_hierarchy_level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allergy`
--

DROP TABLE IF EXISTS `allergy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allergy` (
  `allergy_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL,
  `severity_concept_id` int(11) DEFAULT NULL,
  `coded_allergen` int(11) NOT NULL,
  `non_coded_allergen` varchar(255) DEFAULT NULL,
  `allergen_type` varchar(50) NOT NULL,
  `comment` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '1',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) DEFAULT NULL,
  PRIMARY KEY (`allergy_id`),
  UNIQUE KEY `allergy_id` (`allergy_id`),
  KEY `allergy_patient_id_fk` (`patient_id`),
  KEY `allergy_coded_allergen_fk` (`coded_allergen`),
  KEY `allergy_severity_concept_id_fk` (`severity_concept_id`),
  KEY `allergy_creator_fk` (`creator`),
  KEY `allergy_changed_by_fk` (`changed_by`),
  KEY `allergy_voided_by_fk` (`voided_by`),
  CONSTRAINT `allergy_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `allergy_coded_allergen_fk` FOREIGN KEY (`coded_allergen`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `allergy_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `allergy_patient_id_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `allergy_severity_concept_id_fk` FOREIGN KEY (`severity_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `allergy_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allergy_reaction`
--

DROP TABLE IF EXISTS `allergy_reaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allergy_reaction` (
  `allergy_reaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `allergy_id` int(11) NOT NULL,
  `reaction_concept_id` int(11) NOT NULL,
  `reaction_non_coded` varchar(255) DEFAULT NULL,
  `uuid` char(38) DEFAULT NULL,
  PRIMARY KEY (`allergy_reaction_id`),
  UNIQUE KEY `allergy_reaction_id` (`allergy_reaction_id`),
  KEY `allergy_reaction_allergy_id_fk` (`allergy_id`),
  KEY `allergy_reaction_reaction_concept_id_fk` (`reaction_concept_id`),
  CONSTRAINT `allergy_reaction_allergy_id_fk` FOREIGN KEY (`allergy_id`) REFERENCES `allergy` (`allergy_id`),
  CONSTRAINT `allergy_reaction_reaction_concept_id_fk` FOREIGN KEY (`reaction_concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appframework_component_state`
--

DROP TABLE IF EXISTS `appframework_component_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appframework_component_state` (
  `component_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `component_id` varchar(255) NOT NULL,
  `component_type` varchar(50) NOT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`component_state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appframework_user_app`
--

DROP TABLE IF EXISTS `appframework_user_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appframework_user_app` (
  `app_id` varchar(50) NOT NULL,
  `json` mediumtext NOT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_appointment`
--

DROP TABLE IF EXISTS `appointmentscheduling_appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_appointment` (
  `appointment_id` int(11) NOT NULL AUTO_INCREMENT,
  `time_slot_id` int(11) NOT NULL,
  `visit_id` int(11) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `appointment_type_id` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `reason` varchar(1024) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(4) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `cancel_reason` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `appointment_creator` (`creator`),
  KEY `appointment_changed_by` (`changed_by`),
  KEY `appointment_voided_by` (`voided_by`),
  KEY `appointment_time_slot_id` (`time_slot_id`),
  KEY `appointment_appointment_type_id` (`appointment_type_id`),
  KEY `appointment_visit_id` (`visit_id`),
  KEY `appointment_patient_id` (`patient_id`),
  CONSTRAINT `appointment_appointment_type_id` FOREIGN KEY (`appointment_type_id`) REFERENCES `appointmentscheduling_appointment_type` (`appointment_type_id`),
  CONSTRAINT `appointment_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_patient_id` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `appointment_time_slot_id` FOREIGN KEY (`time_slot_id`) REFERENCES `appointmentscheduling_time_slot` (`time_slot_id`),
  CONSTRAINT `appointment_visit_id` FOREIGN KEY (`visit_id`) REFERENCES `visit` (`visit_id`),
  CONSTRAINT `appointment_voided_by` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_appointment_block`
--

DROP TABLE IF EXISTS `appointmentscheduling_appointment_block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_appointment_block` (
  `appointment_block_id` int(11) NOT NULL AUTO_INCREMENT,
  `location_id` int(11) NOT NULL,
  `provider_id` int(11) DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(4) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`appointment_block_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `appointment_block_creator` (`creator`),
  KEY `appointment_block_changed_by` (`changed_by`),
  KEY `appointment_block_voided_by` (`voided_by`),
  KEY `appointment_block_location_id` (`location_id`),
  KEY `appointment_block_provider_id` (`provider_id`),
  CONSTRAINT `appointment_block_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_block_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_block_location_id` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `appointment_block_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `appointment_block_voided_by` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_appointment_request`
--

DROP TABLE IF EXISTS `appointmentscheduling_appointment_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_appointment_request` (
  `appointment_request_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL,
  `appointment_type_id` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `provider_id` int(11) DEFAULT NULL,
  `requested_by` int(11) DEFAULT NULL,
  `requested_on` datetime NOT NULL,
  `min_time_frame_value` int(11) DEFAULT NULL,
  `min_time_frame_units` varchar(255) DEFAULT NULL,
  `max_time_frame_value` int(11) DEFAULT NULL,
  `max_time_frame_units` varchar(255) DEFAULT NULL,
  `notes` varchar(1024) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(4) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`appointment_request_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `appointment_request_creator` (`creator`),
  KEY `appointment_request_changed_by` (`changed_by`),
  KEY `appointment_request_voided_by` (`voided_by`),
  KEY `appointment_request_appointment_type_id` (`appointment_type_id`),
  KEY `appointment_request_patient_id` (`patient_id`),
  KEY `appointment_request_provider_id` (`provider_id`),
  KEY `appointment_request_requested_by` (`requested_by`),
  CONSTRAINT `appointment_request_appointment_type_id` FOREIGN KEY (`appointment_type_id`) REFERENCES `appointmentscheduling_appointment_type` (`appointment_type_id`),
  CONSTRAINT `appointment_request_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_request_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_request_patient_id` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `appointment_request_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `appointment_request_requested_by` FOREIGN KEY (`requested_by`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `appointment_request_voided_by` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_appointment_status_history`
--

DROP TABLE IF EXISTS `appointmentscheduling_appointment_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_appointment_status_history` (
  `appointment_status_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `appointment_id` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  PRIMARY KEY (`appointment_status_history_id`),
  KEY `appointment_status_history_appointment` (`appointment_id`),
  CONSTRAINT `appointment_status_history_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointmentscheduling_appointment` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_appointment_type`
--

DROP TABLE IF EXISTS `appointmentscheduling_appointment_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_appointment_type` (
  `appointment_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `duration` int(11) NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(4) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `confidential` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`appointment_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `appointment_type_creator` (`creator`),
  KEY `appointment_type_changed_by` (`changed_by`),
  KEY `appointment_type_retired_by` (`retired_by`),
  CONSTRAINT `appointment_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_type_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_block_type_map`
--

DROP TABLE IF EXISTS `appointmentscheduling_block_type_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_block_type_map` (
  `appointment_type_id` int(11) NOT NULL,
  `appointment_block_id` int(11) NOT NULL,
  PRIMARY KEY (`appointment_type_id`,`appointment_block_id`),
  KEY `appointment_block_type_map_appointment_block_id` (`appointment_block_id`),
  CONSTRAINT `appointment_block_type_map_appointment_block_id` FOREIGN KEY (`appointment_block_id`) REFERENCES `appointmentscheduling_appointment_block` (`appointment_block_id`),
  CONSTRAINT `appointment_block_type_map_appointment_type_id` FOREIGN KEY (`appointment_type_id`) REFERENCES `appointmentscheduling_appointment_type` (`appointment_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointmentscheduling_time_slot`
--

DROP TABLE IF EXISTS `appointmentscheduling_time_slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointmentscheduling_time_slot` (
  `time_slot_id` int(11) NOT NULL AUTO_INCREMENT,
  `appointment_block_id` int(11) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(4) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`time_slot_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `appointment_slot_creator` (`creator`),
  KEY `appointment_slot__changed_by` (`changed_by`),
  KEY `appointment_slot_voided_by` (`voided_by`),
  KEY `appointment_slot_appointment_block_id` (`appointment_block_id`),
  CONSTRAINT `appointment_slot__changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_slot_appointment_block_id` FOREIGN KEY (`appointment_block_id`) REFERENCES `appointmentscheduling_appointment_block` (`appointment_block_id`),
  CONSTRAINT `appointment_slot_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointment_slot_voided_by` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calculation_registration`
--

DROP TABLE IF EXISTS `calculation_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calculation_registration` (
  `calculation_registration_id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `provider_class_name` varchar(512) NOT NULL,
  `calculation_name` varchar(512) NOT NULL,
  `configuration` text,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`calculation_registration_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `care_setting`
--

DROP TABLE IF EXISTS `care_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `care_setting` (
  `care_setting_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `care_setting_type` varchar(50) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`care_setting_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `care_setting_creator` (`creator`),
  KEY `care_setting_retired_by` (`retired_by`),
  KEY `care_setting_changed_by` (`changed_by`),
  CONSTRAINT `care_setting_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `care_setting_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `care_setting_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_bookmark`
--

DROP TABLE IF EXISTS `chartsearch_bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_bookmark` (
  `bookmark_id` int(11) NOT NULL AUTO_INCREMENT,
  `bookmark_name` text NOT NULL,
  `search_phrase` text NOT NULL,
  `selected_categories` text,
  `uuid` char(38) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `default_search` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bookmark_id`),
  KEY `bookmark_owner-fk` (`user_id`),
  KEY `bookmark_patient-fk` (`patient_id`),
  CONSTRAINT `bookmark_owner-fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `bookmark_patient-fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_categories`
--

DROP TABLE IF EXISTS `chartsearch_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_categories` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `filter_query` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_category_displayname`
--

DROP TABLE IF EXISTS `chartsearch_category_displayname`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_category_displayname` (
  `displayname_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `diplay_name` varchar(15) NOT NULL,
  `preference_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY (`displayname_id`),
  KEY `categtory_displayname-fk` (`preference_id`),
  KEY `displayname_owner-fk` (`category_id`),
  CONSTRAINT `categtory_displayname-fk` FOREIGN KEY (`preference_id`) REFERENCES `chartsearch_preference` (`preference_id`),
  CONSTRAINT `displayname_owner-fk` FOREIGN KEY (`category_id`) REFERENCES `chartsearch_categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_history`
--

DROP TABLE IF EXISTS `chartsearch_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_history` (
  `search_id` int(11) NOT NULL AUTO_INCREMENT,
  `search_phrase` text NOT NULL,
  `last_searched_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uuid` char(38) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  PRIMARY KEY (`search_id`),
  KEY `history_owner-fk` (`user_id`),
  KEY `history_patient-fk` (`patient_id`),
  CONSTRAINT `history_owner-fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `history_patient-fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_note`
--

DROP TABLE IF EXISTS `chartsearch_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_note` (
  `note_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment` text NOT NULL,
  `search_phrase` text NOT NULL,
  `priority` text NOT NULL,
  `created_or_last_modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uuid` char(38) DEFAULT NULL,
  `display_color` char(10) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  PRIMARY KEY (`note_id`),
  KEY `note_owner-fk` (`user_id`),
  KEY `note_patient-fk` (`patient_id`),
  CONSTRAINT `note_owner-fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `note_patient-fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_preference`
--

DROP TABLE IF EXISTS `chartsearch_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_preference` (
  `preference_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `enable_history` tinyint(1) NOT NULL DEFAULT '1',
  `enable_bookmark` tinyint(1) NOT NULL DEFAULT '1',
  `enable_notes` tinyint(1) NOT NULL DEFAULT '1',
  `enable_duplicateresults` tinyint(1) NOT NULL DEFAULT '1',
  `enable_multiplefiltering` tinyint(1) NOT NULL DEFAULT '1',
  `enable_quicksearches` tinyint(1) NOT NULL DEFAULT '1',
  `enable_defaultsearch` tinyint(1) NOT NULL DEFAULT '1',
  `personalnotes_colors` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`preference_id`),
  KEY `preference_owner-fk` (`user_id`),
  CONSTRAINT `preference_owner-fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_synonym_groups`
--

DROP TABLE IF EXISTS `chartsearch_synonym_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_synonym_groups` (
  `group_id` int(11) NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `is_category` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chartsearch_synonyms`
--

DROP TABLE IF EXISTS `chartsearch_synonyms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartsearch_synonyms` (
  `synonym_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`synonym_id`),
  KEY `fk_synonymgroups_synonyms` (`group_id`),
  CONSTRAINT `fk_synonymgroups_synonyms` FOREIGN KEY (`group_id`) REFERENCES `chartsearch_synonym_groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clob_datatype_storage`
--

DROP TABLE IF EXISTS `clob_datatype_storage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clob_datatype_storage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `value` longtext NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `clob_datatype_storage_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cohort`
--

DROP TABLE IF EXISTS `cohort`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cohort` (
  `cohort_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`cohort_id`),
  UNIQUE KEY `cohort_uuid_index` (`uuid`),
  KEY `user_who_changed_cohort` (`changed_by`),
  KEY `cohort_creator` (`creator`),
  KEY `user_who_voided_cohort` (`voided_by`),
  CONSTRAINT `cohort_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_cohort` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_cohort` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cohort_member`
--

DROP TABLE IF EXISTS `cohort_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cohort_member` (
  `cohort_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `cohort_member_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`cohort_member_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `member_patient` (`patient_id`),
  KEY `cohort_member_creator` (`creator`),
  KEY `parent_cohort` (`cohort_id`),
  CONSTRAINT `cohort_member_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `member_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `parent_cohort` FOREIGN KEY (`cohort_id`) REFERENCES `cohort` (`cohort_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept`
--

DROP TABLE IF EXISTS `concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept` (
  `concept_id` int(11) NOT NULL AUTO_INCREMENT,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `short_name` varchar(255) DEFAULT NULL,
  `description` text,
  `form_text` text,
  `datatype_id` int(11) NOT NULL DEFAULT '0',
  `class_id` int(11) NOT NULL DEFAULT '0',
  `is_set` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `version` varchar(50) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_id`),
  UNIQUE KEY `concept_uuid_index` (`uuid`),
  KEY `concept_classes` (`class_id`),
  KEY `concept_creator` (`creator`),
  KEY `concept_datatypes` (`datatype_id`),
  KEY `user_who_changed_concept` (`changed_by`),
  KEY `concept_code` (`version`),
  KEY `concept_ndx` (`version`),
  KEY `user_who_retired_concept` (`retired_by`),
  CONSTRAINT `concept_classes` FOREIGN KEY (`class_id`) REFERENCES `concept_class` (`concept_class_id`),
  CONSTRAINT `concept_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_datatypes` FOREIGN KEY (`datatype_id`) REFERENCES `concept_datatype` (`concept_datatype_id`),
  CONSTRAINT `user_who_changed_concept` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=173456 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_answer`
--

DROP TABLE IF EXISTS `concept_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_answer` (
  `concept_answer_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `answer_concept` int(11) DEFAULT NULL,
  `answer_drug` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `uuid` char(38) NOT NULL,
  `sort_weight` double DEFAULT NULL,
  PRIMARY KEY (`concept_answer_id`),
  UNIQUE KEY `concept_answer_uuid_index` (`uuid`),
  KEY `answer_creator` (`creator`),
  KEY `answer` (`answer_concept`),
  KEY `answers_for_concept` (`concept_id`),
  KEY `answer_answer_drug_fk` (`answer_drug`),
  CONSTRAINT `answer` FOREIGN KEY (`answer_concept`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `answer_answer_drug_fk` FOREIGN KEY (`answer_drug`) REFERENCES `drug` (`drug_id`),
  CONSTRAINT `answer_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `answers_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28787 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_attribute`
--

DROP TABLE IF EXISTS `concept_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_attribute` (
  `concept_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL,
  `attribute_type_id` int(11) NOT NULL,
  `value_reference` text NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`concept_attribute_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `concept_attribute_concept_fk` (`concept_id`),
  KEY `concept_attribute_attribute_type_id_fk` (`attribute_type_id`),
  KEY `concept_attribute_creator_fk` (`creator`),
  KEY `concept_attribute_changed_by_fk` (`changed_by`),
  KEY `concept_attribute_voided_by_fk` (`voided_by`),
  CONSTRAINT `concept_attribute_attribute_type_id_fk` FOREIGN KEY (`attribute_type_id`) REFERENCES `concept_attribute_type` (`concept_attribute_type_id`),
  CONSTRAINT `concept_attribute_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_attribute_concept_fk` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `concept_attribute_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_attribute_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6205 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_attribute_type`
--

DROP TABLE IF EXISTS `concept_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_attribute_type` (
  `concept_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `min_occurs` int(11) NOT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_attribute_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `concept_attribute_type_creator_fk` (`creator`),
  KEY `concept_attribute_type_changed_by_fk` (`changed_by`),
  KEY `concept_attribute_type_retired_by_fk` (`retired_by`),
  CONSTRAINT `concept_attribute_type_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_attribute_type_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_attribute_type_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_class`
--

DROP TABLE IF EXISTS `concept_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_class` (
  `concept_class_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`concept_class_id`),
  UNIQUE KEY `concept_class_uuid_index` (`uuid`),
  KEY `concept_class_creator` (`creator`),
  KEY `user_who_retired_concept_class` (`retired_by`),
  KEY `concept_class_retired_status` (`retired`),
  KEY `concept_class_name_index` (`name`),
  KEY `concept_class_changed_by` (`changed_by`),
  CONSTRAINT `concept_class_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_class_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_class` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_complex`
--

DROP TABLE IF EXISTS `concept_complex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_complex` (
  `concept_id` int(11) NOT NULL,
  `handler` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`concept_id`),
  CONSTRAINT `concept_attributes` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_datatype`
--

DROP TABLE IF EXISTS `concept_datatype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_datatype` (
  `concept_datatype_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `hl7_abbreviation` varchar(3) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_datatype_id`),
  UNIQUE KEY `concept_datatype_uuid_index` (`uuid`),
  KEY `concept_datatype_creator` (`creator`),
  KEY `user_who_retired_concept_datatype` (`retired_by`),
  KEY `concept_datatype_retired_status` (`retired`),
  KEY `concept_datatype_name_index` (`name`),
  CONSTRAINT `concept_datatype_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_datatype` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_description`
--

DROP TABLE IF EXISTS `concept_description`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_description` (
  `concept_description_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `description` text NOT NULL,
  `locale` varchar(50) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_description_id`),
  UNIQUE KEY `concept_description_uuid_index` (`uuid`),
  KEY `concept_being_described` (`concept_id`),
  KEY `user_who_created_description` (`creator`),
  KEY `user_who_changed_description` (`changed_by`),
  CONSTRAINT `description_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_changed_description` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_description` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24082 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_map_type`
--

DROP TABLE IF EXISTS `concept_map_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_map_type` (
  `concept_map_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `is_hidden` tinyint(1) DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_map_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `name` (`name`),
  KEY `mapped_user_creator_concept_map_type` (`creator`),
  KEY `mapped_user_changed_concept_map_type` (`changed_by`),
  KEY `mapped_user_retired_concept_map_type` (`retired_by`),
  CONSTRAINT `mapped_user_changed_concept_map_type` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `mapped_user_creator_concept_map_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `mapped_user_retired_concept_map_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_name`
--

DROP TABLE IF EXISTS `concept_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name` (
  `concept_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `locale` varchar(50) NOT NULL DEFAULT '',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `concept_name_id` int(11) NOT NULL AUTO_INCREMENT,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `concept_name_type` varchar(50) DEFAULT NULL,
  `locale_preferred` tinyint(1) DEFAULT '0',
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`concept_name_id`),
  UNIQUE KEY `concept_name_id` (`concept_name_id`),
  UNIQUE KEY `concept_name_uuid_index` (`uuid`),
  KEY `user_who_created_name` (`creator`),
  KEY `name_of_concept` (`name`),
  KEY `concept_id` (`concept_id`),
  KEY `unique_concept_name_id` (`concept_id`),
  KEY `user_who_voided_name` (`voided_by`),
  KEY `concept_name_changed_by` (`changed_by`),
  CONSTRAINT `concept_name_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `name_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created_name` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_this_name` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=234030 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_name_tag`
--

DROP TABLE IF EXISTS `concept_name_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name_tag` (
  `concept_name_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(50) NOT NULL,
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`concept_name_tag_id`),
  UNIQUE KEY `concept_name_tag_id` (`concept_name_tag_id`),
  UNIQUE KEY `concept_name_tag_id_2` (`concept_name_tag_id`),
  UNIQUE KEY `concept_name_tag_unique_tags` (`tag`),
  UNIQUE KEY `concept_name_tag_uuid_index` (`uuid`),
  KEY `user_who_created_name_tag` (`creator`),
  KEY `user_who_voided_name_tag` (`voided_by`),
  KEY `concept_name_tag_changed_by` (`changed_by`),
  CONSTRAINT `concept_name_tag_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_name_tag_map`
--

DROP TABLE IF EXISTS `concept_name_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_name_tag_map` (
  `concept_name_id` int(11) NOT NULL,
  `concept_name_tag_id` int(11) NOT NULL,
  KEY `map_name` (`concept_name_id`),
  KEY `map_name_tag` (`concept_name_tag_id`),
  CONSTRAINT `mapped_concept_name` FOREIGN KEY (`concept_name_id`) REFERENCES `concept_name` (`concept_name_id`),
  CONSTRAINT `mapped_concept_name_tag` FOREIGN KEY (`concept_name_tag_id`) REFERENCES `concept_name_tag` (`concept_name_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_numeric`
--

DROP TABLE IF EXISTS `concept_numeric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_numeric` (
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `hi_absolute` double DEFAULT NULL,
  `hi_critical` double DEFAULT NULL,
  `hi_normal` double DEFAULT NULL,
  `low_absolute` double DEFAULT NULL,
  `low_critical` double DEFAULT NULL,
  `low_normal` double DEFAULT NULL,
  `units` varchar(50) DEFAULT NULL,
  `allow_decimal` tinyint(1) DEFAULT NULL,
  `display_precision` int(11) DEFAULT NULL,
  PRIMARY KEY (`concept_id`),
  CONSTRAINT `numeric_attributes` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_proposal`
--

DROP TABLE IF EXISTS `concept_proposal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_proposal` (
  `concept_proposal_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) DEFAULT NULL,
  `encounter_id` int(11) DEFAULT NULL,
  `original_text` varchar(255) NOT NULL DEFAULT '',
  `final_text` varchar(255) DEFAULT NULL,
  `obs_id` int(11) DEFAULT NULL,
  `obs_concept_id` int(11) DEFAULT NULL,
  `state` varchar(32) NOT NULL DEFAULT 'UNMAPPED',
  `comments` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `locale` varchar(50) NOT NULL DEFAULT '',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_proposal_id`),
  UNIQUE KEY `concept_proposal_uuid_index` (`uuid`),
  KEY `user_who_changed_proposal` (`changed_by`),
  KEY `concept_for_proposal` (`concept_id`),
  KEY `user_who_created_proposal` (`creator`),
  KEY `encounter_for_proposal` (`encounter_id`),
  KEY `proposal_obs_concept_id` (`obs_concept_id`),
  KEY `proposal_obs_id` (`obs_id`),
  CONSTRAINT `concept_for_proposal` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `encounter_for_proposal` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `proposal_obs_concept_id` FOREIGN KEY (`obs_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `proposal_obs_id` FOREIGN KEY (`obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `user_who_changed_proposal` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_proposal` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_proposal_tag_map`
--

DROP TABLE IF EXISTS `concept_proposal_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_proposal_tag_map` (
  `concept_proposal_id` int(11) NOT NULL,
  `concept_name_tag_id` int(11) NOT NULL,
  KEY `mapped_concept_proposal_tag` (`concept_name_tag_id`),
  KEY `mapped_concept_proposal` (`concept_proposal_id`),
  CONSTRAINT `mapped_concept_proposal` FOREIGN KEY (`concept_proposal_id`) REFERENCES `concept_proposal` (`concept_proposal_id`),
  CONSTRAINT `mapped_concept_proposal_tag` FOREIGN KEY (`concept_name_tag_id`) REFERENCES `concept_name_tag` (`concept_name_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_reference_map`
--

DROP TABLE IF EXISTS `concept_reference_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_reference_map` (
  `concept_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `uuid` char(38) NOT NULL,
  `concept_reference_term_id` int(11) NOT NULL,
  `concept_map_type_id` int(11) NOT NULL DEFAULT '1',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`concept_map_id`),
  UNIQUE KEY `concept_map_uuid_index` (`uuid`),
  UNIQUE KEY `concept_reference_map_uuid_id` (`uuid`),
  KEY `map_creator` (`creator`),
  KEY `map_for_concept` (`concept_id`),
  KEY `mapped_concept_map_type` (`concept_map_type_id`),
  KEY `mapped_user_changed_ref_term` (`changed_by`),
  KEY `mapped_concept_reference_term` (`concept_reference_term_id`),
  CONSTRAINT `map_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `map_for_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `mapped_concept_map_type` FOREIGN KEY (`concept_map_type_id`) REFERENCES `concept_map_type` (`concept_map_type_id`),
  CONSTRAINT `mapped_concept_reference_term` FOREIGN KEY (`concept_reference_term_id`) REFERENCES `concept_reference_term` (`concept_reference_term_id`),
  CONSTRAINT `mapped_user_changed_ref_term` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=358695 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_reference_source`
--

DROP TABLE IF EXISTS `concept_reference_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_reference_source` (
  `concept_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `hl7_code` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `unique_id` varchar(250) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`concept_source_id`),
  UNIQUE KEY `concept_source_uuid_index` (`uuid`),
  UNIQUE KEY `concept_reference_source_uuid_id` (`uuid`),
  UNIQUE KEY `concept_source_unique_hl7_codes` (`hl7_code`),
  UNIQUE KEY `concept_reference_source_unique_id_unique` (`unique_id`),
  KEY `concept_source_creator` (`creator`),
  KEY `user_who_voided_concept_source` (`retired_by`),
  KEY `unique_hl7_code` (`hl7_code`,`retired`),
  KEY `concept_reference_source_changed_by` (`changed_by`),
  CONSTRAINT `concept_reference_source_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `concept_source_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_concept_source` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_reference_term`
--

DROP TABLE IF EXISTS `concept_reference_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_reference_term` (
  `concept_reference_term_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_source_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) NOT NULL,
  `version` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_reference_term_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `mapped_user_creator` (`creator`),
  KEY `mapped_user_changed` (`changed_by`),
  KEY `mapped_user_retired` (`retired_by`),
  KEY `mapped_concept_source` (`concept_source_id`),
  KEY `idx_code_concept_reference_term` (`code`),
  CONSTRAINT `mapped_concept_source` FOREIGN KEY (`concept_source_id`) REFERENCES `concept_reference_source` (`concept_source_id`),
  CONSTRAINT `mapped_user_changed` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `mapped_user_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `mapped_user_retired` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=298116 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_reference_term_map`
--

DROP TABLE IF EXISTS `concept_reference_term_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_reference_term_map` (
  `concept_reference_term_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `term_a_id` int(11) NOT NULL,
  `term_b_id` int(11) NOT NULL,
  `a_is_to_b_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_reference_term_map_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `mapped_term_a` (`term_a_id`),
  KEY `mapped_term_b` (`term_b_id`),
  KEY `mapped_concept_map_type_ref_term_map` (`a_is_to_b_id`),
  KEY `mapped_user_creator_ref_term_map` (`creator`),
  KEY `mapped_user_changed_ref_term_map` (`changed_by`),
  CONSTRAINT `mapped_concept_map_type_ref_term_map` FOREIGN KEY (`a_is_to_b_id`) REFERENCES `concept_map_type` (`concept_map_type_id`),
  CONSTRAINT `mapped_term_a` FOREIGN KEY (`term_a_id`) REFERENCES `concept_reference_term` (`concept_reference_term_id`),
  CONSTRAINT `mapped_term_b` FOREIGN KEY (`term_b_id`) REFERENCES `concept_reference_term` (`concept_reference_term_id`),
  CONSTRAINT `mapped_user_changed_ref_term_map` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `mapped_user_creator_ref_term_map` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_set`
--

DROP TABLE IF EXISTS `concept_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_set` (
  `concept_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `concept_set` int(11) NOT NULL DEFAULT '0',
  `sort_weight` double DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_set_id`),
  UNIQUE KEY `concept_set_uuid_index` (`uuid`),
  KEY `has_a` (`concept_set`),
  KEY `user_who_created` (`creator`),
  KEY `idx_concept_set_concept` (`concept_id`),
  CONSTRAINT `has_a` FOREIGN KEY (`concept_set`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `is_a` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16185 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_state_conversion`
--

DROP TABLE IF EXISTS `concept_state_conversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_state_conversion` (
  `concept_state_conversion_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) DEFAULT '0',
  `program_workflow_id` int(11) DEFAULT '0',
  `program_workflow_state_id` int(11) DEFAULT '0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_state_conversion_id`),
  UNIQUE KEY `concept_state_conversion_uuid_index` (`uuid`),
  UNIQUE KEY `unique_workflow_concept_in_conversion` (`program_workflow_id`,`concept_id`),
  KEY `triggering_concept` (`concept_id`),
  KEY `affected_workflow` (`program_workflow_id`),
  KEY `resulting_state` (`program_workflow_state_id`),
  CONSTRAINT `concept_triggers_conversion` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `conversion_involves_workflow` FOREIGN KEY (`program_workflow_id`) REFERENCES `program_workflow` (`program_workflow_id`),
  CONSTRAINT `conversion_to_state` FOREIGN KEY (`program_workflow_state_id`) REFERENCES `program_workflow_state` (`program_workflow_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `concept_stop_word`
--

DROP TABLE IF EXISTS `concept_stop_word`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concept_stop_word` (
  `concept_stop_word_id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(50) NOT NULL,
  `locale` varchar(50) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`concept_stop_word_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `Unique_StopWord_Key` (`word`,`locale`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conditions` (
  `condition_id` int(11) NOT NULL AUTO_INCREMENT,
  `additional_detail` varchar(255) DEFAULT NULL,
  `previous_version` int(11) DEFAULT NULL,
  `condition_coded` int(11) DEFAULT NULL,
  `condition_non_coded` varchar(255) DEFAULT NULL,
  `condition_coded_name` int(11) DEFAULT NULL,
  `clinical_status` varchar(50) NOT NULL,
  `verification_status` varchar(50) DEFAULT NULL,
  `onset_date` datetime DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` varchar(38) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `voided_by` int(11) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`condition_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `condition_previous_version_fk` (`previous_version`),
  KEY `condition_condition_coded_fk` (`condition_coded`),
  KEY `condition_condition_coded_name_fk` (`condition_coded_name`),
  KEY `condition_creator_fk` (`creator`),
  KEY `condition_changed_by_fk` (`changed_by`),
  KEY `condition_voided_by_fk` (`voided_by`),
  KEY `condition_patient_fk` (`patient_id`),
  CONSTRAINT `condition_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `condition_condition_coded_fk` FOREIGN KEY (`condition_coded`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `condition_condition_coded_name_fk` FOREIGN KEY (`condition_coded_name`) REFERENCES `concept_name` (`concept_name_id`),
  CONSTRAINT `condition_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `condition_patient_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `condition_previous_version_fk` FOREIGN KEY (`previous_version`) REFERENCES `conditions` (`condition_id`),
  CONSTRAINT `condition_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug`
--

DROP TABLE IF EXISTS `drug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug` (
  `drug_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `combination` tinyint(1) NOT NULL DEFAULT '0',
  `dosage_form` int(11) DEFAULT NULL,
  `maximum_daily_dose` double DEFAULT NULL,
  `minimum_daily_dose` double DEFAULT NULL,
  `route` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `strength` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`drug_id`),
  UNIQUE KEY `drug_uuid_index` (`uuid`),
  KEY `primary_drug_concept` (`concept_id`),
  KEY `drug_creator` (`creator`),
  KEY `drug_changed_by` (`changed_by`),
  KEY `dosage_form_concept` (`dosage_form`),
  KEY `drug_retired_by` (`retired_by`),
  KEY `route_concept` (`route`),
  CONSTRAINT `dosage_form_concept` FOREIGN KEY (`dosage_form`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `drug_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `drug_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `primary_drug_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `route_concept` FOREIGN KEY (`route`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_ingredient`
--

DROP TABLE IF EXISTS `drug_ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug_ingredient` (
  `drug_id` int(11) NOT NULL,
  `ingredient_id` int(11) NOT NULL,
  `uuid` char(38) NOT NULL,
  `strength` double DEFAULT NULL,
  `units` int(11) DEFAULT NULL,
  PRIMARY KEY (`drug_id`,`ingredient_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `drug_ingredient_units_fk` (`units`),
  KEY `drug_ingredient_ingredient_id_fk` (`ingredient_id`),
  CONSTRAINT `drug_ingredient_drug_id_fk` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`drug_id`),
  CONSTRAINT `drug_ingredient_ingredient_id_fk` FOREIGN KEY (`ingredient_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_ingredient_units_fk` FOREIGN KEY (`units`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_order`
--

DROP TABLE IF EXISTS `drug_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug_order` (
  `order_id` int(11) NOT NULL DEFAULT '0',
  `drug_inventory_id` int(11) DEFAULT NULL,
  `dose` double DEFAULT NULL,
  `as_needed` tinyint(1) DEFAULT NULL,
  `dosing_type` varchar(255) DEFAULT NULL,
  `quantity` double DEFAULT NULL,
  `as_needed_condition` varchar(255) DEFAULT NULL,
  `num_refills` int(11) DEFAULT NULL,
  `dosing_instructions` text,
  `duration` int(11) DEFAULT NULL,
  `duration_units` int(11) DEFAULT NULL,
  `quantity_units` int(11) DEFAULT NULL,
  `route` int(11) DEFAULT NULL,
  `dose_units` int(11) DEFAULT NULL,
  `frequency` int(11) DEFAULT NULL,
  `brand_name` varchar(255) DEFAULT NULL,
  `dispense_as_written` tinyint(1) NOT NULL DEFAULT '0',
  `drug_non_coded` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `inventory_item` (`drug_inventory_id`),
  KEY `drug_order_duration_units_fk` (`duration_units`),
  KEY `drug_order_quantity_units` (`quantity_units`),
  KEY `drug_order_route_fk` (`route`),
  KEY `drug_order_dose_units` (`dose_units`),
  KEY `drug_order_frequency_fk` (`frequency`),
  CONSTRAINT `drug_order_dose_units` FOREIGN KEY (`dose_units`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_order_duration_units_fk` FOREIGN KEY (`duration_units`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_order_frequency_fk` FOREIGN KEY (`frequency`) REFERENCES `order_frequency` (`order_frequency_id`),
  CONSTRAINT `drug_order_quantity_units` FOREIGN KEY (`quantity_units`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `drug_order_route_fk` FOREIGN KEY (`route`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `extends_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `inventory_item` FOREIGN KEY (`drug_inventory_id`) REFERENCES `drug` (`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_reference_map`
--

DROP TABLE IF EXISTS `drug_reference_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drug_reference_map` (
  `drug_reference_map_id` int(11) NOT NULL AUTO_INCREMENT,
  `drug_id` int(11) NOT NULL,
  `term_id` int(11) NOT NULL,
  `concept_map_type` int(11) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`drug_reference_map_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `drug_for_drug_reference_map` (`drug_id`),
  KEY `concept_reference_term_for_drug_reference_map` (`term_id`),
  KEY `concept_map_type_for_drug_reference_map` (`concept_map_type`),
  KEY `user_who_changed_drug_reference_map` (`changed_by`),
  KEY `drug_reference_map_creator` (`creator`),
  KEY `user_who_retired_drug_reference_map` (`retired_by`),
  CONSTRAINT `concept_map_type_for_drug_reference_map` FOREIGN KEY (`concept_map_type`) REFERENCES `concept_map_type` (`concept_map_type_id`),
  CONSTRAINT `concept_reference_term_for_drug_reference_map` FOREIGN KEY (`term_id`) REFERENCES `concept_reference_term` (`concept_reference_term_id`),
  CONSTRAINT `drug_for_drug_reference_map` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`drug_id`),
  CONSTRAINT `drug_reference_map_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_drug_reference_map` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_drug_reference_map` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emrapi_conditions`
--

DROP TABLE IF EXISTS `emrapi_conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emrapi_conditions` (
  `condition_id` int(11) NOT NULL AUTO_INCREMENT,
  `previous_condition_id` int(11) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `concept_id` int(11) NOT NULL,
  `condition_non_coded` varchar(1024) DEFAULT NULL,
  `onset_date` datetime DEFAULT NULL,
  `additional_detail` varchar(1024) DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `end_reason` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL,
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`condition_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `condition_uuid_index` (`uuid`),
  KEY `conditions_previous_condition_id_fk` (`previous_condition_id`),
  KEY `conditions_patient_fk` (`patient_id`),
  KEY `conditions_concept_fk` (`concept_id`),
  KEY `conditions_end_reason_fk` (`end_reason`),
  KEY `conditions_created_by_fk` (`creator`),
  KEY `conditions_voided_by_fk` (`voided_by`),
  CONSTRAINT `conditions_concept_fk` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `conditions_created_by_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `conditions_end_reason_fk` FOREIGN KEY (`end_reason`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `conditions_patient_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `conditions_previous_condition_id_fk` FOREIGN KEY (`previous_condition_id`) REFERENCES `emrapi_conditions` (`condition_id`),
  CONSTRAINT `conditions_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounter`
--

DROP TABLE IF EXISTS `encounter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter` (
  `encounter_id` int(11) NOT NULL AUTO_INCREMENT,
  `encounter_type` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `location_id` int(11) DEFAULT NULL,
  `form_id` int(11) DEFAULT NULL,
  `encounter_datetime` datetime NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `visit_id` int(11) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`encounter_id`),
  UNIQUE KEY `encounter_uuid_index` (`uuid`),
  KEY `encounter_datetime_idx` (`encounter_datetime`),
  KEY `encounter_ibfk_1` (`creator`),
  KEY `encounter_type_id` (`encounter_type`),
  KEY `encounter_form` (`form_id`),
  KEY `encounter_location` (`location_id`),
  KEY `encounter_patient` (`patient_id`),
  KEY `user_who_voided_encounter` (`voided_by`),
  KEY `encounter_changed_by` (`changed_by`),
  KEY `encounter_visit_id_fk` (`visit_id`),
  CONSTRAINT `encounter_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_form` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `encounter_ibfk_1` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `encounter_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `encounter_type_id` FOREIGN KEY (`encounter_type`) REFERENCES `encounter_type` (`encounter_type_id`),
  CONSTRAINT `encounter_visit_id_fk` FOREIGN KEY (`visit_id`) REFERENCES `visit` (`visit_id`),
  CONSTRAINT `user_who_voided_encounter` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounter_diagnosis`
--

DROP TABLE IF EXISTS `encounter_diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter_diagnosis` (
  `diagnosis_id` int(11) NOT NULL AUTO_INCREMENT,
  `diagnosis_coded` int(11) DEFAULT NULL,
  `diagnosis_non_coded` varchar(255) DEFAULT NULL,
  `diagnosis_coded_name` int(11) DEFAULT NULL,
  `encounter_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `condition_id` int(11) DEFAULT NULL,
  `certainty` varchar(255) NOT NULL,
  `rank` int(11) NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`diagnosis_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `encounter_diagnosis_encounter_id_fk` (`encounter_id`),
  KEY `encounter_diagnosis_condition_id_fk` (`condition_id`),
  KEY `encounter_diagnosis_creator_fk` (`creator`),
  KEY `encounter_diagnosis_voided_by_fk` (`voided_by`),
  KEY `encounter_diagnosis_changed_by_fk` (`changed_by`),
  KEY `encounter_diagnosis_coded_fk` (`diagnosis_coded`),
  KEY `encounter_diagnosis_coded_name_fk` (`diagnosis_coded_name`),
  KEY `encounter_diagnosis_patient_fk` (`patient_id`),
  CONSTRAINT `encounter_diagnosis_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_diagnosis_coded_fk` FOREIGN KEY (`diagnosis_coded`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `encounter_diagnosis_coded_name_fk` FOREIGN KEY (`diagnosis_coded_name`) REFERENCES `concept_name` (`concept_name_id`),
  CONSTRAINT `encounter_diagnosis_condition_id_fk` FOREIGN KEY (`condition_id`) REFERENCES `conditions` (`condition_id`),
  CONSTRAINT `encounter_diagnosis_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_diagnosis_encounter_id_fk` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `encounter_diagnosis_patient_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `encounter_diagnosis_patient_id_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `encounter_diagnosis_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounter_provider`
--

DROP TABLE IF EXISTS `encounter_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter_provider` (
  `encounter_provider_id` int(11) NOT NULL AUTO_INCREMENT,
  `encounter_id` int(11) NOT NULL,
  `provider_id` int(11) NOT NULL,
  `encounter_role_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `date_voided` datetime DEFAULT NULL,
  `voided_by` int(11) DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`encounter_provider_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `encounter_id_fk` (`encounter_id`),
  KEY `provider_id_fk` (`provider_id`),
  KEY `encounter_role_id_fk` (`encounter_role_id`),
  KEY `encounter_provider_creator` (`creator`),
  KEY `encounter_provider_changed_by` (`changed_by`),
  KEY `encounter_provider_voided_by` (`voided_by`),
  CONSTRAINT `encounter_id_fk` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `encounter_provider_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_provider_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_provider_voided_by` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_role_id_fk` FOREIGN KEY (`encounter_role_id`) REFERENCES `encounter_role` (`encounter_role_id`),
  CONSTRAINT `provider_id_fk` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounter_role`
--

DROP TABLE IF EXISTS `encounter_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter_role` (
  `encounter_role_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`encounter_role_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `encounter_role_unique_name` (`name`),
  KEY `encounter_role_creator_fk` (`creator`),
  KEY `encounter_role_changed_by_fk` (`changed_by`),
  KEY `encounter_role_retired_by_fk` (`retired_by`),
  CONSTRAINT `encounter_role_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_role_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `encounter_role_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounter_type`
--

DROP TABLE IF EXISTS `encounter_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounter_type` (
  `encounter_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `view_privilege` varchar(255) DEFAULT NULL,
  `edit_privilege` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`encounter_type_id`),
  UNIQUE KEY `encounter_type_unique_name` (`name`),
  UNIQUE KEY `encounter_type_uuid_index` (`uuid`),
  KEY `encounter_type_retired_status` (`retired`),
  KEY `user_who_created_type` (`creator`),
  KEY `user_who_retired_encounter_type` (`retired_by`),
  KEY `privilege_which_can_view_encounter_type` (`view_privilege`),
  KEY `privilege_which_can_edit_encounter_type` (`edit_privilege`),
  KEY `encounter_type_changed_by` (`changed_by`),
  CONSTRAINT `encounter_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `privilege_which_can_edit_encounter_type` FOREIGN KEY (`edit_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `privilege_which_can_view_encounter_type` FOREIGN KEY (`view_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `user_who_created_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_encounter_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field`
--

DROP TABLE IF EXISTS `field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field` (
  `field_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `field_type` int(11) DEFAULT NULL,
  `concept_id` int(11) DEFAULT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `attribute_name` varchar(50) DEFAULT NULL,
  `default_value` text,
  `select_multiple` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_id`),
  UNIQUE KEY `field_uuid_index` (`uuid`),
  KEY `field_retired_status` (`retired`),
  KEY `user_who_changed_field` (`changed_by`),
  KEY `concept_for_field` (`concept_id`),
  KEY `user_who_created_field` (`creator`),
  KEY `type_of_field` (`field_type`),
  KEY `user_who_retired_field` (`retired_by`),
  CONSTRAINT `concept_for_field` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `type_of_field` FOREIGN KEY (`field_type`) REFERENCES `field_type` (`field_type_id`),
  CONSTRAINT `user_who_changed_field` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_field` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_field` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field_answer`
--

DROP TABLE IF EXISTS `field_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field_answer` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `answer_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_id`,`answer_id`),
  UNIQUE KEY `field_answer_uuid_index` (`uuid`),
  KEY `field_answer_concept` (`answer_id`),
  KEY `user_who_created_field_answer` (`creator`),
  CONSTRAINT `answers_for_field` FOREIGN KEY (`field_id`) REFERENCES `field` (`field_id`),
  CONSTRAINT `field_answer_concept` FOREIGN KEY (`answer_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_created_field_answer` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `field_type`
--

DROP TABLE IF EXISTS `field_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `field_type` (
  `field_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` text,
  `is_set` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`field_type_id`),
  UNIQUE KEY `field_type_uuid_index` (`uuid`),
  KEY `user_who_created_field_type` (`creator`),
  CONSTRAINT `user_who_created_field_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form`
--

DROP TABLE IF EXISTS `form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form` (
  `form_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `version` varchar(50) NOT NULL DEFAULT '',
  `build` int(11) DEFAULT NULL,
  `published` tinyint(1) NOT NULL DEFAULT '0',
  `xslt` text,
  `template` text,
  `description` text,
  `encounter_type` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retired_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`form_id`),
  UNIQUE KEY `form_uuid_index` (`uuid`),
  KEY `form_published_index` (`published`),
  KEY `form_retired_index` (`retired`),
  KEY `form_published_and_retired_index` (`published`,`retired`),
  KEY `user_who_last_changed_form` (`changed_by`),
  KEY `user_who_created_form` (`creator`),
  KEY `form_encounter_type` (`encounter_type`),
  KEY `user_who_retired_form` (`retired_by`),
  CONSTRAINT `form_encounter_type` FOREIGN KEY (`encounter_type`) REFERENCES `encounter_type` (`encounter_type_id`),
  CONSTRAINT `user_who_created_form` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_last_changed_form` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_form` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form_field`
--

DROP TABLE IF EXISTS `form_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_field` (
  `form_field_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) NOT NULL DEFAULT '0',
  `field_id` int(11) NOT NULL DEFAULT '0',
  `field_number` int(11) DEFAULT NULL,
  `field_part` varchar(5) DEFAULT NULL,
  `page_number` int(11) DEFAULT NULL,
  `parent_form_field` int(11) DEFAULT NULL,
  `min_occurs` int(11) DEFAULT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `required` tinyint(1) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `sort_weight` double DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`form_field_id`),
  UNIQUE KEY `form_field_uuid_index` (`uuid`),
  KEY `user_who_last_changed_form_field` (`changed_by`),
  KEY `user_who_created_form_field` (`creator`),
  KEY `field_within_form` (`field_id`),
  KEY `form_containing_field` (`form_id`),
  KEY `form_field_hierarchy` (`parent_form_field`),
  CONSTRAINT `field_within_form` FOREIGN KEY (`field_id`) REFERENCES `field` (`field_id`),
  CONSTRAINT `form_containing_field` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `form_field_hierarchy` FOREIGN KEY (`parent_form_field`) REFERENCES `form_field` (`form_field_id`),
  CONSTRAINT `user_who_created_form_field` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_last_changed_form_field` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form_resource`
--

DROP TABLE IF EXISTS `form_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_resource` (
  `form_resource_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value_reference` text NOT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `uuid` char(38) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`form_resource_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `unique_form_and_name` (`form_id`,`name`),
  KEY `form_resource_changed_by` (`changed_by`),
  CONSTRAINT `form_resource_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `form_resource_form_fk` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `global_property`
--

DROP TABLE IF EXISTS `global_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `global_property` (
  `property` varchar(255) NOT NULL DEFAULT '',
  `property_value` text,
  `description` text,
  `uuid` char(38) NOT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`property`),
  UNIQUE KEY `global_property_uuid_index` (`uuid`),
  KEY `global_property_property_index` (`property`),
  KEY `global_property_changed_by` (`changed_by`),
  CONSTRAINT `global_property_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hl7_in_archive`
--

DROP TABLE IF EXISTS `hl7_in_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_archive` (
  `hl7_in_archive_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` varchar(255) DEFAULT NULL,
  `hl7_data` text NOT NULL,
  `date_created` datetime NOT NULL,
  `message_state` int(11) DEFAULT '2',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_archive_id`),
  UNIQUE KEY `hl7_in_archive_uuid_index` (`uuid`),
  KEY `hl7_in_archive_message_state_idx` (`message_state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hl7_in_error`
--

DROP TABLE IF EXISTS `hl7_in_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_error` (
  `hl7_in_error_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` text,
  `hl7_data` text NOT NULL,
  `error` varchar(255) NOT NULL DEFAULT '',
  `error_details` mediumtext,
  `date_created` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_error_id`),
  UNIQUE KEY `hl7_in_error_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hl7_in_queue`
--

DROP TABLE IF EXISTS `hl7_in_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_in_queue` (
  `hl7_in_queue_id` int(11) NOT NULL AUTO_INCREMENT,
  `hl7_source` int(11) NOT NULL DEFAULT '0',
  `hl7_source_key` text,
  `hl7_data` text NOT NULL,
  `message_state` int(11) NOT NULL DEFAULT '0',
  `date_processed` datetime DEFAULT NULL,
  `error_msg` text,
  `date_created` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_in_queue_id`),
  UNIQUE KEY `hl7_in_queue_uuid_index` (`uuid`),
  KEY `hl7_source_with_queue` (`hl7_source`),
  CONSTRAINT `hl7_source_with_queue` FOREIGN KEY (`hl7_source`) REFERENCES `hl7_source` (`hl7_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hl7_source`
--

DROP TABLE IF EXISTS `hl7_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hl7_source` (
  `hl7_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`hl7_source_id`),
  UNIQUE KEY `hl7_source_uuid_index` (`uuid`),
  KEY `user_who_created_hl7_source` (`creator`),
  CONSTRAINT `user_who_created_hl7_source` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `htmlformentry_html_form`
--

DROP TABLE IF EXISTS `htmlformentry_html_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `htmlformentry_html_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `xml_data` mediumtext NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `uuid` char(38) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `htmlformentry_html_form_uuid_index` (`uuid`),
  KEY `User who created htmlformentry_htmlform` (`creator`),
  KEY `Form with which this htmlform is related` (`form_id`),
  KEY `User who changed htmlformentry_htmlform` (`changed_by`),
  KEY `user_who_retired_html_form` (`retired_by`),
  CONSTRAINT `Form with which this htmlform is related` FOREIGN KEY (`form_id`) REFERENCES `form` (`form_id`),
  CONSTRAINT `User who changed htmlformentry_htmlform` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `User who created htmlformentry_htmlform` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_html_form` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_auto_generation_option`
--

DROP TABLE IF EXISTS `idgen_auto_generation_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_auto_generation_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `identifier_type` int(11) NOT NULL,
  `source` int(11) NOT NULL,
  `manual_entry_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `automatic_generation_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `location` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `source for idgen_auto_generation_option` (`source`),
  KEY `location_for_auto_generation_option` (`location`),
  KEY `identifier_type for idgen_auto_generation_option` (`identifier_type`),
  CONSTRAINT `identifier_type for idgen_auto_generation_option` FOREIGN KEY (`identifier_type`) REFERENCES `patient_identifier_type` (`patient_identifier_type_id`),
  CONSTRAINT `location_for_auto_generation_option` FOREIGN KEY (`location`) REFERENCES `location` (`location_id`),
  CONSTRAINT `source for idgen_auto_generation_option` FOREIGN KEY (`source`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_id_pool`
--

DROP TABLE IF EXISTS `idgen_id_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_id_pool` (
  `id` int(11) NOT NULL,
  `source` int(11) DEFAULT NULL,
  `batch_size` int(11) DEFAULT NULL,
  `min_pool_size` int(11) DEFAULT NULL,
  `sequential` tinyint(1) NOT NULL DEFAULT '0',
  `refill_with_scheduled_task` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `source for idgen_id_pool` (`source`),
  CONSTRAINT `id for idgen_id_pool` FOREIGN KEY (`id`) REFERENCES `idgen_identifier_source` (`id`),
  CONSTRAINT `source for idgen_id_pool` FOREIGN KEY (`source`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_identifier_source`
--

DROP TABLE IF EXISTS `idgen_identifier_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_identifier_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `identifier_type` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id for idgen_identifier_source` (`id`),
  KEY `identifier_type for idgen_identifier_source` (`identifier_type`),
  KEY `creator for idgen_identifier_source` (`creator`),
  KEY `changed_by for idgen_identifier_source` (`changed_by`),
  KEY `retired_by for idgen_identifier_source` (`retired_by`),
  CONSTRAINT `changed_by for idgen_identifier_source` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `creator for idgen_identifier_source` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `identifier_type for idgen_identifier_source` FOREIGN KEY (`identifier_type`) REFERENCES `patient_identifier_type` (`patient_identifier_type_id`),
  CONSTRAINT `retired_by for idgen_identifier_source` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_log_entry`
--

DROP TABLE IF EXISTS `idgen_log_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_log_entry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` int(11) NOT NULL,
  `identifier` varchar(50) NOT NULL,
  `date_generated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `generated_by` int(11) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id for idgen_log` (`id`),
  KEY `source for idgen_log` (`source`),
  KEY `generated_by for idgen_log` (`generated_by`),
  CONSTRAINT `generated_by for idgen_log` FOREIGN KEY (`generated_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `source for idgen_log` FOREIGN KEY (`source`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_pooled_identifier`
--

DROP TABLE IF EXISTS `idgen_pooled_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_pooled_identifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `pool_id` int(11) NOT NULL,
  `identifier` varchar(50) NOT NULL,
  `date_used` datetime DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pool_id for idgen_pooled_identifier` (`pool_id`),
  CONSTRAINT `pool_id for idgen_pooled_identifier` FOREIGN KEY (`pool_id`) REFERENCES `idgen_id_pool` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_remote_source`
--

DROP TABLE IF EXISTS `idgen_remote_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_remote_source` (
  `id` int(11) NOT NULL,
  `url` varchar(255) NOT NULL,
  `user` varchar(50) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `id for idgen_remote_source` FOREIGN KEY (`id`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_reserved_identifier`
--

DROP TABLE IF EXISTS `idgen_reserved_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_reserved_identifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` int(11) NOT NULL,
  `identifier` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id for idgen_reserved_identifier` (`id`),
  KEY `source for idgen_reserved_identifier` (`source`),
  CONSTRAINT `source for idgen_reserved_identifier` FOREIGN KEY (`source`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idgen_seq_id_gen`
--

DROP TABLE IF EXISTS `idgen_seq_id_gen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idgen_seq_id_gen` (
  `id` int(11) NOT NULL,
  `next_sequence_value` int(11) NOT NULL DEFAULT '-1',
  `base_character_set` varchar(255) NOT NULL,
  `first_identifier_base` varchar(50) NOT NULL,
  `prefix` varchar(100) DEFAULT NULL,
  `suffix` varchar(20) DEFAULT NULL,
  `min_length` int(11) DEFAULT NULL,
  `max_length` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `id for idgen_seq_id_gen` FOREIGN KEY (`id`) REFERENCES `idgen_identifier_source` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquibasechangelog`
--

DROP TABLE IF EXISTS `liquibasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liquibasechangelog` (
  `ID` varchar(63) NOT NULL,
  `AUTHOR` varchar(63) NOT NULL,
  `FILENAME` varchar(200) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquibasechangeloglock`
--

DROP TABLE IF EXISTS `liquibasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liquibasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `location_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `city_village` varchar(255) DEFAULT NULL,
  `state_province` varchar(255) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `county_district` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `address4` varchar(255) DEFAULT NULL,
  `address5` varchar(255) DEFAULT NULL,
  `address6` varchar(255) DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `parent_location` int(11) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `address7` varchar(255) DEFAULT NULL,
  `address8` varchar(255) DEFAULT NULL,
  `address9` varchar(255) DEFAULT NULL,
  `address10` varchar(255) DEFAULT NULL,
  `address11` varchar(255) DEFAULT NULL,
  `address12` varchar(255) DEFAULT NULL,
  `address13` varchar(255) DEFAULT NULL,
  `address14` varchar(255) DEFAULT NULL,
  `address15` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_uuid_index` (`uuid`),
  KEY `name_of_location` (`name`),
  KEY `location_retired_status` (`retired`),
  KEY `user_who_created_location` (`creator`),
  KEY `user_who_retired_location` (`retired_by`),
  KEY `parent_location` (`parent_location`),
  KEY `location_changed_by` (`changed_by`),
  CONSTRAINT `location_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `parent_location` FOREIGN KEY (`parent_location`) REFERENCES `location` (`location_id`),
  CONSTRAINT `user_who_created_location` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_location` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=247 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_attribute`
--

DROP TABLE IF EXISTS `location_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_attribute` (
  `location_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `location_id` int(11) NOT NULL,
  `attribute_type_id` int(11) NOT NULL,
  `value_reference` text NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`location_attribute_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `location_attribute_location_fk` (`location_id`),
  KEY `location_attribute_attribute_type_id_fk` (`attribute_type_id`),
  KEY `location_attribute_creator_fk` (`creator`),
  KEY `location_attribute_changed_by_fk` (`changed_by`),
  KEY `location_attribute_voided_by_fk` (`voided_by`),
  CONSTRAINT `location_attribute_attribute_type_id_fk` FOREIGN KEY (`attribute_type_id`) REFERENCES `location_attribute_type` (`location_attribute_type_id`),
  CONSTRAINT `location_attribute_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_attribute_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_attribute_location_fk` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `location_attribute_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_attribute_type`
--

DROP TABLE IF EXISTS `location_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_attribute_type` (
  `location_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `min_occurs` int(11) NOT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`location_attribute_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `location_attribute_type_unique_name` (`name`),
  KEY `location_attribute_type_creator_fk` (`creator`),
  KEY `location_attribute_type_changed_by_fk` (`changed_by`),
  KEY `location_attribute_type_retired_by_fk` (`retired_by`),
  CONSTRAINT `location_attribute_type_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_attribute_type_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_attribute_type_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_tag`
--

DROP TABLE IF EXISTS `location_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_tag` (
  `location_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`location_tag_id`),
  UNIQUE KEY `location_tag_uuid_index` (`uuid`),
  KEY `location_tag_creator` (`creator`),
  KEY `location_tag_retired_by` (`retired_by`),
  KEY `location_tag_changed_by` (`changed_by`),
  CONSTRAINT `location_tag_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_tag_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `location_tag_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location_tag_map`
--

DROP TABLE IF EXISTS `location_tag_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_tag_map` (
  `location_id` int(11) NOT NULL,
  `location_tag_id` int(11) NOT NULL,
  PRIMARY KEY (`location_id`,`location_tag_id`),
  KEY `location_tag_map_tag` (`location_tag_id`),
  CONSTRAINT `location_tag_map_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `location_tag_map_tag` FOREIGN KEY (`location_tag_id`) REFERENCES `location_tag` (`location_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logic_rule_definition`
--

DROP TABLE IF EXISTS `logic_rule_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_definition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `rule_content` varchar(2048) NOT NULL,
  `language` varchar(255) NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` smallint(6) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `creator_idx` (`creator`),
  KEY `changed_by_idx` (`changed_by`),
  KEY `retired_by_idx` (`retired_by`),
  CONSTRAINT `changed_by_for_rule_definition` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `creator_for_rule_definition` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `retired_by_for_rule_definition` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logic_rule_token`
--

DROP TABLE IF EXISTS `logic_rule_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_token` (
  `logic_rule_token_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `token` varchar(512) NOT NULL,
  `class_name` varchar(512) NOT NULL,
  `state` varchar(512) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`logic_rule_token_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `token_creator` (`creator`),
  KEY `token_changed_by` (`changed_by`),
  CONSTRAINT `token_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `person` (`person_id`),
  CONSTRAINT `token_creator` FOREIGN KEY (`creator`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logic_rule_token_tag`
--

DROP TABLE IF EXISTS `logic_rule_token_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_rule_token_tag` (
  `logic_rule_token_id` int(11) NOT NULL,
  `tag` varchar(512) NOT NULL,
  KEY `token_tag` (`logic_rule_token_id`),
  CONSTRAINT `token_tag` FOREIGN KEY (`logic_rule_token_id`) REFERENCES `logic_rule_token` (`logic_rule_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logic_token_registration`
--

DROP TABLE IF EXISTS `logic_token_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_token_registration` (
  `token_registration_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0002-11-30 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `token` varchar(512) NOT NULL,
  `provider_class_name` varchar(512) NOT NULL,
  `provider_token` varchar(512) NOT NULL,
  `configuration` varchar(2000) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`token_registration_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `token_registration_creator` (`creator`),
  KEY `token_registration_changed_by` (`changed_by`),
  CONSTRAINT `token_registration_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `token_registration_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logic_token_registration_tag`
--

DROP TABLE IF EXISTS `logic_token_registration_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logic_token_registration_tag` (
  `token_registration_id` int(11) NOT NULL,
  `tag` varchar(512) NOT NULL,
  KEY `token_registration_tag` (`token_registration_id`),
  CONSTRAINT `token_registration_tag` FOREIGN KEY (`token_registration_id`) REFERENCES `logic_token_registration` (`token_registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatamapping_metadata_set`
--

DROP TABLE IF EXISTS `metadatamapping_metadata_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatamapping_metadata_set` (
  `metadata_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`metadata_set_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `metadatamapping_metadata_set_creator` (`creator`),
  KEY `metadatamapping_metadata_set_changed_by` (`changed_by`),
  KEY `metadatamapping_metadata_set_retired_by` (`retired_by`),
  CONSTRAINT `metadatamapping_metadata_set_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_set_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_set_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatamapping_metadata_set_member`
--

DROP TABLE IF EXISTS `metadatamapping_metadata_set_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatamapping_metadata_set_member` (
  `metadata_set_member_id` int(11) NOT NULL AUTO_INCREMENT,
  `metadata_set_id` int(11) NOT NULL,
  `metadata_class` varchar(1024) NOT NULL,
  `metadata_uuid` varchar(38) NOT NULL,
  `sort_weight` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`metadata_set_member_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `metadatamapping_metadata_set_member_term_unique_within_set` (`metadata_set_id`,`metadata_uuid`),
  KEY `metadatamapping_metadata_set_member_creator` (`creator`),
  KEY `metadatamapping_metadata_set_member_changed_by` (`changed_by`),
  KEY `metadatamapping_metadata_set_member_retired_by` (`retired_by`),
  CONSTRAINT `metadatamapping_metadata_set_member_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_set_member_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_set_member_metadata_set_id` FOREIGN KEY (`metadata_set_id`) REFERENCES `metadatamapping_metadata_set` (`metadata_set_id`),
  CONSTRAINT `metadatamapping_metadata_set_member_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatamapping_metadata_source`
--

DROP TABLE IF EXISTS `metadatamapping_metadata_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatamapping_metadata_source` (
  `metadata_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`metadata_source_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `metadatamapping_metadata_source_name_unique` (`name`),
  KEY `metadatamapping_metadata_source_creator` (`creator`),
  KEY `metadatamapping_metadata_source_changed_by` (`changed_by`),
  KEY `metadatamapping_metadata_source_retired_by` (`retired_by`),
  CONSTRAINT `metadatamapping_metadata_source_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_source_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_source_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatamapping_metadata_term_mapping`
--

DROP TABLE IF EXISTS `metadatamapping_metadata_term_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatamapping_metadata_term_mapping` (
  `metadata_term_mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `metadata_source_id` int(11) NOT NULL,
  `code` varchar(255) NOT NULL,
  `metadata_class` varchar(1024) DEFAULT NULL,
  `metadata_uuid` varchar(38) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`metadata_term_mapping_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `metadatamapping_metadata_term_code_unique_within_source` (`metadata_source_id`,`code`),
  KEY `metadatamapping_metadata_term_mapping_creator` (`creator`),
  KEY `metadatamapping_metadata_term_mapping_changed_by` (`changed_by`),
  KEY `metadatamapping_metadata_term_mapping_retired_by` (`retired_by`),
  KEY `metadatamapping_idx_mdtm_retired` (`retired`),
  KEY `metadatamapping_idx_mdtm_mdclass` (`metadata_class`(255)),
  KEY `metadatamapping_idx_mdtm_mdsource` (`metadata_source_id`),
  KEY `metadatamapping_idx_mdtm_code` (`code`),
  CONSTRAINT `metadatamapping_metadata_term_mapping_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_term_mapping_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `metadatamapping_metadata_term_mapping_metadata_source_id` FOREIGN KEY (`metadata_source_id`) REFERENCES `metadatamapping_metadata_source` (`metadata_source_id`),
  CONSTRAINT `metadatamapping_metadata_term_mapping_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatasharing_exported_package`
--

DROP TABLE IF EXISTS `metadatasharing_exported_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatasharing_exported_package` (
  `exported_package_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `group_uuid` char(38) NOT NULL,
  `version` int(11) NOT NULL,
  `published` tinyint(1) NOT NULL,
  `date_created` datetime NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) NOT NULL,
  `content` longblob,
  PRIMARY KEY (`exported_package_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `metadatasharing_package_group_uuid` (`group_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatasharing_imported_item`
--

DROP TABLE IF EXISTS `metadatasharing_imported_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatasharing_imported_item` (
  `imported_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `classname` varchar(256) NOT NULL,
  `existing_uuid` char(38) DEFAULT NULL,
  `date_imported` datetime DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `import_type` tinyint(4) DEFAULT '0',
  `assessed` tinyint(1) NOT NULL,
  PRIMARY KEY (`imported_item_id`),
  KEY `metadatasharing_item_uuid` (`uuid`,`existing_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metadatasharing_imported_package`
--

DROP TABLE IF EXISTS `metadatasharing_imported_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadatasharing_imported_package` (
  `imported_package_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `group_uuid` char(38) NOT NULL,
  `subscription_url` varchar(512) DEFAULT NULL,
  `subscription_status` tinyint(4) DEFAULT '0',
  `date_created` datetime NOT NULL,
  `date_imported` datetime DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `description` varchar(256) NOT NULL,
  `import_config` varchar(1024) DEFAULT NULL,
  `remote_version` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`imported_package_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `metadatasharing_package_uuid` (`group_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note` (
  `note_id` int(11) NOT NULL DEFAULT '0',
  `note_type` varchar(50) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `obs_id` int(11) DEFAULT NULL,
  `encounter_id` int(11) DEFAULT NULL,
  `text` text NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `parent` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`note_id`),
  UNIQUE KEY `note_uuid_index` (`uuid`),
  KEY `user_who_changed_note` (`changed_by`),
  KEY `user_who_created_note` (`creator`),
  KEY `encounter_note` (`encounter_id`),
  KEY `obs_note` (`obs_id`),
  KEY `note_hierarchy` (`parent`),
  KEY `patient_note` (`patient_id`),
  CONSTRAINT `encounter_note` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `note_hierarchy` FOREIGN KEY (`parent`) REFERENCES `note` (`note_id`),
  CONSTRAINT `obs_note` FOREIGN KEY (`obs_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `patient_note` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_changed_note` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_note` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification_alert`
--

DROP TABLE IF EXISTS `notification_alert`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_alert` (
  `alert_id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(512) NOT NULL,
  `satisfied_by_any` tinyint(1) NOT NULL DEFAULT '0',
  `alert_read` tinyint(1) NOT NULL DEFAULT '0',
  `date_to_expire` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`alert_id`),
  UNIQUE KEY `notification_alert_uuid_index` (`uuid`),
  KEY `alert_date_to_expire_idx` (`date_to_expire`),
  KEY `user_who_changed_alert` (`changed_by`),
  KEY `alert_creator` (`creator`),
  CONSTRAINT `alert_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_alert` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification_alert_recipient`
--

DROP TABLE IF EXISTS `notification_alert_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_alert_recipient` (
  `alert_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `alert_read` tinyint(1) NOT NULL DEFAULT '0',
  `date_changed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`alert_id`,`user_id`),
  KEY `alert_read_by_user` (`user_id`),
  CONSTRAINT `alert_read_by_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `id_of_alert` FOREIGN KEY (`alert_id`) REFERENCES `notification_alert` (`alert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification_template`
--

DROP TABLE IF EXISTS `notification_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_template` (
  `template_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `template` text,
  `subject` varchar(100) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `recipients` varchar(512) DEFAULT NULL,
  `ordinal` int(11) DEFAULT '0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `notification_template_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `obs`
--

DROP TABLE IF EXISTS `obs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `obs` (
  `obs_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) NOT NULL,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `encounter_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `obs_datetime` datetime NOT NULL,
  `location_id` int(11) DEFAULT NULL,
  `obs_group_id` int(11) DEFAULT NULL,
  `accession_number` varchar(255) DEFAULT NULL,
  `value_group_id` int(11) DEFAULT NULL,
  `value_coded` int(11) DEFAULT NULL,
  `value_coded_name_id` int(11) DEFAULT NULL,
  `value_drug` int(11) DEFAULT NULL,
  `value_datetime` datetime DEFAULT NULL,
  `value_numeric` double DEFAULT NULL,
  `value_modifier` varchar(2) DEFAULT NULL,
  `value_text` text,
  `value_complex` varchar(1000) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `previous_version` int(11) DEFAULT NULL,
  `form_namespace_and_path` varchar(255) DEFAULT NULL,
  `status` varchar(16) NOT NULL DEFAULT 'FINAL',
  `interpretation` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`obs_id`),
  UNIQUE KEY `obs_uuid_index` (`uuid`),
  KEY `obs_datetime_idx` (`obs_datetime`),
  KEY `obs_concept` (`concept_id`),
  KEY `obs_enterer` (`creator`),
  KEY `encounter_observations` (`encounter_id`),
  KEY `obs_location` (`location_id`),
  KEY `obs_grouping_id` (`obs_group_id`),
  KEY `obs_order` (`order_id`),
  KEY `person_obs` (`person_id`),
  KEY `answer_concept` (`value_coded`),
  KEY `obs_name_of_coded_value` (`value_coded_name_id`),
  KEY `answer_concept_drug` (`value_drug`),
  KEY `user_who_voided_obs` (`voided_by`),
  KEY `previous_version` (`previous_version`),
  CONSTRAINT `answer_concept` FOREIGN KEY (`value_coded`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `answer_concept_drug` FOREIGN KEY (`value_drug`) REFERENCES `drug` (`drug_id`),
  CONSTRAINT `encounter_observations` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `obs_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `obs_enterer` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `obs_grouping_id` FOREIGN KEY (`obs_group_id`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `obs_location` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `obs_name_of_coded_value` FOREIGN KEY (`value_coded_name_id`) REFERENCES `concept_name` (`concept_name_id`),
  CONSTRAINT `obs_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `person_obs` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `previous_version` FOREIGN KEY (`previous_version`) REFERENCES `obs` (`obs_id`),
  CONSTRAINT `user_who_voided_obs` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2707 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_frequency`
--

DROP TABLE IF EXISTS `order_frequency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_frequency` (
  `order_frequency_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL,
  `frequency_per_day` double DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_frequency_id`),
  UNIQUE KEY `concept_id` (`concept_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `order_frequency_creator_fk` (`creator`),
  KEY `order_frequency_retired_by_fk` (`retired_by`),
  KEY `order_frequency_changed_by_fk` (`changed_by`),
  CONSTRAINT `order_frequency_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_frequency_concept_id_fk` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `order_frequency_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_frequency_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_group`
--

DROP TABLE IF EXISTS `order_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_group` (
  `order_group_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_set_id` int(11) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `encounter_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_group_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `order_group_patient_id_fk` (`patient_id`),
  KEY `order_group_encounter_id_fk` (`encounter_id`),
  KEY `order_group_creator_fk` (`creator`),
  KEY `order_group_set_id_fk` (`order_set_id`),
  KEY `order_group_voided_by_fk` (`voided_by`),
  KEY `order_group_changed_by_fk` (`changed_by`),
  CONSTRAINT `order_group_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_group_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_group_encounter_id_fk` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `order_group_patient_id_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `order_group_set_id_fk` FOREIGN KEY (`order_set_id`) REFERENCES `order_set` (`order_set_id`),
  CONSTRAINT `order_group_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_set`
--

DROP TABLE IF EXISTS `order_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_set` (
  `order_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `operator` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_set_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `order_set_creator_fk` (`creator`),
  KEY `order_set_retired_by_fk` (`retired_by`),
  KEY `order_set_changed_by_fk` (`changed_by`),
  CONSTRAINT `order_set_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_set_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_set_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_set_member`
--

DROP TABLE IF EXISTS `order_set_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_set_member` (
  `order_set_member_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_type` int(11) NOT NULL,
  `order_template` text,
  `order_template_type` varchar(1024) DEFAULT NULL,
  `order_set_id` int(11) NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `concept_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`order_set_member_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `order_set_member_creator_fk` (`creator`),
  KEY `order_set_member_order_set_id_fk` (`order_set_id`),
  KEY `order_set_member_concept_id_fk` (`concept_id`),
  KEY `order_set_member_order_type_fk` (`order_type`),
  KEY `order_set_member_retired_by_fk` (`retired_by`),
  KEY `order_set_member_changed_by_fk` (`changed_by`),
  CONSTRAINT `order_set_member_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_set_member_concept_id_fk` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `order_set_member_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_set_member_order_set_id_fk` FOREIGN KEY (`order_set_id`) REFERENCES `order_set` (`order_set_id`),
  CONSTRAINT `order_set_member_order_type_fk` FOREIGN KEY (`order_type`) REFERENCES `order_type` (`order_type_id`),
  CONSTRAINT `order_set_member_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_type`
--

DROP TABLE IF EXISTS `order_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_type` (
  `order_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `java_class_name` varchar(255) NOT NULL,
  `parent` int(11) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`order_type_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `order_type_uuid_index` (`uuid`),
  KEY `order_type_retired_status` (`retired`),
  KEY `type_created_by` (`creator`),
  KEY `user_who_retired_order_type` (`retired_by`),
  KEY `order_type_changed_by` (`changed_by`),
  KEY `order_type_parent_order_type` (`parent`),
  CONSTRAINT `order_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_type_parent_order_type` FOREIGN KEY (`parent`) REFERENCES `order_type` (`order_type_id`),
  CONSTRAINT `type_created_by` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_order_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_type_class_map`
--

DROP TABLE IF EXISTS `order_type_class_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_type_class_map` (
  `order_type_id` int(11) NOT NULL,
  `concept_class_id` int(11) NOT NULL,
  PRIMARY KEY (`order_type_id`,`concept_class_id`),
  UNIQUE KEY `concept_class_id` (`concept_class_id`),
  CONSTRAINT `fk_order_type_class_map_concept_class_concept_class_id` FOREIGN KEY (`concept_class_id`) REFERENCES `concept_class` (`concept_class_id`),
  CONSTRAINT `fk_order_type_order_type_id` FOREIGN KEY (`order_type_id`) REFERENCES `order_type` (`order_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_type_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `orderer` int(11) NOT NULL,
  `encounter_id` int(11) NOT NULL,
  `instructions` text,
  `date_activated` datetime DEFAULT NULL,
  `auto_expire_date` datetime DEFAULT NULL,
  `date_stopped` datetime DEFAULT NULL,
  `order_reason` int(11) DEFAULT NULL,
  `order_reason_non_coded` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `accession_number` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `urgency` varchar(50) NOT NULL DEFAULT 'ROUTINE',
  `order_number` varchar(50) NOT NULL,
  `previous_order_id` int(11) DEFAULT NULL,
  `order_action` varchar(50) NOT NULL,
  `comment_to_fulfiller` varchar(1024) DEFAULT NULL,
  `care_setting` int(11) NOT NULL,
  `scheduled_date` datetime DEFAULT NULL,
  `order_group_id` int(11) DEFAULT NULL,
  `sort_weight` double DEFAULT NULL,
  `fulfiller_comment` varchar(1024) DEFAULT NULL,
  `fulfiller_status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `orders_uuid_index` (`uuid`),
  KEY `order_creator` (`creator`),
  KEY `discontinued_because` (`order_reason`),
  KEY `orders_in_encounter` (`encounter_id`),
  KEY `type_of_order` (`order_type_id`),
  KEY `orderer_not_drug` (`orderer`),
  KEY `order_for_patient` (`patient_id`),
  KEY `user_who_voided_order` (`voided_by`),
  KEY `previous_order_id_order_id` (`previous_order_id`),
  KEY `orders_care_setting` (`care_setting`),
  KEY `orders_order_group_id_fk` (`order_group_id`),
  CONSTRAINT `discontinued_because` FOREIGN KEY (`order_reason`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `fk_orderer_provider` FOREIGN KEY (`orderer`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `order_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `order_for_patient` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `orders_care_setting` FOREIGN KEY (`care_setting`) REFERENCES `care_setting` (`care_setting_id`),
  CONSTRAINT `orders_in_encounter` FOREIGN KEY (`encounter_id`) REFERENCES `encounter` (`encounter_id`),
  CONSTRAINT `orders_order_group_id_fk` FOREIGN KEY (`order_group_id`) REFERENCES `order_group` (`order_group_id`),
  CONSTRAINT `previous_order_id_order_id` FOREIGN KEY (`previous_order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `type_of_order` FOREIGN KEY (`order_type_id`) REFERENCES `order_type` (`order_type_id`),
  CONSTRAINT `user_who_voided_order` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `patient_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `allergy_status` varchar(50) NOT NULL DEFAULT 'Unknown',
  PRIMARY KEY (`patient_id`),
  KEY `user_who_changed_pat` (`changed_by`),
  KEY `user_who_created_patient` (`creator`),
  KEY `user_who_voided_patient` (`voided_by`),
  CONSTRAINT `person_id_for_patient` FOREIGN KEY (`patient_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_changed_pat` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_patient` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_patient` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_identifier`
--

DROP TABLE IF EXISTS `patient_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_identifier` (
  `patient_identifier_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `identifier` varchar(50) NOT NULL DEFAULT '',
  `identifier_type` int(11) NOT NULL DEFAULT '0',
  `preferred` tinyint(1) NOT NULL DEFAULT '0',
  `location_id` int(11) DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_identifier_id`),
  UNIQUE KEY `patient_identifier_uuid_index` (`uuid`),
  KEY `identifier_name` (`identifier`),
  KEY `idx_patient_identifier_patient` (`patient_id`),
  KEY `identifier_creator` (`creator`),
  KEY `defines_identifier_type` (`identifier_type`),
  KEY `patient_identifier_ibfk_2` (`location_id`),
  KEY `identifier_voider` (`voided_by`),
  KEY `patient_identifier_changed_by` (`changed_by`),
  CONSTRAINT `defines_identifier_type` FOREIGN KEY (`identifier_type`) REFERENCES `patient_identifier_type` (`patient_identifier_type_id`),
  CONSTRAINT `fk_patient_id_patient_identifier` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `identifier_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `identifier_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_identifier_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_identifier_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_identifier_type`
--

DROP TABLE IF EXISTS `patient_identifier_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_identifier_type` (
  `patient_identifier_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `format` varchar(255) DEFAULT NULL,
  `check_digit` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `required` tinyint(1) NOT NULL DEFAULT '0',
  `format_description` varchar(255) DEFAULT NULL,
  `validator` varchar(200) DEFAULT NULL,
  `location_behavior` varchar(50) DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `uniqueness_behavior` varchar(50) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`patient_identifier_type_id`),
  UNIQUE KEY `patient_identifier_type_uuid_index` (`uuid`),
  KEY `patient_identifier_type_retired_status` (`retired`),
  KEY `type_creator` (`creator`),
  KEY `user_who_retired_patient_identifier_type` (`retired_by`),
  KEY `patient_identifier_type_changed_by` (`changed_by`),
  CONSTRAINT `patient_identifier_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_patient_identifier_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_program`
--

DROP TABLE IF EXISTS `patient_program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_program` (
  `patient_program_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL DEFAULT '0',
  `program_id` int(11) NOT NULL DEFAULT '0',
  `date_enrolled` datetime DEFAULT NULL,
  `date_completed` datetime DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `outcome_concept_id` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_program_id`),
  UNIQUE KEY `patient_program_uuid_index` (`uuid`),
  KEY `user_who_changed` (`changed_by`),
  KEY `patient_program_creator` (`creator`),
  KEY `patient_in_program` (`patient_id`),
  KEY `program_for_patient` (`program_id`),
  KEY `user_who_voided_patient_program` (`voided_by`),
  KEY `patient_program_location_id` (`location_id`),
  KEY `patient_program_outcome_concept_id_fk` (`outcome_concept_id`),
  CONSTRAINT `patient_in_program` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`) ON UPDATE CASCADE,
  CONSTRAINT `patient_program_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_program_location_id` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `patient_program_outcome_concept_id_fk` FOREIGN KEY (`outcome_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `program_for_patient` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`),
  CONSTRAINT `user_who_changed` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_patient_program` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_program_attribute`
--

DROP TABLE IF EXISTS `patient_program_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_program_attribute` (
  `patient_program_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_program_id` int(11) NOT NULL,
  `attribute_type_id` int(11) NOT NULL,
  `value_reference` text NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`patient_program_attribute_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `patient_program_attribute_programid_fk` (`patient_program_id`),
  KEY `patient_program_attribute_attributetype_fk` (`attribute_type_id`),
  KEY `patient_program_attribute_creator_fk` (`creator`),
  KEY `patient_program_attribute_changed_by_fk` (`changed_by`),
  KEY `patient_program_attribute_voided_by_fk` (`voided_by`),
  CONSTRAINT `patient_program_attribute_attributetype_fk` FOREIGN KEY (`attribute_type_id`) REFERENCES `program_attribute_type` (`program_attribute_type_id`),
  CONSTRAINT `patient_program_attribute_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_program_attribute_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_program_attribute_programid_fk` FOREIGN KEY (`patient_program_id`) REFERENCES `patient_program` (`patient_program_id`),
  CONSTRAINT `patient_program_attribute_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient_state`
--

DROP TABLE IF EXISTS `patient_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_state` (
  `patient_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_program_id` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`patient_state_id`),
  UNIQUE KEY `patient_state_uuid_index` (`uuid`),
  KEY `patient_state_changer` (`changed_by`),
  KEY `patient_state_creator` (`creator`),
  KEY `patient_program_for_state` (`patient_program_id`),
  KEY `state_for_patient` (`state`),
  KEY `patient_state_voider` (`voided_by`),
  CONSTRAINT `patient_program_for_state` FOREIGN KEY (`patient_program_id`) REFERENCES `patient_program` (`patient_program_id`),
  CONSTRAINT `patient_state_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_state_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_state_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `state_for_patient` FOREIGN KEY (`state`) REFERENCES `program_workflow_state` (`program_workflow_state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person` (
  `person_id` int(11) NOT NULL AUTO_INCREMENT,
  `gender` varchar(50) DEFAULT '',
  `birthdate` date DEFAULT NULL,
  `birthdate_estimated` tinyint(1) NOT NULL DEFAULT '0',
  `dead` tinyint(1) NOT NULL DEFAULT '0',
  `death_date` datetime DEFAULT NULL,
  `cause_of_death` int(11) DEFAULT NULL,
  `creator` int(11) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `deathdate_estimated` tinyint(1) NOT NULL DEFAULT '0',
  `birthtime` time DEFAULT NULL,
  `cause_of_death_non_coded` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `person_uuid_index` (`uuid`),
  KEY `person_birthdate` (`birthdate`),
  KEY `person_death_date` (`death_date`),
  KEY `person_died_because` (`cause_of_death`),
  KEY `user_who_changed_person` (`changed_by`),
  KEY `user_who_created_person` (`creator`),
  KEY `user_who_voided_person` (`voided_by`),
  CONSTRAINT `person_died_because` FOREIGN KEY (`cause_of_death`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_changed_person` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_person` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_person` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (1,'M',NULL,0,0,NULL,NULL,NULL,'2005-01-01 00:00:00',NULL,NULL,0,NULL,NULL,NULL,'dd279794-76e9-11e9-8cd9-0242ac1c000b',0,NULL,NULL);
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_address`
--

DROP TABLE IF EXISTS `person_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_address` (
  `person_address_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) DEFAULT NULL,
  `preferred` tinyint(1) NOT NULL DEFAULT '0',
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `city_village` varchar(255) DEFAULT NULL,
  `state_province` varchar(255) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` varchar(50) DEFAULT NULL,
  `longitude` varchar(50) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `county_district` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `address4` varchar(255) DEFAULT NULL,
  `address5` varchar(255) DEFAULT NULL,
  `address6` varchar(255) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `address7` varchar(255) DEFAULT NULL,
  `address8` varchar(255) DEFAULT NULL,
  `address9` varchar(255) DEFAULT NULL,
  `address10` varchar(255) DEFAULT NULL,
  `address11` varchar(255) DEFAULT NULL,
  `address12` varchar(255) DEFAULT NULL,
  `address13` varchar(255) DEFAULT NULL,
  `address14` varchar(255) DEFAULT NULL,
  `address15` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_address_id`),
  UNIQUE KEY `person_address_uuid_index` (`uuid`),
  KEY `patient_address_creator` (`creator`),
  KEY `address_for_person` (`person_id`),
  KEY `patient_address_void` (`voided_by`),
  KEY `person_address_changed_by` (`changed_by`),
  CONSTRAINT `address_for_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `patient_address_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `patient_address_void` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `person_address_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_attribute`
--

DROP TABLE IF EXISTS `person_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_attribute` (
  `person_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(50) NOT NULL DEFAULT '',
  `person_attribute_type_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_attribute_id`),
  UNIQUE KEY `person_attribute_uuid_index` (`uuid`),
  KEY `attribute_changer` (`changed_by`),
  KEY `attribute_creator` (`creator`),
  KEY `defines_attribute_type` (`person_attribute_type_id`),
  KEY `identifies_person` (`person_id`),
  KEY `attribute_voider` (`voided_by`),
  CONSTRAINT `attribute_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `defines_attribute_type` FOREIGN KEY (`person_attribute_type_id`) REFERENCES `person_attribute_type` (`person_attribute_type_id`),
  CONSTRAINT `identifies_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_attribute_type`
--

DROP TABLE IF EXISTS `person_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_attribute_type` (
  `person_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `format` varchar(50) DEFAULT NULL,
  `foreign_key` int(11) DEFAULT NULL,
  `searchable` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `edit_privilege` varchar(255) DEFAULT NULL,
  `sort_weight` double DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_attribute_type_id`),
  UNIQUE KEY `person_attribute_type_uuid_index` (`uuid`),
  KEY `attribute_is_searchable` (`searchable`),
  KEY `name_of_attribute` (`name`),
  KEY `person_attribute_type_retired_status` (`retired`),
  KEY `attribute_type_changer` (`changed_by`),
  KEY `attribute_type_creator` (`creator`),
  KEY `user_who_retired_person_attribute_type` (`retired_by`),
  KEY `privilege_which_can_edit` (`edit_privilege`),
  CONSTRAINT `attribute_type_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `attribute_type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `privilege_which_can_edit` FOREIGN KEY (`edit_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `user_who_retired_person_attribute_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_merge_log`
--

DROP TABLE IF EXISTS `person_merge_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_merge_log` (
  `person_merge_log_id` int(11) NOT NULL AUTO_INCREMENT,
  `winner_person_id` int(11) NOT NULL,
  `loser_person_id` int(11) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `merged_data` longtext NOT NULL,
  `uuid` char(38) NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_merge_log_id`),
  UNIQUE KEY `person_merge_log_unique_uuid` (`uuid`),
  KEY `person_merge_log_winner` (`winner_person_id`),
  KEY `person_merge_log_loser` (`loser_person_id`),
  KEY `person_merge_log_creator` (`creator`),
  KEY `person_merge_log_changed_by_fk` (`changed_by`),
  KEY `person_merge_log_voided_by_fk` (`voided_by`),
  CONSTRAINT `person_merge_log_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `person_merge_log_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `person_merge_log_loser` FOREIGN KEY (`loser_person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `person_merge_log_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `person_merge_log_winner` FOREIGN KEY (`winner_person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_name`
--

DROP TABLE IF EXISTS `person_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_name` (
  `person_name_id` int(11) NOT NULL AUTO_INCREMENT,
  `preferred` tinyint(1) NOT NULL DEFAULT '0',
  `person_id` int(11) NOT NULL,
  `prefix` varchar(50) DEFAULT NULL,
  `given_name` varchar(50) DEFAULT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `family_name_prefix` varchar(50) DEFAULT NULL,
  `family_name` varchar(50) DEFAULT NULL,
  `family_name2` varchar(50) DEFAULT NULL,
  `family_name_suffix` varchar(50) DEFAULT NULL,
  `degree` varchar(50) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`person_name_id`),
  UNIQUE KEY `person_name_uuid_index` (`uuid`),
  KEY `first_name` (`given_name`),
  KEY `last_name` (`family_name`),
  KEY `middle_name` (`middle_name`),
  KEY `family_name2` (`family_name2`),
  KEY `user_who_made_name` (`creator`),
  KEY `name_for_person` (`person_id`),
  KEY `user_who_voided_name` (`voided_by`),
  CONSTRAINT `name_for_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON UPDATE CASCADE,
  CONSTRAINT `user_who_made_name` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_name` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `privilege`
--

DROP TABLE IF EXISTS `privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `privilege` (
  `privilege` varchar(255) NOT NULL,
  `description` text,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`privilege`),
  UNIQUE KEY `privilege_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program`
--

DROP TABLE IF EXISTS `program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program` (
  `program_id` int(11) NOT NULL AUTO_INCREMENT,
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `outcomes_concept_id` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL,
  `description` text,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_id`),
  UNIQUE KEY `program_uuid_index` (`uuid`),
  KEY `user_who_changed_program` (`changed_by`),
  KEY `program_concept` (`concept_id`),
  KEY `program_creator` (`creator`),
  KEY `program_outcomes_concept_id_fk` (`outcomes_concept_id`),
  CONSTRAINT `program_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `program_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `program_outcomes_concept_id_fk` FOREIGN KEY (`outcomes_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `user_who_changed_program` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_attribute_type`
--

DROP TABLE IF EXISTS `program_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_attribute_type` (
  `program_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `min_occurs` int(11) NOT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_attribute_type_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `program_attribute_type_creator_fk` (`creator`),
  KEY `program_attribute_type_changed_by_fk` (`changed_by`),
  KEY `program_attribute_type_retired_by_fk` (`retired_by`),
  CONSTRAINT `program_attribute_type_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `program_attribute_type_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `program_attribute_type_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_workflow`
--

DROP TABLE IF EXISTS `program_workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_workflow` (
  `program_workflow_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_workflow_id`),
  UNIQUE KEY `program_workflow_uuid_index` (`uuid`),
  KEY `workflow_changed_by` (`changed_by`),
  KEY `workflow_concept` (`concept_id`),
  KEY `workflow_creator` (`creator`),
  KEY `program_for_workflow` (`program_id`),
  CONSTRAINT `program_for_workflow` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`),
  CONSTRAINT `workflow_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `workflow_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `workflow_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_workflow_state`
--

DROP TABLE IF EXISTS `program_workflow_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_workflow_state` (
  `program_workflow_state_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_workflow_id` int(11) NOT NULL DEFAULT '0',
  `concept_id` int(11) NOT NULL DEFAULT '0',
  `initial` tinyint(1) NOT NULL DEFAULT '0',
  `terminal` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`program_workflow_state_id`),
  UNIQUE KEY `program_workflow_state_uuid_index` (`uuid`),
  KEY `state_changed_by` (`changed_by`),
  KEY `state_concept` (`concept_id`),
  KEY `state_creator` (`creator`),
  KEY `workflow_for_state` (`program_workflow_id`),
  CONSTRAINT `state_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `state_concept` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `state_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `workflow_for_state` FOREIGN KEY (`program_workflow_id`) REFERENCES `program_workflow` (`program_workflow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provider`
--

DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider` (
  `provider_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `provider_role_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `speciality_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`provider_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `provider_changed_by_fk` (`changed_by`),
  KEY `provider_person_id_fk` (`person_id`),
  KEY `provider_retired_by_fk` (`retired_by`),
  KEY `provider_creator_fk` (`creator`),
  KEY `provider_role_id` (`provider_role_id`),
  KEY `provider_role_id_fk` (`role_id`),
  KEY `provider_speciality_id_fk` (`speciality_id`),
  CONSTRAINT `provider_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_ibfk_1` FOREIGN KEY (`provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`),
  CONSTRAINT `provider_person_id_fk` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `provider_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `provider_speciality_id_fk` FOREIGN KEY (`speciality_id`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provider_attribute`
--

DROP TABLE IF EXISTS `provider_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider_attribute` (
  `provider_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `provider_id` int(11) NOT NULL,
  `attribute_type_id` int(11) NOT NULL,
  `value_reference` text NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`provider_attribute_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `provider_attribute_provider_fk` (`provider_id`),
  KEY `provider_attribute_attribute_type_id_fk` (`attribute_type_id`),
  KEY `provider_attribute_creator_fk` (`creator`),
  KEY `provider_attribute_changed_by_fk` (`changed_by`),
  KEY `provider_attribute_voided_by_fk` (`voided_by`),
  CONSTRAINT `provider_attribute_attribute_type_id_fk` FOREIGN KEY (`attribute_type_id`) REFERENCES `provider_attribute_type` (`provider_attribute_type_id`),
  CONSTRAINT `provider_attribute_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_attribute_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_attribute_provider_fk` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`provider_id`),
  CONSTRAINT `provider_attribute_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provider_attribute_type`
--

DROP TABLE IF EXISTS `provider_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider_attribute_type` (
  `provider_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `min_occurs` int(11) NOT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`provider_attribute_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `provider_attribute_type_creator_fk` (`creator`),
  KEY `provider_attribute_type_changed_by_fk` (`changed_by`),
  KEY `provider_attribute_type_retired_by_fk` (`retired_by`),
  CONSTRAINT `provider_attribute_type_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_attribute_type_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `provider_attribute_type_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_provider_role`
--

DROP TABLE IF EXISTS `providermanagement_provider_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_provider_role` (
  `provider_role_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`provider_role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_provider_role_provider_attribute_type`
--

DROP TABLE IF EXISTS `providermanagement_provider_role_provider_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_provider_role_provider_attribute_type` (
  `provider_role_id` int(11) NOT NULL,
  `provider_attribute_type_id` int(11) NOT NULL,
  KEY `provider_role_id` (`provider_role_id`),
  KEY `provider_attribute_type_id` (`provider_attribute_type_id`),
  CONSTRAINT `providermanagement_prpat_provider_attribute_type_fk` FOREIGN KEY (`provider_attribute_type_id`) REFERENCES `provider_attribute_type` (`provider_attribute_type_id`),
  CONSTRAINT `providermanagement_prpat_provider_role_fk` FOREIGN KEY (`provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_provider_role_relationship_type`
--

DROP TABLE IF EXISTS `providermanagement_provider_role_relationship_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_provider_role_relationship_type` (
  `provider_role_id` int(11) NOT NULL,
  `relationship_type_id` int(11) NOT NULL,
  KEY `provider_role_id` (`provider_role_id`),
  KEY `relationship_type_id` (`relationship_type_id`),
  CONSTRAINT `providermanagement_provider_role_relationship_type_ibfk_1` FOREIGN KEY (`provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`),
  CONSTRAINT `providermanagement_provider_role_relationship_type_ibfk_2` FOREIGN KEY (`relationship_type_id`) REFERENCES `relationship_type` (`relationship_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_provider_role_supervisee_provider_role`
--

DROP TABLE IF EXISTS `providermanagement_provider_role_supervisee_provider_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_provider_role_supervisee_provider_role` (
  `provider_role_id` int(11) NOT NULL,
  `supervisee_provider_role_id` int(11) NOT NULL,
  KEY `provider_role_id` (`provider_role_id`),
  KEY `supervisee_provider_role_id` (`supervisee_provider_role_id`),
  CONSTRAINT `providermanagement_prspr_provider_role_fk` FOREIGN KEY (`provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`),
  CONSTRAINT `providermanagement_prspr_supervisee_role_fk` FOREIGN KEY (`supervisee_provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_provider_suggestion`
--

DROP TABLE IF EXISTS `providermanagement_provider_suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_provider_suggestion` (
  `provider_suggestion_id` int(11) NOT NULL AUTO_INCREMENT,
  `criteria` varchar(5000) NOT NULL,
  `evaluator` varchar(255) NOT NULL,
  `relationship_type_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`provider_suggestion_id`),
  KEY `relationship_type_id` (`relationship_type_id`),
  CONSTRAINT `providermanagement_provider_suggestion_ibfk_1` FOREIGN KEY (`relationship_type_id`) REFERENCES `relationship_type` (`relationship_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `providermanagement_supervision_suggestion`
--

DROP TABLE IF EXISTS `providermanagement_supervision_suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `providermanagement_supervision_suggestion` (
  `supervision_suggestion_id` int(11) NOT NULL AUTO_INCREMENT,
  `criteria` varchar(5000) NOT NULL,
  `evaluator` varchar(255) NOT NULL,
  `provider_role_id` int(11) NOT NULL,
  `suggestion_type` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`supervision_suggestion_id`),
  KEY `provider_role_id` (`provider_role_id`),
  CONSTRAINT `providermanagement_supervision_suggestion_ibfk_1` FOREIGN KEY (`provider_role_id`) REFERENCES `providermanagement_provider_role` (`provider_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relationship`
--

DROP TABLE IF EXISTS `relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship` (
  `relationship_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_a` int(11) NOT NULL,
  `relationship` int(11) NOT NULL DEFAULT '0',
  `person_b` int(11) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`relationship_id`),
  UNIQUE KEY `relationship_uuid_index` (`uuid`),
  KEY `relation_creator` (`creator`),
  KEY `person_a_is_person` (`person_a`),
  KEY `person_b_is_person` (`person_b`),
  KEY `relationship_type_id` (`relationship`),
  KEY `relation_voider` (`voided_by`),
  KEY `relationship_changed_by` (`changed_by`),
  CONSTRAINT `person_a_is_person` FOREIGN KEY (`person_a`) REFERENCES `person` (`person_id`),
  CONSTRAINT `person_b_is_person` FOREIGN KEY (`person_b`) REFERENCES `person` (`person_id`),
  CONSTRAINT `relation_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `relation_voider` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `relationship_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `relationship_type_id` FOREIGN KEY (`relationship`) REFERENCES `relationship_type` (`relationship_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relationship_type`
--

DROP TABLE IF EXISTS `relationship_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationship_type` (
  `relationship_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `a_is_to_b` varchar(50) NOT NULL,
  `b_is_to_a` varchar(50) NOT NULL,
  `preferred` tinyint(1) NOT NULL DEFAULT '0',
  `weight` int(11) NOT NULL DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`relationship_type_id`),
  UNIQUE KEY `relationship_type_uuid_index` (`uuid`),
  KEY `user_who_created_rel` (`creator`),
  KEY `user_who_retired_relationship_type` (`retired_by`),
  KEY `relationship_type_changed_by` (`changed_by`),
  CONSTRAINT `relationship_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_created_rel` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_relationship_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_object`
--

DROP TABLE IF EXISTS `report_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_object` (
  `report_object_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `report_object_type` varchar(255) NOT NULL,
  `report_object_sub_type` varchar(255) NOT NULL,
  `xml_data` text,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`report_object_id`),
  UNIQUE KEY `report_object_uuid_index` (`uuid`),
  KEY `user_who_changed_report_object` (`changed_by`),
  KEY `report_object_creator` (`creator`),
  KEY `user_who_voided_report_object` (`voided_by`),
  CONSTRAINT `report_object_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_report_object` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_voided_report_object` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_schema_xml`
--

DROP TABLE IF EXISTS `report_schema_xml`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_schema_xml` (
  `report_schema_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `xml_data` text NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`report_schema_id`),
  UNIQUE KEY `report_schema_xml_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reporting_report_design`
--

DROP TABLE IF EXISTS `reporting_report_design`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_design` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `renderer_type` varchar(255) NOT NULL,
  `properties` text,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `report_definition_uuid` char(38) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `creator for reporting_report_design` (`creator`),
  KEY `changed_by for reporting_report_design` (`changed_by`),
  KEY `retired_by for reporting_report_design` (`retired_by`),
  KEY `report_definition_uuid_for_reporting_report_design` (`report_definition_uuid`),
  CONSTRAINT `changed_by_for_reporting_report_design` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `creator_for_reporting_report_design` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `retired_by_for_reporting_report_design` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reporting_report_design_resource`
--

DROP TABLE IF EXISTS `reporting_report_design_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_design_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `report_design_id` int(11) NOT NULL DEFAULT '0',
  `content_type` varchar(50) DEFAULT NULL,
  `extension` varchar(20) DEFAULT NULL,
  `contents` longblob,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `report_design_id for reporting_report_design_resource` (`report_design_id`),
  KEY `creator for reporting_report_design_resource` (`creator`),
  KEY `changed_by for reporting_report_design_resource` (`changed_by`),
  KEY `retired_by for reporting_report_design_resource` (`retired_by`),
  CONSTRAINT `changed_by_for_reporting_report_design_resource` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `creator_for_reporting_report_design_resource` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `report_design_id_for_reporting_report_design_resource` FOREIGN KEY (`report_design_id`) REFERENCES `reporting_report_design` (`id`),
  CONSTRAINT `retired_by_for_reporting_report_design_resource` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reporting_report_processor`
--

DROP TABLE IF EXISTS `reporting_report_processor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_processor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `processor_type` varchar(255) NOT NULL,
  `configuration` mediumtext,
  `run_on_success` tinyint(1) NOT NULL DEFAULT '1',
  `run_on_error` tinyint(1) NOT NULL DEFAULT '0',
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `report_design_id` int(11) DEFAULT NULL,
  `processor_mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `creator for reporting_report_processor` (`creator`),
  KEY `changed_by for reporting_report_processor` (`changed_by`),
  KEY `retired_by for reporting_report_processor` (`retired_by`),
  KEY `reporting_report_processor_report_design` (`report_design_id`),
  CONSTRAINT `changed_by_for_reporting_report_processor` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `creator_for_reporting_report_processor` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `reporting_report_processor_report_design` FOREIGN KEY (`report_design_id`) REFERENCES `reporting_report_design` (`id`),
  CONSTRAINT `retired_by_for_reporting_report_processor` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reporting_report_request`
--

DROP TABLE IF EXISTS `reporting_report_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reporting_report_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(38) NOT NULL,
  `base_cohort_uuid` char(38) DEFAULT NULL,
  `base_cohort_parameters` text,
  `report_definition_uuid` char(38) NOT NULL,
  `report_definition_parameters` text,
  `renderer_type` varchar(255) NOT NULL,
  `renderer_argument` varchar(255) DEFAULT NULL,
  `requested_by` int(11) NOT NULL DEFAULT '0',
  `request_datetime` datetime NOT NULL,
  `priority` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `evaluation_start_datetime` datetime DEFAULT NULL,
  `evaluation_complete_datetime` datetime DEFAULT NULL,
  `render_complete_datetime` datetime DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `schedule` varchar(100) DEFAULT NULL,
  `process_automatically` tinyint(1) NOT NULL DEFAULT '0',
  `minimum_days_to_preserve` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `requested_by for reporting_report_request` (`requested_by`),
  CONSTRAINT `requested_by_for_reporting_report_request` FOREIGN KEY (`requested_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`role`),
  UNIQUE KEY `role_uuid_index` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_privilege`
--

DROP TABLE IF EXISTS `role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_privilege` (
  `role` varchar(50) NOT NULL DEFAULT '',
  `privilege` varchar(255) NOT NULL,
  PRIMARY KEY (`privilege`,`role`),
  KEY `role_privilege_to_role` (`role`),
  CONSTRAINT `privilege_definitions` FOREIGN KEY (`privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `role_privilege_to_role` FOREIGN KEY (`role`) REFERENCES `role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_role`
--

DROP TABLE IF EXISTS `role_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_role` (
  `parent_role` varchar(50) NOT NULL DEFAULT '',
  `child_role` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`parent_role`,`child_role`),
  KEY `inherited_role` (`child_role`),
  CONSTRAINT `inherited_role` FOREIGN KEY (`child_role`) REFERENCES `role` (`role`),
  CONSTRAINT `parent_role` FOREIGN KEY (`parent_role`) REFERENCES `role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scheduler_task_config`
--

DROP TABLE IF EXISTS `scheduler_task_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduler_task_config` (
  `task_config_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `schedulable_class` text,
  `start_time` datetime DEFAULT NULL,
  `start_time_pattern` varchar(50) DEFAULT NULL,
  `repeat_interval` int(11) NOT NULL DEFAULT '0',
  `start_on_startup` tinyint(1) NOT NULL DEFAULT '0',
  `started` tinyint(1) NOT NULL DEFAULT '0',
  `created_by` int(11) DEFAULT '0',
  `date_created` datetime DEFAULT '2005-01-01 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `last_execution_time` datetime DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`task_config_id`),
  UNIQUE KEY `scheduler_task_config_uuid_index` (`uuid`),
  KEY `scheduler_changer` (`changed_by`),
  KEY `scheduler_creator` (`created_by`),
  CONSTRAINT `scheduler_changer` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `scheduler_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scheduler_task_config_property`
--

DROP TABLE IF EXISTS `scheduler_task_config_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduler_task_config_property` (
  `task_config_property_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` text,
  `task_config_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`task_config_property_id`),
  KEY `task_config_for_property` (`task_config_id`),
  CONSTRAINT `task_config_for_property` FOREIGN KEY (`task_config_id`) REFERENCES `scheduler_task_config` (`task_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `serialized_object`
--

DROP TABLE IF EXISTS `serialized_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serialized_object` (
  `serialized_object_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `subtype` varchar(255) NOT NULL,
  `serialization_class` varchar(255) NOT NULL,
  `serialized_data` mediumtext NOT NULL,
  `date_created` datetime NOT NULL,
  `creator` int(11) NOT NULL,
  `date_changed` datetime DEFAULT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `date_retired` datetime DEFAULT NULL,
  `retired_by` int(11) DEFAULT NULL,
  `retire_reason` varchar(1000) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`serialized_object_id`),
  UNIQUE KEY `serialized_object_uuid_index` (`uuid`),
  KEY `serialized_object_creator` (`creator`),
  KEY `serialized_object_changed_by` (`changed_by`),
  KEY `serialized_object_retired_by` (`retired_by`),
  CONSTRAINT `serialized_object_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `serialized_object_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `serialized_object_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test_order`
--

DROP TABLE IF EXISTS `test_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_order` (
  `order_id` int(11) NOT NULL DEFAULT '0',
  `specimen_source` int(11) DEFAULT NULL,
  `laterality` varchar(20) DEFAULT NULL,
  `clinical_history` text,
  `frequency` int(11) DEFAULT NULL,
  `number_of_repeats` int(11) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `test_order_specimen_source_fk` (`specimen_source`),
  KEY `test_order_frequency_fk` (`frequency`),
  CONSTRAINT `test_order_frequency_fk` FOREIGN KEY (`frequency`) REFERENCES `order_frequency` (`order_frequency_id`),
  CONSTRAINT `test_order_order_id_fk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `test_order_specimen_source_fk` FOREIGN KEY (`specimen_source`) REFERENCES `concept` (`concept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `uiframework_user_defined_page_view`
--

DROP TABLE IF EXISTS `uiframework_user_defined_page_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uiframework_user_defined_page_view` (
  `page_view_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `template_type` varchar(50) NOT NULL,
  `template_text` mediumtext NOT NULL,
  `uuid` varchar(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`page_view_id`),
  UNIQUE KEY `uuid` (`uuid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_property`
--

DROP TABLE IF EXISTS `user_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_property` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `property` varchar(100) NOT NULL DEFAULT '',
  `property_value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_id`,`property`),
  CONSTRAINT `user_property_to_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `role` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`role`,`user_id`),
  KEY `user_role_to_users` (`user_id`),
  CONSTRAINT `role_definitions` FOREIGN KEY (`role`) REFERENCES `role` (`role`),
  CONSTRAINT `user_role_to_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_id` varchar(50) NOT NULL DEFAULT '',
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `salt` varchar(128) DEFAULT NULL,
  `secret_question` varchar(255) DEFAULT NULL,
  `secret_answer` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `person_id` int(11) NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `activation_key` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `user_who_changed_user` (`changed_by`),
  KEY `user_creator` (`creator`),
  KEY `user_who_retired_this_user` (`retired_by`),
  KEY `person_id_for_user` (`person_id`),
  CONSTRAINT `person_id_for_user` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  CONSTRAINT `user_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_changed_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_this_user` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','','6f0be51d599f59dd1269e12e17949f8ecb9ac963e467ac1400cf0a02eb9f8861ce3cca8f6d34d93c0ca34029497542cbadda20c949affb4cb59269ef4912087b','c788c6ad82a157b712392ca695dfcf2eed193d7f','',NULL,1,'2005-01-01 00:00:00',NULL,NULL,1,0,NULL,NULL,NULL,'ec14165a-76e9-11e9-8cd9-0242ac1c000b',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visit`
--

DROP TABLE IF EXISTS `visit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visit` (
  `visit_id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) NOT NULL,
  `visit_type_id` int(11) NOT NULL,
  `date_started` datetime NOT NULL,
  `date_stopped` datetime DEFAULT NULL,
  `indication_concept_id` int(11) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`visit_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `visit_patient_index` (`patient_id`),
  KEY `visit_type_fk` (`visit_type_id`),
  KEY `visit_location_fk` (`location_id`),
  KEY `visit_creator_fk` (`creator`),
  KEY `visit_voided_by_fk` (`voided_by`),
  KEY `visit_changed_by_fk` (`changed_by`),
  KEY `visit_indication_concept_fk` (`indication_concept_id`),
  CONSTRAINT `visit_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_indication_concept_fk` FOREIGN KEY (`indication_concept_id`) REFERENCES `concept` (`concept_id`),
  CONSTRAINT `visit_location_fk` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`),
  CONSTRAINT `visit_patient_fk` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `visit_type_fk` FOREIGN KEY (`visit_type_id`) REFERENCES `visit_type` (`visit_type_id`),
  CONSTRAINT `visit_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visit_attribute`
--

DROP TABLE IF EXISTS `visit_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visit_attribute` (
  `visit_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `visit_id` int(11) NOT NULL,
  `attribute_type_id` int(11) NOT NULL,
  `value_reference` text NOT NULL,
  `uuid` char(38) NOT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `voided` tinyint(1) NOT NULL DEFAULT '0',
  `voided_by` int(11) DEFAULT NULL,
  `date_voided` datetime DEFAULT NULL,
  `void_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`visit_attribute_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `visit_attribute_visit_fk` (`visit_id`),
  KEY `visit_attribute_attribute_type_id_fk` (`attribute_type_id`),
  KEY `visit_attribute_creator_fk` (`creator`),
  KEY `visit_attribute_changed_by_fk` (`changed_by`),
  KEY `visit_attribute_voided_by_fk` (`voided_by`),
  CONSTRAINT `visit_attribute_attribute_type_id_fk` FOREIGN KEY (`attribute_type_id`) REFERENCES `visit_attribute_type` (`visit_attribute_type_id`),
  CONSTRAINT `visit_attribute_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_attribute_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_attribute_visit_fk` FOREIGN KEY (`visit_id`) REFERENCES `visit` (`visit_id`),
  CONSTRAINT `visit_attribute_voided_by_fk` FOREIGN KEY (`voided_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visit_attribute_type`
--

DROP TABLE IF EXISTS `visit_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visit_attribute_type` (
  `visit_attribute_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `datatype` varchar(255) DEFAULT NULL,
  `datatype_config` text,
  `preferred_handler` varchar(255) DEFAULT NULL,
  `handler_config` text,
  `min_occurs` int(11) NOT NULL,
  `max_occurs` int(11) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`visit_attribute_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `visit_attribute_type_creator_fk` (`creator`),
  KEY `visit_attribute_type_changed_by_fk` (`changed_by`),
  KEY `visit_attribute_type_retired_by_fk` (`retired_by`),
  CONSTRAINT `visit_attribute_type_changed_by_fk` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_attribute_type_creator_fk` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_attribute_type_retired_by_fk` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `visit_type`
--

DROP TABLE IF EXISTS `visit_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visit_type` (
  `visit_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creator` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int(11) DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`visit_type_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `visit_type_creator` (`creator`),
  KEY `visit_type_changed_by` (`changed_by`),
  KEY `visit_type_retired_by` (`retired_by`),
  CONSTRAINT `visit_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_type_creator` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `visit_type_retired_by` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-12 15:46:57
