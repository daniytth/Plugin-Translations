package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class NoAnimation extends Animation {

    public NoAnimation(Player player, Crate crate, Location location, boolean silent, boolean force) {
        super(player, 0, crate, location, silent, force);
    }

    protected void end() {
        generateRewards();
        for (CrateReward reward : getCompactRewards()) {
            reward.run(player);
        }
    }
}
