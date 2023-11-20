package org.openmrs.eip.deindentification;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.eip.component.entity.BaseEntity;

/**
 * Builder class for {@link DeIdentifyClassMetadata} objects
 */
public class DeIdentifyClassMetadataBuilder {
	
	private DeIdentifyClassMetadataBuilder() {
	}
	
	public DeIdentifyClassMetadata build(Class<? extends BaseEntity> entityClass, Set<String> deIdentifyColumnNames) {
		/*SessionFactory sf = SyncContext.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class);
		MappingMetamodel metamodel = (MappingMetamodel) sf.getMetamodel();
		EntityType entityType = metamodel.get(entityClass);
		AbstractEntityPersister persister = (AbstractEntityPersister) metamodel.entityPersister(entityClass);
		boolean[] nullability = persister.getPropertyNullability();
		final String[] propertyNames = persister.getPropertyNames();
		Set<Field> deIdentifyFields = new HashSet();
		Set<Field> requiredFields = new HashSet();
		Set<Field> uniqueFields = new HashSet();
		Map<Field, Integer> fieldAndLengthMap = new HashMap();
		
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			if (persister.getPropertyColumnNames(propertyName).length != 1) {
				throw new EIPException("Found multiple columns mapped to " + entityClass.getName() + "." + propertyName);
			}
			
			String columnName = persister.getPropertyColumnNames(propertyName)[0];
			if (deIdentifyColumnNames.contains(columnName)) {
				SingularAttribute attribute = (SingularAttribute) entityType.getAttribute(propertyName);
				Field field = (Field) attribute.getJavaMember();
				deIdentifyFields.add(field);
				Column columnAnn = field.getAnnotation(Column.class);
				fieldAndLengthMap.put(field, columnAnn.length());
				if (!nullability[i] || !attribute.isOptional() || field.getAnnotation(NotNull.class) != null) {
					requiredFields.add(field);
				}
				
				if (columnAnn.unique()) {
					uniqueFields.add(field);
				}
			}
		}
		return new DeIdentifyClassMetadataImpl(deIdentifyFields, requiredFields, uniqueFields, fieldAndLengthMap);*/
		return null;
	}
	
	public static DeIdentifyClassMetadataBuilder getInstance() {
		return DeIdentifyClassMetadataBuilderHolder.INSTANCE;
	}
	
	//Ensures lazy loading since inner classes are not loaded until they are first referenced
	private static class DeIdentifyClassMetadataBuilderHolder {
		
		private static DeIdentifyClassMetadataBuilder INSTANCE = new DeIdentifyClassMetadataBuilder();
		
	}
	
	private static class DeIdentifyClassMetadataImpl implements DeIdentifyClassMetadata {
		
		private final Set<Field> DE_IDENTIFY_FIELDS = new HashSet();
		
		private final Set<Field> REQUIRED_FIELDS = new HashSet();
		
		private final Set<Field> UNIQUE_FIELDS = new HashSet();
		
		private final Map<Field, Integer> FIELD_AND_LENGTH_MAP = new HashMap();
		
		private DeIdentifyClassMetadataImpl(Set<Field> deIdentifyFields, Set<Field> requiredFields, Set<Field> uniqueFields,
		    Map<Field, Integer> fieldAndLengthMap) {
			REQUIRED_FIELDS.addAll(requiredFields);
			UNIQUE_FIELDS.addAll(uniqueFields);
			DE_IDENTIFY_FIELDS.addAll(deIdentifyFields);
			FIELD_AND_LENGTH_MAP.putAll(fieldAndLengthMap);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#deIndentify(Field)
		 */
		@Override
		public boolean deIndentify(Field field) {
			return DE_IDENTIFY_FIELDS.contains(field);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#isRequired(Field)
		 */
		@Override
		public boolean isRequired(Field field) {
			return REQUIRED_FIELDS.contains(field);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#isUnique(Field)
		 */
		@Override
		public boolean isUnique(Field field) {
			return UNIQUE_FIELDS.contains(field);
		}
		
		/**
		 * @see DeIdentifyClassMetadata#getLength(Field)
		 */
		@Override
		public Integer getLength(Field field) {
			return FIELD_AND_LENGTH_MAP.get(field);
		}
		
	}
	
}
