INSERT INTO receiver_retry_queue (id, model_class_name, identifier, entity_payload, attempt_count, exception_type, message, date_created)
VALUES  (1, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 1, 'java.lang.Exception', 'Testing', '2022-06-16 00:00:01'),
        (2, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 1, 'java.lang.Exception', 'Testing', '2022-06-16 00:00:02'),
        (3, 'org.openmrs.eip.component.model.PatientModel', 'uuid-1', '{}', 1, 'java.lang.Exception', 'Testing', '2022-06-16 00:00:03'),
        (4, 'org.openmrs.eip.component.model.PersonModel', 'uuid-2', '{}', 1, 'java.lang.Exception', '', '2022-06-16 00:00:00'),
        (5, 'org.openmrs.eip.component.model.OrderModel', 'uuid-1', '{}', 1, 'java.lang.Exception', null, '2022-06-16 00:00:05');
