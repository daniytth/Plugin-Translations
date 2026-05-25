package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class ForceFieldAnimation extends Animation {

    public ForceFieldAnimation(PlacedCrate placed) {
        super(30, placed);
    }

    protected void run() {
        for (int i = 0; i < 4; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (frame * 12 + (90 * (i + (i * 10)))) / 360) / (1.75 - (frame * 0.03));
            y = Math.sin(Math.PI * 2 * (frame * 12 + (90 * (i + (i * 10)))) / 360) / (1.75 - (frame * 0.03));
            loc.add(x, 0.25 - (frame * 0.05), y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
