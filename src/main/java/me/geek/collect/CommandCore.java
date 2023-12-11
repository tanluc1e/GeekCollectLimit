package me.geek.collect;

import me.geek.collect.api.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GeekCollectLimit
 * me.geek.collect
 *
 * @author 老廖
 * @recoded by TanLuc
 * @since 2023/10/3 10:20
 */
public class CommandCore implements TabExecutor {
    private final List<String> empty = Collections.emptyList();

    private final List<String> tab = Arrays.asList("reload", "resetAll", "reset");

    /*
        gcl reload
        gcl resetAll player
        gcl reset player type
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 || !sender.hasPermission("geek.collect.admin")) return false;

        int length = args.length;

        if (args[0].equals(tab.get(0))) {
            GeekCollectLimit.reload();
            sender.sendMessage("Configuration file has been reloaded...");
            return true;
        } else if (length > 1 && args[0].equals(tab.get(1))) {
            Player player = Bukkit.getPlayerExact(args[1]);
            if (player != null) {
                PlayerData data = GeekCollectLimit.getPlayerData(player.getUniqueId());
                if (data != null) {
                    data.resetAll();
                    sender.sendMessage("Reset " + player.getName() + " data successfully");
                }
            } else {
                sender.sendMessage("This player does not exist");
            }
        } else if (length > 2 && args[0].equals(tab.get(2))) {
            Player player = Bukkit.getPlayerExact(args[1]);
            Material material = Material.valueOf(args[2]);
            if (player != null) {
                PlayerData data = GeekCollectLimit.getPlayerData(player.getUniqueId());
                if (data != null && data.reset(material)) {
                    sender.sendMessage("Reset  " + player.getName() + " of "+ material.name() +" sucessfully");
                }
            } else {
                sender.sendMessage("This player does not exist");
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;
        switch (length) {
            case 1: {
                return empty;
            }
            case 2: {
                if (args[0].equals(tab.get(1))) {
                    return empty;
                } else {
                    return Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                }
            }
            case 3: {
                if (args[0].equals(tab.get(2))) {
                    return GeekCollectLimit.getConfigKeys(true)
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}
