package org.openmrs.eip.component.mapper.operations;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.entity.BaseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
public class CopyStandardFieldsFunction<E extends BaseEntity, M extends BaseModel> implements UnaryOperator<Context<E, M>> {
	
	@Override
	public Context<E, M> apply(final Context<E, M> context) {
		M model = context.getModel();
		E entity = context.getEntity();
		
		if (context.getDirection() == MappingDirectionEnum.MODEL_TO_ENTITY) {
			BeanUtils.copyProperties(model, entity);
		} else if (context.getDirection() == MappingDirectionEnum.ENTITY_TO_MODEL) {
			BeanUtils.copyProperties(entity, model);
		}
		
		return new Context<>(entity, model, context.getDirection());
	}
}
