package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class ConeAnimation extends Animation {

    public ConeAnimation(PlacedCrate placed) {
        super(70, placed);
    }

    protected void run() {
        for (int i = 0; i < 8; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (frame * 12 + (45 * i)) / 360) / (1.0 + (frame * 0.03));
            y = Math.sin(Math.PI * 2 * (frame * 12 + (45 * i)) / 360) / (1.0 + (frame * 0.03));
            loc.add(x, -0.75 + (frame * 0.05), y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
