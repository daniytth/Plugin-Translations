package com.artillexstudios.axcrates.hooks.models;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.entity.Player;

public interface ModelHook {

    String getName();

    void spawnCrate(PlacedCrate crate);

    void removeCrate(PlacedCrate crate);

    void open(Player player, PlacedCrate crate);

    void close(Player player, PlacedCrate crate);

    void join(Player player);

    void leave(Player player);
}
