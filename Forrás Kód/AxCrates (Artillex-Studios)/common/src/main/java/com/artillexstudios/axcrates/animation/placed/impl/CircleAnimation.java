package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class CircleAnimation extends Animation {

    public CircleAnimation(PlacedCrate placed) {
        super(36, placed);
    }

    protected void run() {
        for (int i = 0; i < 36; i++) {
            final Location loc = location.clone();
            double x = Math.cos(Math.PI * 2 * (frame * 12 + (10 * i)) / 360) / (0.25 + (frame / 30D));
            double y = Math.sin(Math.PI * 2 * (frame * 12 + (10 * i)) / 360) / (0.25 + (frame / 30D));
            loc.add(x, -1.5, y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
