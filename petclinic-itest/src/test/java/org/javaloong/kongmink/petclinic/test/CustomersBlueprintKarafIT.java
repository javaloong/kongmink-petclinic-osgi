package org.javaloong.kongmink.petclinic.test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.karaf.itests.KarafTestSupport;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.service.OwnerService;
import org.javaloong.kongmink.petclinic.customers.service.PetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.maven;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CustomersBlueprintKarafIT extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        Option[] options = new Option[]{
                KarafDistributionOption.features(
                        maven().groupId("org.javaloong.kongmink").artifactId("petclinic-osgi-features")
                                .type("xml").classifier("features").versionAsInProject())
        };
        return Stream.of(super.config(), options).flatMap(Stream::of).toArray(Option[]::new);
    }

    private String getLocation() throws Exception {
        return "http://localhost:" + getHttpPort() + "/cxf/customers/";
    }

    @Test
    public void test() throws Exception {
        // install features
        installAndAssertFeature("petclinic-osgi-datasource-h2");
        installAndAssertFeature("petclinic-osgi-customers-api");
        installAndAssertFeature("petclinic-osgi-customers-blueprint");

        // check the provider service
        assertServiceAvailable(OwnerService.class);
        assertServiceAvailable(PetService.class);

        // get the owner service
        OwnerService ownerService = getOsgiService(OwnerService.class);

        // use the owner service and assert state or result
        assertThat(ownerService.findAllOwners(), hasSize(0));
        Owner owner = new Owner();
        owner.setFirstName("fn3");
        owner.setLastName("ln3");
        owner.setAddress("addr3");
        owner.setCity("city3");
        owner.setTelephone("22222222");
        ownerService.saveOwner(owner);

        // use the owner resource and assert state or result
        WebClient client = WebClient.create(getLocation(),
                Collections.singletonList(new JacksonJsonProvider()));
        @SuppressWarnings("unchecked")
        Collection<Owner> owners = (Collection<Owner>) client
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .path("api/owners")
                .getCollection(Owner.class);
        assertThat(owners, hasSize(1));
        assertThat(owners, hasItem(anyOf(hasProperty("firstName", is("fn3")),
                hasProperty("lastName", is("ln3")))));
    }
}
