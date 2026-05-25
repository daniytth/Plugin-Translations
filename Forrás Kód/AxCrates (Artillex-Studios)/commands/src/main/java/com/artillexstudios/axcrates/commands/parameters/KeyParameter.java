package com.artillexstudios.axcrates.commands.parameters;

import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class KeyParameter implements ParameterType<BukkitCommandActor, Key> {

    @Override
    public Key parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        Key key = KeyManager.getKey(name);
        if (key == null) throw new CommandErrorException("Kulcs nem található: " + name);
        return key;
    }

    @NotNull
    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return (context) -> KeyManager.getKeys().keySet();
    }
}
