CREATE TRIGGER after_${operationLower}_${tableName}
    AFTER ${operationUpper}
    ON ${tableName} FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		${assign_entity_id_statement};
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (@entity_id, '${tableName}', '${operationUpper}', now(), uuid());
	END IF;
END