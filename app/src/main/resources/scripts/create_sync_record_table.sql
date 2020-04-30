CREATE TABLE `dbsync_sync_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entity_id` varchar(38) NOT NULL,
  `entity_table_name` varchar(50) NOT NULL,
  `operation` varchar(6) NOT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'NEW',
  `status_message` varchar(255) DEFAULT NULL,
  `sync_date` datetime DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `openmrs_version` varchar(255) NOT NULL DEFAULT '2.3.0',
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
