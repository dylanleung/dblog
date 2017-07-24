package io.dblog.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mariadb.jdbc.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * Created by Pelin on 17/7/18.
 */
@Import({
        DBSetting.class
})
@ComponentScan(basePackageClasses = DBConfig.class)
public class DBConfig {

    @Bean
    public DataSource dataSource(DBSetting setting) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(buildJdbcUrl(setting));
        config.setUsername(setting.getUsername());
        config.setPassword(setting.getPassword());
        config.setDriverClassName(Driver.class.getName());

        config.setMinimumIdle(16);
        config.setMaximumPoolSize(256);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(setting.getLeakDetectionThreshold());
        return new HikariDataSource(config);
    }

    private static String buildJdbcUrl(DBSetting setting) {
        String url = "jdbc:mysql:failover://${host}/${database}?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&autoReconnect=true&connectTimeout=${timeout}";

        return url.replace("${host}", setting.getHost())
                  .replace("${database}", setting.getHost())
                  .replace("${timeout}", setting.getTimeout().toString());
    }
}