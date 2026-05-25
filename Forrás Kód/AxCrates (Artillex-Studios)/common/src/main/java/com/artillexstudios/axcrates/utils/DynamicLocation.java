package com.artillexstudios.axcrates.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DynamicLocation {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final DynamicWorld dynamicWorld;

    public static DynamicLocation of(Location l) {
        return of(l, DynamicWorld.of(l.getWorld()));
    }

    public static DynamicLocation of(Location l, DynamicWorld dynamicWorld) {
        return of(dynamicWorld, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    public static DynamicLocation of(DynamicWorld dynamicWorld, double x, double y, double z) {
        return of(dynamicWorld, x, y, z, 0, 0);
    }

    public static DynamicLocation of(DynamicWorld dynamicWorld, double x, double y, double z, float yaw, float pitch) {
        return new DynamicLocation(dynamicWorld, x, y, z, yaw, pitch);
    }

    public static DynamicLocation deserialize(String val) {
        String[] s = val.trim().split(";");
        return of(DynamicWorld.of(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]), Float.parseFloat(s[4]), Float.parseFloat(s[5]));
    }

    public static String serialize(DynamicLocation val) {
        return String.format("%s;%s;%s;%s;%s;%s", val.getDynamicWorld().getName(), val.getX(), val.getY(), val.getZ(), val.getYaw(), val.getPitch());
    }

    private DynamicLocation(DynamicWorld dynamicWorld, double x, double y, double z) {
        this(dynamicWorld, x, y, z, 0, 0);
    }

    private DynamicLocation(DynamicWorld dynamicWorld, double x, double y, double z, float yaw, float pitch) {
        this.dynamicWorld = dynamicWorld;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Nullable
    public Location getLocation() {
        World world = dynamicWorld.get();
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public DynamicWorld getDynamicWorld() {
        return dynamicWorld;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DynamicLocation that = (DynamicLocation) o;
        return Double.compare(x, that.x) == 0 && Double.compare(y, that.y) == 0 && Double.compare(z, that.z) == 0 && Float.compare(yaw, that.yaw) == 0 && Float.compare(pitch, that.pitch) == 0 && Objects.equals(dynamicWorld, that.dynamicWorld);
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        result = 31 * result + Float.hashCode(yaw);
        result = 31 * result + Float.hashCode(pitch);
        result = 31 * result + Objects.hashCode(dynamicWorld);
        return result;
    }
}
