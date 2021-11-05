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
package org.javaloong.kongmink.petclinic.customers.internal.web;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"ownerData.xml", "petData.xml"})
public class OwnerResourceIT extends WebResourceTestSupport {

    @Test
    @DataSet(transactional = true)
    public void addOwner_ShouldReturnValidationErrors() {
        Owner owner = new Owner();
        owner.setFirstName("fn3");
        owner.setLastName("ln3");
        owner.setAddress("addr3");
        owner.setCity("city3");

        Response response = webTarget()
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(owner));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
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

        Response response = webTarget()
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(owner));

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    public void updateOwner_ShouldReturnValidationErrors() {
        Map<String, Object> map = new HashMap<>();
        map.put("telephone", "xxx");

        Response response = webTarget()
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(map));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("updateOwnerDataExpected.xml")
    public void updateOwner_ShouldReturnHttpStatusOk() {
        Map<String, Object> map = new HashMap<>();
        map.put("telephone", "222222");

        Response response = webTarget()
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(map));

        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void getOwner_OwnerNotFound_ShouldReturnHttpStatusNotFound() {
        Response response = webTarget()
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 0)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getOwner_OwnerFound_ShouldReturnFoundOwner() {
        Response response = webTarget()
                .path("/owners/{ownerId}")
                .resolveTemplate("ownerId", 1)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Owner owner = response.readEntity(Owner.class);
        assertThat(owner).isNotNull().matches(
                p -> p.getFirstName().equals("George") && p.getLastName().equals("Franklin"));
    }

    @Test
    public void getOwners_OwnersFound_ShouldReturnFoundOwners() {
        Response response = webTarget()
                .path("/owners")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Collection<Owner> owners = response.readEntity(
                new GenericType<Collection<Owner>>() {
                });
        assertThat(owners).hasSize(2);
    }

    @Test
    public void getOwnersList_OwnersListNotFound_ShouldReturnFoundOwnersList() {
        Response response = webTarget()
                .path("/owners/*/lastname/{lastName}")
                .resolveTemplate("lastName", "0")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void getOwnersList_OwnersListFound_ShouldReturnFoundOwnersList() {
        Response response = webTarget()
                .path("/owners/*/lastname/{lastName}")
                .resolveTemplate("lastName", "Franklin")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        Collection<Owner> owners = response.readEntity(
                new GenericType<Collection<Owner>>() {
                });
        assertThat(owners).hasSize(1);
    }
}
