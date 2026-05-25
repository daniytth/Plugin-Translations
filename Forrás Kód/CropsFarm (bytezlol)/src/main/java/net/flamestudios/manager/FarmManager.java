package net.flamestudios.manager;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import net.flamestudios.FarmSystem;
import net.flamestudios.config.FarmConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FarmManager {

    private final ConcurrentMap<BlockKey, WrappedTask> scheduledRegenerations = new ConcurrentHashMap<>();

    public void handleCropBreak(final @NotNull Player player, final @NotNull Block block) {
        final BlockData blockData = block.getBlockData();
        if (!(blockData instanceof final Ageable ageable)) return;
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        final Material cropType = block.getType();
        final FarmConfig.CropSettings settings = FarmSystem.getInstance().getFarmConfig().get(cropType);
        if (settings == null) return;

        giveDrops(player, settings);
        scheduleRegeneration(block, cropType, settings);
    }

    private void giveDrops(final @NotNull Player player, final @NotNull FarmConfig.CropSettings settings) {
        final ItemStack drops = new ItemStack(settings.dropMaterial(), settings.dropAmount());
        final HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(drops);

        if (leftover.isEmpty()) return;

        final Location dropLocation = player.getLocation();
        for (final ItemStack item : leftover.values()) {
            player.getWorld().dropItemNaturally(dropLocation, item);
        }
    }

    private void scheduleRegeneration(final @NotNull Block block, final @NotNull Material cropType, final @NotNull FarmConfig.CropSettings settings) {
        final BlockKey key = BlockKey.of(block);

        final WrappedTask previous = scheduledRegenerations.remove(key);
        if (previous != null) previous.cancel();

        block.setType(Material.AIR, false);

        final Location location = block.getLocation();
        final long delayTicks = settings.regenerationDelaySeconds() * 20L;
        final int targetAge = settings.regenerationAge();

        final WrappedTask task = FarmSystem.getInstance().getFoliaLib().getScheduler().runAtLocationLater(
                location,
                () -> regenerateCrop(location, cropType, targetAge, key),
                delayTicks
        );

        if (task != null) scheduledRegenerations.put(key, task);
    }

    private void regenerateCrop(final @NotNull Location location, final @NotNull Material cropType, final int age, final @NotNull BlockKey key) {
        try {
            final Block block = location.getBlock();
            block.setType(cropType, false);

            final BlockData blockData = block.getBlockData();
            if (blockData instanceof final Ageable ageable) {
                ageable.setAge(Math.min(age, ageable.getMaximumAge()));
                block.setBlockData(ageable, false);
            }
        } finally {
            scheduledRegenerations.remove(key);
        }
    }

    public void shutdown() {
        scheduledRegenerations.values().forEach(WrappedTask::cancel);
        scheduledRegenerations.clear();
    }

    private record BlockKey(@NotNull UUID world, int x, int y, int z) {
        static @NotNull BlockKey of(final @NotNull Block block) {
            return new BlockKey(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        }
    }
}