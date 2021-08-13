package org.javaloong.kongmink.petclinic.customers.blueprint.impl.util;

public interface BeanMapper {

    <D> D map(Object source, Class<D> destinationType);

    void map(Object source, Object destination);
}
