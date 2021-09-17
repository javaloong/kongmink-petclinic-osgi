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

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.javaloong.kongmink.petclinic.customers.internal.security.Roles;
import org.javaloong.kongmink.petclinic.customers.internal.util.BeanMapper;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.service.PetService;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.Map;

@Component(service = PetResource.class)
@JaxrsResource
@JaxrsName(PetResource.RESOURCE_NAME)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@JSONRequired
@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {

    public static final String RESOURCE_NAME = "pet";

    private final PetService petService;
    private final BeanMapper beanMapper;

    @Activate
    public PetResource(@Reference PetService petService, @Reference BeanMapper beanMapper) {
        this.petService = petService;
        this.beanMapper = beanMapper;
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/pettypes")
    @GET
    public Response getPetTypes() {
        return Response.ok(this.petService.findPetTypes()).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addPet(@Valid Pet pet) {
        petService.savePet(pet);
        return Response.status(Status.CREATED).entity(pet).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/{petId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response updatePet(@PathParam("petId") int petId, Map<String, Object> attributes) {
        Pet pet = petService.findPetById(petId);
        if (pet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        beanMapper.map(attributes, pet);
        petService.savePet(pet);
        return Response.noContent().entity(pet).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/{petId}")
    @DELETE
    public Response deletePet(@PathParam("petId") int petId) {
        Pet pet = petService.findPetById(petId);
        if (pet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        petService.deletePet(pet);
        return Response.noContent().build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/{petId}")
    @GET
    public Response getPet(@PathParam("petId") int petId) {
        Pet pet = this.petService.findPetById(petId);
        if (pet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(pet).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/")
    @GET
    public Response getPets() {
        Collection<Pet> pets = this.petService.findAllPets();
        if (pets.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(pets).build();
    }
}
