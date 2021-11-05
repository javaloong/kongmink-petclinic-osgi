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

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.junit.Rule;
import org.ops4j.pax.exam.Option;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.sql.Connection;

import static com.github.database.rider.core.util.ClassUtils.isOnClasspath;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

public abstract class DBUnitTestSupport extends PaxExamTestSupport {

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(getConnectionHolder());

    @Inject
    EntityManagerFactory emf;

    @Override
    protected Option baseOptions() {
        return composite(super.baseOptions(), dbunit());
    }

    protected Option dbunit() {
        return composite(
                mavenBundle("commons-collections", "commons-collections").versionAsInProject(),
                wrappedBundle(mavenBundle("org.dbunit", "dbunit").versionAsInProject()),
                wrappedBundle(mavenBundle("com.github.database-rider", "rider-core").versionAsInProject())
        );
    }

    private ConnectionHolder getConnectionHolder() {
        return () -> {
            Connection conn;
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            if (isHibernateOnClasspath() && em.getDelegate() instanceof Session) {
                conn = ((SessionImpl) em.unwrap(Session.class)).connection();
            } else {
                /**
                 * see here:http://wiki.eclipse.org/EclipseLink/Examples/JPA/EMAPI#Getting_a_JDBC_Connection_from_an_EntityManager
                 */
                tx.begin();
                conn = em.unwrap(Connection.class);
                tx.commit();
            }
            return conn;
        };
    }

    private boolean isHibernateOnClasspath() {
        return isOnClasspath("org.hibernate.Session");
    }
}
