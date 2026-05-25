package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Take {
    INSTANCE;

    public void execute(CommandSender sender, List<Player> player, Key key, Integer amount, boolean silent, boolean physical) {
        if (amount == null) amount = 1;

        ItemStack item = key.item().clone();
        item.setAmount(amount);

        List<Player> success = new ArrayList<>();
        if (physical) {
            for (Player pl : player) {
                int found = 0;
                for (ItemStack it : pl.getInventory().getStorageContents()) {
                    if (it == null) continue;
                    String keyVal = new NBTWrapper(it).getString("axcrates-key");
                    if (!Objects.equals(KeyManager.getKey(keyVal), key)) continue;
                    found += it.getAmount();
                    if (found >= amount) break;
                }

                if (found < amount) continue;
                success.add(pl);

                int removed = 0;
                for (ItemStack it : pl.getInventory().getStorageContents()) {
                    if (it == null) continue;
                    String keyVal = new NBTWrapper(it).getString("axcrates-key");
                    if (!Objects.equals(KeyManager.getKey(keyVal), key)) continue;
                    int oldRemoved = removed;
                    removed += it.getAmount();
                    removed = Math.min(amount, removed);
                    it.setAmount(it.getAmount() + oldRemoved - removed);
                    if (removed >= amount) break;
                }
            }
        } else {
            for (Player pl : player) {
                int keys = AxCrates.getDatabase().getVirtualKeys(pl, key);
                if (keys < amount) continue;
                success.add(pl);
                AxCrates.getDatabase().takeVirtualKey(pl, key, amount);
            }
        }

        String players = (success.isEmpty() && player.size() == 1)
                ? player.get(0).getName()
                : success.size() == 1
                ? success.get(0).getName()
                : LANG.getString("x-players").replace("%amount%", "" + success.size());

        Map<String, String> replacements = Map.of(
                "%key%", ItemUtils.getFormattedItemName(item),
                "%amount%", "" + item.getAmount(),
                "%player%", players
        );

        if (!success.isEmpty()) {
            MESSAGEUTILS.sendLang(sender, "key.take.staff", replacements);
            if (!silent) {
                for (Player pl : player) {
                    MESSAGEUTILS.sendLang(pl, "key.take.player", replacements);
                }
            }
        } else {
            MESSAGEUTILS.sendLang(sender, "key.take.not-enough", replacements);
        }
    }
}
