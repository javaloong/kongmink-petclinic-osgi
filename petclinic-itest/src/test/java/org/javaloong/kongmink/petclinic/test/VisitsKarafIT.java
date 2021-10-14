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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VisitsKarafIT extends AbstractKarafIT {

    @Configuration
    public Option[] config() {
        return OptionUtils.combine(super.config(),
                addFeatures("petclinic-osgi-datasource-h2", "petclinic-osgi-visits-ds")
        );
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
}
