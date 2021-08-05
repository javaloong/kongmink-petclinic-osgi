package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import org.javaloong.kongmink.petclinic.customers.model.Owner;

import java.util.Collection;

public interface OwnerRepository {

    Owner findById(int id);

    void save(Owner owner);

    void delete(Owner owner);

    Collection<Owner> findAllByLastName(String lastName);

    Collection<Owner> findAll();
}
