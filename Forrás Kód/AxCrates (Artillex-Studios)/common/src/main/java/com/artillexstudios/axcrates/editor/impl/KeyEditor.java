package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.ItemUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyEditor extends EditorBase {
    private final EditorBase lastGui;
    public KeyEditor(Player player, EditorBase lastGui) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .pageSize(36)
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lKulcsok"))
                .create()
        );
        this.lastGui = lastGui;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        ((PaginatedGui) gui).clearPageItems();

        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        KeyManager.getKeys().forEach((key, value) -> {
            final List<String> lore = new ArrayList<>();
            if (value.item().getItemMeta() != null && value.item().getItemMeta().getLore() != null)
                lore.addAll(value.item().getItemMeta().getLore());
            lore.addAll(Arrays.asList("",
                    "&#DDDDDDɪᴅ: " + key,
                    "&#FF4400&l> &#FF4400Kattintás &8- &#EE4400Kulcs Megszerzése",
                    "&#FF4400&l> &#FF4400Shift + Bal Kattintás &8- &#EE4400Eredeti Item Megszerzése",
                    "&#FF4400&l> &#FF4400Shift + Jobb Kattintás &8- &#EE4400Kulcs Törlése"));
            final ItemStack item = ItemBuilder.create(value.item().clone())
                    .setLore(lore)
                    .get();

            super.addCustom(item, event -> {
                if (event.isRightClick() && event.isShiftClick()) {
                    final File fl = new File(AxCrates.getInstance().getDataFolder(), "keys/" + key + ".yml");
                    fl.delete();
                    KeyManager.refresh();
                    open();
                    return;
                }

                if (event.isLeftClick() && event.isShiftClick()) {
                    ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(value.original()), player.getLocation());
                    return;
                }

                ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(value.item()), player.getLocation());
            });
        });

        super.addCustom(makeItem(
                        Material.BELL,
                        "&#FF4400&lÚj Kulcs",
                        " ",
                        " &7- &fTarts egy tárgyat a kurzorodon",
                        " &7- &fmajd kattints az új kulcs létrehozásához.",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Új kulcs létrehozása"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;
                    final SignInput signGUI = new SignInput.Builder().setLines(StringUtils.formatList(List.of("",
                            "-----------",
                            "Írd le az új",
                            "kulcsnak a nevét!"))
                    ).setHandler((player1, result) -> {
                        String name = result[0];
                        if (name.isBlank()) return;
                        final Config config = new Config(new File(AxCrates.getInstance().getDataFolder(), "keys/" + name + ".yml"));
                        ItemUtils.saveItem(event.getCursor(), config, "item");
                        config.save();
                        KeyManager.refresh();
                        Scheduler.get().run(scheduledTask -> open());
                    }).build(player);
                    signGUI.open();
                },
                "4"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza"
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
                    ((PaginatedGui) gui).previous();
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
                    ((PaginatedGui) gui).next();
                },
                "51"
        );

        gui.open(player);
    }
}
