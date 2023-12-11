package me.geek.collect;

import me.geek.collect.api.LimitConfig;
import me.geek.collect.api.PermConfig;
import me.geek.collect.api.PlayerData;
import me.geek.collect.entity.DailyLimitConfigImpl;
import me.geek.collect.entity.LimitConfigImpl;
import me.geek.collect.entity.PermConfigImpl;
import me.geek.collect.listener.CollectListener;
import me.geek.collect.listener.DataListener;
import me.geek.collect.papi.GeekCollectLimitPAPIExpansion;
import me.geek.collect.papi.GetBlockCount;
import me.geek.collect.sql.SqlConfig;
import me.geek.collect.utils.Pair;
import me.geek.collect.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * GeekCollectLimit
 * me.geek.collect
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 1:50
 */
public class GeekCollectLimit extends JavaPlugin {
    private static GeekCollectLimit instance;

    private static DataManger dataManger;

    private static SqlConfig sqlConfig;

    private static DailyLimitConfigImpl dailyLimitConfig;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        reload();
    }

    @Override
    public void onEnable() {
        dataManger = new DataManger(sqlConfig, this);
        dataManger.start();
        PluginCommand command = getCommand("GeekCollectLimit");
        if (command != null) command.setExecutor(new CommandCore());
        Bukkit.getPluginManager().registerEvents(new DataListener(), this);
        Bukkit.getPluginManager().registerEvents(new CollectListener(), this);

        Bukkit.getOnlinePlayers().forEach(it -> dataManger.addSelectTask(it));

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> dataManger.addSaveAllTask(false), 20 * 60 * 10, 20 * 60 * 10);

        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new GeekCollectLimitPAPIExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        dataManger.addSaveAllTask(true);
       // dataManger.close();
    }

    @NotNull
    public static DataManger getDataManger() {
        return dataManger;
    }

    @Nullable
    public static PlayerData getPlayerData(UUID uuid) {
        return dataManger.getPlayerData(uuid);
    }

    public static boolean checkWorld(World world) {
        return dailyLimitConfig.worlds.contains(world.getName());
    }

    @Nullable
    public static LimitConfig checkConfig(@NotNull Player player, @NotNull Material type) {
        // 先找符合权限的配置
        // 如果考虑性能，可以在数据初始化时查找到权限组配置，并赋予。
        Optional<PermConfig> a = dailyLimitConfig.permLimitConfigs.stream()
                .filter(it -> player.hasPermission(it.getPermission()))
                .findFirst();
        if (a.isPresent()) {
            return a.get().getConfig(type);
        } else {
            // 找默认配置
            return dailyLimitConfig.defLimitConfig.get(type);
        }
    }

    public static Set<Material> getConfigKeys(boolean merge) {
        Set<Material> list = new HashSet<>(dailyLimitConfig.defLimitConfig.keySet());
        if (merge) {
            dailyLimitConfig.permLimitConfigs.stream()
                    .map(PermConfig::getKeys)
                    .forEach(list::addAll);
        }
        return list;
    }

    public static void log(@NotNull String msg) {
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("§8[§3§lAbyss§8]" + "§f "+msg.replace("&","§"));
    }

    public static void reload() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                new File(instance.getDataFolder(), "config.yml")
        );
        // load sql
        ConfigurationSection sql = yml.getConfigurationSection("data_storage");
        if (sql == null) throw new NullPointerException("Missing database configuration...");
        loadSqlConfig(sql);

        // load set
        if (dailyLimitConfig != null) {
            dailyLimitConfig.clear();
        }
        List<String> world = yml.getStringList("daily_Limit.world");

        ConfigurationSection a2 = yml.getConfigurationSection("daily_Limit");
        if (a2 == null) throw new NullPointerException("Missing daily limit configuration...");

        ConfigurationSection a3 = a2.getConfigurationSection("perm");
        if (a3 == null) throw new NullPointerException("Missing daily limit permission group configuration...");

        dailyLimitConfig = new DailyLimitConfigImpl(world, loadDefaultGroup(a2), loadPermGroup(a3));
        // load lang
        File file = new File(new File(instance.getDataFolder(), "lang"), "zh_CN.yml");
        if (!file.exists()) {
            instance.saveResource("lang/zh_CN.yml", false);
        }
        Utils.initLang(YamlConfiguration.loadConfiguration(file));
    }

    private static List<PermConfig> loadPermGroup(ConfigurationSection yml) {
        List<PermConfig> permConfigList = new ArrayList<>();
        yml.getKeys(false).forEach(it -> {
            int priority = Integer.parseInt(it);
            String perm = yml.getString(it+".permission");
            HashMap<Material, LimitConfig> map = new HashMap<>();

            List<?> list = yml.getList(it+".limit");
            if (list != null) {
                for (Object o : list) {
                    if (o instanceof Map) {
                        LimitConfig config = load((Map<?, ?>) o);
                        map.put(config.getMaterial(), config);
                    }
                }
            }
            permConfigList.add(new PermConfigImpl(priority, perm, map));

        });
        Collections.sort(permConfigList);
        return permConfigList;
    }
    private static HashMap<Material, LimitConfig> loadDefaultGroup(ConfigurationSection yml) {
        HashMap<Material, LimitConfig> map = new HashMap<>();
        List<?> list = yml.getList("default");
        if (list != null) {
            for (Object o : list) {
                if (o instanceof Map) {
                    LimitConfig config = load((Map<?, ?>) o);
                    map.put(config.getMaterial(), config);
                }
            }
        }
        return map;
    }

    private static void loadSqlConfig(@NotNull ConfigurationSection yml) {
        boolean mysql = yml.getString("use_type", "sqlite").equalsIgnoreCase("mysql");
        String host = yml.getString("mysql.host");
        int port = yml.getInt("mysql.port");
        String database = yml.getString("mysql.database");
        String username = yml.getString("mysql.username");
        String password = yml.getString("mysql.password");
        String params = yml.getString("mysql.params");
        sqlConfig = new SqlConfig(mysql, instance.getDataFolder(), host, port, database, username, password, params);
    }

    private static LimitConfig load(Map<?, ?> map) {
        Material material = Material.valueOf(map.get("material").toString().toUpperCase(Locale.ROOT));
        String name = Utils.colored(map.get("displayName").toString());
        int amount = Integer.parseInt(map.get("amount").toString());
        return new LimitConfigImpl(material, name, amount);
    }


}
