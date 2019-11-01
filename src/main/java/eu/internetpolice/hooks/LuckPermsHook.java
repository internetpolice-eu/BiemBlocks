package eu.internetpolice.hooks;

import eu.internetpolice.BiemBlocks;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class LuckPermsHook extends PluginHook {
    private LuckPermsApi api;

    public LuckPermsHook(@NotNull BiemBlocks plugin) {
        super("LuckPerms", plugin);
    }

    @Override
    public boolean onHook() {
        RegisteredServiceProvider<LuckPermsApi> rsp = plugin.getServer().getServicesManager().getRegistration(LuckPermsApi.class);
        if (rsp == null) {
            plugin.getLogger().warning("Failed to get LuckPermsApi rsp.");
            return false;
        }

        api = rsp.getProvider();
        return true;
    }

    public int getUserBoughtCount(Player player) {
        int amount = 0;

        User user = api.getUser(player.getUniqueId());
        if (user != null) {
            Contexts ctx = api.getContextsForPlayer(player);
            String metaValue = user.getCachedData().getMetaData(ctx).getMeta().getOrDefault("biemblocks-bought", "0");

            try {
                amount = Integer.parseInt(metaValue);
            } catch (NumberFormatException ignored) {}
        }

        return amount;
    }

    public int getUserBuyLimit(Player player) {
        int amount = 0;

        User user = api.getUser(player.getUniqueId());
        if (user != null) {
            Contexts ctx = api.getContextsForPlayer(player);
            Group userGroup = api.getGroup(user.getPrimaryGroup());
            if (userGroup != null) {
                String metaValue = userGroup.getCachedData().getMetaData(ctx).getMeta()
                        .getOrDefault("biemblocks-buylimit", "0");

                try {
                    amount = Integer.parseInt(metaValue);
                } catch (NumberFormatException ignored) {}
            }
        }

        return amount;
    }

    public void addUserBoughtCount(Player player, Integer increment) {
        int amount = getUserBoughtCount(player) + increment;
        setUserBoughtCount(player, amount);
    }

    public void setUserBoughtCount(Player player, Integer amount) {
        User user = api.getUser(player.getUniqueId());
        if (user != null) {
            Node oldNode = api.getNodeFactory().makeMetaNode("biemblocks-bought",
                    String.valueOf(getUserBoughtCount(player))).build();
            Node newNode = api.getNodeFactory().makeMetaNode("biemblocks-bought", amount.toString()).build();

            user.unsetPermission(oldNode);
            user.setPermission(newNode);
            api.getUserManager().saveUser(user);
        }
    }
}
