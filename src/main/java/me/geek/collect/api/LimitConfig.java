package me.geek.collect.api;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * GeekCollectLimit
 * me.geek.collect.api
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 11:13
 */
public interface LimitConfig {

    @NotNull
    Material getMaterial();

    @NotNull
    String getDisplayName();

    int getAmount();
}
