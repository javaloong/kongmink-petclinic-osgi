package org.javaloong.kongmink.petclinic.customers.blueprint.impl.service;

import org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository.PetRepository;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.javaloong.kongmink.petclinic.customers.service.PetService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Transactional
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void savePet(Pet pet) {
        petRepository.save(pet);
    }

    @Override
    public void deletePet(Pet pet) {
        petRepository.delete(pet);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public List<PetType> findPetTypes() {
        return petRepository.findPetTypes();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Pet findPetById(int id) {
        return petRepository.findById(id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Collection<Pet> findAllPets() {
        return petRepository.findAll();
    }
}
