package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"ownerData.xml", "petData.xml"})
public class PetRepositoryImplIT extends DatabaseTestSupport {

    private PetRepository petRepository;

    @Before
    public void setup() {
        petRepository = PetRepositoryImpl.newInstance(emProvider.getEm());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("createPetDataExpected.xml")
    public void testCreatePet() throws Exception {
        Pet pet = new Pet();
        pet.setName("Leo2");
        pet.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
        PetType petType = new PetType();
        petType.setId(1);
        pet.setType(petType);
        Owner owner = new Owner();
        owner.setId(1);
        pet.setOwner(owner);
        petRepository.save(pet);
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("updatePetDataExpected.xml")
    public void testUpdatePet() throws Exception {
        Pet pet = petRepository.findById(1);
        PetType petType = new PetType();
        petType.setId(2);
        pet.setType(petType);
        petRepository.save(pet);
    }

    @Test
    public void testFindPetTypes() {
        Collection<PetType> petTypes = petRepository.findPetTypes();
        assertThat(petTypes).hasSize(6);
    }

    @Test
    public void testFindPetById() {
        Pet pet = petRepository.findById(1);
        assertThat(pet).isNotNull().matches(
                p -> p.getName().equals("Leo") && p.getType().getName().equals("cat"));
    }

    @Test
    public void testFindAllPets() {
        Collection<Pet> pets = petRepository.findAll();
        assertThat(pets).hasSize(5);
    }
}
