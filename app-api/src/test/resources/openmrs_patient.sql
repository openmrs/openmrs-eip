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
