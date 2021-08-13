package org.javaloong.kongmink.petclinic.customers.blueprint.impl.web;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.util.EntityManagerProvider;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.javaloong.kongmink.petclinic.customers.blueprint.impl.util.BeanMapper;
import org.javaloong.kongmink.petclinic.customers.blueprint.impl.util.ModelMapperBeanMapper;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@RunWith(JUnit4.class)
public abstract class WebResourceTestSupport {

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance("customers");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());

    private JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
    private ValidationExceptionMapper validationExceptionMapper = new ValidationExceptionMapper();
    private BeanMapper beanMapper = new ModelMapperBeanMapper();

    public JacksonJsonProvider jacksonJsonProvider() {
        return jacksonJsonProvider;
    }

    public ValidationExceptionMapper validationExceptionMapper() {
        return validationExceptionMapper;
    }

    public BeanMapper getBeanMapper() {
        return beanMapper;
    }

    public WebTarget target(String uri) {
        return ClientBuilder.newClient()
                .register(jacksonJsonProvider)
                .target(uri);
    }
}
