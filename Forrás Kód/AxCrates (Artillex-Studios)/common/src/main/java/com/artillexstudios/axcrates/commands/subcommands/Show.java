package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.previews.impl.PreviewGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;

import java.io.File;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Show {
    INSTANCE;

    public void execute(CommandSender sender, Crate crate, @Nullable Player player) {
        if (player == null) {
            if (!(sender instanceof Player pl)) throw new CommandErrorException("must-be-player");
            player = pl;
        }

        final File preview = new File(AxCrates.getInstance().getDataFolder(), "previews/" + crate.previewTemplate + ".yml");
        if (preview.exists()) {
            new PreviewGui(new Config(preview), crate).open(player);
        } else {
            MESSAGEUTILS.sendLang(player, "errors.no-preview", Map.of("%crate%", crate.displayName));
        }
    }
}
