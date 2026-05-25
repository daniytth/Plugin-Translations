package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class SpawnerAnimation extends Animation {

    public SpawnerAnimation(PlacedCrate placed) {
        super(36, placed);
    }

    protected void run() {
        for (int i = 0; i < 36; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (10 * i) / 360) / 0.25;
            y = Math.sin(Math.PI * 2 * (10 * i) / 360) / 0.25;
            loc.add(x, -1.5, y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
        double x, y;
        for (int i = 0; i < 8; i++) {
            final Location loc = location.clone();
            x = Math.cos(Math.PI * 2 * (10 * frame) / 360) / (0.3 + i * 0.1);
            y = Math.sin(Math.PI * 2 * (10 * frame) / 360) / (0.3 + i * 0.1);
            loc.add(x, -1.0, y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
        for (int i = 0; i < 8; i++) {
            final Location loc = location.clone();
            x = Math.cos(Math.PI * 2 * (10 * (frame + 18)) / 360) / (0.3 + i * 0.1);
            y = Math.sin(Math.PI * 2 * (10 * (frame + 18)) / 360) / (0.3 + i * 0.1);
            loc.add(x, -1.0, y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
