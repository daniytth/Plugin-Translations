package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public enum Drop {
    INSTANCE;

    public void execute(CommandSender sender, Key key, Location location, Integer amount, boolean withVelocity) {
        if (amount == null) amount = 1;

        final ItemStack item = key.item().clone();
        item.setAmount(amount);
        final Item it = location.getWorld().dropItem(location, item);
        if (!withVelocity) it.setVelocity(new Vector(0, 0, 0));
    }
}
