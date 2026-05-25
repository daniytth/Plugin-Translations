package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramType;
import com.artillexstudios.axapi.hologram.HologramTypes;
import com.artillexstudios.axapi.hologram.page.HologramPage;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.packetentity.PacketEntity;
import com.artillexstudios.axapi.packetentity.meta.entity.DisplayMeta;
import com.artillexstudios.axapi.packetentity.meta.entity.ItemEntityMeta;
import com.artillexstudios.axapi.packetentity.meta.entity.TextDisplayMeta;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.StaticPlaceholder;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.animation.placed.impl.AuraAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.BeamsAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ChainsAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.CircleAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ConeAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.DoubleSpiralAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ForceFieldAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.HaloAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.OrbitAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SimpleAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SpawnerAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SphereAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SpiralAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.TornadoAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.VortexAnimation;
import com.artillexstudios.axcrates.crates.previews.impl.PreviewGui;
import com.artillexstudios.axcrates.hooks.HookManager;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class PlacedCrate {
    private final DynamicLocation location;
    private final Crate crate;
    private Hologram hologram = null;
    private Animation animation = null;
    private final File preview;
    private final boolean hasPreview;
    private PacketEntity entity;

    public PlacedCrate(@NotNull DynamicLocation location, @NotNull Crate crate) {
        this.location = location;
        this.crate = crate;

        preview = new File(AxCrates.getInstance().getDataFolder(), "previews/" + crate.previewTemplate + ".yml");
        hasPreview = preview.exists();

        if (location.getLocation() != null) spawn();
    }

    public void spawn() {
        ModelHook modelHook = getModelHook();
        if (modelHook != null && crate.placedTextureEnabled) {
            modelHook.spawnCrate(this);
        }

        if (crate.placedHologramEnabled) {
            Location holoLoc = location.getLocation().clone();
            holoLoc.add(0.5, 0.5, 0.5);
            holoLoc.add(crate.placedHologramOffsetX, crate.placedHologramOffsetY, crate.placedHologramOffsetZ);
            hologram = new Hologram(holoLoc);

            HologramPage<String, HologramType<String>> page = hologram.createPage(HologramTypes.TEXT);

            Section section = CONFIG.getSection("holograms");
            page.setEntityMetaHandler(m -> {
                TextDisplayMeta meta = (TextDisplayMeta) m;
                meta.seeThrough(section.getBoolean("see-through"));
                meta.alignment(TextDisplayMeta.Alignment.valueOf(section.getString("alignment").toUpperCase()));
                meta.backgroundColor(Integer.parseInt(section.getString("background-color"), 16));
                meta.lineWidth(1000);
                meta.billboardConstrain(DisplayMeta.BillboardConstrain.valueOf(section.getString("billboard").toUpperCase()));
            });

            List<String> lines = new ArrayList<>();
            for (String line : crate.placedHologramLines) {
                lines.add(line.replace("%crate%", crate.displayName));
            }

            page.setContent(String.join("<reset><br>", StringUtils.formatListToString(lines)));
            page.spawn();
        }

        if (crate.placedParticleEnabled) {
            String[] anim = crate.placedParticleAnimation.split("-");
            animation = switch (anim[0].toLowerCase()) {
                case "simple" -> new SimpleAnimation(this);
                case "spiral" -> new SpiralAnimation(this);
                case "doublespiral" -> new DoubleSpiralAnimation(this);
                case "tornado" -> new TornadoAnimation(this);
                case "chains" -> new ChainsAnimation(this);
                case "sphere" -> new SphereAnimation(this);
                case "beams" -> new BeamsAnimation(this);
                case "halo" -> new HaloAnimation(this);
                case "cone" -> new ConeAnimation(this);
                case "vortex" -> new VortexAnimation(this);
                case "forcefield" -> new ForceFieldAnimation(this);
                case "orbit" -> new OrbitAnimation(this);
                case "spawner" -> new SpawnerAnimation(this);
                case "circle" -> new CircleAnimation(this);
                case "aura" -> new AuraAnimation(this);
                default -> throw new IllegalStateException("Animáció nem létezik: " + crate.placedParticleAnimation);
            };
        }
    }

    public void openPreview(Player player) {
        if (!hasPreview) {
            MESSAGEUTILS.sendLang(player, "errors.no-preview", Map.of("%crate%", crate.displayName));
            return;
        }
        new PreviewGui(new Config(preview), crate).open(player);
    }

    private long lastOpen = 0;
    public void open(Player player) {
        if (!CONFIG.getBoolean("actually-open-container.enabled", true)) return;
        Block block = location.getLocation().getBlock();
        lastOpen = System.currentTimeMillis();
        if (block.getState() instanceof Lidded lidded) {
            lidded.open();

            long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
            Scheduler.get().runLater(scheduledTask -> {
                if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
                lidded.close();
            }, stayOpenTime / 50);
            return;
        }

        ModelHook modelHook = getModelHook();
        if (modelHook != null) {
            modelHook.open(player, this);

            long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
            Scheduler.get().runLater(scheduledTask -> {
                if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
                modelHook.close(player, this);
            }, stayOpenTime / 50);
        }
    }

    public void showReward(Player player, ItemStack reward, String display) {
        if (!CONFIG.getBoolean("actually-open-container.show-reward", true)) return;
        EntityType entityType;
        try {
            entityType = EntityType.valueOf("ITEM");
        } catch (Exception ex) {
            entityType = EntityType.valueOf("DROPPED_ITEM");
        }

        if (entity != null) entity.remove();
        entity = NMSHandlers.getNmsHandler().createEntity(entityType, location.getLocation().clone().add(0.5, 2, 0.5));
        final ItemEntityMeta meta = (ItemEntityMeta) entity.meta();
        meta.hasNoGravity(true);
        meta.customNameVisible(true);
        meta.itemStack(WrappedItemStack.wrap(reward));
        meta.name(StringUtils.format(display));
        entity.spawn();
        long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
        Scheduler.get().runLater(scheduledTask2 -> {
            if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
            entity.remove();
        }, stayOpenTime / 50);
    }

    public void tick() {
        if (animation == null) return;
        animation.play();
    }

    public void remove() {
        if (hologram != null) hologram.remove();
        ModelHook modelHook = getModelHook();
        if (modelHook != null) modelHook.removeCrate(this);
        animation = null;
    }

    public DynamicLocation getLocation() {
        return location;
    }

    public Crate getCrate() {
        return crate;
    }

    public Hologram getHologram() {
        return hologram;
    }

    @Nullable
    private ModelHook getModelHook() {
        return HookManager.getModelHooks().stream().filter(mh -> mh.getName().equalsIgnoreCase(crate.placedTextureMode)).findAny().orElse(null);
    }
}
