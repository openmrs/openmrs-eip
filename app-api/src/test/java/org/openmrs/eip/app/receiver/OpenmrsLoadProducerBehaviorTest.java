package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.component.utils.HashUtils.computeHash;
import static org.openmrs.eip.component.utils.HashUtils.getStoredHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelExecutionException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.camel.OpenmrsLoadProducer;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.PersonNameRepository;
import org.openmrs.eip.component.repository.PersonRepository;
import org.openmrs.eip.component.repository.light.UserLightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:openmrs_core_data.sql")
public class OpenmrsLoadProducerBehaviorTest extends BaseReceiverTest {
	
	private static final String URI_LOAD = "openmrs:load";
	
	private static final String HASH_COUNT_URI = "sql:SELECT count(*) as c FROM person_hash?dataSource="
	        + MGT_DATASOURCE_NAME;
	
	@Autowired
	private UserLightRepository userLightRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PersonNameRepository nameRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Before
	public void setup() {
		SyncContext.setAppUser(userLightRepo.findById(1L).get());
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	private long getPersonHashCount() {
		Map<Object, Long> result = (Map) producerTemplate.requestBody(HASH_COUNT_URI, null, List.class).get(0);
		return result.get("c");
	}
	
	@Test
	public void process_shouldNotDuplicateANewEntityReferencedByEntitiesBeingProcessedInParallel() throws Exception {
		final String siteId = "site-uuid";
		final String personUuid = "person-uuid";
		final String userUuid = "user-uuid";
		assertNull(personRepo.findByUuid(personUuid));
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		
		final long initialPersonCount = personRepo.count();
		final long initialUserCount = userLightRepo.count();
		final long initialNameCount = nameRepo.count();
		int size = 50;
		ExecutorService executor = Executors.newFixedThreadPool(size);
		List<CompletableFuture<Void>> futures = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			final String nameUuid = "name-uuid-" + i;
			assertNull(nameRepo.findByUuid(personUuid));
			PersonNameModel name = new PersonNameModel();
			name.setUuid(nameUuid);
			name.setPersonUuid(PersonLight.class.getName() + "(" + personUuid + ")");
			name.setCreatorUuid(UserLight.class.getName() + "(" + userUuid + ")");
			name.setDateCreated(LocalDateTime.now());
			SyncModel syncModel = new SyncModel(name.getClass(), name, metadata);
			futures.add(CompletableFuture.runAsync(() -> {
				producerTemplate.sendBody("openmrs:load", syncModel);
			}, executor));
		}
		
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
		
		assertEquals(initialPersonCount + 1, personRepo.count());
		assertEquals(initialUserCount + 1, userLightRepo.count());
		assertEquals(initialNameCount + size, nameRepo.count());
	}
	
	@Test
	public void process_shouldNotDuplicateANewEntityAndHashWhenProcessedInParallelByMultipleSites() throws Exception {
		final String siteId = "site-uuid";
		final String personUuid = "person-uuid";
		final String userUuid = "1c2b12d1-5c4f-415f-871b-b98a22137606";
		assertNull(personRepo.findByUuid(personUuid));
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(siteId);
		
		final long initialPersonCount = personRepo.count();
		final long initialPersonHashCount = getPersonHashCount();
		int size = 50;
		ExecutorService executor = Executors.newFixedThreadPool(size);
		List<CompletableFuture<Void>> futures = new ArrayList(size);
		AtomicInteger passCount = new AtomicInteger();
		AtomicInteger failureCount = new AtomicInteger();
		for (int i = 0; i < size; i++) {
			PersonModel person = new PersonModel();
			person.setUuid(personUuid);
			person.setCreatorUuid(UserLight.class.getName() + "(" + userUuid + ")");
			person.setDateCreated(LocalDateTime.now());
			SyncModel syncModel = new SyncModel(person.getClass(), person, metadata);
			futures.add(CompletableFuture.runAsync(() -> {
				try {
					producerTemplate.sendBody(URI_LOAD, syncModel);
					passCount.incrementAndGet();
				}
				catch (DataIntegrityViolationException e) {
					failureCount.incrementAndGet();
				}
				catch (CamelExecutionException e) {
					if (ExceptionUtils.getRootCause(e) instanceof JdbcSQLIntegrityConstraintViolationException) {
						failureCount.incrementAndGet();
					}
				}
			}, executor));
		}
		
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
		
		assertEquals(initialPersonCount + 1, personRepo.count());
		assertEquals(initialPersonHashCount + 1, getPersonHashCount());
		assertEquals(size - passCount.get(), failureCount.get());
	}
	
	@Test
	public void process_shouldUpdateTheStoredHashIfItAlreadyExistsForAnEntityBeingInserted() {
		final String personUuid = "person-uuid";
		assertNull(personRepo.findByUuid(personUuid));
		PersonModel person = new PersonModel();
		person.setUuid(personUuid);
		person.setCreatorUuid(UserLight.class.getName() + "(" + TestConstants.USER_UUID + ")");
		person.setDateCreated(LocalDateTime.now());
		PersonHash existingHash = new PersonHash();
		existingHash.setIdentifier(personUuid);
		existingHash.setHash(computeHash(person));
		existingHash.setDateCreated(LocalDateTime.now());
		OpenmrsLoadProducer.saveHash(existingHash, producerTemplate, false);
		assertNotNull(getStoredHash(personUuid, PersonHash.class, producerTemplate));
		assertNull(existingHash.getDateChanged());
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier("site-id");
		SyncModel syncModel = new SyncModel(person.getClass(), person, metadata);
		//For testing purposes let us update a field so that the hash is different
		person.setDateChanged(LocalDateTime.now());
		
		producerTemplate.sendBody(URI_LOAD, syncModel);
		
		BaseHashEntity updatedHash = getStoredHash(personUuid, PersonHash.class, producerTemplate);
		assertEquals(computeHash(person), updatedHash.getHash());
		assertNotNull(updatedHash.getDateChanged());
	}
	
}
