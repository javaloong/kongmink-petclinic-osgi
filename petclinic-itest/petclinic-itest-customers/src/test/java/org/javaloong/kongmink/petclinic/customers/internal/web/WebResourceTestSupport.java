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
                mavenBundle("org.apache.commons", "commons-lang3").versionAsInProject(),
                mavenBundle("org.modelmapper", "modelmapper").versionAsInProject(),

                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-bean-validator").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-rest").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-rest-core").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-customers-api").versionAsInProject(),
                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-customers-ds").versionAsInProject(),

                mavenBundle("org.javaloong.kongmink", "petclinic-osgi-itest-common").versionAsInProject()
        );
    }
}
