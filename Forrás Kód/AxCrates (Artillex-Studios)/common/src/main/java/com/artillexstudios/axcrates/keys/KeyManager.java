package com.artillexstudios.axcrates.keys;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;

public class KeyManager {
    private static final HashMap<String, Key> keys = new HashMap<>();

    public static void refresh() {
        keys.clear();

        final File path = new File(AxCrates.getInstance().getDataFolder(), "keys");
        if (path.exists()) {
            for (File file : path.listFiles()) {
                final Config settings = new Config(file);
                final String name = file.getName().replace(".yml", "");

                final ItemBuilder builder = ItemBuilder.create(settings.getSection("item"));

//                List<String> lore = List.of();
//                final ItemStack it = builder.get();
//                if (it.getItemMeta() != null && it.getItemMeta().getLore() != null) {
//                    lore = it.getItemMeta().getLore();
//                }

                final ItemStack original = builder.clonedGet();
//                builder.setLore(List.of());
                final ItemStack item = builder.get();
                NBTWrapper wrapper = new NBTWrapper(item);
                wrapper.set("axcrates-key", name);
                wrapper.build();
                keys.put(name, new Key(name, settings, item, original, null));
            }
        }
    }

    @Nullable
    public static Key getKey(String name) {
        return keys.getOrDefault(name, null);
    }

    @NotNull
    public static HashMap<String, Key> getKeys() {
        return keys;
    }

    public static List<ItemStack> hasKey(Player player, Crate crate) {
        final List<ItemStack> allItems = new ArrayList<>();
        if (CONFIG.getBoolean("force-key-in-hand")) {
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR)
                allItems.add(player.getInventory().getItemInMainHand());
            if (player.getInventory().getItemInOffHand().getType() != Material.AIR)
                allItems.add(player.getInventory().getItemInOffHand());
        } else {
            for (ItemStack it : player.getInventory().getStorageContents()) {
                if (it == null) continue;
                allItems.add(it);
            }
        }

        if (allItems.isEmpty()) return null;

        final List<ItemStack> keyItems = new ArrayList<>();
        for (ItemStack it : allItems) {
            NBTWrapper wrapper = new NBTWrapper(it);
            String name = wrapper.getString("axcrates-key");
            if (name == null) continue;
            Key key = KeyManager.getKey(name);
            if (key == null) continue;
            if (!crate.keysAllowed.contains(key)) continue;
            keyItems.add(it);
        }

        if (keyItems.isEmpty()) return null;
        return keyItems;
    }
}
