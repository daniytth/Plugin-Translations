package com.artillexstudios.axcrates.animation.opening;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.utils.HistoryUtils;
import com.artillexstudios.axcrates.utils.ItemUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class Animation {
    public static List<Animation> animations = Collections.synchronizedList(new ArrayList<>());
    protected final Crate crate;
    protected final Location location;
    protected int frame = 0;
    protected int totalFrames;
    protected HashMap<CrateTier, List<CrateReward>> rewards;
    protected final Player player;
    protected final boolean silent;
    protected final boolean force;

    public Animation(Player player, int totalFrames, Crate crate, Location location, boolean silent, boolean force) {
        this.player = player;
        this.totalFrames = totalFrames;
        this.crate = crate;
        this.location = location.clone().add(0.5, 0.5, 0.5);
        this.silent = silent;
        this.force = force;
        animations.add(this);
    }

    public void play() {
        frame++;
        if (frame >= totalFrames) {
            animations.remove(this);
            end();

            StringBuilder rwHist = new StringBuilder();
            final List<CrateReward> rewardList = getCompactRewards();
            for (CrateReward reward : rewardList) {
                int am = reward.getDisplay().getAmount();
                rwHist.append(am > 1 ? am + "x " : "").append(ItemUtils.getFormattedItemName(reward.getDisplay()));
                if (rewardList.get(rewardList.size() - 1).equals(reward)) continue;
                rwHist.append(", ");
            }

            HistoryUtils.writeToHistory(PlainTextComponentSerializer.plainText().serialize(StringUtils.format(
                    String.format("%s kinyitott egy %s ládát (id: %s) és ezt kapta: %s" + (force ? " [FORCE]" : ""),
                            player.getName(),
                            crate.displayName,
                            crate.name,
                            rwHist)
            )));

            if (rewardList.size() == 1) {
                String item = ItemUtils.getFormattedItemName(rewardList.get(0).getDisplay());
                int rewAm = rewardList.get(0).getDisplay().getAmount();
                if (!silent) MESSAGEUTILS.sendLang(player, "reward.single", Map.of("%crate%", crate.displayName, "%reward%", (rewAm > 1 ? rewAm + "x " : "") + item));

                final Optional<PlacedCrate> optional = crate.getPlacedCrates().stream().filter(placed -> placed.getLocation().equals(location.clone().add(-0.5, -0.5, -0.5))).findAny();
                if (optional.isPresent()) {
                    optional.get().showReward(player, rewardList.get(0).getDisplay(), (rewAm > 1 ? rewAm + "x " : "") + item);
                }

                return;
            }

            final List<String> messages = new ArrayList<>();
            final List<String> tiers = new ArrayList<>();

            int am = 0;
            for (Map.Entry<CrateTier, List<CrateReward>> entry : rewards.entrySet()) {
                List<String> rewards = new ArrayList<>();
                for (CrateReward reward : entry.getValue()) {
                    String item = ItemUtils.getFormattedItemName(reward.getDisplay());
                    int rewAm = reward.getDisplay().getAmount();
                    rewards.add(LANG.getString("reward.multiple.reward").replace("%reward%", (rewAm > 1 ? rewAm + "x " : "") + item));
                }

                for (String s : LANG.getStringList("reward.multiple.tier")) {
                    if (s.contains("%rewards%")) {
                        tiers.addAll(rewards);
                        continue;
                    }
                    tiers.add(s.replace("%tier%", entry.getKey().getName()).replace("%amount%", "" + entry.getKey().getRollAmount()));
                }
                if (rewards.size() > am) tiers.add(" ");
                am++;
            }

            for (String s : LANG.getStringList("reward.multiple.main")) {
                if (s.contains("%tiers%")) {
                    messages.addAll(tiers);
                    continue;
                }
                messages.add(s.replace("%crate%", crate.displayName));
            }

            if (!silent) {
                player.sendMessage(StringUtils.formatToString(CONFIG.getString("prefix") + messages.get(0)));
                for (int i = 1; i < messages.size(); i++) {
                    player.sendMessage(StringUtils.formatToString(messages.get(i)));
                }
            }

            return;
        }
        run();
    }

    public List<CrateReward> getCompactRewards() {
        final List<CrateReward> rew = new ArrayList<>();
        for (List<CrateReward> crateReward : rewards.values()) {
            rew.addAll(crateReward);
        }
        return rew;
    }

    public HashMap<CrateTier, List<CrateReward>> getRewards() {
        return rewards;
    }

    protected void generateRewards() {
        rewards = crate.getCrateRewards().rollAll();
    }

    protected void run() {
    }

    protected void end() {
    }

    public enum Options {
        CIRCLE,
        NONE
    }
}
