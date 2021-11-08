package org.javaloong.kongmink.petclinic.vets.internal.web;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.javaloong.kongmink.petclinic.itest.common.DBUnitTestSupport;
import org.ops4j.pax.exam.Option;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

public class WebResourceTestSupport extends DBUnitTestSupport {

    @Inject
    ClientBuilder clientBuilder;

    public WebTarget webTarget() {
        return webTarget("http://localhost:8080/api/");
    }

    public WebTarget webTarget(String uri) {
        return clientBuilder.build()
                .register(new JacksonJsonProvider())
                .target(uri);
    }

    @Override
    protected Option testBundles() {
        return composite(
                mavenBundle("org.apache.geronimo.specs", "geronimo-validation_2.0_spec", "1.1"),
                mavenBundle("org.glassfish", "jakarta.el").versionAsInProject(),

                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-bean-validator").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-rest").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-vets-api").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-vets-ds").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-itest-common").versionAsInProject()
        );
    }
}
