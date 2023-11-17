INSERT INTO sender_retry_queue (id, table_name, primary_key_id, operation, destination, attempt_count, exception_type, message, snapshot, date_created)
VALUES
(1, 'person', '1', 'c', 'direct:db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(2, 'person', '1', 'u', 'direct:db-sync', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(3, 'person', '2', 'c', 'direct:invalid-dest', 1, 'java.lang.Exception', '', 0, '2020-02-27 00:00:00'),
(4, 'person', '1', 'c', 'direct:senaite', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00'),
(5, 'orders', '1', 'c', 'mock:event-listener', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00'),
(6, 'orders', '2', 'c', 'direct:db-sync', 1, 'java.lang.Exception', null, 0, '2020-02-27 00:00:00'),
(7, 'orders', '101', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(8, 'orders', '103', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(9, 'test_order', '105', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(10, 'drug_order', '107', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(11, 'orders', '111', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00'),
(12, 'referral_order', '113', 'c', 'mock:event-listener', 1, 'java.lang.Exception', 'Testing', 0, '2020-02-27 00:00:00');
