package com.artillexstudios.axcrates.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class DynamicWorld {
    private Reference<World> reference;
    private final String name;

    public static DynamicWorld of(World world) {
        return new DynamicWorld(world.getName());
    }

    public static DynamicWorld of(String name) {
        return new DynamicWorld(name);
    }

    private DynamicWorld(String name) {
        this.name = name;
        this.reference = new WeakReference<>(Bukkit.getWorld(name));
    }

    @Nullable
    public World get() {
        World world = reference.get();
        if (world == null) {
            reference = new WeakReference<>(Bukkit.getWorld(name));
            world = reference.get();
        }
        return world;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DynamicWorld that = (DynamicWorld) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
