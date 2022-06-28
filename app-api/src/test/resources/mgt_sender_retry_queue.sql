INSERT INTO sender_retry_queue (id, table_name, primary_key_id, operation, destination, attempt_count, exception_type, message, snapshot, date_created)
VALUES  (1, 'person', '1', 'c', 'out-bound-db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-06-27 00:00:00'),
        (2, 'person', '1', 'u', 'out-bound-db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-06-27 00:00:00'),
        (3, 'patient', '1', 'u', 'out-bound-db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-06-27 00:00:00'),
        (4, 'person', '2', 'c', 'invalid-dest', 1, 'java.lang.Exception', '', 0, '2020-06-27 00:00:00'),
        (5, 'person', '1', 'c', 'senaite', 1, 'java.lang.Exception', null, 0, '2020-06-27 00:00:00'),
        (6, 'orders', '1', 'c', 'out-bound-db-sync', 1, 'java.lang.Exception', null, 0, '2020-06-27 00:00:00');
