INSERT INTO person (person_id,gender,creator,date_created,birthdate_estimated,dead,deathdate_estimated,voided,uuid)
VALUES (101, 'M', 1, '2022-06-28 00:00:00', 0, 0, 0, 0, 'abfd940e-32dc-491f-8038-a8f3afe3e35b');

INSERT INTO person_name (person_name_id,person_id,given_name,family_name,preferred,creator,date_created,voided,uuid)
VALUES (1, 101, 'John', 'Doe', 1, 1, '2022-06-28 00:00:00', 0, '1bfd940e-32dc-491f-8038-a8f3afe3e35a'),
       (2, 101, 'Horatio', 'Hornblower', 0, 1, '2022-06-28 00:00:00', 0, '2bfd940e-32dc-491f-8038-a8f3afe3e35a');

INSERT INTO patient (patient_id,creator,date_created,voided,allergy_status)
VALUES (101, 1, '2022-06-28 00:00:00', 0, 'Unknown');

INSERT INTO patient_identifier_type (patient_identifier_type_id,name,check_digit,required,creator,date_created,retired,uuid)
VALUES (1, 'OpenMRS ID', 0, 0, 1, '2022-06-28 00:00:00', 0, '1dfd940e-32dc-491f-8038-a8f3afe3e35e'),
       (2, 'SSN', 0, 0, 1, '2022-06-28 00:00:00', 0, '2dfd940e-32dc-491f-8038-a8f3afe3e35e');

INSERT INTO patient_identifier (patient_identifier_id,patient_id,identifier,identifier_type,preferred,creator,date_created,voided,uuid)
VALUES (1, 101, 'QWERTY', 1, 1, 1, '2022-06-28 00:00:00', 0, '1cfd940e-32dc-491f-8038-a8f3afe3e35c'),
       (2, 101, '111-11-1111', 2, 0, 1, '2022-06-28 00:00:00', 0, '2cfd940e-32dc-491f-8038-a8f3afe3e35c');

INSERT INTO person_attribute_type (person_attribute_type_id,name,format,searchable,retired,creator,date_created,uuid)
VALUES (1, 'Mobile Phone', 'java.lang.String', 1, 0, 1, now(), 'e2e3fd64-1d5f-11e0-b929-000c29ad1d07'),
       (2, 'Home Phone', 'java.lang.String', 1, 0, 1, now(), 'e6c97a9d-a77b-401f-b06e-81900e21ed1d'),
       (3, 'Birth Place', 'java.lang.String', 0, 0, 1, now(), 'f6c97a9d-a77b-401f-b06e-81900e21ed1e');

INSERT INTO person_attribute (person_attribute_id,person_attribute_type_id,person_id,value,voided,creator,date_created,uuid)
VALUES (1, 1, 1, '3172566786', 0, 1, now(), '1efd940e-32dc-491f-8038-a8f3afe3e35f'),
       (2, 2, 101, '0987654321', 0, 1, now(), '2efd940e-32dc-491f-8038-a8f3afe3e35f'),
       (3, 3, 101, 'Kampala', 0, 1, now(), '3efd940e-32dc-491f-8038-a8f3afe3e35f'),
       (4, 1, 101, '1234567890', 0, 1, now(), '4efd940e-32dc-491f-8038-a8f3afe3e35f');
