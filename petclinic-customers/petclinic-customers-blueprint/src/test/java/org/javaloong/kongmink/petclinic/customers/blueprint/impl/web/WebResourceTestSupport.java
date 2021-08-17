package org.javaloong.kongmink.petclinic.customers.blueprint.impl.web;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.util.EntityManagerProvider;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.persistence.EntityManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@RunWith(JUnit4.class)
public abstract class WebResourceTestSupport {

    private static final String PERSISTENCE_UNIT_NAME = "customers";

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance(PERSISTENCE_UNIT_NAME);

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());

    public static EntityManager em() {
        return EntityManagerProvider.em(PERSISTENCE_UNIT_NAME);
    }

    private final JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();

    public WebTarget target(String uri) {
        return ClientBuilder.newClient()
                .register(jacksonJsonProvider)
                .target(uri);
    }
}
