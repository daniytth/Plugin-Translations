package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Give {
    INSTANCE;

    public void execute(CommandSender sender, List<Player> player, Key key, boolean virtual, boolean silent, Integer amount) {
        if (amount == null) amount = 1;

        ItemStack item = key.item().clone();
        item.setAmount(amount);

        String players = player.size() == 1
                ? player.get(0).getName()
                : LANG.getString("x-players").replace("%amount%", "" + player.size());

        Map<String, String> replacements = Map.of(
                "%key%", ItemUtils.getFormattedItemName(item),
                "%amount%", "" + item.getAmount(),
                "%player%", players
        );

        MESSAGEUTILS.sendLang(sender, "key.give.staff", replacements);
        if (!silent) {
            for (Player pl : player) {
                MESSAGEUTILS.sendLang(pl, "key.give.player", replacements);
            }
        }
        if (!virtual) {
            for (Player pl : player) {
                ContainerUtils.INSTANCE.addOrDrop(pl.getInventory(), List.of(item.clone()), pl.getLocation());
            }
        } else {
            for (Player pl : player) {
                AxCrates.getDatabase().giveVirtualKey(pl, key, amount);
            }
        }
    }
}
