package me.geek.collect.entity;

import me.geek.collect.DataManger;
import me.geek.collect.api.PlayerData;
import me.geek.collect.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GeekCollectLimit
 * me.geek.collect.entity
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 6:40
 */
public final class PlayerDataImpl implements PlayerData {

    private final UUID uuid;

    private long timer;

    private final ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();

    public PlayerDataImpl(UUID uuid, long timer) {
        this.uuid = uuid;
        this.timer = timer;
    }

    @Override
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    @Override
    public @NotNull UUID getUUID() {
        return this.uuid;
    }

    @Override
    public long getTimer() {
        return this.timer;
    }

    @Override
    public int getCollectAmount(@NotNull Material type) {
        return cache.getOrDefault(type.name(), 0);
    }

    @Override
    public void setCollectAmount(@NotNull Material type, int amount) {
        cache.compute(type.name(), (key, value) -> amount);
    }

    @Override
    public boolean addCollectAmount(@NotNull Material type, int amount) {
        return cache.computeIfPresent(type.name(), (key, value) -> value + amount) != null;
    }

    @Override
    public void inisLimit(@NotNull Material type, int def) {
        if (!addCollectAmount(type, def)) {
            setCollectAmount(type, def);
        }
    }

    @Override
    public boolean reset(@NotNull Material type) {
        return cache.remove(type.name()) != null;
    }

    @Override
    public void resetAll() {
        cache.clear();
        this.timer = Utils.getTodayTime();
    }

    @Override
    public String toJson() {
        return DataManger.gsonBuilder.create().toJson(this);
    }
}
