package me.geek.collect.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.geek.collect.GeekCollectLimit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GeekCollectLimitPAPIExpansion extends PlaceholderExpansion {

    private final GeekCollectLimit plugin;
    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public GeekCollectLimitPAPIExpansion(GeekCollectLimit plugin) {
        this.plugin = plugin;
        register(); // Ensure the register method is called here or in your main plugin class.
    }

    @Override
    public String getIdentifier() {
        return "gcl";
    }

    @Override
    public String getAuthor() {
        return "TanLuc";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.startsWith("blocks")) {
            GetBlockCount getBlockCount = new GetBlockCount(plugin);
            System.out.println("MATCH gcl_blocks");
            return getBlockCount.process(player, identifier);
        } else if (identifier.startsWith("blocks_")) {
            System.out.println("MATCH blocks_");
        }

        String materialName = identifier.toLowerCase();
        Material material = Material.matchMaterial(materialName);

        Placeholder placeholder = placeholders.get(materialName);


            if (placeholder != null) {
                return placeholder.process(player, identifier);
            }


        System.out.println("ONPLACE MATERIAL NAME " + materialName);
        System.out.println("ONPLACE MATERIAL " + material);
        return null;
    }
}
