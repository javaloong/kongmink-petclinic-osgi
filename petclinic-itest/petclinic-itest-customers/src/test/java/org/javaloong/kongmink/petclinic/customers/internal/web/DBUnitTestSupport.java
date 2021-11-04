package org.javaloong.kongmink.petclinic.customers.internal.web;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.junit.Rule;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.sql.Connection;

import static com.github.database.rider.core.util.ClassUtils.isOnClasspath;

public abstract class DBUnitTestSupport extends PaxExamTestSupport {

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(getConnectionHolder());

    @Inject
    EntityManagerFactory emf;

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
