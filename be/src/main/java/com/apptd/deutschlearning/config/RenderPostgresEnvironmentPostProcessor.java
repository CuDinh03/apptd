package com.apptd.deutschlearning.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Bổ sung map env → datasource khi context không đi qua {@code main} (test). Trên Render, {@link RenderDatabaseUrlSupport#applySystemPropertiesFromOSEnv()} trong main là lớp bảo vệ chính.
 */
public class RenderPostgresEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RenderPostgresEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean applied = RenderDatabaseUrlSupport.applyToSpringEnvironment(environment);

        if (!applied && isProd(environment)) {
            log.error(
                    "Profile prod nhưng chưa map được datasource: thiếu DATABASE_URL, SPRING_DATASOURCE_URL (jdbc:postgresql:…), "
                            + "hoặc PGHOST+PGDATABASE+PGUSER+PGPASSWORD. Flyway sẽ thử localhost:5432 và fail. "
                            + "Trên Render: gắn DATABASE_URL (Internal URL) vào Web Service, rồi deploy lại image mới có bootstrap main().");
        }
    }

    private static boolean isProd(ConfigurableEnvironment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
