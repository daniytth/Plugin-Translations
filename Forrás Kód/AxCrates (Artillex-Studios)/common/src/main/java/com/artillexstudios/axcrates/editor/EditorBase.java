package com.artillexstudios.axcrates.editor;

import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.items.component.type.ItemLore;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.listeners.InteractListener;
import com.artillexstudios.axcrates.listeners.PlayerListeners;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class EditorBase {
    protected final Player player;
    protected final BaseGui gui;

    public EditorBase(Player player, BaseGui gui) {
        this.player = player;
        this.gui = gui;
    }

    public ItemStack makeItem(Material material, @Nullable String name, String... lore) {
        final ItemBuilder builder = ItemBuilder.create(material);
        if (name != null) builder.setName(name);
        if (lore != null) builder.setLore(Arrays.asList(lore));
        return builder.get();
    }

    private GuiItem start(ItemStack item, @Nullable String... slots) {
        final GuiItem guiItem = new GuiItem(item);
        if (slots == null || slots.length == 0)
            gui.addItem(guiItem);
        else {
            gui.setItem(getSlots(slots), guiItem);}
        return guiItem;
    }

    public void addInputInteger(ItemStack item, int c, Consumer<Integer> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Bal Kattintás &8- &#00FF00+1",
                "&#FF4400&l> &#FF4400Jobb Kattintás &8- &#FF0000-1",
                "&#FF4400&l> &#FF4400Shift + Bal Kattintás &8- &#00FF00+10",
                "&#FF4400&l> &#FF4400Shift + Jobb Kattintás &8- &#FF0000-10"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(new GuiAction<>() {
            int current = c;
            public void execute(InventoryClickEvent event) {
                if (!event.isShiftClick())
                    if (event.isLeftClick()) current++;
                    else current--;
                else if (event.isLeftClick()) current += 10;
                else current -= 10;

                value.accept(current);
            }
        });
    }

    public void addInputDouble(ItemStack item, double c, Consumer<Double> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Bal Kattintás &8- &#00FF00+0.1",
                "&#FF4400&l> &#FF4400Jobb Kattintás &8- &#FF0000-0.1",
                "&#FF4400&l> &#FF4400Shift + Bal Kattintás &8- &#00FF00+1",
                "&#FF4400&l> &#FF4400Shift + Jobb Kattintás &8- &#FF0000-1"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(new GuiAction<>() {
            double current = c;
            public void execute(InventoryClickEvent event) {
                if (!event.isShiftClick())
                    if (event.isLeftClick()) current += 0.1D;
                    else current -= 0.1D;
                else if (event.isLeftClick()) current += 1D;
                else current -= 1D;

                value.accept(current);
            }
        });
    }

    public void addInputBoolean(ItemStack item, boolean c, Consumer<Boolean> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Átkapcsolás"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(new GuiAction<>() {
            final boolean current = c;
            public void execute(InventoryClickEvent event) {
                value.accept(!current);
            }
        });
    }

    public void addOpenMenu(ItemStack item, EditorBase editor, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Menű Megnyitása"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(event -> editor.open());
    }

    public void addCustom(ItemStack item, GuiAction<InventoryClickEvent> action, String... slots) {
        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(action);
    }

    public void addFiller(ItemStack item, String... slots) {
        start(item, slots);
    }

    public void addInputText(ItemStack item, Consumer<String> value, String... slots) {
        addInputText(item, null, value, slots);
    }

    public void addInputText(ItemStack item, @Nullable String message, Consumer<String> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Szerkesztés"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(event -> {
            startConversation(
                    player,
                    message != null ? message : "&#FF6600Írj új szöveget: &#DDDDDD(írd be hogy &#FF6600mégse &#DDDDDDa megszakításhoz)",
                    input -> {
                        open();
                        if (input.equalsIgnoreCase("mégse")) return;

                        value.accept(input);
                    }
            );
        });
    }

    public void addInputMultiText(ItemStack item, List<String> textsOriginal, Consumer<List<String>> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Bal Kattintás &8- &#00FF00Új Sor Hozzáadása",
                "&#FF4400&l> &#FF4400Jobb Kattintás &8- &#FF0000Utolsó Sor Törlése",
                "&#FF4400&l> &#FF4400Shift + Jobb Kattintás &8- &#DD0000Összes Sor Törlése"
        );

        final GuiItem guiItem = start(item, slots);
        List<String> texts = new ArrayList<>(textsOriginal);
        guiItem.setAction(event -> {
            if (event.isRightClick() && event.isShiftClick()) {
                texts.clear();
                value.accept(texts);
                return;
            }
            if (event.isRightClick()) {
                texts.remove(texts.size() - 1);
                value.accept(texts);
                return;
            }
            if (event.isLeftClick()) {
                startConversation(
                        player,
                        "&#FF6600Írj új szöveget: &#DDDDDD(írd be hogy &#FF6600mégse &#DDDDDDa megszakításhoz)",
                        input -> {
                            open();
                            if (input.equalsIgnoreCase("mégse")) return;

                            texts.add(input);
                            value.accept(texts);
                        }
                );
            }
        });
    }

    public void addInputMultiLocation(ItemStack item, List<DynamicLocation> textsOriginal, Consumer<List<DynamicLocation>> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Bal Kattintás &8- &#00FF00Add New Location",
                "&#FF4400&l> &#FF4400Jobb Kattintás &8- &#FF0000Delete Last Location",
                "&#FF4400&l> &#FF4400Shift + Jobb Kattintás &8- &#DD0000Clear All Locations"
        );

        final GuiItem guiItem = start(item, slots);
        List<DynamicLocation> texts = new ArrayList<>(textsOriginal);
        guiItem.setAction(event -> {
            if (event.isRightClick() && event.isShiftClick()) {
                texts.clear();
                value.accept(texts);
                return;
            }
            if (event.isRightClick()) {
                texts.remove(texts.size() - 1);
                value.accept(texts);
                return;
            }
            if (event.isLeftClick()) {
                startConversation(
                        player,
                        "&#FF6600Right click a block! &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)",
                        input -> {
                            InteractListener.selectionLocations.remove(player);
                            open();
                        }
                );
                InteractListener.selectionLocations.put(player, location -> {
                    texts.add(location);
                    value.accept(texts);
                    PlayerListeners.getInputs().remove(player);
                    open();
                });
            }
        });
    }

    public void addInputEnum(ItemStack item, List<String> c, String sel, Consumer<String> value, String... slots) {
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Bal Kattintás &8- &#FF4400Next Value",
                "&#FF4400&l> &#FF4400Jobb Kattintás &8- &#FF4400Previous Value"
        );

        final GuiItem guiItem = start(item, slots);
        guiItem.setAction(new GuiAction<>() {
            final List<String> current = c;
            String selected = sel;
            public void execute(InventoryClickEvent event) {
                int idx = current.indexOf(selected);
                if (event.isLeftClick()) {
                    if (idx + 1 >= current.size()) idx = -1;
                    selected = current.get(idx + 1);
                }
                else {
                    if (idx - 1 < 0) idx = current.size();
                    selected = current.get(idx - 1);
                }

                value.accept(selected);
            }
        });
    }

//    public void addInputInteger(int slot, String route, Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        int original = file.getInt(route);
//        replacements.put("{0}", "" + original);
//
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00+1");
//        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000-1");
//        lore.add("&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+10");
//        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-10");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());
//
//        List<String> finalLore = lore;
//        guiItem.setAction(event -> {
//            int current = file.getInt(route);
//
//            if (!event.isShiftClick())
//                if (event.isLeftClick()) file.set(route, current + 1);
//                else file.set(route, current - 1);
//            else
//            if (event.isLeftClick()) file.set(route, current + 10);
//            else file.set(route, current - 10);
//
//            file.save();
//            replacements.put("{0}", "" + file.getInt(route));
//            guiItem.setItemStack(new ItemBuilder(material).setName(name, replacements).setLore(finalLore, replacements).get());
//            gui.update();
//        });
//
//        gui.setItem(slot, guiItem);
//    }
//
//    public void addInputFloat(int slot, String route, Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        float original = file.getFloat(route);
//        replacements.put("{0}", "" + original);
//
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00+0.1");
//        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000-0.1");
//        lore.add("&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+1");
//        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-1");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());
//
//        List<String> finalLore = lore;
//        guiItem.setAction(event -> {
//            float current = file.getFloat(route);
//
//            if (!event.isShiftClick())
//                if (event.isLeftClick()) current = current + 0.1f;
//                else current = current - 0.1f;
//            else
//            if (event.isLeftClick()) current = current + 1f;
//            else current = current - 1f;
//
//            file.set(route, Float.parseFloat(String.format("%.1f", current)));
//            file.save();
//            replacements.put("{0}", "" + file.getFloat(route));
//            guiItem.setItemStack(new ItemBuilder(material).setName(name, replacements).setLore(finalLore, replacements).get());
//            gui.update();
//        });
//
//        gui.setItem(slot, guiItem);
//    }

//    public void addInputBoolean(int slot, String route, Material enabled, Material disabled, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        boolean original = file.getBoolean(route);
//        replacements.put("{0}", "" + original);
//
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Toggle");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(original ? enabled : disabled).setName(name, replacements).setLore(lore, replacements).get());
//
//        List<String> finalLore = lore;
//        guiItem.setAction(event -> {
//            boolean current = file.getBoolean(route);
//
//            file.set(route, !current);
//
//            file.save();
//            replacements.put("{0}", "" + file.getBoolean(route));
//            guiItem.setItemStack(new ItemBuilder(!current ? enabled : disabled).setName(name, replacements).setLore(finalLore, replacements).get());
//            gui.update();
//        });
//
//        gui.setItem(slot, guiItem);
//    }

//    public void addInputOpenMenu(int slot, EditorBase menu, Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Open Menu");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
//
//        guiItem.setAction(event -> menu.open());
//
//        gui.setItem(slot, guiItem);
//    }

//    public void addInputEmpty(int slot, Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
//        gui.setItem(slot, guiItem);
//    }

//    public void addInputText(int slot, String route,  Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        String original = file.getString(route);
//        replacements.put("{0}", original);
//
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Edit Text");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());
//
//        guiItem.setAction(event -> {
//            startConversation(event.getWhoClicked(), new StringPrompt() {
//                @Override
//                public String getPromptText(@NotNull ConversationContext context) {
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF6600Write the new text: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)"));
//                    return "";
//                }
//                @Override
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (!input.equalsIgnoreCase("cancel")) {
//                        file.set(route, input);
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            });
//        });
//
//        gui.setItem(slot, guiItem);
//    }
//
//    public void addInputMultiText(int slot, String route,  Material material, String name, List<String> l2) {
//        List<String> original = new ArrayList<>(file.getStringList(route));
//
//        List<String> lore = new ArrayList<>();
//        for (String str : l2) {
//            if (str.equals("{0}")) {
//                for (String o : original) {
//                    lore.add(StringUtils.formatToString(o));
//                }
//                continue;
//            }
//            lore.add(str);
//        }
//
//        lore.add(" ");
//        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00Add New Line");
//        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000Delete Last Line");
//        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#DD0000Clear All Lines");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
//
//        guiItem.setAction(event -> {
//            if (event.isShiftClick() && event.isRightClick()) {
//                file.set(route, new ArrayList<>());
//                file.save();
//                open();
//                return;
//            }
//
//            if (event.isRightClick()) {
//                if (original.isEmpty()) return;
//                original.remove(original.size() - 1);
//                file.set(route, original);
//                file.save();
//                open();
//                return;
//            }
//
//            startConversation(event.getWhoClicked(), new StringPrompt() {
//                @Override
//                public String getPromptText(@NotNull ConversationContext context) {
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF6600Write the new line: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)"));
//                    return "";
//                }
//                @Override
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (!input.equalsIgnoreCase("cancel")) {
//                        original.add(input);
//                        file.set(route, original);
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            });
//        });
//
//        gui.setItem(slot, guiItem);
//    }

//    public void addInputCustom(int slot, Material material, String name, List<String> lore, GuiAction<InventoryClickEvent> action) {
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
//        guiItem.setAction(action);
//
//        gui.setItem(slot, guiItem);
//    }

//    public void addFiller(List<Integer> slots, Material material, String name, List<String> lore) {
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
//        gui.setItem(slots, guiItem);
//    }

//    public void addInputLocation(int slot, String route,  Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        final String strLoc = file.getString(route);
//        if (strLoc.isEmpty()) {
//            replacements.put("{0}", "---");
//            replacements.put("{1}", "---");
//            replacements.put("{2}", "---");
//            replacements.put("{3}", "---");
//        } else {
//            Location original = Serializers.LOCATION.deserialize(strLoc);
//            final DecimalFormat df = new DecimalFormat("#.##");
//            replacements.put("{0}", original.getWorld().getName());
//            replacements.put("{1}", df.format(original.getX()));
//            replacements.put("{2}", df.format(original.getY()));
//            replacements.put("{3}", df.format(original.getZ()));
//        }
//
//        lore.add(" ");
//        lore.add("&#FFEE00Click &7- &#FFEE00Edit Location");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());
//
//        guiItem.setAction(event -> {
//            if (ClassUtils.INSTANCE.classExists("io.papermc.paper.threadedregions.RegionizedServer")) {
//                event.getWhoClicked().sendMessage(StringUtils.formatToString("&#FF0000This feature is not supported on folia, please edit manually in the sumo's config!"));
//                return;
//            }
//            player.closeInventory();
//
//            final StringPrompt prompt = new StringPrompt() {
//                @NotNull
//                public String getPromptText(@NotNull ConversationContext context) {
//                    return "";
//                }
//
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (input.equalsIgnoreCase("DONE")) {
//                        file.set(route, Serializers.LOCATION.serialize(player.getLocation()));
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            };
//
//            startConversation(player, new StringPrompt() {
//                @NotNull
//                @Override
//                public String getPromptText(@NotNull ConversationContext context) {
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF4400Go to the location and write &#00FF00'DONE' &#FFEE00or write &#FF0000'CANCEL' &#FFEE00to stop"));
//                    return "";
//                }
//
//                @Nullable
//                @Override
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (input.equalsIgnoreCase("DONE")) {
//                        file.set(route, Serializers.LOCATION.serialize(player.getLocation()));
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            });
//        });
//
//        gui.setItem(slot, guiItem);
//    }

    public void open() {

    }

    public void startConversation(HumanEntity player, String message, Consumer<String> consumer) {
        player.closeInventory();
        player.sendMessage(StringUtils.formatToString(message));
        PlayerListeners.getInputs().put((Player) player, consumer);
    }

    protected List<Integer> getSlots(String... strings) {
        final List<Integer> slots = new ArrayList<>();

        for (String str : strings) {
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
        WrappedItemStack.edit(item, wrapped -> {
            ItemLore itemLore = wrapped.get(DataComponents.lore());
            List<Component> components = new ArrayList<>(itemLore.lines());
            components.addAll(StringUtils.formatList(Arrays.asList(lore)));
            wrapped.set(DataComponents.lore(), new ItemLore(components));
            return wrapped;
        });
    }
}
