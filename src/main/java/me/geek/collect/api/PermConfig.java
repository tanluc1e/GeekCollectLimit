package me.geek.collect.api;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * GeekCollectLimit
 * me.geek.collect.api
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 11:16
 */
public interface PermConfig extends Comparable<PermConfig> {

    int getPriority();

    @NotNull
    String getPermission();

    @Nullable
    LimitConfig getConfig(Material type);

    @NotNull
    Set<Material> getKeys();
}
