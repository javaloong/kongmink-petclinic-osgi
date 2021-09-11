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
package org.javaloong.kongmink.petclinic.test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.karaf.itests.KarafTestSupport;
import org.javaloong.kongmink.petclinic.visits.model.Visit;
import org.javaloong.kongmink.petclinic.visits.service.VisitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VisitsDSKarafIT extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        Option[] options = new Option[]{
                // features
                KarafDistributionOption.features(
                        maven().groupId("org.javaloong.kongmink").artifactId("petclinic-osgi-features")
                                .type("xml").classifier("features").versionAsInProject()),
                // bundles
                mavenBundle().groupId("org.apache.aries.spec").artifactId("org.apache.aries.javax.jax.rs-api")
                        .version("1.0.1")
        };
        return Stream.of(super.config(), options).flatMap(Stream::of).toArray(Option[]::new);
    }

    private String getLocation() throws Exception {
        return "http://localhost:" + getHttpPort() + "/api/";
    }

    @Test
    public void test() throws Exception {
        // install features
        installAndAssertFeature("petclinic-osgi-datasource-h2");
        installAndAssertFeature("petclinic-osgi-visits-api");
        installAndAssertFeature("petclinic-osgi-visits-ds");

        // check the provider service
        assertServiceAvailable(VisitService.class);

        // get the visit service
        VisitService visitService = getOsgiService(VisitService.class);

        // use the visit service and assert state or result
        assertThat(visitService.findAllVisits(), hasSize(0));
        Visit visit = new Visit();
        visit.setDate(new Date());
        visit.setDescription("hello");
        visit.setPetId(1);
        visitService.saveVisit(visit);

        // use the visit resource and assert state or result
        ClientBuilder clientBuilder = getOsgiService(ClientBuilder.class);
        WebTarget webTarget = clientBuilder.build()
                .register(new JacksonJsonProvider())
                .target(getLocation());
        Collection<Visit> visits = webTarget
                .path("/visits")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<Collection<Visit>>() {});
        assertThat(visits, hasSize(1));
        assertThat(visits, hasItem(anyOf(hasProperty("description", is("hello")),
                hasProperty("petId", is(1)))));
    }
}
