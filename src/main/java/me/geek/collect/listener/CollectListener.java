package me.geek.collect.listener;

import me.geek.collect.GeekCollectLimit;
import me.geek.collect.api.PlayerData;
import me.geek.collect.utils.Utils;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

/**
 * GeekCollectLimit
 * me.geek.collect.listener
 *
 * @author 老廖
 * @since 2023/10/3 9:30
 */
public class CollectListener implements Listener {

    @EventHandler
    public void breaks(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (GeekCollectLimit.checkWorld(player.getWorld())) {
            Material material = event.getBlock().getType();
            Optional.ofNullable(GeekCollectLimit.checkConfig(player, material))
                    .ifPresent(it -> {
                        PlayerData playerData = GeekCollectLimit.getDataManger().getPlayerData(player.getUniqueId());
                        if (playerData == null) return;
                        int amount = playerData.getCollectAmount(material);
                        if (amount == 0) {
                            playerData.setCollectAmount(material, 1);
                          //  player.sendMessage("DEBUG is 0 -> " + it.getDisplayName() + " +1 NOW -> " + playerData.getCollectAmount(material) + "/" + it.getAmount());

                        } else if (amount >= it.getAmount()) {
                            event.setCancelled(true);
                            Utils.sendLang(player, "收集失败-已达上限", it.getDisplayName());
                           // player.sendMessage("破坏物品失败 " + it.getDisplayName() + " 以达收集上限...");

                        } else {
                            playerData.addCollectAmount(material, 1);
                          //  player.sendMessage("DEBUG -> " + it.getDisplayName() + " +1 NOW -> " + playerData.getCollectAmount(material) + "/" + it.getAmount());

                        }
                    });
        }
    }

}
