package eu.internetpolice;

import eu.internetpolice.command.BuyclaimblocksCommand;
import eu.internetpolice.hooks.GriefPreventionHook;
import eu.internetpolice.hooks.HookManager;
import eu.internetpolice.hooks.LuckPermsHook;
import eu.internetpolice.hooks.PluginHook;
import eu.internetpolice.hooks.VaultHook;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BiemBlocks extends JavaPlugin {
    private HookManager hookManager;

    @Override
    public void onEnable() {
        hookManager = new HookManager(this);
        hookManager.loadPluginHook(new GriefPreventionHook(this));
        hookManager.loadPluginHook(new LuckPermsHook(this));
        hookManager.loadPluginHook(new VaultHook(this));

        getCommand("bbb").setExecutor(new BuyclaimblocksCommand(this));
    }

    public double getBlockPrice() {
        if (getHookManager().getPluginHook("GriefPrevention").isPresent()) {
            GriefPreventionHook gpHook = (GriefPreventionHook) getHookManager().getPluginHook("GriefPrevention").get();
            return gpHook.getBlockPrice();
        }

        return 10.0;
    }

    public void buyClaimblocks(Player player, int amount) {
        if (!getHookManager().getPluginHook("GriefPrevention").isPresent() ||
                !getHookManager().getPluginHook("LuckPerms").isPresent() ||
                !getHookManager().getPluginHook("Vault").isPresent()) {
            return;
        }

        GriefPreventionHook gpHook = (GriefPreventionHook) getHookManager().getPluginHook("GriefPrevention").get();
        LuckPermsHook lpHook = (LuckPermsHook) getHookManager().getPluginHook("LuckPerms").get();
        VaultHook vaultHook = (VaultHook) getHookManager().getPluginHook("Vault").get();

        double balance = vaultHook.getEconomy().getBalance(player);
        double totalCost = amount * getBlockPrice();

        if (totalCost > balance) {
            player.sendMessage(ChatColor.RED + "Error: you don't have enough money to buy " + amount +
                    " claim blocks. Price: $" + totalCost);
            return;
        }

        if (getUserBlocksBuyable(player) < amount) {
            player.sendMessage(ChatColor.RED + "Error: " + amount + " is over your limit, you have " +
                    getUserBlocksBuyable(player) + " claim blocks left to buy.");
            return;
        }

        vaultHook.getEconomy().withdrawPlayer(player, totalCost);
        gpHook.addClaimBlocks(player, amount);
        lpHook.addUserBoughtCount(player, amount);

        player.sendMessage(ChatColor.GOLD + "You have bought " + amount + " claim blocks for $" + totalCost + ".");
    }

    public int getUserBuyLimit(Player player) {
        Optional<PluginHook> hook = getHookManager().getPluginHook("LuckPerms");
        if (hook.isPresent()) {
            LuckPermsHook lpHook = (LuckPermsHook) hook.get();
            return lpHook.getUserBuyLimit(player);
        }

        return 0;
    }

    public int getUserBoughtCount(Player player) {
        Optional<PluginHook> hook = getHookManager().getPluginHook("LuckPerms");
        if (hook.isPresent()) {
            LuckPermsHook lpHook = (LuckPermsHook) hook.get();
            return lpHook.getUserBoughtCount(player);
        }

        return 0;
    }

    public int getUserBlocksBuyable(Player player) {
        int amount = getUserBuyLimit(player) - getUserBoughtCount(player);
        return Math.max(amount, 0);
    }

    @NotNull
    public HookManager getHookManager() {
        return hookManager;
    }
}
