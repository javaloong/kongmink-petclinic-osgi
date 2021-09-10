package org.javaloong.kongmink.petclinic.customers.internal.web;

import org.javaloong.kongmink.petclinic.customers.internal.util.BeanMapper;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.service.PetService;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
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

    private PetService petService;
    private BeanMapper beanMapper;

    @Path("/pettypes")
    @GET
    public Response getPetTypes() {
        return Response.ok(this.petService.findPetTypes()).build();
    }

    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addPet(@Valid Pet pet) {
        petService.savePet(pet);
        return Response.status(Status.CREATED).entity(pet).build();
    }

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

    @Path("/{petId}")
    @GET
    public Response getPet(@PathParam("petId") int petId) {
        Pet pet = this.petService.findPetById(petId);
        if (pet == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(pet).build();
    }

    @Path("/")
    @GET
    public Response getPets() {
        Collection<Pet> pets = this.petService.findAllPets();
        if (pets.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(pets).build();
    }

    @Reference
    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    @Reference
    public void setBeanMapper(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }
}
