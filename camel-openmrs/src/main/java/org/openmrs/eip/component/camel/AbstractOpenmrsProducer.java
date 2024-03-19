package org.openmrs.eip.component.camel;

import org.apache.camel.support.DefaultProducer;
import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.mapper.operations.DecomposedUuid;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

public abstract class AbstractOpenmrsProducer extends DefaultProducer {
	
	private static Logger log = LoggerFactory.getLogger(AbstractOpenmrsProducer.class);
	
	private static final String LIGHT_ENTITY_PKG = LightEntity.class.getPackage().getName();
	
	protected static ApplicationContext appContext;
	
	protected ProducerParams params;
	
	public AbstractOpenmrsProducer(final OpenmrsEndpoint endpoint, final ApplicationContext applicationContext,
	    final ProducerParams params) {
		super(endpoint);
		appContext = applicationContext;
		this.params = params;
	}
	
	/**
	 * Gets the repository for the specified openmrs type
	 *
	 * @param openmrsType openmrs type to match
	 * @return OpenmrsRepository instance
	 */
	protected static OpenmrsRepository<LightEntity> getEntityLightRepository(String openmrsType) {
		String lightEntityTypeName = LIGHT_ENTITY_PKG + "." + openmrsType.substring(openmrsType.lastIndexOf(".") + 1)
		        + "Light";
		
		if (log.isDebugEnabled()) {
			log.debug("OpenMRS type: " + openmrsType + " is mapped to DB sync light entity type: " + lightEntityTypeName);
		}
		
		Class<? extends LightEntity> lightEntityType;
		try {
			lightEntityType = (Class<? extends LightEntity>) Class.forName(lightEntityTypeName);
		}
		catch (ClassNotFoundException e) {
			throw new EIPException("Failed to load light entity class: " + lightEntityTypeName, e);
		}
		
		return getEntityLightRepository(lightEntityType);
	}
	
	/**
	 * Gets the repository for the specified light entity type
	 *
	 * @param lightEntityType light entity type to match
	 * @return OpenmrsRepository instance
	 */
	private static OpenmrsRepository<LightEntity> getEntityLightRepository(Class<? extends LightEntity> lightEntityType) {
		ResolvableType resType = ResolvableType.forClassWithGenerics(OpenmrsRepository.class, lightEntityType);
		
		String[] beanNames = appContext.getBeanNamesForType(resType);
		if (beanNames.length != 1) {
			if (beanNames.length == 0) {
				throw new EIPException("No light repository found for type " + lightEntityType);
			} else {
				throw new EIPException("Found multiple light repositories for type " + lightEntityType);
			}
		}
		
		Object lightRepo = appContext.getBean(beanNames[0]);
		
		if (log.isDebugEnabled()) {
			log.debug("Using light entity repo: " + lightRepo + " for light entity type: " + lightEntityType);
		}
		
		return (OpenmrsRepository<LightEntity>) lightRepo;
	}
	
	/**
	 * Loads and returns a light entity matching the specified decomposed uuid
	 *
	 * @param composedUuid the composed uuid
	 * @param <T>
	 * @return the Light entity object
	 */
	protected static <T extends LightEntity> T getLightEntity(String composedUuid) {
		DecomposedUuid decomposedUuid = ModelUtils.decomposeUuid(composedUuid).get();
		return (T) getEntityLightRepository(decomposedUuid.getEntityType()).findByUuid(decomposedUuid.getUuid());
	}
	
}
