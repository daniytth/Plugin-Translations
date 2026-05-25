package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    private final CrateReward reward;

    public ItemEditor(Player player, EditorBase lastGui, Crate crate, CrateReward reward) {
        super(player, Gui.gui()
                .disableItemSwap()
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create()
        );
        this.lastGui = lastGui;
        this.crate = crate;
        this.reward = reward;
    }

    public void open() {
        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        final ItemStack item = reward.getDisplay().clone();
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Item Megkapása",
                "&#FF4400&l> &#FF4400Kattintás Item Tartás Közben &8- &#FF4400Megjelenő Item Cserélése"
        );
        super.addCustom(item,
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                        ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(reward.getDisplay().clone()), player.getLocation());
                        return;
                    }

                    reward.setDisplay(event.getCursor());
                    event.getCursor().setAmount(0);
                    crate.getCrateRewards().save();
                    open();
                },
                "4"
        );

        super.addOpenMenu(makeItem(
                        Material.GOLD_INGOT,
                        "&#FF4400&lTárgy Jutalmak"
                ),
                new ItemRewardEditor(player, this, crate, reward),
                "22"
        );

        final List<String> lore = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Jelenlegi Parancsok:"
        ));

        for (String cmd : reward.getCommands()) {
            lore.add("&#DDDDDD/" + cmd);
        }

        super.addInputMultiText(makeItem(
                        Material.COMMAND_BLOCK,
                        "&#FF4400&lParancs Jutalmak",
                        lore.toArray(new String[0])
                ),
                reward.getCommands(),
                strings -> {
                    reward.setCommands(strings);
                    crate.getCrateRewards().save();
                    open();
                },
                "24"
        );

        super.addInputText(makeItem(
                        Material.DIAMOND,
                        "&#FF4400&lEsély",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi esély: &f" + reward.getChance() + "%"
                ),
                "&#FF6600Írd be az új esélyt: &#DDDDDD(írd azt, hogy &#FF6600cancel &#DDDDDDa leállításhoz)",
                current -> {
                    if (NumberUtils.isDouble(current)) {
                        reward.setChance(Double.parseDouble(current));
                        crate.getCrateRewards().save();
                    } else {
                        // todo: message, not a number
                    }
                    open();
                },
                "20"
        );

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
    }
}
