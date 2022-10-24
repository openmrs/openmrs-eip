package org.openmrs.eip.component.mapper.operations;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.utils.ModelUtils;
import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.entity.light.LightEntity;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;

@Slf4j
@Component("linkLightEntity")
public class LinkLightEntityConsumer<E extends BaseEntity, M extends BaseModel> implements BiConsumer<Context<E, M>, String> {
	
	private ApplicationContext applicationContext;
	
	private static final String UUID_SUFFIX = "Uuid";
	
	public LinkLightEntityConsumer(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void accept(final Context<E, M> context, final String modelAttributeName) {
		
		String entityAttributeName = getEntityAttributeName(modelAttributeName);
		
		if (!context.getEntityBeanWrapper().isReadableProperty(entityAttributeName)) {
			log.warn("No getter exists for attribute " + entityAttributeName + " in class " + context.getEntity().getClass()
			        + ". " + "The attribute will be ignored");
			return;
		}
		
		PropertyDescriptor entityDesc = context.getEntityBeanWrapper().getPropertyDescriptor(entityAttributeName);
		
		String linkedEntityUuid = (String) context.getModelBeanWrapper().getPropertyValue(modelAttributeName);
		ModelUtils.decomposeUuid(linkedEntityUuid).ifPresent(decomposedUuid -> {
			LightService service = getService(entityDesc, decomposedUuid.getEntityType());
			context.getEntityBeanWrapper().setPropertyValue(entityAttributeName,
			    service.getOrInitEntity(decomposedUuid.getUuid()));
		});
	}
	
	private LightService getService(final PropertyDescriptor entityDesc,
	                                final Class<? extends LightEntity> linkedEntityType) {
		String[] beanNamesForType = applicationContext
		        .getBeanNamesForType(ResolvableType.forClassWithGenerics(LightService.class, linkedEntityType));
		
		if (beanNamesForType.length == 0) {
			throw new EIPException("Unable to find service of type " + LightService.class + " with class parameter "
			        + entityDesc.getPropertyType());
		}
		
		return (LightService) applicationContext.getBean(beanNamesForType[0]);
	}
	
	private String getEntityAttributeName(final String modelAttributeName) {
		if (!modelAttributeName.contains(UUID_SUFFIX)) {
			throw new IllegalArgumentException("The model attribute should be suffixed with " + UUID_SUFFIX);
		}
		return modelAttributeName.substring(0, modelAttributeName.length() - UUID_SUFFIX.length());
	}
}
