package net.flamestudios.config;

import lombok.Getter;
import net.flamestudios.FarmSystem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FarmConfig {

    @Getter
    private final Set<String> regionNames = ConcurrentHashMap.newKeySet();

    @Getter
    private final Map<Material, CropSettings> cropSettings = new ConcurrentHashMap<>();

    public void load() {
        FarmSystem.getInstance().reloadConfig();
        final FileConfiguration config = FarmSystem.getInstance().getConfig();

        regionNames.clear();
        regionNames.addAll(config.getStringList("regions"));

        cropSettings.clear();
        final ConfigurationSection cropsSection = config.getConfigurationSection("crops");
        if (cropsSection == null) return;

        for (final String key : cropsSection.getKeys(false)) {
            final Material material = Material.matchMaterial(key.toUpperCase());
            if (material == null) continue;

            final ConfigurationSection section = cropsSection.getConfigurationSection(key);
            if (section == null) continue;

            final Material dropMaterial = resolveDropMaterial(material);
            if (dropMaterial == null) continue;

            cropSettings.put(material, new CropSettings(
                    dropMaterial,
                    section.getInt("drop-amount", 1),
                    section.getLong("regeneration-delay", 2L),
                    section.getInt("regeneration-age", 7)
            ));
        }
    }

    public @Nullable CropSettings get(final @NotNull Material crop) {
        return cropSettings.get(crop);
    }

    private @Nullable Material resolveDropMaterial(final @NotNull Material crop) {
        return switch (crop) {
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case WHEAT -> Material.WHEAT;
            case BEETROOTS -> Material.BEETROOT;
            case NETHER_WART -> Material.NETHER_WART;
            default -> null;
        };
    }

    public record CropSettings(
            @NotNull Material dropMaterial,
            int dropAmount,
            long regenerationDelaySeconds,
            int regenerationAge
    ) {}
}