package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class BreakListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (!event.getPlayer().isSneaking()) return;

        for (Crate crate : CrateManager.getCrates().values()) {
            for (PlacedCrate placedCrate : crate.getPlacedCrates()) {
                if (!placedCrate.getLocation().equals(DynamicLocation.of(event.getBlock().getLocation()))) continue;

                event.setCancelled(true);

                List<DynamicLocation> locations = new ArrayList<>(crate.placedLocations);
                locations.remove(placedCrate.getLocation());

                ArrayList<String> locs = new ArrayList<>();
                for (DynamicLocation location : locations) {
                    locs.add(DynamicLocation.serialize(location));
                }
                crate.settings.set("placed.locations", locs);
                crate.settings.save();
                crate.reload();
                MESSAGEUTILS.sendLang(event.getPlayer(), "editor.removed-location");
                return;
            }
        }
    }
}
