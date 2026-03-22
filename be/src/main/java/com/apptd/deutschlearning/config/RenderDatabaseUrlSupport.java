package com.apptd.deutschlearning.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Map {@code DATABASE_URL} / libpq / {@code SPRING_DATASOURCE_URL} → JDBC cho Postgres.
 * Dùng từ {@code main()} (system properties) và từ {@link RenderPostgresEnvironmentPostProcessor}.
 */
public final class RenderDatabaseUrlSupport {

    private static final Logger log = LoggerFactory.getLogger(RenderDatabaseUrlSupport.class);

    private RenderDatabaseUrlSupport() {}

    public record DatasourceConfig(String url, String username, String password) {}

    /**
     * Gọi đầu tiên trong {@code main()} — trước {@code SpringApplication.run}, để Flyway luôn thấy đúng URL (Render Docker).
     */
    public static boolean applySystemPropertiesFromOSEnv() {
        DatasourceConfig cfg = resolve(System::getenv);
        if (cfg == null) {
            return false;
        }
        System.setProperty("spring.datasource.url", cfg.url());
        System.setProperty("spring.datasource.username", cfg.username() != null ? cfg.username() : "");
        System.setProperty("spring.datasource.password", cfg.password() != null ? cfg.password() : "");
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        log.info("Bootstrap (main): đã gán spring.datasource.* từ env (Postgres).");
        return true;
    }

    /** Thêm {@link MapPropertySource} — hỗ trợ chạy test / context không qua {@code main}. */
    public static boolean applyToSpringEnvironment(ConfigurableEnvironment environment) {
        DatasourceConfig cfg = resolve(environment::getProperty);
        if (cfg == null) {
            return false;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", cfg.url());
        map.put("spring.datasource.username", cfg.username() != null ? cfg.username() : "");
        map.put("spring.datasource.password", cfg.password() != null ? cfg.password() : "");
        map.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("renderPostgresJdbc", map));
        log.info("EnvironmentPostProcessor: đã map env → spring.datasource.* (Postgres).");
        return true;
    }

    public static DatasourceConfig resolve(Function<String, String> get) {
        DatasourceConfig c = trySpringJdbcUrl(get);
        if (c != null) {
            return c;
        }
        c = tryDatabaseUrl(get);
        if (c != null) {
            return c;
        }
        return tryLibpq(get);
    }

    private static DatasourceConfig trySpringJdbcUrl(Function<String, String> get) {
        String jdbc = get.apply("SPRING_DATASOURCE_URL");
        if (jdbc == null || jdbc.isBlank() || !jdbc.startsWith("jdbc:postgresql:")) {
            return null;
        }
        String sslMode = firstNonBlank(get.apply("PG_SSLMODE"), "prefer");
        String url = jdbc.contains("sslmode=") ? jdbc : appendSslMode(jdbc, sslMode);
        String user = get.apply("SPRING_DATASOURCE_USERNAME");
        String pass = get.apply("SPRING_DATASOURCE_PASSWORD");
        if (pass == null) {
            pass = "";
        }
        return new DatasourceConfig(url, user, pass);
    }

    private static DatasourceConfig tryDatabaseUrl(Function<String, String> get) {
        String raw = firstNonBlank(
                get.apply("DATABASE_URL"),
                get.apply("RENDER_DATABASE_URL"),
                get.apply("POSTGRES_URL"));
        if (raw == null || !raw.trim().startsWith("postgres")) {
            return null;
        }
        raw = raw.trim();
        int q = raw.indexOf('?');
        String queryPart = "";
        if (q >= 0) {
            queryPart = raw.substring(q);
            raw = raw.substring(0, q);
        }
        Parsed p = parsePostgresUrl(raw);
        if (p == null) {
            log.warn("Không parse được DATABASE_URL.");
            return null;
        }
        String sslMode = firstNonBlank(get.apply("PG_SSLMODE"), "prefer");
        String jdbcBase = String.format("jdbc:postgresql://%s:%d/%s", p.host, p.port, p.dbName);
        String jdbcUrl = mergeQuery(jdbcBase, queryPart, sslMode);
        return new DatasourceConfig(jdbcUrl, p.user, p.password);
    }

    private static DatasourceConfig tryLibpq(Function<String, String> get) {
        String host = get.apply("PGHOST");
        if (host == null || host.isBlank()) {
            return null;
        }
        String db = get.apply("PGDATABASE");
        String user = get.apply("PGUSER");
        String pass = get.apply("PGPASSWORD");
        if (pass == null) {
            pass = "";
        }
        if (db == null || db.isBlank() || user == null || user.isBlank()) {
            return null;
        }
        int port = 5432;
        String pgPort = get.apply("PGPORT");
        if (pgPort != null && !pgPort.isBlank()) {
            try {
                port = Integer.parseInt(pgPort.trim());
            } catch (NumberFormatException ignored) {
                // 5432
            }
        }
        String sslMode = firstNonBlank(get.apply("PG_SSLMODE"), "prefer");
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=%s", host, port, db, sslMode);
        return new DatasourceConfig(jdbcUrl, user, pass);
    }

    private static String appendSslMode(String jdbcUrl, String sslMode) {
        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=" + sslMode;
    }

    private static String mergeQuery(String jdbcBase, String originalQueryWithQuestion, String sslMode) {
        String q = originalQueryWithQuestion == null || originalQueryWithQuestion.isEmpty()
                ? ""
                : originalQueryWithQuestion.substring(1);
        if (q.isEmpty()) {
            return jdbcBase + "?sslmode=" + sslMode;
        }
        if (q.contains("sslmode=")) {
            return jdbcBase + "?" + q;
        }
        return jdbcBase + "?" + q + "&sslmode=" + sslMode;
    }

    private record Parsed(String user, String password, String host, int port, String dbName) {}

    private static Parsed parsePostgresUrl(String rawWithoutQuery) {
        try {
            String s = rawWithoutQuery.replaceFirst("^postgres(ql)?://", "");
            int slash = s.indexOf('/');
            if (slash <= 0 || slash >= s.length() - 1) {
                return null;
            }
            String dbName = s.substring(slash + 1);
            if (dbName.isBlank()) {
                return null;
            }
            String authority = s.substring(0, slash);
            int at = authority.lastIndexOf('@');
            if (at <= 0) {
                return null;
            }
            String userInfo = authority.substring(0, at);
            String hostPort = authority.substring(at + 1);
            if (hostPort.isBlank()) {
                return null;
            }
            int colon = userInfo.indexOf(':');
            String user = colon >= 0 ? userInfo.substring(0, colon) : userInfo;
            String password = colon >= 0 && colon < userInfo.length() - 1 ? userInfo.substring(colon + 1) : "";
            user = urlDecode(user);
            password = urlDecode(password);

            String host;
            int port;
            if (hostPort.startsWith("[")) {
                int br = hostPort.indexOf(']');
                if (br < 0) {
                    return null;
                }
                host = hostPort.substring(1, br);
                String rest = hostPort.substring(br + 1);
                if (rest.startsWith(":") && rest.length() > 1) {
                    port = Integer.parseInt(rest.substring(1));
                } else {
                    port = 5432;
                }
            } else {
                int lp = hostPort.lastIndexOf(':');
                if (lp > 0) {
                    String maybePort = hostPort.substring(lp + 1);
                    if (maybePort.chars().allMatch(Character::isDigit)) {
                        host = hostPort.substring(0, lp);
                        port = Integer.parseInt(maybePort);
                    } else {
                        host = hostPort;
                        port = 5432;
                    }
                } else {
                    host = hostPort;
                    port = 5432;
                }
            }
            if (host.isBlank()) {
                return null;
            }
            return new Parsed(user, password, host, port, dbName);
        } catch (Exception e) {
            log.debug("parsePostgresUrl: {}", e.toString());
            return null;
        }
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}
