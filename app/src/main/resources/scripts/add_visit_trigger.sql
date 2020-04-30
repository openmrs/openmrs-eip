DELIMITER ##

CREATE TRIGGER after_insert_visit
    AFTER INSERT
    ON visit FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (NEW.uuid, 'visit', 'INSERT', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_update_visit
    AFTER UPDATE
    ON visit FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, 'visit', 'UPDATE', now(), uuid());
	END IF;
END ##


CREATE TRIGGER after_delete_visit
    AFTER DELETE
    ON visit FOR EACH ROW
BEGIN
	IF @skip_create_sync_record IS NULL OR @skip_create_sync_record != true THEN
		INSERT INTO dbsync_sync_record (entity_id, entity_table_name, operation, date_created, uuid)
		VALUES (OLD.uuid, 'visit', 'DELETE', now(), uuid());
	END IF;
END ##

DELIMITER ;