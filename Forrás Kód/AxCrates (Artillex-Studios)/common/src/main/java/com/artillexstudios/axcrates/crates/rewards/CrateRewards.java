package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CrateRewards {
    protected final Config settings;
    protected final LinkedHashMap<String, CrateTier> tiers = new LinkedHashMap<>();

    public CrateRewards(Config settings) {
        this.settings = settings;
        updateTiers();
    }

    public void updateTiers() {
        final HashSet<String> loadedTiers = new HashSet<>();
        for (String str : settings.getSection("rewards").getRoutesAsStrings(false)) {
            loadedTiers.add(str);
            if (tiers.containsKey(str)) {
                tiers.get(str).reload(new LinkedList<>(settings.getMapList("rewards." + str)));
                continue;
            }
            tiers.put(str, new CrateTier(new LinkedList<>(settings.getMapList("rewards." + str)), str));
        }

        for (String str : tiers.keySet()) {
            if (loadedTiers.contains(str)) continue;
            tiers.remove(str);
        }
    }

    public void createNewTier(String name) {
        tiers.put(name, new CrateTier(name));
    }

    public HashMap<String, CrateTier> getTiers() {
        return tiers;
    }

    public HashMap<CrateTier, List<CrateReward>> rollAll() {
        final HashMap<CrateTier, List<CrateReward>> map = new HashMap<>();
        for (CrateTier tier : tiers.values()) {
            List<CrateReward> rewards = new ArrayList<>();
            for (int i = 0; i < tier.rollAmount; i++) {
                rewards.add(tier.roll());
            }
            map.put(tier, rewards);
        }

        return map;
    }

    public boolean hasRewards() {
        boolean hasRewards = false;
        for (CrateTier tier : tiers.values()) {
            if (!tier.getRewards().isEmpty()) {
                hasRewards = true;
                break;
            }
        }
        return hasRewards;
    }

    public void save() {
        final LinkedHashMap<Object, Object> tierList = new LinkedHashMap<>();
        for (CrateTier tier : tiers.values()) {
            final LinkedList<Map<Object, Object>> rewardList = new LinkedList<>();
            rewardList.add(Map.of("roll-amount", tier.getRollAmount()));

            for (CrateReward reward : tier.getRewards()) {
                final LinkedList<Map<Object, Object>> items = new LinkedList<>();
                for (ItemStack it : reward.getItems()) {
                    items.add(ItemUtils.saveItem(it));
                }

                final LinkedHashMap<Object, Object> contents = new LinkedHashMap<>();
                contents.put("chance", reward.getChance());
                contents.put("display", ItemUtils.saveItem(reward.getDisplay()));
                if (!reward.getItems().isEmpty())
                    contents.put("items", items);
                if (!reward.getCommands().isEmpty())
                    contents.put("commands", reward.getCommands());

                rewardList.add(contents);
            }

            tierList.put(tier.getName(), rewardList);
        }

        settings.set("rewards", tierList);
        settings.save();
    }
}
