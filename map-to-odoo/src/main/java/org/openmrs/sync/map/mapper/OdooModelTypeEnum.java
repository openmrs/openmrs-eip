package org.openmrs.sync.map.mapper;

import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.common.model.sync.PersonModel;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public enum OdooModelTypeEnum {
    CUSTOMER(PersonModel.class, new PersonToCustomerMapper());

    private Class<? extends BaseModel> derivedFromModelClass;
    private Function<BaseModel, OdooModel> mapper;

    OdooModelTypeEnum(final Class<? extends BaseModel> derivedFromModelClass,
                      final Function<BaseModel, OdooModel> mapper) {
        this.derivedFromModelClass = derivedFromModelClass;
        this.mapper = mapper;
    }

    public Class<? extends BaseModel> getDerivedFromModelClass() {
        return derivedFromModelClass;
    }

    public Function<BaseModel, OdooModel> getMapper() {
        return mapper;
    }

    public static Optional<OdooModelTypeEnum> getDerivedOdooEntityType(final Class<? extends BaseModel> syncTableModelClass) {
        return Arrays.stream(values())
                .filter(e -> e.getDerivedFromModelClass().equals(syncTableModelClass))
                .findFirst();
    }
}
