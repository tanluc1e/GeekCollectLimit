package me.geek.collect.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * GeekCollectLimit
 * me.geek.collect.utils
 *
 * @author 老廖
 * @since 2023/10/3 11:53
 */
public final class Utils {
    private static final HashMap<String, String> lang = new HashMap<>();

    public static void initLang(YamlConfiguration yamlConfiguration) {
        lang.clear();
        yamlConfiguration.getKeys(false).forEach(it -> lang.put(it, colored(yamlConfiguration.getString(it, it))));
    }

    public static void sendLang(Player player, String node, String... exp) {
        String meg = lang.get(node);
        if (meg != null) {
            if (exp.length != 0) {
                int index = 0;
                for (String o : exp) {
                    meg = meg.replace("{"+index+"}", o);
                    index++;
                }
                player.sendMessage(meg);
            } else {
                player.sendMessage(meg);
            }
        } else player.sendMessage(node);
    }

    public static long getTodayTime() {
        //设置时区
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String colored(String test) {
        return test.replace("&","§");
    }

    public static List<String> colored(List<String> test) {
        return test.stream().map(it -> it.replace("&","§")).collect(Collectors.toList());
    }
}
