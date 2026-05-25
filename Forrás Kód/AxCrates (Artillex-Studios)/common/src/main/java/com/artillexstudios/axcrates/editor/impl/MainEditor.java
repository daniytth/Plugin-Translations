package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.subcommands.Reload;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainEditor extends EditorBase {
    public MainEditor(Player player) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lFő"))
                .create()
        );
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        super.addOpenMenu(makeItem(
                        Material.CHEST,
                        "&#FF4400&lLádák"
                ),
                new CrateEditor(player, this),
                "20"
        );

        super.addOpenMenu(makeItem(
                        Material.RED_CANDLE,
                        "&#FF4400&lKulcsok"
                ),
                new KeyEditor(player, this),
                "24"
        );

        super.addCustom(makeItem(
                        Material.LIME_DYE,
                        "&#FF4400&lÚjratöltés"
                ),
                event -> {
                    player.closeInventory();
                    Reload.INSTANCE.execute(player);
                },
                "22"
        );

        super.addCustom(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBezárás",
                        "",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Leltár Bezárása"
                ),
                event -> {
                    player.closeInventory();
                },
                "49"
        );

        gui.open(player);
    }
}
