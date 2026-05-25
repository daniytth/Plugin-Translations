package com.artillexstudios.axcrates.keys;

import com.artillexstudios.axapi.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Key(String name, Config settings, ItemStack item, ItemStack original, List<Component> lore) {
}
