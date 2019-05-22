package org.cicr.sync.core.mapper.modelToEntity;

import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.model.OpenMrsModel;

import java.util.function.Function;

public interface ModelToEntityMapper<M extends OpenMrsModel, E extends OpenMrsEty> extends Function<M, E> {
}
