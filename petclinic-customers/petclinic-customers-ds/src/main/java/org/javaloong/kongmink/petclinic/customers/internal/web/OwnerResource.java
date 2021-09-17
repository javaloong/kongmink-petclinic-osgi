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
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.service.OwnerService;
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

@Component(service = OwnerResource.class)
@JaxrsResource
@JaxrsName(OwnerResource.RESOURCE_NAME)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@JSONRequired
@Path("/owners")
@Produces(MediaType.APPLICATION_JSON)
public class OwnerResource {

    public static final String RESOURCE_NAME = "owner";

    private final OwnerService ownerService;
    private final BeanMapper beanMapper;

    @Activate
    public OwnerResource(@Reference OwnerService ownerService, @Reference BeanMapper beanMapper) {
        this.ownerService = ownerService;
        this.beanMapper = beanMapper;
    }

    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response addOwner(@Valid Owner owner) {
        ownerService.saveOwner(owner);
        return Response.ok(owner).status(Status.CREATED).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/{ownerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public Response updateOwner(@PathParam("ownerId") int ownerId, Map<String, Object> attributes) {
        Owner owner = ownerService.findOwnerById(ownerId);
        if (owner == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        beanMapper.map(attributes, owner);
        ownerService.saveOwner(owner);
        return Response.status(Status.NO_CONTENT).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @Path("/{ownerId}")
    @DELETE
    public Response deleteOwner(@PathParam("ownerId") int ownerId) {
        Owner owner = ownerService.findOwnerById(ownerId);
        if (owner == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        ownerService.deleteOwner(owner);
        return Response.status(Status.NO_CONTENT).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @GET
    @Path("/{ownerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOwner(@PathParam("ownerId") int ownerId) {
        Owner owner = this.ownerService.findOwnerById(ownerId);
        if (owner == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(owner).status(Status.OK).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @GET
    @Path("/")
    public Response getOwners() {
        Collection<Owner> owners = this.ownerService.findAllOwners();
        if (owners.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(owners).status(Status.OK).build();
    }

    @RequiresRoles(Roles.OWNER_ADMIN)
    @GET
    @Path("/*/lastname/{lastName}")
    public Response getOwnersList(@PathParam("lastName") String ownerLastName) {
        if (ownerLastName == null) {
            ownerLastName = "";
        }
        Collection<Owner> owners = this.ownerService.findAllOwnersByLastName(ownerLastName);
        if (owners.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(owners).status(Status.OK).build();
    }
}
