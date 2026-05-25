package com.artillexstudios.axcrates.commands.parameters;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class CrateParameter implements ParameterType<BukkitCommandActor, Crate> {

    @Override
    public Crate parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        Crate crate = CrateManager.getCrate(name);
        if (crate == null) throw new CommandErrorException("Láda nem található: " + name);
        return crate;
    }

    @NotNull
    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return (context) -> CrateManager.getCrates().keySet();
    }
}
