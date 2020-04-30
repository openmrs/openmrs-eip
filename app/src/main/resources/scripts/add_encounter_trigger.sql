DELIMITER ##

CREATE TRIGGER after_insert_encounter
    AFTER INSERT
    ON encounter FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (NEW.uuid, 'encounter', 'INSERT', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_update_encounter
    AFTER UPDATE
    ON encounter FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, 'encounter', 'UPDATE', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_delete_encounter
    AFTER DELETE
    ON encounter FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, 'encounter', 'DELETE', now(), uuid());
	END IF;
END ##

DELIMITER ;