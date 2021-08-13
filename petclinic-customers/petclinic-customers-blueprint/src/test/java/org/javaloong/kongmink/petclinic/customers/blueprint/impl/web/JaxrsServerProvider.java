package org.javaloong.kongmink.petclinic.customers.blueprint.impl.web;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature;
import org.apache.cxf.testutil.common.TestUtil;
import org.apache.cxf.validation.BeanValidationProvider;
import org.junit.rules.ExternalResource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class JaxrsServerProvider<I> extends ExternalResource {

    private final Class<I> serviceInterface;
    private final Supplier<? extends I> implementationSupplier;
    private final List<Object> providers = new LinkedList<>();
    private Server server;
    private String baseUrl;

    public static <I> JaxrsServerProvider<I> jaxrsServer(Class<I> serviceInterface, Supplier<? extends I> serviceImplementationSupplier) {
        return new JaxrsServerProvider<>(serviceInterface, serviceImplementationSupplier);
    }

    public JaxrsServerProvider(Class<I> serviceInterface, Supplier<? extends I> implementationSupplier) {
        this.serviceInterface = serviceInterface;
        this.implementationSupplier = implementationSupplier;
    }

    private JAXRSBeanValidationFeature jaxrsBeanValidationFeature() {
        BeanValidationProvider beanValidationProvider = new BeanValidationProvider();
        JAXRSBeanValidationFeature feature = new JAXRSBeanValidationFeature();
        feature.setProvider(beanValidationProvider);
        return feature;
    }

    @Override
    protected void before() {
        final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();

        baseUrl = String.format("http://localhost:%s/", TestUtil.getNewPortNumber(serviceInterface));
        factory.setAddress(baseUrl);
        factory.setProviders(providers);
        factory.setFeatures(Arrays.asList(new LoggingFeature(), jaxrsBeanValidationFeature()));
        factory.setResourceClasses(serviceInterface);
        factory.setResourceProvider(serviceInterface, new SingletonResourceProvider(implementationSupplier.get(), true));

        this.server = factory.create();
    }

    @Override
    protected void after() {
        this.server.destroy();
    }

    public String baseUrl() {
        return baseUrl;
    }

    public JaxrsServerProvider<I> withProvider(Object provider) {
        providers.add(provider);
        return this;
    }
}
