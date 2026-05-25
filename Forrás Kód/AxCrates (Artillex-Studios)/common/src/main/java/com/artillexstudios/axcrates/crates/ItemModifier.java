package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.items.PacketItemModifier;
import com.artillexstudios.axapi.items.PacketItemModifierListener;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.items.component.type.ItemLore;
import com.artillexstudios.axapi.items.nbt.CompoundTag;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemModifier implements PacketItemModifierListener {
    @Override
    public void modifyItemStack(Player player, WrappedItemStack stack, PacketItemModifier.Context context) {
        final CompoundTag tag = stack.get(DataComponents.customData());
        if (!tag.contains("axcrates-key")) return;

        final Key key = KeyManager.getKey(tag.getString("axcrates-key"));
        if (key == null) return;

        final WrappedItemStack original = WrappedItemStack.wrap(key.item());

        stack.set(DataComponents.material(), key.item().getType());
        if (context == PacketItemModifier.Context.EQUIPMENT || context == PacketItemModifier.Context.DROPPED_ITEM) {
            return;
        }

        byte[] serialized = stack.serialize();
        tag.putByteArray("axcrates-previous-state", serialized);
        stack.set(DataComponents.customData(), tag);

        List<Component> lore = stack.get(DataComponents.lore()).lines();
        if (lore.isEmpty()) {
            stack.set(DataComponents.lore(), new ItemLore(key.lore()));
        } else {
            List<Component> newLore = new ArrayList<>(key.lore());
            newLore.addAll(lore);
            stack.set(DataComponents.lore(), new ItemLore(newLore));
        }

        stack.set(DataComponents.customName(), original.get(DataComponents.customName()));
        stack.set(DataComponents.enchantments(), original.get(DataComponents.enchantments()));
        stack.set(DataComponents.dyedColor(), original.get(DataComponents.dyedColor()));
        stack.set(DataComponents.customModelData(), original.get(DataComponents.customModelData()));
        stack.set(DataComponents.profile(), original.get(DataComponents.profile()));
        stack.set(DataComponents.enchantmentGlintOverride(), original.get(DataComponents.enchantmentGlintOverride()));
    }

    @Override
    public void restore(WrappedItemStack stack) {
        final CompoundTag tag = stack.get(DataComponents.customData());
        if (!tag.contains("axcrates-key")) return;

        byte[] previous = tag.getByteArray("axcrates-previous-state");
        if (previous == null || previous.length == 0) return;

        WrappedItemStack wrapped = WrappedItemStack.wrap(previous);
        ItemLore lore = wrapped.get(DataComponents.lore());
        stack.set(DataComponents.lore(), lore);
        stack.set(DataComponents.customName(), Component.empty());
        tag.remove("axcrates-previous-state");
        stack.set(DataComponents.customData(), tag);
    }
}
