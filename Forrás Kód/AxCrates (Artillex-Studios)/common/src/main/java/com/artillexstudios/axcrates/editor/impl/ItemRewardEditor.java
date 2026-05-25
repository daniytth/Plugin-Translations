package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class ItemRewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    private final CrateReward reward;

    public ItemRewardEditor(Player player, EditorBase lastGui, Crate crate, CrateReward reward) {
        super(player, Gui.storage()
                .disableItemSwap()
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create());
        this.lastGui = lastGui;
        this.crate = crate;
        this.reward = reward;
    }

    public void open() {
        gui.setDefaultTopClickAction(event -> {
            if (event.getSlot() < 9 || event.getSlot() > 44) {
                event.setCancelled(true);
                return;
            }
        });

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        gui.setCloseGuiAction(event -> {
            final LinkedList<ItemStack> items = new LinkedList<>();
            int slot = 0;
            for (ItemStack it : gui.getInventory()) {
                if (gui.getGuiItem(slot++) != null) continue;
                if (it == null) continue;
                items.add(it.clone());
            }
            reward.setItems(items);
            crate.getCrateRewards().save();
            Scheduler.get().run(scheduledTask -> lastGui.open());
        });

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Vissza a menűbe"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
        int n = 9;
        for (ItemStack it : reward.getItems()) {
            gui.getInventory().setItem(n, it);
            n++;
        }
    }
}
