package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WorldListeners implements Listener {

    @EventHandler
    public void onLoad(@NotNull WorldLoadEvent event) {
        Scheduler.get().runLater(scheduledTask -> {
            for (PlacedCrate placedCrate : getAllPlaced()) {
                if (placedCrate.getLocation().getDynamicWorld().getName().equals(event.getWorld().getName())) {
                    placedCrate.spawn();
                }
            }
        }, 10);
    }

    @EventHandler
    public void unUnload(@NotNull WorldUnloadEvent event) {
        for (PlacedCrate placedCrate : getAllPlaced()) {
            if (placedCrate.getLocation().getDynamicWorld().getName().equals(event.getWorld().getName())) {
                placedCrate.remove();
            }
        }
    }

    private List<PlacedCrate> getAllPlaced() {
        List<PlacedCrate> list = new ArrayList<>();
        for (Crate crate : CrateManager.getCrates().values()) {
            list.addAll(crate.getPlacedCrates());
        }
        return list;
    }
}
