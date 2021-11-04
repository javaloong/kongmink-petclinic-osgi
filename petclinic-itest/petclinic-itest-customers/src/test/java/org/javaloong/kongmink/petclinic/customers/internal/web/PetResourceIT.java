package org.javaloong.kongmink.petclinic.customers.internal.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"ownerData.xml", "petData.xml"})
public class PetResourceIT extends DBUnitTestSupport {

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("createPetDataExpected.xml")
    public void addPet_ShouldAddPetAndReturnHttpStatusCreated() throws Exception {
        Pet pet = new Pet();
        pet.setName("Leo2");
        pet.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
        PetType petType = new PetType();
        petType.setId(1);
        pet.setType(petType);
        Owner owner = new Owner();
        owner.setId(1);
        pet.setOwner(owner);

        Response response = webTarget()
                .path("/pets")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(pet));

        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("updatePetDataExpected.xml")
    public void updatePet_ShouldReturnHttpStatusOk() {
        PetType petType = new PetType();
        petType.setId(2);
        Map<String, Object> map = new HashMap<>();
        map.put("type", petType);

        Response response = webTarget()
                .path("/pets/{petId}")
                .resolveTemplate("petId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(map));

        assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void getPet_PetNotFound_ShouldReturnHttpStatusNotFound() {
        Response response = webTarget()
                .path("/pets/{petId}")
                .resolveTemplate("petId", 0)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getPet_PetFound_ShouldReturnFoundPet() {
        Response response = webTarget()
                .path("/pets/{petId}")
                .resolveTemplate("petId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Pet pet = response.readEntity(Pet.class);
        assertThat(pet).isNotNull().matches(
                p -> p.getName().equals("Leo") && p.getType().getName().equals("cat"));
    }

    @Test
    public void getPets_PetsFound_ShouldReturnFoundPets() {
        Response response = webTarget()
                .path("/pets")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Collection<Pet> pets = response.readEntity(
                new GenericType<Collection<Pet>>() {});
        assertThat(pets).hasSize(5);
    }
}
