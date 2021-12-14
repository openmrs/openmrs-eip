package org.openmrs.eip.component;

import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * Holds contextual data for the application
 */
@Component
public class SyncContext implements ApplicationContextAware {
	
	private static ApplicationContext appContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
	
	/**
	 * Gets the bean matching the specified type from the application context
	 *
	 * @return an instance of the bean matching the specified type
	 */
	public static <T> T getBean(Class<T> clazz) {
		return appContext.getBean(clazz);
	}
	
	/**
	 * Gets the {@link SyncEntityRepository} for the entity class mapped to the specified table name
	 *
	 * @param tableName the name of the table
	 * @return SyncEntityRepository for the entity mapped to the table
	 */
	public static SyncEntityRepository getRepositoryBean(String tableName) {
		Class<? extends BaseEntity> entityClass = TableToSyncEnum.getTableToSyncEnum(tableName).getEntityClass();
		ResolvableType resType = ResolvableType.forClassWithGenerics(SyncEntityRepository.class, entityClass);
		String[] beanNames = appContext.getBeanNamesForType(resType);
		if (beanNames.length != 1) {
			if (beanNames.length == 0) {
				throw new EIPException("No entity repository found for type: " + entityClass);
			} else {
				throw new EIPException("Found multiple entity repositories for type: " + entityClass);
			}
		}
		
		return (SyncEntityRepository) appContext.getBean(beanNames[0]);
	}
	
}
