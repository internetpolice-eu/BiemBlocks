package eu.internetpolice.command;

import eu.internetpolice.BiemBlocks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankblocksCommand implements CommandExecutor {
    private final BiemBlocks plugin;

    protected final String REGEX_VALID_NAME = "[a-zA-Z0-9_]{1,16}";

    public RankblocksCommand(BiemBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("biemblocks.rankblocks") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Error: You don't have permission for this command.");
            return true;
        }

        // rankblocks <target> <blockcount>
        if (args.length == 2) {
            if (isValidPlayerName(args[0])) {
                Player target = plugin.getServer().getPlayerExact(args[0]);
                if (target != null) {
                    int amount = getAmount(args[1]) - plugin.getUserRankedCount(target);

                    if (amount > 0) {
                        plugin.giveRankblocks(target, amount);
                        sender.sendMessage(ChatColor.GOLD + String.format("You have given %d blocks to %s",
                            amount, target.getDisplayName()));
                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "Error: Invalid amount: " + amount);
                    return true;
                }

                sender.sendMessage(ChatColor.RED + "Error: The specified player cannot be found: " + args[0]);
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Error: Invalid player name: " + args[0]);
            return true;
        }
        return false;
    }

    /**
     * Checks if the given String is a valid playername.
     *
     * @param input String to check.
     * @return True if valid, false otherwise.
     */
    protected boolean isValidPlayerName(@NotNull String input) {
        return input.matches(REGEX_VALID_NAME);
    }

    private int getAmount(String amount) {
        try {
            return Integer.parseInt(amount);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
