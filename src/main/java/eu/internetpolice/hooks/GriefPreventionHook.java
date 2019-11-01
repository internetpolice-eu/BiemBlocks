package eu.internetpolice.hooks;

import eu.internetpolice.BiemBlocks;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionHook extends PluginHook {
    private GriefPrevention griefPrevention;

    public GriefPreventionHook(@NotNull BiemBlocks plugin) {
        super("GriefPrevention", plugin);
    }

    @Override
    public boolean onHook() {
        if (plugin.getServer().getPluginManager().getPlugin("GriefPrevention") == null) {
            plugin.getLogger().warning("The GriefPrevention plugin is not loaded.");
            return false;
        }

        griefPrevention = (GriefPrevention) plugin.getServer().getPluginManager().getPlugin("GriefPrevention");
        return true;
    }

    public void addClaimBlocks(Player player, int amount) {
        PlayerData playerData = griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        playerData.setBonusClaimBlocks(playerData.getBonusClaimBlocks() + amount);
        griefPrevention.dataStore.savePlayerData(player.getUniqueId(), playerData);
    }

    public double getBlockPrice() {
        return GriefPrevention.instance.config_economy_claimBlocksPurchaseCost;
    }
}
