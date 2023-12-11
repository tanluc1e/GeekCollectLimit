package me.geek.collect.sql.impl;

import com.zaxxer.hikari.HikariDataSource;
import me.geek.collect.sql.SqlConfig;
import me.geek.collect.sql.SqlService;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * GeekCollectLimit
 * me.geek.collect.sql.impl
 *
 * @author 老廖
 * @since 2023/10/3 6:40
 */
public class Sqlite implements SqlService {
    private HikariDataSource dataSource;

    private final SqlConfig sqlConfig;

    public Sqlite(SqlConfig sqlConfig) {
        this.sqlConfig = sqlConfig;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void startSql() {
        final String url = "jdbc:sqlite:"+ sqlConfig.dataFolder + File.separator + "data.db";
        dataSource = new HikariDataSource();
        dataSource.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        dataSource.addDataSourceProperty("url", url);
        //附件参数
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setMaxLifetime(1800000);
        dataSource.setKeepaliveTime(0);
        dataSource.setConnectionTimeout(5000);
        dataSource.setIdleTimeout(60000);
        dataSource.setPoolName("Geek-Sqlite");
    }

    @Override
    public void stopSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
