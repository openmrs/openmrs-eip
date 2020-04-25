DELIMITER ##

SET @table_name = 'visit';

CREATE TRIGGER after_insert_visit
    AFTER INSERT
    ON visit FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (NEW.uuid, @table_name, 'INSERT', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_update_visit
    AFTER UPDATE
    ON visit FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, @table_name, 'UPDATE', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_delete_visit
    AFTER DELETE
    ON visit FOR EACH ROW
BEGIN
	IF @source_name IS NULL THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, @table_name, 'DELETE', now(), uuid());
	END IF;
END ##

DELIMITER ;