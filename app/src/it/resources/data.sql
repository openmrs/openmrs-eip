insert into users(uuid, user_id, system_id, creator, date_created, person_id, retired)
values ('user_uuid', '1', 'admin', '1', '2005-01-01 00:00:00', '1', false);

insert into person(person_id, gender, birthdate, birthdate_estimated, dead, death_date, cause_of_death, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid, deathdate_estimated, birthtime)
values ('1', 'M', NULL, false, false, NULL, NULL, NULL, '2005-01-01 00:00:00', NULL, NULL, false, NULL, NULL, NULL, 'dd279794-76e9-11e9-8cd9-0242ac1c000b', false, NULL);

insert into person_address(person_address_id, person_id, preferred, address1, address2, city_village, state_province, postal_code, country, latitude, longitude, creator, date_created, voided, voided_by, date_voided, void_reason, county_district, address3, address6, address5, address4, uuid, date_changed, changed_by, start_date, end_date, address7, address8, address9, address10, address11, address12, address13, address14, address15)
values ('1', NULL, true, 'chemin perdu', NULL, 'ville', NULL, NULL, NULL, NULL, NULL, '1', '2005-01-01 00:00:00', false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'uuid_person_address', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
