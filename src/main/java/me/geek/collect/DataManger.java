package me.geek.collect;

import com.google.gson.GsonBuilder;
import me.geek.collect.utils.Pair;
import me.geek.collect.api.PlayerData;
import me.geek.collect.entity.PlayerDataImpl;
import me.geek.collect.utils.Exclude;
import me.geek.collect.sql.SqlConfig;
import me.geek.collect.sql.impl.Mysql;
import me.geek.collect.sql.SqlService;
import me.geek.collect.sql.impl.Sqlite;
import me.geek.collect.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * GeekCollectLimit
 * me.geek.collect.entity
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 6:40
 */
public final class DataManger {

    public static final GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new Exclude());

    private final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS `data` (" +
            " `id` integer PRIMARY KEY, " +
            " `uuid` CHAR(36) NOT NULL , " +
            " `data` longblob Not Null" +
            ");";
    private final String MySQL_CREATE = "CREATE TABLE IF NOT EXISTS `data` (" +
            " `id` INT(36) NOT NULL AUTO_INCREMENT, " +
            " `uuid` CHAR(36) NOT NULL UNIQUE, " +
            " `data` TEXT Not Null, " +
            "PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    private final LinkedBlockingQueue<Pair<Object, Consumer<PlayerData>>> queue = new LinkedBlockingQueue<>(500);

    private final List<BukkitTask> bukkitTask = new ArrayList<>();

    private final SqlConfig sqlConfig;

    private final Plugin plugin;

    private SqlService sqlService;

    DataManger(@NotNull SqlConfig sqlConfig, @NotNull GeekCollectLimit plugin) {
        this.sqlConfig = sqlConfig;
        this.plugin = plugin;
    }

    @Nullable
    public PlayerData getPlayerData(@NotNull UUID uuid) {
        return cache.get(uuid);
    }

    private void addTask(@NotNull Pair<Object, Consumer<PlayerData>> run) {
        queue.offer(run);
    }

    public void addSaveAllTask(boolean close) {
        if (close) {
            addTask(new Pair<>(null, it -> close()));
        } else {
            addTask(new Pair<>(null, null));
        }
    }

    public void addUpdateTask(Player player) {
        Optional.ofNullable(cache.get(player.getUniqueId()))
                .ifPresent(it -> addTask(new Pair<>(it, null)));
    }

    public void addSelectTask(Player player) {
        UUID uuid = player.getUniqueId();
        addTask(new Pair<>(uuid, data -> {
            if (data.getTimer() <= System.currentTimeMillis()) {
                data.resetAll();
                Utils.sendLang(player, "每日收集上限重置");
                addTask(new Pair<>(data, null));
            }
        }));
    }

    public void start() {
        /*
        // 某些模组端可能不会加载连接池的包，手动加载
        try {
            Class.forName("com.zaxxer.hikari.HikariDataSource");
        } catch (Exception ignored) {
            GeekCollectLimit.log("无法获取连接池实现，手动加载...");
            new EnvClassLoader().addPath();
        }
         */

        if (sqlService != null) return;
        if (sqlConfig.isMysql) {
            sqlService = new Mysql(sqlConfig);
        } else sqlService = new Sqlite(sqlConfig);
        // 启动
        sqlService.startSql();
        // 建表
        try (Connection connection = sqlService.getConnection()){
            try (Statement statement = connection.createStatement()){
                if (!sqlConfig.isMysql) {
                    statement.execute(SQL_CREATE);
                } else statement.execute(MySQL_CREATE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 启动
        bukkitTask.add(Bukkit.getScheduler().runTaskAsynchronously(plugin, new QueueThread()));
        bukkitTask.add(Bukkit.getScheduler().runTaskAsynchronously(plugin, new QueueThread()));
    }


    @NotNull
    public Connection getConnection() throws SQLException {
       return sqlService.getConnection();
    }

    public void close() {
        sqlService.stopSql();
        queue.clear();
        bukkitTask.forEach(BukkitTask::cancel);
        bukkitTask.clear();
    }

    private void insert(@NotNull PlayerData playerData) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO data(`uuid`,`data`) VALUES(?,?);")) {
                statement.setString(1, playerData.getUUID().toString());
                statement.setString(2, playerData.toJson());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(@NotNull PlayerData data) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `data` SET `data`=? WHERE `uuid`=?")) {
                statement.setString(1, data.toJson());
                statement.setString(2, data.getUUID().toString());
                statement.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateAll() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `data` SET `data`=? WHERE `uuid`=?")) {
                for (Map.Entry<UUID, PlayerData> data : cache.entrySet()) {
                    statement.setString(1, data.getValue().toJson());
                    statement.setString(2, data.getKey().toString());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @NotNull
    private PlayerData select(@NotNull UUID uuid) {
        PlayerData data = new PlayerDataImpl(uuid, Utils.getTodayTime());
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM `data` WHERE uuid=?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    byte[] a = resultSet.getBytes("data");
                    data = gsonBuilder.create().fromJson(new String(a, StandardCharsets.UTF_8), PlayerDataImpl.class);
                } else {
                    // 新玩家
                    insert(data);
                }
                cache.put(uuid, data);
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 线程任务，暂未设计客制化的执行体系。
     */
    private final class QueueThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Pair<Object, Consumer<PlayerData>> take = queue.take();

                    if (take.first == null) {
                        updateAll();
                        if (take.second != null) {
                            take.second.accept(null);
                        }
                        return;
                    }

                    // 如果是 UUID 则查询数据
                    if (take.first instanceof UUID) {
                        UUID uuid = (UUID) take.first;
                        PlayerData data = select(uuid);
                        cache.put(data.getUUID(), data);
                        if (take.second != null) {
                            take.second.accept(data);
                        }
                    } else if (take.first instanceof PlayerData) {
                        // 是 PlayerData 更新数据
                        PlayerData data = (PlayerData) take.first;
                        update(data);
                        if (take.second != null) {
                            take.second.accept(data);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
