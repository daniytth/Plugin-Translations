package com.artillexstudios.axcrates.animation.placed;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Animation {
    protected final int totalFrames;
    protected final Location location;
    protected int realFrame;
    protected int frame;
    protected final Particle particle;
    protected Particle.DustOptions options;
    protected int dir;
    protected final PlacedCrate placed;
    protected final boolean backwards;
    protected final boolean reverse;
    protected final float speed;

    public Animation(int totalFrames, PlacedCrate placed) {
        this.totalFrames = totalFrames;
        this.location = placed.getLocation().getLocation().clone();
        this.location.add(0.5, 0.5, 0.5);
        this.placed = placed;
        this.backwards = placed.getCrate().placedParticleBackwards;
        this.reverse = placed.getCrate().placedParticleReverse;
        this.speed = placed.getCrate().placedParticleSpeed;

        this.dir = backwards ? -1 : 1;
        this.frame = backwards ? totalFrames - 1 : 0;
        this.realFrame = frame;

        String[] s = placed.getCrate().placedParticleParticle.split(";");
        if (s.length == 4) {
            options = new Particle.DustOptions(Color.fromRGB(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])), 1f);
        } else if (s.length == 5) {
            options = new Particle.DustOptions(Color.fromRGB(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])), Float.parseFloat(s[4]));
        } else options = null;

        this.particle = Particle.valueOf(s[0].toUpperCase());
    }

    public void play() {
        realFrame += dir;
        frame = (int) Math.ceil(realFrame * speed);
        if (frame >= totalFrames || 0 >= frame) {
            if (reverse) {
                dir *= -1;
            } else {
                realFrame = backwards ? totalFrames - 1 : 0;
                frame = realFrame;
            }
        }

        run();
    }

    protected void run() {
    }

    public enum Options {
        AURA, BEAMS, CHAINS, CIRCLE, CONE, DOUBLESPIRAL, FORCEFIELD, HALO, ORBIT, SIMPLE, SPAWNER, SPHERE, SPIRAL, TORNADO, VORTEX
    }
}
