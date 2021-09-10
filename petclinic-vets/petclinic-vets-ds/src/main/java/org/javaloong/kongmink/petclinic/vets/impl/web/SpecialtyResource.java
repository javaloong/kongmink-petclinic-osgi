package org.javaloong.kongmink.petclinic.vets.impl.web;

import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
import org.javaloong.kongmink.petclinic.vets.service.SpecialtyService;
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

    public static final String RESOURCE_NAME = "specialties";

    private SpecialtyService specialtyService;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpecialty(@Valid Specialty specialty) {
        this.specialtyService.saveSpecialty(specialty);
        return Response.status(Status.CREATED).entity(specialty).build();
    }

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

    @GET
    @Path("/{specialtyId}")
    public Response getSpecialty(@PathParam("specialtyId") int specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (specialty == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(specialty).build();
    }

    @GET
    @Path("")
    public Response getAllSpecialties() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        if (specialties.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(specialties).build();
    }

    @Reference
    public void setSpecialtyService(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }
}
