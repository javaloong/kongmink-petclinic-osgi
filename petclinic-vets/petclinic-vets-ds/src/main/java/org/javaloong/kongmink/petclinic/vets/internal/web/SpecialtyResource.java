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
import org.javaloong.kongmink.petclinic.vets.service.SpecialtyService;
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

@Component(service = SpecialtyResource.class)
@JaxrsResource
@JaxrsName(SpecialtyResource.RESOURCE_NAME)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@JSONRequired
@Path("/specialties")
@Produces(MediaType.APPLICATION_JSON)
public class SpecialtyResource {

    public static final String RESOURCE_NAME = "specialty";

    private final SpecialtyService specialtyService;

    @Activate
    public SpecialtyResource(@Reference SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpecialty(@Valid Specialty specialty) {
        this.specialtyService.saveSpecialty(specialty);
        return Response.status(Status.CREATED).entity(specialty).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @PUT
    @Path("/{specialtyId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSpecialty(@PathParam("specialtyId") int specialtyId, @Valid Specialty specialty) {
        Specialty currentSpecialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (currentSpecialty == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        currentSpecialty.setName(specialty.getName());
        this.specialtyService.saveSpecialty(currentSpecialty);
        return Response.noContent().entity(currentSpecialty).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @DELETE
    @Path("/{specialtyId}")
    public Response deleteSpecialty(@PathParam("specialtyId") int specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        this.specialtyService.deleteSpecialty(specialty);
        return Response.noContent().build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @GET
    @Path("/{specialtyId}")
    public Response getSpecialty(@PathParam("specialtyId") int specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(specialty).build();
    }

    @RequiresRoles(Roles.VET_ADMIN)
    @GET
    @Path("")
    public Response getAllSpecialties() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        if (specialties.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(specialties).build();
    }
}
