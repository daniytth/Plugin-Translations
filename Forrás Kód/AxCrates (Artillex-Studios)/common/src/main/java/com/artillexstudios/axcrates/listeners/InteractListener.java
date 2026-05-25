package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.crates.previews.impl.PreviewGui;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class InteractListener implements Listener {
    public static final HashMap<Player, Consumer<DynamicLocation>> selectionLocations = new HashMap<>();
    private static final WeakHashMap<Player, Long> cooldowns = new WeakHashMap<>();

    @EventHandler (priority = EventPriority.LOW)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = event.getItem();

        String key;
        if (handItem != null && (key = new NBTWrapper(handItem).getString("axcrates-key")) != null) {
            event.setCancelled(true);
            final Key k = KeyManager.getKey(key);
            if (k != null) {
                for (Crate crate : CrateManager.getCrates().values()) {
                    if (!crate.keysAllowed.contains(k)) continue;
                    if (!crate.keyMode.equals("lootbox")) continue;

                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        final File preview = new File(AxCrates.getInstance().getDataFolder(), "previews/" + crate.previewTemplate + ".yml");
                        if (!crate.previewTemplate.equalsIgnoreCase("none") && preview.exists()) {
                            new PreviewGui(new Config(preview), crate).open(player);
                        } else {
                            MESSAGEUTILS.sendLang(player, "errors.no-preview", Map.of("%crate%", crate.displayName));
                        }
                        break;
                    }
                    final boolean multiOpen = CONFIG.getBoolean("multi-opening.enabled");
                    final int max = CONFIG.getInt("multi-opening.max");
                    crate.open(event.getPlayer(), (player.isSneaking() && multiOpen) ? max : 1, false, false, null, event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : event.getPlayer().getTargetBlock(new HashSet<>(), 5).getLocation());
                    break;
                }
            }
        }

        if (event.getClickedBlock() == null) return;

        DynamicLocation clickedBlock = DynamicLocation.of(event.getClickedBlock().getLocation());
        if (selectionLocations.containsKey(player)) {
            selectionLocations.remove(player).accept(clickedBlock);
            event.setCancelled(true);
            return;
        }

        for (Crate crate : CrateManager.getCrates().values()) {
            for (PlacedCrate placedCrate : crate.getPlacedCrates()) {
                if (!placedCrate.getLocation().equals(clickedBlock)) continue;
                if (interact(player, placedCrate, event.getAction())) event.setCancelled(true);
                return;
            }
        }
    }

    public static boolean interact(Player player, PlacedCrate placedCrate, Action action) {
        if (cooldowns.containsKey(player) && System.currentTimeMillis() - cooldowns.get(player) < 100) {
            return true;
        }
        cooldowns.put(player, System.currentTimeMillis());
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) return false;
            placedCrate.openPreview(player);
        } else {
            final boolean multiOpen = CONFIG.getBoolean("multi-opening.enabled");
            final int max = CONFIG.getInt("multi-opening.max");
            placedCrate.getCrate().open(player, (player.isSneaking() && multiOpen) ? max : 1, false, false, placedCrate, null);
        }
        return true;
    }
}
