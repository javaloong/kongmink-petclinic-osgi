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

import org.apache.karaf.itests.KarafTestSupport;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import static org.ops4j.pax.exam.CoreOptions.maven;

public abstract class AbstractKarafIT extends KarafTestSupport {

    protected Option addFeatures(final String... features) {
        return KarafDistributionOption.features(
                maven().groupId("org.javaloong.kongmink").artifactId("petclinic-osgi-features")
                        .type("xml").classifier("features").versionAsInProject(), features);
    }
}
