package org.cicr.sync.core.mapper.entitiesToModel;

import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.model.OpenMrsModel;

import java.util.function.Function;

public interface EntityToModelMapper<E extends OpenMrsEty, M extends OpenMrsModel> extends Function<E, M> {
}
