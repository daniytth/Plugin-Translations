package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    
    public HologramEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create());
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        boolean enabled = crate.placedHologramEnabled;
        super.addInputBoolean(makeItem(
                        enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lHologram Emgedélyezéve",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi érték: &f" + enabled
                ),
                enabled,
                bool -> {
                    crate.settings.set("placed.hologram.enabled", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "4"
        );

        float offsetX = crate.placedHologramOffsetX;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lHelyeltérés X",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi értéke: &f" + String.format("%.1f", offsetX) + " blokkok"
                ),
                offsetX,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.x", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "19"
        );

        float offsetY = crate.placedHologramOffsetY;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lHelyeltérés Y",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi értéke: &f" + String.format("%.1f", offsetY) + " blokkok"
                ),
                offsetY,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.y", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        float offsetZ = crate.placedHologramOffsetZ;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lHelyeltérés Z",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi értéke: &f" + String.format("%.1f", offsetZ) + " blokkok"
                ),
                offsetZ,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.z", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "23"
        );

        final List<String> lore = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Jelenlegi értéke:"
        ));

        List<String> lines = crate.placedHologramLines;
        for (String str : lines) {
            lore.add("&f" + str);
        }
        super.addInputMultiText(makeItem(
                        Material.ANVIL,
                        "&#FF4400&lHologram Sorok",
                        lore.toArray(new String[0])

                ),
                lines,
                strings -> {
                    crate.settings.set("placed.hologram.lines", strings);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "24"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Vissta az előző menűbe"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
