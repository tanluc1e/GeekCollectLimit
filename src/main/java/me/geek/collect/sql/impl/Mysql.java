package me.geek.collect.sql.impl;

import com.zaxxer.hikari.HikariDataSource;
import me.geek.collect.sql.SqlConfig;
import me.geek.collect.sql.SqlService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * GeekCollectLimit
 * me.geek.collect.sql.impl
 *
 * @author 老廖
 * @since 2023/10/3 6:40
 */
public final class Mysql implements SqlService {
    private HikariDataSource dataSource;
    private final SqlConfig mysqlData;

    public Mysql(SqlConfig sqlConfig) {
        this.mysqlData = sqlConfig;

    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void startSql() {
        String url = "jdbc:mysql://"+mysqlData.host+":"+mysqlData.port+"/"+mysqlData.database+""+mysqlData.params;
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(mysqlData.username);
        dataSource.setPassword(mysqlData.password);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        } catch (RuntimeException | NoClassDefFoundError | ClassNotFoundException e) {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setMaxLifetime(1800000);
        dataSource.setKeepaliveTime(0);
        dataSource.setConnectionTimeout(5000);
        dataSource.setPoolName("GeekEnv");
    }

    @Override
    public void stopSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
