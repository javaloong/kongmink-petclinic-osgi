package org.javaloong.kongmink.petclinic.customers.blueprint.impl.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository.OwnerRepositoryImpl;
import org.javaloong.kongmink.petclinic.customers.blueprint.impl.service.OwnerServiceImpl;
import org.javaloong.kongmink.petclinic.customers.blueprint.impl.util.ModelMapperBeanMapper;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"ownerData.xml", "petData.xml"})
public class OwnerResourceIT extends WebResourceTestSupport {

    @ClassRule
    public static JaxrsServerProvider<OwnerResource> server = JaxrsServerProvider
            .jaxrsServer(OwnerResource.class, () -> new OwnerResource(
                    new OwnerServiceImpl(new OwnerRepositoryImpl(em())), new ModelMapperBeanMapper()))
            .withProvider(jacksonJsonProvider())
            .withProvider(validationExceptionMapper());

    @Test
    @DataSet(transactional = true)
    public void addOwner_ShouldReturnValidationErrors() {
        Owner owner = new Owner();
        owner.setFirstName("fn3");
        owner.setLastName("ln3");
        owner.setAddress("addr3");
        owner.setCity("city3");

        Response response = target(server.baseUrl())
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(owner));

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("createOwnerDataExpected.xml")
    public void addOwner_ShouldAddOwnerAndReturnHttpStatusCreated() {
        Owner owner = new Owner();
        owner.setFirstName("fn3");
        owner.setLastName("ln3");
        owner.setAddress("addr3");
        owner.setCity("city3");
        owner.setTelephone("222222");

        Response response = target(server.baseUrl())
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(owner));

        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    public void updateOwner_ShouldReturnValidationErrors() {
        Map<String, Object> map = new HashMap<>();
        map.put("telephone", "xxx");

        Response response = target(server.baseUrl())
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(map));

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("updateOwnerDataExpected.xml")
    public void updateOwner_ShouldReturnHttpStatusOk() {
        Map<String, Object> map = new HashMap<>();
        map.put("telephone", "222222");

        Response response = target(server.baseUrl())
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(map));

        assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void getOwner_OwnerNotFound_ShouldReturnHttpStatusNotFound() {
        Response response = target(server.baseUrl())
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 0)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getOwner_OwnerFound_ShouldReturnFoundOwner() {
        Response response = target(server.baseUrl())
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Owner owner = response.readEntity(Owner.class);
        assertThat(owner).isNotNull().matches(
                p -> p.getFirstName().equals("George") && p.getLastName().equals("Franklin"));
    }

    @Test
    public void getOwners_OwnersFound_ShouldReturnFoundOwners() {
        Response response = target(server.baseUrl())
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Collection<Owner> owners = response.readEntity(
                new GenericType<Collection<Owner>>() {
                });
        assertThat(owners).hasSize(2);
    }

    @Test
    public void getOwnersList_OwnersListNotFound_ShouldReturnFoundOwnersList() {
        Response response = target(server.baseUrl())
                .path("/owners/*/lastname/{lastName}")
                .resolveTemplate("lastName", "0")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getOwnersList_OwnersListFound_ShouldReturnFoundOwnersList() {
        Response response = target(server.baseUrl())
                .path("/owners/*/lastname/{lastName}")
                .resolveTemplate("lastName", "Franklin")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        Collection<Owner> owners = response.readEntity(
                new GenericType<Collection<Owner>>() {
                });
        assertThat(owners).hasSize(1);
    }
}
