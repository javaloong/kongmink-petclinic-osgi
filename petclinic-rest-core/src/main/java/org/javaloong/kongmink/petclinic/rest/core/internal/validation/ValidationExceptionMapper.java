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
package org.javaloong.kongmink.petclinic.rest.core.internal.validation;

import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;

@Component
@JaxrsExtension
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        ErrorInfo errorInfo = new ErrorInfo(exception.getClass().getName(), exception.getLocalizedMessage());
        if (exception instanceof ConstraintViolationException) {
            StringBuilder messages = new StringBuilder();
            ConstraintViolationException constraint = (ConstraintViolationException)exception;
            for (ConstraintViolation<?> violation: constraint.getConstraintViolations()) {
                messages.append(buildErrorMessage(violation)).append('\n');
            }
            errorInfo = new ErrorInfo(exception.getClass().getName(), messages.toString());
        }
        return status(Response.Status.BAD_REQUEST).entity(errorInfo).build();
    }

    protected String buildErrorMessage(ConstraintViolation<?> violation) {
        return "Value " + (violation.getInvalidValue() != null ? "'" + violation.getInvalidValue().toString() + "'" : "(null)") + " of " + violation.getRootBeanClass().getSimpleName() + "." + violation.getPropertyPath() + ": " + violation.getMessage();
    }

    private static class ErrorInfo {
        public final String className;
        public final String exMessage;

        public ErrorInfo(String className, String exMessage) {
            this.className = className;
            this.exMessage = exMessage;
        }
    }
}
