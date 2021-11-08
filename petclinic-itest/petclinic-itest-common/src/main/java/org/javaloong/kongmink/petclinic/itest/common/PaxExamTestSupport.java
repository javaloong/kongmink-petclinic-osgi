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
package org.javaloong.kongmink.petclinic.itest.common;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.inject.Inject;
import java.io.File;

import static org.ops4j.pax.exam.Constants.START_LEVEL_SYSTEM_BUNDLES;
import static org.ops4j.pax.exam.Constants.START_LEVEL_TEST_BUNDLE;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.configurationFolder;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class PaxExamTestSupport {

    @Inject
    BundleContext bundleContext;

    public static <T> T getService(BundleContext bundleContext, Class<T> type) {
        ServiceReference<T> serviceReference = bundleContext.getServiceReference(type);
        return bundleContext.getService(serviceReference);
    }

    public <T> T getService(Class<T> type) {
        return getService(bundleContext, type);
    }

    @Configuration
    public Option[] config() {
        return new Option[]{
                baseOptions(),
                hsqldb(),
                ariesJpa(),
                hibernate(),
                ariesJaxRSWhiteboard(),
                ariesJaxRSWhiteboardJackson(),
                testBundles(),

                // Felix config admin
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin")
                        .versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
                // Felix coordinator
                mavenBundle("org.apache.felix", "org.apache.felix.coordinator")
                        .versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES),
                // Felix scr
                mavenBundle("org.apache.felix", "org.apache.felix.scr")
                        .versionAsInProject().startLevel(START_LEVEL_SYSTEM_BUNDLES)
        };
    }

    protected Option baseOptions() {
        return composite(
                // Settings for the OSGi 4.3 Weaving
                // By default, we will not weave any classes. Change this setting to include classes
                // that you application needs to have woven.
                systemProperty("org.apache.aries.proxy.weaving.enabled").value("none"),
                // This gives a fast fail when any bundle is unresolved
                systemProperty("pax.exam.osgi.unresolved.fail").value("true"),
                frameworkStartLevel(START_LEVEL_TEST_BUNDLE),
                workingDirectory("target/pax-exam"),
                logback(),
                junit(),
                configurationFolder(new File("src/test/resources/config"))
        );
    }

    protected Option logback() {
        return composite(
                systemProperty("logback.configurationFile").value("src/test/resources/logback.xml"),
                url("link:classpath:META-INF/links/org.slf4j.api.link")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                url("link:classpath:META-INF/links/ch.qos.logback.core.link")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                url("link:classpath:META-INF/links/ch.qos.logback.classic.link")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES)
        );
    }

    protected Option junit() {
        return composite(
                junitBundles(),
                mavenBundle("org.assertj", "assertj-core").versionAsInProject(),
                mavenBundle("org.awaitility", "awaitility").versionAsInProject()
        );
    }

    protected abstract Option testBundles();

    protected Option ariesJaxRSWhiteboardJackson() {
        return composite(
                mavenBundle("com.fasterxml.jackson.core", "jackson-core", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.core", "jackson-annotations", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.core", "jackson-databind", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.jaxrs", "jackson-jaxrs-base", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.jaxrs", "jackson-jaxrs-json-provider", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.jackson.module", "jackson-module-jaxb-annotations", "2.12.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.yaml", "snakeyaml", "1.27")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jax.rs", "org.apache.aries.jax.rs.jackson", "2.0.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option ariesJaxRSWhiteboard() {
        return composite(
                cxf(),
                mavenBundle("jakarta.xml.bind", "jakarta.xml.bind-api", "2.3.3")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.osgi", "org.osgi.util.function", "1.1.0")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.osgi", "org.osgi.util.promise", "1.1.1")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.osgi", "org.osgi.service.jaxrs", "1.0.0")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.apache.aries.component-dsl", "org.apache.aries.component-dsl.component-dsl", "1.2.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jax.rs", "org.apache.aries.jax.rs.whiteboard", "2.0.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option cxf() {
        return composite(
                httpService(),
                systemPackage("javax.annotation;version=1.2"),
                mavenBundle("org.apache.aries.spec", "org.apache.aries.javax.jax.rs-api", "1.0.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml.woodstox", "woodstox-core", "6.2.6")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.ws.xmlschema", "xmlschema-core", "2.2.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.codehaus.woodstox", "stax2-api", "4.2.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-core", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-rt-frontend-jaxrs", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-rt-rs-client", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-rt-rs-sse", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-rt-security", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.cxf", "cxf-rt-transports-http", "3.4.5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option httpService() {
        return composite(
                mavenBundle("org.apache.felix", "org.apache.felix.http.servlet-api", "1.1.2")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.apache.felix", "org.apache.felix.http.jetty", "4.1.12")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES)
        );
    }

    protected Option hibernate() {
        return composite(
                transaction(),
                systemPackages("javax.xml.bind;version=2.2", "javax.xml.bind.annotation;version=2.2",
                        "javax.xml.bind.annotation.adapters;version=2.2"),
                systemPackages("javax.xml.stream;version=1.0", "javax.xml.stream.events;version=1.0",
                        "javax.xml.stream.util;version=1.0"),
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.antlr", "2.7.7_5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.dom4j", "1.6.1_5")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("com.fasterxml", "classmate", "1.5.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.javassist", "javassist", "3.27.0-GA")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("net.bytebuddy", "byte-buddy", "1.10.10")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.jboss.logging", "jboss-logging", "3.4.1.Final")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.jboss", "jandex", "2.2.3.Final")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.hibernate.common", "hibernate-commons-annotations", "5.1.0.Final")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.hibernate", "hibernate-core", "5.4.32.Final")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.hibernate", "hibernate-osgi", "5.4.32.Final")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("jakarta.persistence", "jakarta.persistence-api").versionAsInProject()
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option ariesJpa() {
        return composite(
                ariesProxy(),
                // jndi
                mavenBundle("org.apache.aries", "org.apache.aries.util", "1.1.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.api", "1.1.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.core", "1.0.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.url", "1.1.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                // blueprint
                mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.api", "1.0.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.core", "1.10.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                // jpa
                mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.api", "2.7.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container", "2.7.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.support", "2.7.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.jpa.javax.persistence", "javax.persistence_2.1", "2.7.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option ariesProxy() {
        return composite(
                mavenBundle("org.ow2.asm", "asm", "9.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ow2.asm", "asm-util", "9.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ow2.asm", "asm-tree", "9.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ow2.asm", "asm-analysis", "9.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ow2.asm", "asm-commons", "9.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy", "1.1.11")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),

                mavenBundle("org.apache.aries.spifly", "org.apache.aries.spifly.dynamic.bundle", "1.3.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option transaction() {
        return composite(
                // api
                mavenBundle("javax.interceptor", "javax.interceptor-api", "1.2.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.javax-inject", "1_3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("javax.el", "javax.el-api", "3.0.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("javax.enterprise", "cdi-api", "1.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("javax.transaction", "javax.transaction-api", "1.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                // tm
                mavenBundle("org.apache.aries.transaction", "org.apache.aries.transaction.manager", "1.3.3")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.aries.transaction", "org.apache.aries.transaction.blueprint", "2.3.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }

    protected Option hsqldb() {
        return composite(
                systemPackage("javax.transaction;version=1.2.0"),
                systemPackage("javax.transaction.xa;version=1.2.0"),
                // just for DBCP2
                systemPackage("javax.transaction.xa;version=1.2.0;partial=true;mandatory:=partial"),
                mavenBundle("org.osgi", "org.osgi.service.jdbc", "1.0.0")
                        .startLevel(START_LEVEL_SYSTEM_BUNDLES),
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.cglib", "3.3.0_1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jasypt", "1.9.3_1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.commons", "commons-pool2", "2.11.1")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.apache.commons", "commons-dbcp2", "2.9.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("commons-logging", "commons-logging", "1.2")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc", "1.5.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-hsqldb", "1.5.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-config", "1.5.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-pool-common", "1.5.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-pool-dbcp2", "1.5.0")
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1),
                mavenBundle("org.hsqldb", "hsqldb").versionAsInProject()
                        .startLevel(START_LEVEL_TEST_BUNDLE - 1)
        );
    }
}
