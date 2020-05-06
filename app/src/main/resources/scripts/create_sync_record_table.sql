CREATE TABLE `dbsync_sync_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entity_id` varchar(38) NOT NULL,
  `entity_table_name` varchar(50) NOT NULL,
  `operation` varchar(6) NOT NULL,
  `date_created` datetime NOT NULL,
  `uuid` char(38) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
