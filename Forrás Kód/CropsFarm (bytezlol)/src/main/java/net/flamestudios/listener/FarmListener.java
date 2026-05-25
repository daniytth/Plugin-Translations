package net.flamestudios.listener;

import net.flamestudios.FarmSystem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public final class FarmListener implements Listener {

    private static final Set<Material> CROPS = EnumSet.of(
            Material.CARROTS,
            Material.POTATOES,
            Material.WHEAT,
            Material.BEETROOTS,
            Material.NETHER_WART
    );

    public FarmListener() {
        FarmSystem.getInstance().getServer().getPluginManager().registerEvents(this, FarmSystem.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (!CROPS.contains(block.getType())) return;

        if (!FarmSystem.getInstance().getRegionService().isInFarmRegion(block)) return;

        event.setDropItems(false);
        event.setExpToDrop(0);

        final Player player = event.getPlayer();
        FarmSystem.getInstance().getFarmManager().handleCropBreak(player, block);
    }
}