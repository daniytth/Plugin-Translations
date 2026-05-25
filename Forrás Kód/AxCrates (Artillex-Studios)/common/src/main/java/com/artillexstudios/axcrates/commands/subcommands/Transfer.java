package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Transfer {
    INSTANCE;

    public void execute(Player sender, OfflinePlayer player, Key key, Integer amount) {
        if (amount == null) amount = 1;

        int finalAmount = amount;
        AxCrates.getThreadedQueue().submit(() -> {
            Map<String, String> replacements = new HashMap<>(Map.of(
                    "%key%", ItemUtils.getFormattedItemName(key.item()),
                    "%amount%", "" + finalAmount,
                    "%player%", Optional.ofNullable(player.getName()).orElse("---")
            ));

            int keys = AxCrates.getDatabase().getVirtualKeys(sender, key);
            if (keys < finalAmount) {
                MESSAGEUTILS.sendLang(sender, "key.transfer.not-enough", replacements);
                return;
            }

            AxCrates.getDatabase().takeVirtualKey(sender, key, finalAmount);
            AxCrates.getDatabase().giveVirtualKey(player, key, finalAmount);

            MESSAGEUTILS.sendLang(sender, "key.transfer.sender", replacements);

            Player otherPlayer = player.getPlayer();
            if (otherPlayer != null) {
                replacements.put("%player%", sender.getName());
                MESSAGEUTILS.sendLang(otherPlayer, "key.transfer.receiver", replacements);
            }
        });
    }
}
