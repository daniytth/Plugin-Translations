package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class ChainsAnimation extends Animation {

    public ChainsAnimation(PlacedCrate placed) {
        super(1, placed);
    }

    protected void run() {
        for (int j = 0; j < 11; j++) {
            for (int i = 0; i < 4; i++) {
                final Location loc = location.clone();
                switch (i) {
                    case 0 -> loc.add(0.125 * j, -0.15 * j, 0.125 * j);
                    case 1 -> loc.add(0.125 * j, -0.15 * j, -0.125 * j);
                    case 2 -> loc.add(-0.125 * j, -0.15 * j, 0.125 * j);
                    case 3 -> loc.add(-0.125 * j, -0.15 * j, -0.125 * j);
                }
                location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
            }
        }
    }
}
