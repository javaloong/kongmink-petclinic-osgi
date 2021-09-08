package org.javaloong.kongmink.petclinic.vets.impl.web;

import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
import org.javaloong.kongmink.petclinic.vets.model.Vet;
import org.javaloong.kongmink.petclinic.vets.service.VetService;
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

    public static final String RESOURCE_NAME = "vets";

    private VetService vetService;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addVet(@Valid Vet vet) {
        this.vetService.saveVet(vet);
        return Response.status(Status.CREATED).entity(vet).build();
    }

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
        for(Specialty spec : vet.getSpecialties()) {
            currentVet.addSpecialty(spec);
        }
        this.vetService.saveVet(currentVet);
        return Response.noContent().entity(currentVet).build();
    }

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

    @GET
    @Path("/{vetId}")
    public Response getVet(@PathParam("vetId") int vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(vet).build();
    }

    @GET
    @Path("")
    public Response getAllVets() {
        Collection<Vet> vets = this.vetService.findAllVets();
        if (vets.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(vets).build();
    }

    @Reference
    public void setVetService(VetService vetService) {
        this.vetService = vetService;
    }
}
