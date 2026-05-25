package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;

import static com.artillexstudios.axcrates.AxCrates.LANG;

public enum Keys {
    INSTANCE;

    public void execute(CommandSender sender, @Nullable OfflinePlayer player) {
        if (player == null) {
            if (!(sender instanceof Player pl)) throw new CommandErrorException("must-be-player");
            player = pl;
        }

        String route = "virtual-keys.";
        if (sender.equals(player)) route += "self";
        else route += "other";

        for (String s : LANG.getStringList(route)) {
            if (s.equals("%keys%")) {
                int am = 0;
                for (Key key : KeyManager.getKeys().values()) {
                    int amount = AxCrates.getDatabase().getVirtualKeys(player, key);
                    if (amount == 0) continue;
                    s = LANG.getString("virtual-keys.key").replace("%amount%", "" + amount).replace("%key%", ItemUtils.getFormattedItemName(key.item()));
                    sender.sendMessage(StringUtils.formatToString(s.replace("%player%", player.getName())));
                    am++;
                }
                if (am == 0) {
                    sender.sendMessage(StringUtils.formatToString(LANG.getString("virtual-keys.no-keys")));
                }
                continue;
            }
            sender.sendMessage(StringUtils.formatToString(s.replace("%player%", player.getName())));
        }
    }
}
