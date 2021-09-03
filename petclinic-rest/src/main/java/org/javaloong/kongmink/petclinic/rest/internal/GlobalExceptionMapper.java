package org.javaloong.kongmink.petclinic.rest.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;

/**
 * Trap exceptions.
 *
 * @author Xu Cheng
 */
@Component
@JaxrsExtension
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception e) {
        ObjectMapper mapper = new ObjectMapper();
        ErrorInfo errorInfo = new ErrorInfo(e);
        String jsonString = "{}";
        try {
            jsonString = mapper.writeValueAsString(errorInfo);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return status(Response.Status.BAD_REQUEST).entity(jsonString).build();
    }

    private static class ErrorInfo {
        public final String className;
        public final String exMessage;

        public ErrorInfo(Exception ex) {
            this.className = ex.getClass().getName();
            this.exMessage = ex.getLocalizedMessage();
        }
    }
}
