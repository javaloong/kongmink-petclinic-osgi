package org.javaloong.kongmink.petclinic.rest.openapi.internal;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Open Api Jaxrs Configuration")
@interface OpenApiJaxrsConfiguration {

    String description() default "Service REST API";

    String title() default "My Service";

    String contact() default "me@me.com";
}

@Component(service = OpenAPI.class)
@Designate(ocd = OpenApiJaxrsConfiguration.class)
@JaxrsApplicationSelect("(" + JaxrsWhiteboardConstants.JAX_RS_NAME + "=" + RESTConstants.JAX_RS_NAME + ")")
public class RestOpenApi extends OpenAPI {

    private OpenApiJaxrsConfiguration openApiJaxrsConfiguration;

    @Activate
    public RestOpenApi(OpenApiJaxrsConfiguration openApiJaxrsConfiguration) {
        super();

        info(new Info()
                .title(openApiJaxrsConfiguration.title())
                .description(openApiJaxrsConfiguration.description())
                .contact(new Contact()
                        .email(openApiJaxrsConfiguration.contact())));
    }
}
