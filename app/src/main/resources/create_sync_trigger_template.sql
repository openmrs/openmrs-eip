CREATE TRIGGER after_${operationLower}_${tableName}
    AFTER ${operationUpper}
    ON ${tableName} FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (${refRow}.uuid, '${tableName}', '${operationUpper}', now(), uuid());
	END IF;
END