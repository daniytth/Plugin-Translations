package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CrateTier {
    protected final String name;
    protected int rollAmount;
    protected final LinkedList<CrateReward> rewards = new LinkedList<>();

    public CrateTier(@NotNull LinkedList<Map<Object, Object>> section, String name) {
        this(name);
        reload(section);
    }

    public CrateTier(String name) {
        this.name = name;
    }

    public CrateReward roll() {
        if (rewards.isEmpty()) return null;
        final HashMap<CrateReward, Double> chances = new HashMap<>();
        for (CrateReward reward : rewards) {
            chances.put(reward, reward.getChance());
        }
        return RandomUtils.randomValue(chances);
    }

    public int getRollAmount() {
        return rollAmount;
    }

    public LinkedList<CrateReward> getRewards() {
        return rewards;
    }

    public void setRollAmount(int rollAmount) {
        this.rollAmount = rollAmount;
    }

    public String getName() {
        return name;
    }

    public void addRewardItem(ItemStack itemOriginal) {
        final ItemStack item = itemOriginal.clone();
        final CrateReward reward = new CrateReward();
        reward.setDisplay(item);
        reward.setItems(List.of(item));
        reward.setChance(10);
        rewards.add(reward);
    }

    public void addRewardCommand(ItemStack itemOriginal, String command) {
        final ItemStack item = itemOriginal.clone();
        final CrateReward reward = new CrateReward();
        reward.setDisplay(item);
        reward.setCommands(List.of(command));
        reward.setChance(10);
        rewards.add(reward);
    }

    public void reload(@NotNull LinkedList<Map<Object, Object>> section) {
        int idx = 0;
        for (Map<Object, Object> str : section) {
            if (str.containsKey("roll-amount")) {
                rollAmount = (int) str.get("roll-amount");
                continue;
            }

            final List<String> commands = (List<String>) str.getOrDefault("commands", new ArrayList<>());
            final ArrayList<ItemStack> items = new ArrayList<>();
            double chance = (double) str.get("chance");

            var map = (List<Map<Object, Object>>) str.get("items");
            if (map != null) {
                final LinkedList<Map<Object, Object>> map2 = new LinkedList<>(map);
                for (Map<Object, Object> it : map2) {
                    items.add(ItemBuilder.create(it).get());
                }
            }

            final ItemStack display;
            if (str.containsKey("display"))
                display = ItemBuilder.create((Map<Object, Object>) str.get("display")).get();
            else
                display = new ItemStack(Material.RED_BANNER);

            CrateReward reward;
            if (idx < rewards.size()) {
                reward = rewards.get(idx);
            } else {
                reward = new CrateReward();
                rewards.add(reward);
            }

            reward.setChance(chance);
            reward.setItems(items);
            reward.setCommands(commands);
            reward.setDisplay(display);

            idx++;
        }

        rewards.subList(idx, rewards.size()).clear();
    }
}
