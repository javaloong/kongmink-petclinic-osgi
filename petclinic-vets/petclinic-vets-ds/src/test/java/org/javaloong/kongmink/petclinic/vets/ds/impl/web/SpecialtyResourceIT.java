package org.javaloong.kongmink.petclinic.vets.ds.impl.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.vets.impl.service.SpecialtyServiceImpl;
import org.javaloong.kongmink.petclinic.vets.impl.web.SpecialtyResource;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
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
public class SpecialtyResourceIT extends WebResourceTestSupport {

    @ClassRule
    public static JaxrsServerProvider<SpecialtyResource> server = JaxrsServerProvider
            .jaxrsServer(SpecialtyResource.class, () -> {
                SpecialtyServiceImpl specialtyService = new SpecialtyServiceImpl();
                specialtyService.setJpaTemplate(jpaTemplateSpy());
                SpecialtyResource resource = new SpecialtyResource();
                resource.setSpecialtyService(specialtyService);
                return resource;
            })
            .withProvider(jacksonJsonProvider())
            .withProvider(validationExceptionMapper());

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet(value = "createSpecialtyDataExpected.xml", compareOperation = CompareOperation.CONTAINS)
    public void addSpecialty_ShouldAddSpecialtyAndReturnHttpStatusCreated() {
        Specialty specialty = new Specialty();
        specialty.setName("xxx");

        Response response = target(server.baseUrl())
                .path("/specialties")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(specialty));

        assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet(value = "updateSpecialtyDataExpected.xml", compareOperation = CompareOperation.CONTAINS)
    public void updateSpecialty_ShouldReturnHttpStatusOk() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology1");

        Response response = target(server.baseUrl())
                .path("/specialties/{specialtyId}")
                .resolveTemplate("specialtyId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(specialty));

        assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void getSpecialty_SpecialtyNotFound_ShouldReturnHttpStatusNotFound() {
        Response response = target(server.baseUrl())
                .path("/specialties/{specialtyId}")
                .resolveTemplate("specialtyId", 0)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getSpecialty_SpecialtyFound_ShouldReturnFoundSpecialty() {
        Response response = target(server.baseUrl())
                .path("/specialties/{specialtyId}")
                .resolveTemplate("specialtyId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Specialty specialty = response.readEntity(Specialty.class);
        assertThat(specialty).isNotNull().matches(p -> p.getName().equals("radiology"));
    }

    @Test
    public void getSpecialties_SpecialtiesFound_ShouldReturnFoundSpecialties() {
        Response response = target(server.baseUrl())
                .path("/specialties")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Collection<Specialty> specialties = response.readEntity(new GenericType<Collection<Specialty>>() {});
        assertThat(specialties).hasSize(3);
    }
}
