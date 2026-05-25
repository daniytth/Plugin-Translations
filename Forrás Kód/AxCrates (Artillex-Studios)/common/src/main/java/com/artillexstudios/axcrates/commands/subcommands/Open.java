package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.crates.Crate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Open {
    INSTANCE;

    public void execute(CommandSender sender, Crate crate, Player player, Integer amount, boolean force, boolean silent) {
        if (amount == null) amount = 1;
        crate.open(player, amount, silent, force, null, null);
    }
}
