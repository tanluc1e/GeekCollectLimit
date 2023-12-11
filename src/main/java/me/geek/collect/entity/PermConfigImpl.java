package me.geek.collect.entity;

import me.geek.collect.api.LimitConfig;
import me.geek.collect.api.PermConfig;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

/**
 * GeekCollectLimit
 * me.geek.collect.entity
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 9:48
 */
public final class PermConfigImpl implements PermConfig {

    private final int priority;

    private final String permission;

    private final HashMap<Material, LimitConfig> limitConfig;

    public PermConfigImpl(int priority, String permission, HashMap<Material, LimitConfig> limitConfig) {
        this.priority = priority;
        this.permission = permission;
        this.limitConfig = limitConfig;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    @NotNull
    public String getPermission() {
        return permission;
    }

    @Override
    @Nullable
    public LimitConfig getConfig(Material type) {
        return limitConfig.get(type);
    }

    @Override
    @NotNull
    public Set<Material> getKeys() {
        return limitConfig.keySet();
    }

    @Override
    public int compareTo(@NotNull PermConfig o) {
        return o.getPriority() - this.priority;
    }
}
