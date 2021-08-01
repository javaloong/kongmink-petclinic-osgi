package org.javaloong.kongmink.petclinic.customers.service;

import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;

import java.util.Collection;
import java.util.List;

public interface PetService {

    void savePet(Pet pet);

    void deletePet(Pet pet);

    List<PetType> findPetTypes();

    Pet findPetById(int id);

    Collection<Pet> findAllPets();
}
