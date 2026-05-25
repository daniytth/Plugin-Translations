package com.artillexstudios.axcrates.crates.previews.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.items.component.type.ItemLore;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.previews.GuiFrame;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.utils.SoundUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreviewGui extends GuiFrame {
    private final PaginatedGui gui;
    private final Crate crate;

    public PreviewGui(Config settings, Crate crate) {
        super(settings);
        this.crate = crate;
        this.gui = Gui.paginated()
                .disableAllInteractions()
                .title(StringUtils.format(settings.getString("title")
                        .replace("%crate%", crate.displayName)))
                .pageSize(settings.getInt("page-size", 36))
                .rows(settings.getInt("rows", 6))
                .create();
        setGui(gui);
        update();
    }

    public void update() {
        super.createItem("filler");

        gui.clearPageItems();
        if (gui.getPagesNum() > 1) {
            super.createItem("previous-page", event -> {
                SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("previous-page.sound"));
                gui.previous();
            });

            super.createItem("next-page", event -> {
                SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("next-page.sound"));
                gui.next();
            });
        }

        super.createItem("close", event -> {
            SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("close.sound"));
            event.getWhoClicked().closeInventory();
        });

        for (CrateTier tier : crate.getCrateRewards().getTiers().values()) {
            for (CrateReward reward : tier.getRewards()) {
                gui.addItem(new GuiItem(makeReward(reward.getDisplay().clone(), tier, reward)));
            }
        }
    }

    public void open(Player player) {
        open(1, player);
    }

    public void open(int page, Player player) {
        gui.open(player, page);
    }

    public ItemStack makeReward(ItemStack it, CrateTier tier, CrateReward reward) {
        List<String> loreText = file.getStringList("reward");
        List<Component> lore = new ArrayList<>();

        WrappedItemStack wrap = WrappedItemStack.wrap(it);

        Map<String, String> replacements = Map.of(
                "%chance%", "" + reward.getChance(),
                "%tier%", tier.getName()
        );

        for (String line : loreText) {
            if (line.equals("%lore%")) {
                lore.addAll(wrap.get(DataComponents.lore()).lines());
            } else {
                lore.add(StringUtils.format(line, replacements));
            }
        }

        wrap.set(DataComponents.lore(), new ItemLore(lore));

        return wrap.toBukkit();
    }
}
