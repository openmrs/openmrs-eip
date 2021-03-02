INSERT INTO sender_retry_queue (table_name, primary_key_id, operation, destination, attempt_count, snapshot, date_created)
VALUES
('person', '1', 'c', 'direct:db-sync', 1, 0, '2020-02-27 00:00:00'),
('person', '2', 'c', 'direct:invalid-dest', 1, 0, '2020-02-27 00:00:00');