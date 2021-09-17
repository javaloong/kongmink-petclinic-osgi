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
package org.javaloong.kongmink.petclinic.vets.internal.web;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.javaloong.kongmink.petclinic.vets.internal.security.Roles;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
import org.javaloong.kongmink.petclinic.vets.model.Vet;
import org.javaloong.kongmink.petclinic.vets.service.VetService;
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

@Component(service = VetResource.class)
@JaxrsResource
@JaxrsName(VetResource.RESOURCE_NAME)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@JSONRequired
@Path("/vets")
@Produces(MediaType.APPLICATION_JSON)
public class VetResource {

    public static final String RESOURCE_NAME = "vet";

    private final VetService vetService;

    @Activate
    public VetResource(@Reference VetService vetService) {
        this.vetService = vetService;
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addVet(@Valid Vet vet) {
        this.vetService.saveVet(vet);
        return Response.status(Status.CREATED).entity(vet).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @PUT
    @Path("/{vetId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateVet(@PathParam("vetId") int vetId, @Valid Vet vet) {
        Vet currentVet = this.vetService.findVetById(vetId);
        if (currentVet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        currentVet.setFirstName(vet.getFirstName());
        currentVet.setLastName(vet.getLastName());
        currentVet.clearSpecialties();
        for (Specialty spec : vet.getSpecialties()) {
            currentVet.addSpecialty(spec);
        }
        this.vetService.saveVet(currentVet);
        return Response.noContent().entity(currentVet).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @DELETE
    @Path("/{vetId}")
    public Response deleteVet(@PathParam("vetId") int vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        this.vetService.deleteVet(vet);
        return Response.noContent().build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @GET
    @Path("/{vetId}")
    public Response getVet(@PathParam("vetId") int vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(vet).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @GET
    @Path("")
    public Response getAllVets() {
        Collection<Vet> vets = this.vetService.findAllVets();
        if (vets.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(vets).build();
    }
}
