package org.openmrs.eip.deindentification;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.BaseEntity;

/**
 * Builder class for {@link DeIdentifyClassMetadata} objects
 */
public class DeIdentifyClassMetadataBuilder {
	
	private DeIdentifyClassMetadataBuilder() {
	}
	
	public DeIdentifyClassMetadata build(Class<? extends BaseEntity> entityClass, Set<String> deIdentifyColumnNames) {
		SessionFactory sf = SyncContext.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class);
		MetamodelImplementor metamodel = (MetamodelImplementor) sf.getMetamodel();
		EntityType entityType = metamodel.entity(entityClass);
		AbstractEntityPersister persister = (AbstractEntityPersister) metamodel.entityPersister(entityClass);
		boolean[] nullability = persister.getPropertyNullability();
		final String[] propertyNames = persister.getPropertyNames();
		Set<Field> requiredFields = new HashSet();
		Set<Field> deIdentifyFields = new HashSet();
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			if (persister.getPropertyColumnNames(propertyName).length != 1) {
				
			}
			
			String columnName = persister.getPropertyColumnNames(propertyName)[0];
			if (deIdentifyColumnNames.contains(columnName)) {
				SingularAttribute attribute = (SingularAttribute) entityType.getAttribute(propertyName);
				Field field = (Field) attribute.getJavaMember();
				deIdentifyFields.add(field);
				if (!attribute.isOptional()) {
					//if (!nullability[i]) {
					requiredFields.add(field);
				}
			}
		}
		return new DeIdentifyClassMetadataImpl(deIdentifyFields, requiredFields, new HashSet());
	}
	
	public static DeIdentifyClassMetadataBuilder getInstance() {
		return DeIdentifyClassMetadataBuilderHolder.INSTANCE;
	}
	
	//Ensures lazy loading since inner classes are not loaded until they are first referenced
	private static class DeIdentifyClassMetadataBuilderHolder {
		
		private static DeIdentifyClassMetadataBuilder INSTANCE = new DeIdentifyClassMetadataBuilder();
		
	}
	
	private static class DeIdentifyClassMetadataImpl implements DeIdentifyClassMetadata {
		
		private final Set<Field> REQUIRED_FIELDS = new HashSet();
		
		private final Set<Field> UNIQUE_FIELDS = new HashSet();
		
		private final Set<Field> DE_IDENTIFY_FIELDS = new HashSet();
		
		private DeIdentifyClassMetadataImpl(Set<Field> deIdentifyFields, Set<Field> requiredFields,
		    Set<Field> uniqueFields) {
			REQUIRED_FIELDS.addAll(requiredFields);
			UNIQUE_FIELDS.addAll(uniqueFields);
			DE_IDENTIFY_FIELDS.addAll(deIdentifyFields);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#isRequired(Field)
		 */
		public boolean isRequired(Field field) {
			return REQUIRED_FIELDS.contains(field);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#isUnique(Field)
		 */
		public boolean isUnique(Field field) {
			return UNIQUE_FIELDS.contains(field);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#deIndentify(Field)
		 */
		public boolean deIndentify(Field field) {
			return DE_IDENTIFY_FIELDS.contains(field);
		}
		
	}
	
}
