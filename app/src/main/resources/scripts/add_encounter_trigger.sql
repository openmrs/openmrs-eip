DELIMITER ##

SET @table_name = 'encounter';

CREATE TRIGGER after_insert_encounter
    AFTER INSERT
    ON encounter FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (NEW.uuid, @table_name, 'INSERT', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_update_encounter
    AFTER UPDATE
    ON encounter FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, @table_name, 'UPDATE', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_delete_encounter
    AFTER DELETE
    ON encounter FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, @table_name, 'DELETE', now(), uuid());
	END IF;
END ##

DELIMITER ;