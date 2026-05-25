package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class DoubleSpiralAnimation extends Animation {

    public DoubleSpiralAnimation(PlacedCrate placed) {
        super(60, placed);
    }

    protected void run() {
        for (int i = 0; i < 2; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (frame * 12 + (180 * i)) / 360) / 1.25;
            y = Math.sin(Math.PI * 2 * (frame * 12 + (180 * i)) / 360) / 1.25;
            loc.add(x, -0.75 + (frame * 0.05), y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
