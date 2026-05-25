package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class HaloAnimation extends Animation {

    public HaloAnimation(PlacedCrate placed) {
        super(1, placed);
    }

    protected void run() {
        for (int i = 0; i < 30; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (i * 12) / 360);
            y = Math.sin(Math.PI * 2 * (i * 12) / 360);
            loc.add(x, 0.6, y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
