package net.flamestudios.service;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.flamestudios.FarmSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class RegionService {

    private final Map<Long, Boolean> chunkCache = new ConcurrentHashMap<>();
    private volatile RegionContainer container;

    public void init() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return;
        try {
            this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        } catch (Exception e) {
            FarmSystem.getInstance().getLogger().log(Level.SEVERE, "WorldGuard nem elérhető, a régiós ellenzőrzések le vannak tiltva.", e);
        }
    }

    public void invalidate() {
        chunkCache.clear();
    }

    public boolean isInFarmRegion(final @NotNull Block block) {
        if (container == null) return false;

        final Set<String> regionNames = FarmSystem.getInstance().getFarmConfig().getRegionNames();
        if (regionNames.isEmpty()) return false;

        final long chunkKey = chunkKey(block);
        final Boolean cached = chunkCache.get(chunkKey);
        if (cached != null) return cached;

        final boolean result = check(block.getLocation(), regionNames);

        if (chunkCache.size() < 8192) {
            chunkCache.put(chunkKey, result);
        }

        return result;
    }

    private boolean check(final @NotNull Location location, final @NotNull Set<String> regionNames) {
        try {
            final RegionQuery query = container.createQuery();
            final ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));

            for (final ProtectedRegion region : set) {
                if (regionNames.contains(region.getId())) return true;
            }
        } catch (Exception e) {
            FarmSystem.getInstance().getLogger().log(Level.WARNING, "WorldGuard régió ellenzőrzés sikertelen.", e);
        }
        return false;
    }

    private long chunkKey(final @NotNull Block block) {
        final int worldHash = block.getWorld().getUID().hashCode();
        final int chunkX = block.getX() >> 4;
        final int chunkZ = block.getZ() >> 4;
        return (((long) chunkX) << 32) | (chunkZ & 0xFFFFFFFFL) ^ ((long) worldHash << 16);
    }
}