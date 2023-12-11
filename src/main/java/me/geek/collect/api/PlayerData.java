package me.geek.collect.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * GeekCollectLimit
 * me.geek.collect.api
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 6:41
 */
public interface PlayerData {

    @Nullable
    Player getPlayer();

    @NotNull
    UUID getUUID();

    long getTimer();

    int getCollectAmount(@NotNull Material type);

    void setCollectAmount(@NotNull Material type, int amount);

    boolean addCollectAmount(@NotNull Material type, int amount);

    void inisLimit(@NotNull Material type, int def);

    boolean reset(@NotNull Material type);

    void resetAll();

    String toJson();

}
