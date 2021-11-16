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
package org.javaloong.kongmink.petclinic.migration.flyway.internal;

import org.flywaydb.core.Flyway;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.Thread.currentThread;
import static org.javaloong.kongmink.petclinic.migration.flyway.internal.FlywayMigrator.DEFAULT_HOOK_KEY_VALUE;

@Component(property = PreHook.KEY_NAME + "=" + DEFAULT_HOOK_KEY_VALUE)
public class FlywayMigrator implements PreHook {

    private static final Logger log = LoggerFactory.getLogger(FlywayMigrator.class);

    static final String DEFAULT_HOOK_KEY_VALUE = "flywayMigrator";

    @Override
    public void prepare(DataSource dataSource) throws SQLException {
        log.info("Started migration for pre hook: {}", DEFAULT_HOOK_KEY_VALUE);
        final ClassLoader ldr = currentThread().getContextClassLoader();
        currentThread().setContextClassLoader(Flyway.class.getClassLoader());
        try {
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations(resolveLocation(dataSource))
                    .outOfOrder(true)
                    .validateOnMigrate(false)
                    .load()
                    .migrate();
        } finally {
            currentThread().setContextClassLoader(ldr);
        }
    }

    private String resolveLocation(DataSource dataSource) throws SQLException {
        String jdbcUrl = getJdbcUrl(dataSource);
        String[] urlParts = jdbcUrl.split(":");
        return String.format("classpath:db/migration/%s", urlParts[1]);
    }

    private String getJdbcUrl(DataSource dataSource) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        }
    }
}
