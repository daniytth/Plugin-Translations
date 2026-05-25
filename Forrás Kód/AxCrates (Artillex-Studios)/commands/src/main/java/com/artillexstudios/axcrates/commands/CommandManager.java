package com.artillexstudios.axcrates.commands;

import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.commands.parameters.CrateParameter;
import com.artillexstudios.axcrates.commands.parameters.KeyParameter;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.commands.utils.CommandExceptions;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class CommandManager {
    private static Lamp<BukkitCommandActor> handler = null;

    public static void load() {
        Lamp.Builder<BukkitCommandActor> builder = BukkitLamp.builder(AxCrates.getInstance());

        builder.parameterTypes(parameters -> {
            parameters.addParameterType(Key.class, new KeyParameter());
            parameters.addParameterType(Crate.class, new CrateParameter());
        });

        builder.exceptionHandler(new CommandExceptions());

        handler = builder.build();

        reload();
    }

    public static void reload() {
        handler.unregisterAllCommands();

        handler.register(new MainCommand());
    }
}
