package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.util.EntityManagerProvider;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public abstract class DatabaseTestSupport {

    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.instance("customers");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(emProvider.connection());
}
