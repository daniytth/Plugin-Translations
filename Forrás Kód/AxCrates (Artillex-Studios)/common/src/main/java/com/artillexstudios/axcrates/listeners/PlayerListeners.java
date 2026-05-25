package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.hooks.HookManager;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;
import java.util.function.Consumer;

public class PlayerListeners implements Listener {
    private static final WeakHashMap<Player, Consumer<String>> inputs = new WeakHashMap<>();

    public static WeakHashMap<Player, Consumer<String>> getInputs() {
        return inputs;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        for (ModelHook modelHook : HookManager.getModelHooks()) {
            modelHook.join(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        for (ModelHook modelHook : HookManager.getModelHooks()) {
            modelHook.leave(event.getPlayer());
        }
    }

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Consumer<String> consumer = inputs.remove(event.getPlayer());
        if (consumer == null) return;
        Scheduler.get().run(scheduledTask -> consumer.accept(event.getMessage()));
        event.getPlayer().sendMessage(StringUtils.formatToString("&f" + event.getMessage()));
        event.setCancelled(true);
    }
}
