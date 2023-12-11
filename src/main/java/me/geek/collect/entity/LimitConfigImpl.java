package me.geek.collect.entity;

import me.geek.collect.api.LimitConfig;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * GeekCollectLimit
 * me.geek.collect.entity
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 9:35
 */
public final class LimitConfigImpl implements LimitConfig {
    private final Material material;

    private final String displayName;

    private final int amount;

    public LimitConfigImpl(Material material, String displayName, int amount) {
        this.material = material;
        this.displayName = displayName;
        this.amount = amount;
    }


    @Override
    @NotNull
    public Material getMaterial() {
        return material;
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
