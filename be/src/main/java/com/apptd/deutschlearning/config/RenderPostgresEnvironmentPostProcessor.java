package com.apptd.deutschlearning.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Map env → datasource. Phải chạy <em>sau</em> {@code ConfigDataEnvironmentPostProcessor} (ORDER = HIGHEST + 10):
 * nếu chạy trước rồi {@code addFirst}, ConfigData cũng {@code addFirst} sau đó và {@code application.yml}
 * sẽ nằm trên cùng — {@code spring.datasource.url} mặc định (localhost, sslmode=prefer) ghi đè JDBC từ {@code DATABASE_URL},
 * Flyway/Hikari nối sai host và lỗi SSL (vd. EOF khi auth trên Render).
 */
public class RenderPostgresEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RenderPostgresEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean applied = RenderDatabaseUrlSupport.applyToSpringEnvironment(environment);

        if (!applied && isProd(environment)) {
            log.info(
                    "Profile prod: không có DATABASE_URL Postgres — dùng spring.datasource.* từ application.yml "
                            + "(MySQL: DB_HOST, MYSQL_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD).");
        }
    }

    private static boolean isProd(ConfigurableEnvironment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
