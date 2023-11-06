INSERT INTO sender_retry_queue (id, table_name, primary_key_id, operation, attempt_count, exception_type, message, snapshot, date_created)
VALUES  (1, 'person', '1', 'c', 1, 'org.openmrs.eip.component.exception.EIPException', 'Testing', 0, '2020-06-27 00:00:00.001'),
        (2, 'person', '1', 'u', 1, 'org.openmrs.eip.component.exception.EIPException', 'Testing', 0, '2020-06-27 00:00:00.002'),
        (3, 'person', '1', 'u', 1, 'org.apache.activemq.artemis.api.core.ActiveMQNativeIOError', 'Testing', 0, '2020-06-27 00:00:00.003'),
        (4, 'orders', '1', 'c', 1, 'org.apache.activemq.artemis.api.core.ActiveMQNativeIOError', null, 0, '2020-06-27 00:00:00.000');
