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
package org.javaloong.kongmink.petclinic.itest.karaf;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.maven;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class KarafIntegrationTest extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        return OptionUtils.combine(super.config(),
                KarafDistributionOption.features(
                        maven().groupId("org.javaloong.kongmink").artifactId("petclinic-osgi-features")
                                .type("xml").classifier("features").versionAsInProject(),
                        "petclinic-osgi-datasource-hsqldb",
                        "petclinic-osgi-customers-ds",
                        "petclinic-osgi-vets-ds",
                        "petclinic-osgi-visits-ds",
                        "petclinic-osgi-rest-openapi",
                        "petclinic-osgi-rest-auth")
        );
    }

    @Test
    public void testCustomersApi() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-customers-api");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testCustomersDS() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-customers-ds");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testVetsApi() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-vets-api");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testVetsDS() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-vets-ds");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testVisitsApi() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-visits-api");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testVisitsDS() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-visits-ds");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testRestOpenApi() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-rest-openapi");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }

    @Test
    public void testRestAuth() {
        final Bundle bundle = findBundleByName("org.javaloong.kongmink.petclinic-osgi-rest-auth");
        assertNotNull(bundle);
        assertEquals(Bundle.ACTIVE, bundle.getState());
    }
}
