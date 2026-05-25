package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.listeners.InteractListener;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class RewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;
    private int idx = 0;
    private CrateTier tier;

    public RewardEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .rows(6)
                .pageSize(36)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create()
        );
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        if (idx > crate.getCrateRewards().getTiers().size() - 1) idx = 0;
        else if (idx < 0) idx = crate.getCrateRewards().getTiers().size() - 1;
        tier = new ArrayList<>(crate.getCrateRewards().getTiers().values()).get(idx);

        final PaginatedGui rewardGui = (PaginatedGui) gui;
        rewardGui.clearPageItems();

        rewardGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        int rollAmount = tier.getRollAmount();
        super.addInputInteger(makeItem(
                        Material.ANVIL,
                        "&#FF4400&lEgyszerre Kiosztandó Jutalom Összege",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Értéke: &f" + rollAmount
                ),
                rollAmount,
                integer -> {
                    tier.setRollAmount(integer);
                    crate.getCrateRewards().save();
                    open();
                },
                "2"
        );

        super.addCustom(makeItem(
                        Material.BELL,
                        "&#FF4400&lTier",
                        " ",
                        "&#FF4400&l> &#FFCC00Kiválasztott: &f" + tier.getName(),
                        " ",
                        "&#FF4400&l> &#FF4400Bal Klikk &8- &#FF4400Következő tier",
                        "&#FF4400&l> &#FF4400Jobb Klikk &8- &#FF4400Előző tier",
                        "&#FF4400&l> &#FF4400Shift + Bal Klikk &8- &#FF4400Új tier létrehozása",
                        "&#FF4400&l> &#FF4400Shift + Jobb Klikk &8- &#FF4400Kiválasztott tier törlése"
                ),
                event -> {
                    if (event.isShiftClick() && event.isLeftClick()) {
                        final SignInput signGUI = new SignInput.Builder().setLines(StringUtils.formatList(List.of("",
                                "-----------",
                                "Írd le a",
                                "új tier nevét!"))
                        ).setHandler((player1, result) -> {
                            String name = result[0];
                            if (name.isBlank()) return;
                            crate.getCrateRewards().createNewTier(name);
                            crate.getCrateRewards().save();
                            Scheduler.get().run(scheduledTask -> open());
                        }).build(player);
                        signGUI.open();
                        return;
                    }

                    if (crate.getCrateRewards().getTiers().size() == 1) {
                        MESSAGEUTILS.sendLang(player, "editor.only-one-tier");
                        return;
                    }

                    if (event.isShiftClick() && event.isRightClick()) {
                        crate.getCrateRewards().getTiers().remove(tier.getName());
                        crate.getCrateRewards().save();
                        open();
                        return;
                    }

                    if (event.isLeftClick()) idx++;
                    else idx--;
                    open();
                },
                "4"
        );

        super.addCustom(makeItem(
                        Material.GOLD_INGOT,
                        "&#FF4400&lJutalom Hozzáadása",
                        " ",
                        "&#FF4400&l> &#FF4400Bal Kattintás Miközben Tartod Az Itemet &8- &#FF4400Új Tárgy Jutalom Hozzáadása",
                        "&#FF4400&l> &#FF4400Jobb Kattintás Miközben Tartod Az Itemet &8- &#FF4400Új Parancs Jutalom Hozzáadása"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                        MESSAGEUTILS.sendLang(player, "editor.hold-something");
                        return;
                    }
                    ItemStack cursor = event.getCursor().clone();
                    event.getCursor().setAmount(0);
                    if (event.isRightClick()) {
                        startConversation(
                                player,
                                "&#FF4400Írd ide a parancsot perjel (/) nélkül: &#DDDDDD(írd azt, hogy &#FF6600cancel &#DDDDDDa leállításhoz)\n" +
                                        "&#DDDDDD(használd a %player% helyőrzőt a játékos nevéhez)",
                                input -> {
                                    if (!input.equalsIgnoreCase("mégse")) {
                                        tier.addRewardCommand(cursor, input);
                                        crate.getCrateRewards().save();
                                    }

                                    open();
                                }
                        );
                    } else {
                        tier.addRewardItem(cursor);
                        crate.getCrateRewards().save();
                        open();
                    }
                },
                "6"
        );

        int i = 0;
        for (CrateReward reward : tier.getRewards()) {
            final ItemStack item = reward.getDisplay().clone();

            extendLore(item,
                    "",
                    "&#FF4400&l> &#FFCC00Esély: &f" + reward.getChance() + "%",
                    "",
                    "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Jutalom szerkesztése",
                    "&#FF4400&l> &#FF4400Dobás &8- &#FF4400Jutalom eltávolítása",
                    "&#FF4400&l> &#FF4400Shift + Bal klikk &8- &#FF4400Mozgatás balra",
                    "&#FF4400&l> &#FF4400Shift + Jobb klikk &8- &#FF4400Mozgatás jobbra"
            );

            int id = i;
            rewardGui.addItem(new GuiItem(item, event -> {
                if (event.getClick() == ClickType.DROP) {
                    // delete
                    tier.getRewards().remove(reward);
                } else if (event.isShiftClick() && event.isLeftClick()) {
                    // move left
                    if (id == 0) return;
                    Collections.swap(tier.getRewards(), id, id - 1);
                } else if (event.isShiftClick() && event.isRightClick()) {
                    // move right
                    if (id == tier.getRewards().size() - 1) return;
                    Collections.swap(tier.getRewards(), id, id + 1);
                } else {
                    new ItemEditor(player, this, crate, reward).open();
                    return;
                }

                crate.getCrateRewards().save();
                open();
            }));
            i++;
        }

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Vissza az előző menűbe"
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
                    rewardGui.previous();
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
                    rewardGui.next();
                },
                "51"
        );

        gui.open(player);
    }
}
