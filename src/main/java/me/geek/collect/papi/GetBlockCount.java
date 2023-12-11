package me.geek.collect.papi;

import me.geek.collect.GeekCollectLimit;
import me.geek.collect.api.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GetBlockCount implements Placeholder {
    private final GeekCollectLimit plugin;

    public GetBlockCount(GeekCollectLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public String process(Player player, String identifier) {
        if (player != null) {

            String materialName = identifier.toLowerCase().replace("blocks_", "");
            Material material = Material.matchMaterial(materialName);

            if (material != null) {
                PlayerData playerData = plugin.getPlayerData(player.getUniqueId());
                if (playerData != null) {
                    int collectAmount = playerData.getCollectAmount(material);
                    return String.valueOf(collectAmount);
                }
            }

            System.out.println("MATERIAL " + material);
            System.out.println("MATERIAL NAME " + materialName);
        }
        return "0";
    }
}
