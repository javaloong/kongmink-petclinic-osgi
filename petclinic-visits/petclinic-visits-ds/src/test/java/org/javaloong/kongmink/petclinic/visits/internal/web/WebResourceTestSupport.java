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
package org.javaloong.kongmink.petclinic.visits.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.util.EntityManagerProvider;
import org.apache.aries.jpa.supplier.EmSupplier;
import org.apache.aries.jpa.support.impl.ResourceLocalJpaTemplate;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;

import javax.persistence.EntityManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(JUnit4.class)
public abstract class WebResourceTestSupport {

    private static final String PERSISTENCE_UNIT_NAME = "visits";

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance(PERSISTENCE_UNIT_NAME);

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());

    public static EntityManager em() {
        return EntityManagerProvider.em(PERSISTENCE_UNIT_NAME);
    }

    public static JpaTemplate jpaTemplate() {
        EmSupplier emSupplier = new EmSupplier() {
            @Override
            public void preCall() { }

            @Override
            public EntityManager get() {
                return em();
            }

            @Override
            public void postCall() { }
        };
        return new ResourceLocalJpaTemplate(emSupplier, new DummyCoordinator());
    }

    public static JpaTemplate jpaTemplateSpy() {
        JpaTemplate jpaTemplate = jpaTemplate();
        JpaTemplate jpaTemplateSpy = Mockito.spy(jpaTemplate);
        doAnswer(invocation -> jpaTemplate.txExpr(TransactionType.Required, invocation.getArgument(1)))
                .when(jpaTemplateSpy).txExpr(any(TransactionType.class), any());
        return jpaTemplateSpy;
    }

    public static JacksonJsonProvider jacksonJsonProvider() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        return new JacksonJsonProvider(objectMapper);
    }

    public static ValidationExceptionMapper validationExceptionMapper() {
        return new ValidationExceptionMapper();
    }

    public WebTarget target(String uri) {
        return ClientBuilder.newClient()
                .register(jacksonJsonProvider())
                .target(uri);
    }

    static class DummyCoordinator implements Coordinator {

        private final Deque<Coordination> coordinations = new ArrayDeque<>();

        @Override
        public Coordination create(String name, long timeMillis) {
            throw new IllegalStateException();
        }

        @Override
        public Coordination begin(String name, long timeMillis) {
            Coordination oldCoordination = coordinations.peekLast();
            Coordination coordination = new DummyCoordination(oldCoordination);
            this.coordinations.push(coordination);
            return coordination;
        }

        @Override
        public Coordination peek() {
            return coordinations.peek();
        }

        @Override
        public Coordination pop() {
            return coordinations.pop();
        }

        @Override
        public boolean fail(Throwable cause) {
            return false;
        }

        @Override
        public boolean addParticipant(Participant participant) {
            return false;
        }

        @Override
        public Collection<Coordination> getCoordinations() {
            return null;
        }

        @Override
        public Coordination getCoordination(long id) {
            return null;
        }
    }

    static class DummyCoordination implements Coordination {

        private final Set<Participant> participants = new HashSet<>();
        private final Map<Class<?>, Object> vars = new HashMap<>();
        private final Coordination enclosing;

        public DummyCoordination(Coordination enclosing) {
            this.enclosing = enclosing;
        }

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void end() {
            Iterator<Participant> it = participants.iterator();
            while (it.hasNext()) {
                try {
                    it.next().ended(this);
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        @Override
        public boolean fail(Throwable cause) {
            return false;
        }

        @Override
        public Throwable getFailure() {
            return null;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public void addParticipant(Participant participant) {
            this.participants.add(participant);
        }

        @Override
        public List<Participant> getParticipants() {
            return null;
        }

        @Override
        public Map<Class<?>, Object> getVariables() {
            return vars;
        }

        @Override
        public long extendTimeout(long timeMillis) {
            return 0;
        }

        @Override
        public void join(long timeMillis) {}

        @Override
        public Coordination push() {
            return null;
        }

        @Override
        public Thread getThread() {
            return null;
        }

        @Override
        public Bundle getBundle() {
            return null;
        }

        @Override
        public Coordination getEnclosingCoordination() {
            return enclosing;
        }
    }
}
