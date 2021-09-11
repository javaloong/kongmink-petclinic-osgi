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
package org.javaloong.kongmink.petclinic.visits.internal.web;

import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.javaloong.kongmink.petclinic.visits.model.Visit;
import org.javaloong.kongmink.petclinic.visits.service.VisitService;
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

@Component(service = VisitResource.class)
@JaxrsResource
@JaxrsName(VisitResource.RESOURCE_NAME)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@JSONRequired
@Path("/visits")
@Produces(MediaType.APPLICATION_JSON)
public class VisitResource {

    public static final String RESOURCE_NAME = "visit";

    private final VisitService visitService;

    @Activate
    public VisitResource(@Reference VisitService visitService) {
        this.visitService = visitService;
    }

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
}
