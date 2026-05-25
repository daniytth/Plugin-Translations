package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AllowedKeyEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;

    public AllowedKeyEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.paginated()
                .disableAllInteractions()
                .rows(6)
                .pageSize(36)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create()
        );
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        final PaginatedGui paginatedGui = (PaginatedGui) gui;
        paginatedGui.clearPageItems();

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        int am = 0;
        for (Key key : KeyManager.getKeys().values()) {
            if (crate.keysAllowed.contains(key)) {
                final ItemStack item = key.original().clone();
                extendLore(item,
                        "",
                        "&#DDDDDDɪᴅ: " + key.name(),
                        "&#DDDDDDKiválasztva",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400A Kijelölés Visszavonása"
                );
                am++;
                super.addCustom(item,
                        event -> {
                            final ArrayList<String> keys = new ArrayList<>();
                            for (Key key2 : KeyManager.getKeys().values()) {
                                if (crate.keysAllowed.contains(key2))
                                    keys.add(key2.name());
                            }
                            keys.remove(key.name());
                            crate.settings.set("key.allowed", keys);
                            crate.settings.save();
                            crate.refreshSettings();
                            open();
                        }
                );
            }
        }

        if (am != 0) { // add padding
            while (am++ % 9 != 0) {
                paginatedGui.addItem(new GuiItem(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                )));
            }

            for (int i = 0; i < 9; i++) {
                paginatedGui.addItem(new GuiItem(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                )));
            }
        }

        for (Key key : KeyManager.getKeys().values()) {
            if (crate.keysAllowed.contains(key)) continue;
            final ItemStack item = key.original().clone();
            extendLore(item,
                    "",
                    "&#DDDDDDɪᴅ: " + key.name(),
                    "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Kiválasztás"
            );
            super.addCustom(item,
                    event -> {
                        final ArrayList<String> keys = new ArrayList<>();
                        for (Key key2 : KeyManager.getKeys().values()) {
                            if (crate.keysAllowed.contains(key2))
                                keys.add(key2.name());
                        }
                        keys.add(key.name());
                        crate.settings.set("key.allowed", keys);
                        crate.settings.save();
                        crate.refreshSettings();
                        open();
                    }
            );
        }

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Vissza a menűbe" // todo: change all of this to the right menu
                ),
                lastGui,
                "49"
        );

        super.addCustom(makeItem(
                        Material.ARROW,
                        "&#FF4400&lElőző",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Előző Oldal"
                ),
                event -> {
                    paginatedGui.previous();
                },
                "47"
        );

        super.addCustom(makeItem(
                        Material.ARROW,
                        "&#FF4400&lKövetkezző",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Következző Oldal"
                ),
                event -> {
                    paginatedGui.next();
                },
                "51"
        );

        gui.open(player);
    }
}
