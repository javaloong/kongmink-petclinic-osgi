package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;

import java.util.Collection;
import java.util.List;

public interface PetRepository {

    void save(Pet pet);

    void delete(Pet pet);

    List<PetType> findPetTypes();

    Pet findById(int id);

    Collection<Pet> findAll();
}
