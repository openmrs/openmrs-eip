package org.openmrs.eip.component.service;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.eip.component.SyncContext;

@Slf4j
public abstract class PatientServiceUtils {
	
	/**
	 * Inserts a patient row with the specified patient data
	 * 
	 * @param personId the person id
	 * @param uuid the patient uuid
	 * @param voided specified if the patient is voided or not
	 * @param creatorId the user id of the creator
	 * @param dateCreated date created of the patient
	 */
	public static void createPatient(Long personId, String uuid, boolean voided, Long creatorId, LocalDateTime dateCreated) {
		
		EntityManager em = SyncContext.getBean(EntityManagerFactory.class).createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			String sql = "insert into patient (patient_id,creator,date_created,voided) values (?, ?, ?, ?)";
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, personId);
			query.setParameter(2, creatorId);
			query.setParameter(3, dateCreated);
			query.setParameter(4, voided);
			
			query.executeUpdate();
			
			tx.commit();
		}
		catch (Exception e) {
			log.warn("Failed to insert row for patient with uuid: " + uuid);
			if (tx != null) {
				tx.rollback();
			}
			
			throw e;
		}
		finally {
			if (em != null) {
				em.close();
			}
		}
	}
	
}
