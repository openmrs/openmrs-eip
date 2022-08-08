INSERT INTO receiver_conflict_queue (id, model_class_name, identifier, entity_payload, is_resolved, site_id, date_created)
VALUES  (1, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 0, 1, '2022-06-16 00:00:00'),
        (2, 'org.openmrs.eip.component.model.PersonModel', 'uuid-1', '{}', 0, 1, '2022-06-16 00:00:00'),
        (3, 'org.openmrs.eip.component.model.PatientModel', 'uuid-1', '{}', 0, 1, '2022-06-16 00:00:00'),
        (4, 'org.openmrs.eip.component.model.PersonModel', 'uuid-2', '{}', 1, 1, '2022-06-16 00:00:00'),
        (5, 'org.openmrs.eip.component.model.OrderModel', 'uuid-1', '{}', 0, 1, '2022-06-16 00:00:00');
