package me.geek.collect.papi;

import me.geek.collect.GeekCollectLimit;
import me.geek.collect.api.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;


/**
 * @warning Its not work with default value, its get player limit from permissions
 */
public class GetBlockLimit implements Placeholder {
    @Override
    public String process(Player player, String identifier) {
        if (player != null) {
            String materialName = identifier.toLowerCase();
            Material material = Material.matchMaterial(materialName);

            if (material != null) {
                PlayerData playerData = GeekCollectLimit.getDataManger().getPlayerData(player.getUniqueId());
                if (playerData != null) {
                    int blockLimit = GeekCollectLimit.checkConfig(player, material).getAmount();
                    return String.valueOf(blockLimit);
                }
            }
        }
        return "0";
    }
}
