package com.apptd.deutschlearning.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
 * Render / PaaS: map {@code DATABASE_URL} (postgresql://…) hoặc biến libpq ({@code PGHOST}…) sang JDBC.
 * Nếu không có, Spring dùng application.yml → localhost — trên Render cần gắn DB hoặc đặt env thủ công.
 */
public class RenderPostgresEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RenderPostgresEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean applied = false;

        applied = trySpringJdbcUrl(environment) || applied;
        applied = tryDatabaseUrl(environment) || applied;
        applied = tryLibpq(environment) || applied;

        if (!applied && isProd(environment)) {
            log.error(
                    "Profile prod nhưng chưa map được datasource: thiếu DATABASE_URL, SPRING_DATASOURCE_URL (jdbc:postgresql:…), "
                            + "hoặc bộ PGHOST+PGDATABASE+PGUSER+PGPASSWORD. Flyway sẽ thử localhost:5432 và fail. "
                            + "Trên Render: Environment → Add Environment Variable → From Database → chọn Postgres, "
                            + "hoặc dán Internal Database URL vào DATABASE_URL.");
        }
    }

    private static boolean isProd(ConfigurableEnvironment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    /** Đã set sẵn JDBC URL (một số nền tảng / ops). */
    private static boolean trySpringJdbcUrl(ConfigurableEnvironment environment) {
        String jdbc = environment.getProperty("SPRING_DATASOURCE_URL");
        if (jdbc == null || jdbc.isBlank() || !jdbc.startsWith("jdbc:postgresql:")) {
            return false;
        }
        String sslMode = environment.getProperty("PG_SSLMODE", "prefer");
        String url = jdbc.contains("sslmode=") ? jdbc : appendSslMode(jdbc, sslMode);
        apply(environment, url, environment.getProperty("SPRING_DATASOURCE_USERNAME"), environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
        log.info("Đã dùng SPRING_DATASOURCE_URL (JDBC Postgres).");
        return true;
    }

    private static boolean tryDatabaseUrl(ConfigurableEnvironment environment) {
        String raw = firstNonBlank(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("RENDER_DATABASE_URL"),
                environment.getProperty("POSTGRES_URL"));
        if (raw == null || !raw.trim().startsWith("postgres")) {
            if (raw == null) {
                log.debug("Không có DATABASE_URL / POSTGRES_URL.");
            }
            return false;
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
            log.warn("Không parse được DATABASE_URL (định dạng không như postgresql://user:pass@host:port/db).");
            return false;
        }
        String sslMode = environment.getProperty("PG_SSLMODE", "prefer");
        String jdbcBase = String.format("jdbc:postgresql://%s:%d/%s", p.host, p.port, p.dbName);
        String jdbcUrl = mergeQuery(jdbcBase, queryPart, sslMode);
        apply(environment, jdbcUrl, p.user, p.password);
        log.info("Đã map DATABASE_URL → JDBC (host={}, port={}, db={}).", p.host, p.port, p.dbName);
        return true;
    }

    /** Biến chuẩn libpq; Render đôi khi hiển thị từng field thay vì một URL. */
    private static boolean tryLibpq(ConfigurableEnvironment environment) {
        String host = environment.getProperty("PGHOST");
        if (host == null || host.isBlank()) {
            return false;
        }
        String db = environment.getProperty("PGDATABASE");
        String user = environment.getProperty("PGUSER");
        String pass = environment.getProperty("PGPASSWORD", "");
        if (db == null || db.isBlank() || user == null || user.isBlank()) {
            log.warn("Có PGHOST nhưng thiếu PGDATABASE hoặc PGUSER — bỏ qua libpq.");
            return false;
        }
        int port = 5432;
        String pgPort = environment.getProperty("PGPORT");
        if (pgPort != null && !pgPort.isBlank()) {
            try {
                port = Integer.parseInt(pgPort.trim());
            } catch (NumberFormatException ignored) {
                // giữ 5432
            }
        }
        String sslMode = environment.getProperty("PG_SSLMODE", "prefer");
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=%s", host, port, db, sslMode);
        apply(environment, jdbcUrl, user, pass);
        log.info("Đã ghép JDBC từ PGHOST / PGPORT / PGDATABASE / PGUSER.");
        return true;
    }

    private static void apply(ConfigurableEnvironment environment, String jdbcUrl, String username, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", jdbcUrl);
        if (username != null && !username.isBlank()) {
            map.put("spring.datasource.username", username);
        }
        if (password != null) {
            map.put("spring.datasource.password", password);
        }
        map.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
        environment.getPropertySources().addFirst(new MapPropertySource("renderPostgresJdbc", map));
    }

    private static String appendSslMode(String jdbcUrl, String sslMode) {
        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=" + sslMode;
    }

    /** Giữ query từ URL gốc (vd. sslmode) và bổ sung sslmode nếu thiếu. */
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

    /**
     * postgresql://user:pass@host:port/db — pass có thể chứa ':' nếu đã encode; tách theo @ cuối cùng trước path.
     */
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
            log.debug("parsePostgresUrl: {}", e.getMessage());
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
