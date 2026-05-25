package com.artillexstudios.axcrates.crates.previews;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiFrame {
    private static final ItemStack air = new ItemStack(Material.AIR);
    protected final Config file;
    protected BaseGui gui;

    public GuiFrame(Config file) {
        this.file = file;
    }

    public void setGui(BaseGui gui) {
        this.gui = gui;
        for (String str : file.getBackingDocument().getRoutesAsStrings(false)) createItem(str);
    }

    @NotNull
    public Config getFile() {
        return file;
    }

    protected ItemStack buildItem(@NotNull String key) {
        if (file.getSection(key) == null) return air;
        return ItemBuilder.create(file.getSection(key)).get();
    }

    protected ItemStack buildItem(@NotNull String key, Map<String, String> replacements) {
        if (file.getSection(key) == null) return air;
        return ItemBuilder.create(file.getSection(key), replacements).get();
    }

    protected void createItem(@NotNull String route) {
        createItem(route, null, Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action) {
        createItem(route, action, Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        if (file.getString(route + ".type") == null && file.getString(route + ".material") == null) return;
        final GuiItem guiItem = new GuiItem(buildItem(route, replacements), action);
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        gui.setItem(getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots), guiItem);
    }

    protected List<Integer> getSlots(List<String> s) {
        final List<Integer> slots = new ArrayList<>();

        for (String str : s) {
            if (NumberUtils.isInt(str)) {
                slots.add(Integer.parseInt(str));
            } else {
                String[] split = str.split("-");
                int min = Integer.parseInt(split[0]);
                int max = Integer.parseInt(split[1]);
                for (int i = min; i <= max; i++) {
                    slots.add(i);
                }
            }
        }

        return slots;
    }

    protected void extendLore(ItemStack item, String... lore) {
        final ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.getLore() != null)
            newLore.addAll(meta.getLore());
        newLore.addAll(StringUtils.formatListToString(Arrays.asList(lore)));
        meta.setLore(newLore);
        item.setItemMeta(meta);
    }
}
