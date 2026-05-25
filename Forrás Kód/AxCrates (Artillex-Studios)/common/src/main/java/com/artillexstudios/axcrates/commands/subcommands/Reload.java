package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.lang.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Reload {
    INSTANCE;

    public void execute(CommandSender sender) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400[AxCrates] &#FFAA00Konfiguráció újratöltése..."));
        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400╠ &#FFAA00Újratöltve: &fconfig.yml&#FFAA00!"));

        if (!LANG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "lang.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400╠ &#FFAA00Újratöltve: &flang.yml&#FFAA00!"));

        LanguageManager.reload();

        // make sure to first load all the keys
        KeyManager.refresh();
        CrateManager.refresh();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400╚ &#FFAA00Sikeres újratöltés!"));
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }
}
