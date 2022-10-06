INSERT INTO receiver_conflict_queue (id, model_class_name, identifier, entity_payload, is_snapshot, is_resolved, site_id, date_sent_by_sender, message_uuid, date_received, date_created)
VALUES  (1, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 0, 0, 1, '2022-06-16 00:00:00', '1cfd940e-32dc-491f-8038-a8f3afe3e36d', '2022-06-16 00:00:05', '2022-06-16 00:00:10'),
        (2, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 0, 0, 1, '2022-06-16 00:00:00', '2cfd940e-32dc-491f-8038-a8f3afe3e36d', '2022-06-16 00:00:05', '2022-06-16 00:00:10'),
        (3, 'org.openmrs.eip.component.model.PatientModel', 'uuid-1', '{}', 0, 0, 1, '2022-06-16 00:00:00', '3cfd940e-32dc-491f-8038-a8f3afe3e36d', '2022-06-16 00:00:05', '2022-06-16 00:00:10'),
        (4, 'org.openmrs.eip.component.model.PersonModel', 'uuid-2', '{}', 0, 1, 1, '2022-06-16 00:00:00', '4cfd940e-32dc-491f-8038-a8f3afe3e36d', '2022-06-16 00:00:05', '2022-06-16 00:00:10'),
        (5, 'org.openmrs.eip.component.model.OrderModel', 'uuid-1', '{}', 0, 0, 1, '2022-06-16 00:00:00', '5cfd940e-32dc-491f-8038-a8f3afe3e36d', '2022-06-16 00:00:05', '2022-06-16 00:00:10');
