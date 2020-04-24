package eu.internetpolice.hooks;

import eu.internetpolice.BiemBlocks;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class LuckPermsHook extends PluginHook {
    private LuckPerms api;

    public LuckPermsHook(@NotNull BiemBlocks plugin) {
        super("LuckPerms", plugin);
    }

    @Override
    public boolean onHook() {
        RegisteredServiceProvider<LuckPerms> rsp = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (rsp == null) {
            plugin.getLogger().warning("Failed to get LuckPermsApi rsp.");
            return false;
        }

        api = rsp.getProvider();
        return true;
    }

    public int getUserBoughtCount(Player player) {
        int amount = 0;

        QueryOptions options = api.getContextManager().getQueryOptions(player);
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String metaValue = user.getCachedData().getMetaData(options).getMetaValue("biemblocks-bought");
            if (metaValue != null) {
                try {
                    amount = Integer.parseInt(metaValue);
                } catch (NumberFormatException ignored) {}
            }
        }

        return amount;
    }

    public int getUserBuyLimit(Player player) {
        int amount = 0;

        QueryOptions options = api.getContextManager().getQueryOptions(player);
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String metaValue = user.getCachedData().getMetaData(options).getMetaValue("biemblocks-buylimit");
            if (metaValue != null) {
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
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            MetaNode oldNode = MetaNode.builder("biemblocks-bought",
                String.valueOf(getUserBoughtCount(player))).build();
            MetaNode newNode = MetaNode.builder("biemblocks-bought", amount.toString()).build();

            DataMutateResult oldResult = user.data().remove(oldNode);
            DataMutateResult newResult = user.data().add(newNode);
            api.getUserManager().saveUser(user);
        }
    }
}
