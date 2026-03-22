package com.apptd.deutschlearning.config;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Render Postgres blueprint trả {@code DATABASE_URL} dạng {@code postgresql://user:pass@host:port/db}
 * — chuyển sang JDBC để Spring Boot + Flyway dùng được.
 */
public class RenderPostgresEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RenderPostgresEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String raw = firstNonBlank(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("RENDER_DATABASE_URL"));
        if (raw == null || !raw.startsWith("postgres")) {
            if (raw == null) {
                log.debug("Không có DATABASE_URL — dùng jdbc từ application.yml (DB_HOST / POSTGRES_PORT / …).");
            }
            return;
        }
        try {
            URI uri = URI.create(raw.replaceFirst("^postgres(ql)?://", "postgresql://"));
            String userInfo = uri.getUserInfo();
            if (userInfo == null || userInfo.isEmpty()) {
                return;
            }
            int colon = userInfo.indexOf(':');
            String user = colon >= 0 ? userInfo.substring(0, colon) : userInfo;
            String password = colon >= 0 && colon < userInfo.length() - 1
                    ? userInfo.substring(colon + 1)
                    : "";

            String host = uri.getHost();
            if (host == null) {
                return;
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path == null || path.isEmpty() || "/".equals(path)) {
                return;
            }
            String dbName = path.startsWith("/") ? path.substring(1) : path;
            String sslMode = environment.getProperty("PG_SSLMODE", "prefer");
            String jdbcUrl = String.format(
                    "jdbc:postgresql://%s:%d/%s?sslmode=%s", host, port, dbName, sslMode);

            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", jdbcUrl);
            map.put("spring.datasource.username", user);
            map.put("spring.datasource.password", password);
            map.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
            environment.getPropertySources().addFirst(new MapPropertySource("renderPostgresJdbc", map));
            log.info("Đã map DATABASE_URL → JDBC (host={}, port={}, db={}).", host, port, dbName);
        } catch (Exception e) {
            log.warn(
                    "Không parse được DATABASE_URL — dùng application.yml. Lỗi: {}",
                    e.getMessage());
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
