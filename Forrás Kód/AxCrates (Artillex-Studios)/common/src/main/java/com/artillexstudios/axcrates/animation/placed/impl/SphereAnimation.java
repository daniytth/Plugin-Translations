package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;

public class SphereAnimation extends Animation {

    public SphereAnimation(PlacedCrate placed) {
        super(50, placed);
    }

    protected void run() {
        final Location loc = location.clone();
        location.getWorld().spawnParticle(particle, loc, frame, 0.015 * frame, 0.015 * frame, 0.015 * frame, 1, options);
    }
}
