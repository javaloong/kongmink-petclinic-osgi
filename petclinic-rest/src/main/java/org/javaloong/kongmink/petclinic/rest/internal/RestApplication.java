package org.javaloong.kongmink.petclinic.rest.internal;

import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationBase;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;

import javax.ws.rs.core.Application;

/**
 * The JAX-RS application for the rest JAX-RS resources.
 *
 * @author Xu Cheng
 */
@Component(service = Application.class,
        property = {"servlet.init.hide-service-list-page=true"})
@JaxrsName(RESTConstants.JAX_RS_NAME)
@JaxrsApplicationBase("api")
public class RestApplication extends Application {
}
