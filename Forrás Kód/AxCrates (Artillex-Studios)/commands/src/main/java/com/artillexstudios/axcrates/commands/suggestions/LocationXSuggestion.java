package com.artillexstudios.axcrates.commands.suggestions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class LocationXSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        final Player player = context.actor().asPlayer();
        final List<String> list = new ArrayList<>();
        if (player == null) return list;
        final Location pl = player.getLocation();
        list.add("" + (pl.getBlockX() + 0.5f));
        list.add((pl.getBlockX() + 0.5f) + " " + pl.getBlockY() + " " + (pl.getBlockZ() + 0.5f));
        final Block b = player.getTargetBlockExact(5);
        if (b == null) return list;
        list.add("" + (b.getX() + 0.5f));
        list.add((b.getX() + 0.5f) + " " + (b.getY() + 1.0f) + " " + (b.getZ() + 0.5f));
        return list;
    }
}