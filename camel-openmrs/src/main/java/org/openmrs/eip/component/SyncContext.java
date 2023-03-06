package org.openmrs.eip.component;

import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.entity.light.UserLight;
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
	
	private static UserLight appUser;
	
	private static UserLight adminUser;
	
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
	 * Gets the bean matching the specified bean name from the application context
	 *
	 * @return an instance of the bean matching the specified name
	 */
	public static <T> T getBean(String name) {
		return (T) appContext.getBean(name);
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
	
	/**
	 * Gets the user
	 *
	 * @return the user
	 */
	public static UserLight getAppUser() {
		return appUser;
	}
	
	/**
	 * Sets the user
	 *
	 * @param user the user to set
	 */
	public static void setAppUser(UserLight user) {
		SyncContext.appUser = user;
	}
	
	/**
	 * Gets the admin user
	 *
	 * @return the admin user
	 */
	public static UserLight getAdminUser() {
		return adminUser;
	}
	
	/**
	 * Sets the admin user
	 *
	 * @param user the admin user to set
	 */
	public static void setAdminUser(UserLight user) {
		SyncContext.adminUser = user;
	}
	
}
