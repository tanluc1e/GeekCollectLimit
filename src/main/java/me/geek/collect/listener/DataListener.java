package me.geek.collect.listener;


import me.geek.collect.DataManger;
import me.geek.collect.GeekCollectLimit;
import me.geek.collect.utils.Pair;
import me.geek.collect.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;



/**
 * GeekCollectLimit
 * me.geek.collect.listener
 *
 * @author 老廖
 * @since 2023/10/3 6:40
 */
public class DataListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DataManger dataManger = GeekCollectLimit.getDataManger();
        dataManger.addSelectTask(player);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DataManger dataManger = GeekCollectLimit.getDataManger();
        dataManger.addUpdateTask(player);
    }


}
