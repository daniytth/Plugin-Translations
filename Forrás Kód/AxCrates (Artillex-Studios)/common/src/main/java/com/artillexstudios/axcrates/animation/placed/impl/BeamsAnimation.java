package com.artillexstudios.axcrates.animation.placed.impl;

import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BeamsAnimation extends Animation {
    final List<Vector> points = new ArrayList<>();

    public BeamsAnimation(PlacedCrate placed) {
        super(30, placed);

        points.add(new Vector(1, 0, 0));
        points.add(new Vector(0, 0, 1));
        points.add(new Vector(-1, 0, 0));
        points.add(new Vector(0, 0, -1));

        points.add(new Vector(0.75, 0, 0.75));
        points.add(new Vector(-0.75, 0, -0.75));
        points.add(new Vector(0.75, 0, -0.75));
        points.add(new Vector(-0.75, 0, 0.75));
    }

    protected void run() {
        final Location loc = location.clone();
        loc.add(0, -0.5 + (frame * 0.1), 0);
        for (Vector p : points) {
            final Location loc2 = loc.clone();
            loc2.add(p);
            location.getWorld().spawnParticle(particle, loc2, 1, 0, 0, 0, 1, options);
        }
    }
}
