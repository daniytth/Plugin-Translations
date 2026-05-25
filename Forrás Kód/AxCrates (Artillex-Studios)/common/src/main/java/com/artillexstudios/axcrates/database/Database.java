package com.artillexstudios.axcrates.database;

import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.OfflinePlayer;

public interface Database {

    String getType();

    void setup();

    int getKeyId(Key key);

    int getPlayerId(OfflinePlayer player);

    void setVirtualKey(OfflinePlayer player, Key key, int amount);

    void giveVirtualKey(OfflinePlayer player, Key key, int amount);

    boolean takeVirtualKey(OfflinePlayer player, Key key, int amount);

    void resetVirtualKey(OfflinePlayer player, Key key);

    void reset(OfflinePlayer player);

    int getVirtualKeys(OfflinePlayer player, Key key);

    void disable();
}
