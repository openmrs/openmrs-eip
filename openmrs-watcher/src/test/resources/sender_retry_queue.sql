INSERT INTO sender_retry_queue (table_name, primary_key_id, operation, destination, attempt_count, exception_type, message, snapshot, date_created)
VALUES
('person', '1', 'c', 'direct:db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
('person', '1', 'u', 'direct:db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
('person', '2', 'c', 'direct:invalid-dest', 1, 'java.lang.Exception', '', 0, '2020-02-27 00:00:00'),
('person', '1', 'c', 'direct:senaite', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00'),
('orders', '1', 'c', 'mock:event-listener', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00'),
('orders', '2', 'c', 'direct:db-sync', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00');
