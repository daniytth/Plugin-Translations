package com.artillexstudios.axcrates.utils;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.lang.LanguageManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ItemUtils {

    public static void saveItem(ItemStack item, Config config, String route) { // todo: make this work with latest axapi
//        config.set(route, new ItemBuilder(item.clone()).serialize(false));
//        final ItemStack itOriginal = new ItemBuilder(item.clone()).get();
//        final ItemStack itNew = new ItemBuilder(config.getSection(route)).get();
//        if (!itNew.isSimilar(itOriginal)) {
//            config.set(route, new ItemBuilder(item.clone()).serialize(true));
//        }
        config.set(route, ItemBuilder.create(item.clone()).serialize(true));
        config.save();
    }

    public static Map<Object, Object> saveItem(ItemStack item) { // todo: make this work with latest axapi
//        var map = new ItemBuilder(item.clone()).serialize(false);
//        final ItemStack itOriginal = new ItemBuilder(item.clone()).get();
//        final ItemStack itNew = new ItemBuilder(map).get();
//        if (!itNew.isSimilar(itOriginal)) {
//            return new ItemBuilder(item.clone()).serialize(true);
//        }
//        return map;
        return ItemBuilder.create(item.clone()).serialize(true);
    }

    @NotNull
    public static String getFormattedItemName(@NotNull ItemStack itemStack) {
        return (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName().isBlank()) ? LanguageManager.getTranslated(itemStack.getType()) : itemStack.getItemMeta().getDisplayName().replace("§", "&");
    }
}
