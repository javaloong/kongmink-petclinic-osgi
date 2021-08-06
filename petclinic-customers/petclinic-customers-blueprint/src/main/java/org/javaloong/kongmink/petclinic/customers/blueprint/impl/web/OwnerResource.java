/*
 * Copyright 2012-2019 the original author or authors.
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
package org.javaloong.kongmink.petclinic.customers.blueprint.impl.web;

import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.service.OwnerService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/owners")
@Produces(MediaType.APPLICATION_JSON)
public class OwnerResource {

    private final OwnerService ownerService;

    public OwnerResource(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void add(Owner owner) {
        ownerService.saveOwner(owner);
    }

    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public void update(@PathParam("id") int id, Owner request) {
        Owner owner = ownerService.findOwnerById(id);
        ownerService.saveOwner(owner);
    }

    @Path("/{id}")
    @DELETE
    public void remove(@PathParam("id") int id) {
        Owner owner = ownerService.findOwnerById(id);
        ownerService.deleteOwner(owner);
    }

    @Path("/{id}")
    @GET
    public Owner get(@PathParam("id") int id) {
        return ownerService.findOwnerById(id);
    }

    @Path("")
    @GET
    public Collection<Owner> list() {
        return ownerService.findAllOwners();
    }
}
