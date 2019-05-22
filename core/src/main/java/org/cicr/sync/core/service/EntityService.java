package org.cicr.sync.core.service;

import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.model.OpenMrsModel;

import java.util.List;

public interface EntityService<E extends OpenMrsEty, M extends OpenMrsModel> {

    E save(M entity);

    List<M> getModels();
}
