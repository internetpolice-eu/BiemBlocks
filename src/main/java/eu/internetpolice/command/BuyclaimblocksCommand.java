package eu.internetpolice.command;

import eu.internetpolice.BiemBlocks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuyclaimblocksCommand implements CommandExecutor {
    private BiemBlocks plugin;

    public BuyclaimblocksCommand(BiemBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only in-game players can use " + label);
            return true;
        }

        if (!sender.hasPermission("biemblocks.buyclaimblocks")) {
            sender.sendMessage(ChatColor.RED + "Error: you are not allowed to buy claim blocks.");
            return true;
        }

        if (args.length == 1) {
            int amount = 0;
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Error: invalid amount: " + amount);
            } else {
                plugin.buyClaimblocks((Player)sender, amount);
                return true;
            }
        }

        sender.sendMessage(ChatColor.WHITE + "You can buy claim blocks for $" + plugin.getBlockPrice() +
                " per block with a maximum of " + plugin.getUserBuyLimit((Player)sender));
        sender.sendMessage(ChatColor.WHITE + "Available to buy: " + plugin.getUserBlocksBuyable((Player)sender));
        sender.sendMessage(ChatColor.WHITE + "/" + label + " <amount>");
        return true;
    }
}
