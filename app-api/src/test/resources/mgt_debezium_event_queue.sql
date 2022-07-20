INSERT INTO debezium_event_queue (id, table_name, primary_key_id, operation, snapshot, date_created)
VALUES (2, 'visit', '1', 'c', 0, '2020-06-28 00:00:00.001'),
       (1, 'encounter', '1', 'c', 0, '2020-06-28 00:00:00.001'),
       (3, 'patient', '101', 'u', 0, '2020-06-28 00:00:00.000');
