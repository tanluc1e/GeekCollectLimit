package me.geek.collect.entity;

import me.geek.collect.api.LimitConfig;
import me.geek.collect.api.PermConfig;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;

/**
 * GeekCollectLimit
 * me.geek.collect.entity
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 14:09
 */
public final class DailyLimitConfigImpl {

    public final List<String> worlds;

    public final HashMap<Material, LimitConfig> defLimitConfig;

    public final List<PermConfig> permLimitConfigs;

    public DailyLimitConfigImpl(List<String> worlds, HashMap<Material, LimitConfig> defLimitConfig, List<PermConfig> permLimitConfigs) {
        this.worlds = worlds;
        this.defLimitConfig = defLimitConfig;
        this.permLimitConfigs = permLimitConfigs;
    }

    public void clear() {
        this.worlds.clear();
        this.defLimitConfig.clear();
        this.permLimitConfigs.clear();
    }

}
