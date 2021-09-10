package org.javaloong.kongmink.petclinic.customers.internal.util;

public interface BeanMapper {

    <D> D map(Object source, Class<D> destinationType);

    void map(Object source, Object destination);
}
