/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaloong.kongmink.petclinic.vets.internal.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.vets.internal.service.SpecialtyServiceImpl;
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
                return new SpecialtyResource(specialtyService);
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
        Collection<Specialty> specialties = response.readEntity(new GenericType<Collection<Specialty>>() {
        });
        assertThat(specialties).hasSize(3);
    }
}
