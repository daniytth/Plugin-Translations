package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;

public class SimpleAnimation extends Animation {

    public SimpleAnimation(PlacedCrate placed) {
        super(1, placed);
    }

    protected void run() {
        location.getWorld().spawnParticle(particle, location, 5, 0.5, 0.5, 0.5, 1, options);
    }
}
