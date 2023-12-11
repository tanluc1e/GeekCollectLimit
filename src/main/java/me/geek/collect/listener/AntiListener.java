package me.geek.collect.listener;

import me.geek.collect.GeekCollectLimit;
import me.geek.collect.api.PlayerData;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * GeekCollectLimit
 * me.geek.collect.listener
 *
 * @author 老廖
 * @since 2023/10/3 13:57
 */
// TODO("这个类未完成，暂时没有很好的解决方案，需要放置 TNT、漏斗、活塞等途径破坏方块被玩家拾取...")
public final class AntiListener implements Listener  {

    /**
     * 记录玩家扔出的物品，
     */
    private final HashMap<UUID, Item> collect = new HashMap<>();

    /**
     * 记录玩家放置的漏斗
     */
    private final HashMap<InventoryHolder, UUID> hopper = new HashMap<>();

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        // GeekCollectLimit.log("PlayerDropItemEvent");
        //  event.getItemDrop().setMetadata("collect-limit", new FixedMetadataValue(plugin, true));
        collect.put(event.getItemDrop().getUniqueId(), event.getItemDrop());
    }

    @EventHandler
    public void pick(InventoryPickupItemEvent event) {
        // GeekCollectLimit.log("InventoryPickupItemEvent");
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Hopper) {
            if (hopper.containsKey(holder)) {
                Item item = event.getItem();
                // 只要不是玩家扔的就禁止，
                if (!collect.containsKey(item.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        Material material = event.getBlock().getType();
        if (material == Material.HOPPER || material == Material.HOPPER_MINECART) {
            InventoryHolder holder = (Hopper) event.getBlock().getState();
            // 添加作弊漏斗
            hopper.put(holder, event.getPlayer().getUniqueId());
        }
    }


    @EventHandler
    public void pick(EntityPickupItemEvent event) {
        //  GeekCollectLimit.log("EntityPickupItemEvent");
        if (event.getEntity() instanceof Player) {

            Item item = event.getItem();

            //if (!item.hasMetadata("collect-limit")) {
            if (!collect.containsKey(item.getUniqueId())) {
                Player player = (Player) event.getEntity();
                Material material = item.getItemStack().getType();
                Optional.ofNullable(GeekCollectLimit.checkConfig(player, material))
                        .ifPresent(it -> {
                            PlayerData playerData = GeekCollectLimit.getDataManger().getPlayerData(player.getUniqueId());
                            if (playerData == null) return;
                            if (playerData.getCollectAmount(material) >= it.getAmount()) {
                                event.setCancelled(true);
                                player.sendMessage("拾取物品失败 " + it.getDisplayName() + " 以达收集上限...");
                            }
                        });
            } else {
                // 有说明是玩家自己扔的
                collect.remove(item.getUniqueId());
            }
        }
    }

    @EventHandler
    public void breaks(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        // 移除作弊漏斗
        if (material == Material.HOPPER || material == Material.HOPPER_MINECART) {
            hopper.remove((Hopper) event.getBlock().getState());
        }
    }
}
