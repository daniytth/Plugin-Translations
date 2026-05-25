package com.artillexstudios.axcrates.hooks.other;

import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @NotNull
    @Override
    public String getAuthor() {
        return "ArtillexStudios";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "axcrates";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0-beta4";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String[] args = params.split("_");

        if (args.length == 2 && args[0].equalsIgnoreCase("keys")) {
            final Key key = KeyManager.getKey(args[1]);
            if (key == null) return "Érvénytelen Kulcs";
            return "" + AxCrates.getDatabase().getVirtualKeys(offlinePlayer, key);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("cratekeys")) {
            final Crate crate = CrateManager.getCrate(args[1]);
            if (crate == null) return "Érvénytelen Láda";
            int am = 0;
            for (Key key : crate.keysAllowed) {
                am += AxCrates.getDatabase().getVirtualKeys(offlinePlayer, key);
            }
            return "" + am;
        }

        return null;
    }
}
