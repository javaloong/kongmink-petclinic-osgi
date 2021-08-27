package org.javaloong.kongmink.petclinic.visits.ds.impl.web;

import org.javaloong.kongmink.petclinic.visits.model.Visit;
import org.javaloong.kongmink.petclinic.visits.service.VisitService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JSONRequired;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collection;
import java.util.Map;

@Component(service = VisitResource.class)
@JaxrsResource
@JSONRequired
@Path("/visits")
@Produces(MediaType.APPLICATION_JSON)
public class VisitResource {

    private VisitService visitService;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addVisit(@Valid Visit visit) {
        this.visitService.saveVisit(visit);
        return Response.status(Status.CREATED).entity(visit).build();
    }

    @PUT
    @Path("/{visitId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateVisit(@PathParam("visitId") int visitId, @Valid Visit visit) {
        Visit currentVisit = this.visitService.findVisitById(visitId);
        if (currentVisit == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        currentVisit.setDescription(visit.getDescription());
        this.visitService.saveVisit(currentVisit);
        return Response.noContent().entity(currentVisit).build();
    }

    @DELETE
    @Path("/{visitId}")
    public Response deleteVisit(@PathParam("visitId") int visitId) {
        Visit visit = this.visitService.findVisitById(visitId);
        if (visit == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        this.visitService.deleteVisit(visit);
        return Response.noContent().build();
    }

    @GET
    @Path("/{visitId}")
    public Response getVisit(@PathParam("visitId") int visitId) {
        Visit visit = this.visitService.findVisitById(visitId);
        if (visit == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(visit).build();
    }

    @GET
    @Path("")
    public Response getAllVisits() {
        Collection<Visit> visits = this.visitService.findAllVisits();
        if (visits.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(visits).build();
    }

    @Reference
    public void setVisitService(VisitService visitService) {
        this.visitService = visitService;
    }
}
