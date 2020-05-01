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

    private static final String KEY_BOUGHT_RANKED = "biemblocks-ranked";
    private static final String KEY_BOUGHT_USER = "biemblocks-bought";
    private static final String KEY_BUY_LIMIT = "biemblocks-buylimit";

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

    public int getRankBoughtCount(Player player) {
        int amount = 0;

        QueryOptions options = api.getContextManager().getQueryOptions(player);
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String metaValue = user.getCachedData().getMetaData(options).getMetaValue(KEY_BOUGHT_RANKED);
            if (metaValue != null) {
                try {
                    amount = Integer.parseInt(metaValue);
                } catch (NumberFormatException ignored) {}
            }
        }

        return amount;
    }

    public int getUserBoughtCount(Player player) {
        int amount = 0;

        QueryOptions options = api.getContextManager().getQueryOptions(player);
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            String metaValue = user.getCachedData().getMetaData(options).getMetaValue(KEY_BOUGHT_USER);
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
            String metaValue = user.getCachedData().getMetaData(options).getMetaValue(KEY_BUY_LIMIT);
            if (metaValue != null) {
                try {
                    amount = Integer.parseInt(metaValue);
                } catch (NumberFormatException ignored) {}
            }
        }

        return amount;
    }

    public void addRankBoughtCount(Player player, Integer increment) {
        int amount = getRankBoughtCount(player) + increment;
        setRankBoughtCount(player, amount);
    }

    public void addUserBoughtCount(Player player, Integer increment) {
        int amount = getUserBoughtCount(player) + increment;
        setUserBoughtCount(player, amount);
    }

    public void setRankBoughtCount(Player player, Integer amount) {
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            MetaNode oldNode = MetaNode.builder(KEY_BOUGHT_RANKED,
                String.valueOf(getRankBoughtCount(player))).build();
            MetaNode newNode = MetaNode.builder(KEY_BOUGHT_RANKED, amount.toString()).build();

            DataMutateResult oldResult = user.data().remove(oldNode);
            DataMutateResult newResult = user.data().add(newNode);
            api.getUserManager().saveUser(user);
        }
    }

    public void setUserBoughtCount(Player player, Integer amount) {
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            MetaNode oldNode = MetaNode.builder(KEY_BOUGHT_USER,
                String.valueOf(getUserBoughtCount(player))).build();
            MetaNode newNode = MetaNode.builder(KEY_BOUGHT_USER, amount.toString()).build();

            DataMutateResult oldResult = user.data().remove(oldNode);
            DataMutateResult newResult = user.data().add(newNode);
            api.getUserManager().saveUser(user);
        }
    }
}
