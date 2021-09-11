package org.javaloong.kongmink.petclinic.vets.internal.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.vets.internal.service.VetServiceImpl;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
import org.javaloong.kongmink.petclinic.vets.model.Vet;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"vetData.xml"})
public class VetResourceIT extends WebResourceTestSupport {

    @ClassRule
    public static JaxrsServerProvider<VetResource> server = JaxrsServerProvider
            .jaxrsServer(VetResource.class, () -> {
                VetServiceImpl vetService = new VetServiceImpl();
                vetService.setJpaTemplate(jpaTemplateSpy());
                VetResource resource = new VetResource();
                resource.setVetService(vetService);
                return resource;
            })
            .withProvider(jacksonJsonProvider())
            .withProvider(validationExceptionMapper());

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet(value = "createVetDataExpected.xml", compareOperation = CompareOperation.CONTAINS)
    public void addVet_ShouldAddVetAndReturnHttpStatusCreated() {
        Vet vet = new Vet();
        vet.setFirstName("Leo");
        vet.setLastName("xxx");
        Specialty specialty = new Specialty();
        specialty.setId(1);
        vet.addSpecialty(specialty);

        Response response = target(server.baseUrl())
                .path("/vets")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(vet));

        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet(value = "updateVetDataExpected.xml", compareOperation = CompareOperation.CONTAINS)
    public void updateVet_ShouldReturnHttpStatusOk() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter2");
        Specialty specialty = new Specialty();
        specialty.setId(1);
        vet.addSpecialty(specialty);

        Response response = target(server.baseUrl())
                .path("/vets/{vetId}")
                .resolveTemplate("vetId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(vet));

        assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void getVet_VetNotFound_ShouldReturnHttpStatusNotFound() {
        Response response = target(server.baseUrl())
                .path("/vets/{vetId}")
                .resolveTemplate("vetId", 0)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getVet_VetFound_ShouldReturnFoundVet() {
        Response response = target(server.baseUrl())
                .path("/vets/{vetId}")
                .resolveTemplate("vetId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Vet vet = response.readEntity(Vet.class);
        assertThat(vet).isNotNull().matches(
                p -> p.getFirstName().equals("James") && p.getLastName().equals("Carter"));
    }

    @Test
    public void getVets_VetsFound_ShouldReturnFoundVets() {
        Response response = target(server.baseUrl())
                .path("/vets")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Collection<Vet> vets = response.readEntity(new GenericType<Collection<Vet>>() {});
        assertThat(vets).hasSize(3);
    }
}
