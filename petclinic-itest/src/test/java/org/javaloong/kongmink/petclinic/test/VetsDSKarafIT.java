package org.javaloong.kongmink.petclinic.test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.karaf.itests.KarafTestSupport;
import org.javaloong.kongmink.petclinic.vets.model.Specialty;
import org.javaloong.kongmink.petclinic.vets.model.Vet;
import org.javaloong.kongmink.petclinic.vets.service.SpecialtyService;
import org.javaloong.kongmink.petclinic.vets.service.VetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
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
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.maven;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VetsDSKarafIT extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        Option[] options = new Option[]{
                // features
                KarafDistributionOption.features(
                        maven().groupId("org.javaloong.kongmink").artifactId("petclinic-osgi-features")
                                .type("xml").classifier("features").versionAsInProject()),
                // bundles
                CoreOptions.mavenBundle().groupId("org.apache.aries.spec").artifactId("org.apache.aries.javax.jax.rs-api").version("1.0.1")
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
        installAndAssertFeature("petclinic-osgi-vets-api");
        installAndAssertFeature("petclinic-osgi-vets-ds");

        // check the provider service
        assertServiceAvailable(VetService.class);
        assertServiceAvailable(SpecialtyService.class);

        // get the specialty service
        SpecialtyService specialtyService = getOsgiService(SpecialtyService.class);

        // use the specialty service and assert state or result
        assertThat(specialtyService.findAllSpecialties(), hasSize(0));
        Specialty specialty = new Specialty();
        specialty.setName("xxx");
        specialtyService.saveSpecialty(specialty);

        // get the vet service
        VetService vetService = getOsgiService(VetService.class);

        // use the vet service and assert state or result
        assertThat(vetService.findAllVets(), hasSize(0));
        Vet vet = new Vet();
        vet.setFirstName("fn1");
        vet.setLastName("ln1");
        vet.addSpecialty(specialty);
        vetService.saveVet(vet);

        // use the vet resource and assert state or result
        ClientBuilder clientBuilder = getOsgiService(ClientBuilder.class);
        WebTarget webTarget = clientBuilder.build()
                .register(new JacksonJsonProvider())
                .target(getLocation());
        Collection<Vet> vets = webTarget
                .path("/vets")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<Collection<Vet>>() {});
        assertThat(vets, hasSize(1));
        assertThat(vets, hasItem(anyOf(hasProperty("firstName", is("fn1")),
                hasProperty("specialties", hasItem(hasProperty("id", is(1)))))));
    }
}
