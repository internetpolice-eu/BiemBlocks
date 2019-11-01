package eu.internetpolice.hooks;

import eu.internetpolice.BiemBlocks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHook extends PluginHook {
    private Economy economy;

    public VaultHook(@NotNull BiemBlocks plugin) {
        super("Vault", plugin);
    }

    @Override
    public boolean onHook() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("The Vault plugin is not loaded.");
            return false;
        }

        return setupEconomy();
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("Failed to get Vault Economy rsp.");
            return false;
        }

        economy = rsp.getProvider();
        return true;
    }
}
