package eu.internetpolice.hooks;

import eu.internetpolice.BiemBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HookManager {
    private final BiemBlocks plugin;
    private final Map<String, PluginHook> loadedHooks = new HashMap<>();

    public HookManager(@NotNull BiemBlocks plugin) {
        this.plugin = plugin;
    }

    public boolean loadPluginHook(@NotNull PluginHook hook) {
        if (hook.onHook()) {
            loadedHooks.put(hook.getName(), hook);
            plugin.getLogger().info("Succesfully hooked into: " + hook.getName());
            return true;
        }
        plugin.getLogger().warning("Failed to load hook for plugin: " + hook.getName());
        return false;
    }

    public boolean unloadPluginHook(@NotNull PluginHook hook) {
        if (hook.onUnhook()) {
            loadedHooks.remove(hook.getName());
            return true;
        }
        plugin.getLogger().warning("Failed to unload hook for plugin: " + hook.getName());
        return false;
    }

    public Optional<PluginHook> getPluginHook(@NotNull String name) {
        return Optional.ofNullable(loadedHooks.get(name.replace(' ', '_')));
    }
}
