package org.openmrs.eip.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class CreateTestData {
	
	private static final String fileName = "data.sql";
	
	public static void main(String[] args) {
		try {
			addPatients();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addPatients() throws Exception {
		FileUtils.writeLines(new File(fileName), Collections.emptyList(), false);
		final String ID_PH = "{i}";
		final String G_PH = "{g}";
		final String M_PH = "{m}";
		final String H_PH = "{h}";
		final String V_PH = "{v}";
		final String B_PH = "{b}";
		final String U_PH = "{u}";
		final String person = "INSERT INTO person (person_id,gender,birthdate,creator,date_created,voided,uuid) VALUES ("
		        + ID_PH + ", '" + G_PH + "', '" + B_PH + "', 1, now(), " + V_PH + ", '" + U_PH + "');";
		final String name = "INSERT INTO person_name (person_id,given_name,family_name,creator,date_created,voided,uuid) VALUES ("
		        + ID_PH + ", 'John" + ID_PH + "', 'Doe" + ID_PH + "', 1, now(), 0, 'name-uuid-" + ID_PH + "');";
		final String address = "INSERT INTO person_address (person_id,address1,creator,date_created,voided,uuid) VALUES ("
		        + ID_PH + ", '123 Ocean Dr " + ID_PH + "', 1, now(), 0, 'address-uuid-" + ID_PH + "');";
		final String phones = "INSERT INTO person_attribute (person_attribute_type_id,person_id,value,voided,creator,date_created,uuid) VALUES (8, "
		        + ID_PH + ", '" + M_PH + "', 0, 1, now(), 'mobile-uuid-" + ID_PH + "'), (9, " + ID_PH + ", '" + H_PH
		        + "', 0, 1, now(), 'home-uuid-" + ID_PH + "');";
		final String patient = "INSERT INTO patient (patient_id,creator,date_created) VALUES (" + ID_PH + ", 1, now());";
		final String id = "INSERT INTO patient_identifier (patient_id,identifier,identifier_type,location_id,creator,date_created,voided,uuid) VALUES ("
		        + ID_PH + ", '" + ID_PH + "', 2, 1, 1, now(), 0, 'id-uuid-" + ID_PH + "');";
		
		int start = 2;
		int count = 1000;
		List<String> rows = new ArrayList(count);
		for (int i = start; i < (count + start); i++) {
			String gender = i % 2 == 0 ? "M" : "F";
			String birthdate = i % 2 == 0 ? "2000-02-02" : "2005-05-05";
			
			String personRow = person.replace(ID_PH, "" + i).replace(G_PH, gender).replace(B_PH, birthdate).replace(U_PH,
			    UUID.randomUUID().toString());
			if (i == 400) {
				personRow = personRow.replace(V_PH, "1");
			} else {
				personRow = personRow.replace(V_PH, "0");
			}
			rows.add(personRow);
			rows.add(name.replace(ID_PH, "" + i));
			rows.add(address.replace(ID_PH, "" + i));
			rows.add(phones.replace(ID_PH, "" + i).replace(M_PH, String.format("%10s", i).replace(' ', '0')).replace(H_PH,
			    String.format("%10s", i, i).replace(' ', '0')));
			rows.add(patient.replace(ID_PH, "" + i));
			rows.add(id.replace(ID_PH, "" + i));
			
			if (i % 10000000 == 0) {
				FileUtils.writeLines(new File(fileName), rows, true);
				rows.clear();
			}
		}
		
		if (!rows.isEmpty())
			FileUtils.writeLines(new File(fileName), rows, true);
		
		System.out.println("Done");
	}
	
}
