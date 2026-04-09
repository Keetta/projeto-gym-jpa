package org.example.config;

import org.flywaydb.core.Flyway;

public class FlyWayConfig {

    public static void migrate() {

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/academia",
                        "nick",
                        "nicki12072007"
                )
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
        System.out.println("✅ Flyway executado com sucesso!");
    }
}