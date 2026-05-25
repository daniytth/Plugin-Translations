package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramType;
import com.artillexstudios.axapi.hologram.HologramTypes;
import com.artillexstudios.axapi.hologram.page.HologramPage;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.packetentity.PacketEntity;
import com.artillexstudios.axapi.packetentity.meta.EntityMeta;
import com.artillexstudios.axapi.packetentity.meta.Metadata;
import com.artillexstudios.axapi.packetentity.meta.entity.DisplayMeta;
import com.artillexstudios.axapi.packetentity.meta.entity.TextDisplayMeta;
import com.artillexstudios.axapi.packetentity.meta.serializer.EntityDataSerializers;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.LANG;

public class CircleAnimation extends Animation {
    private final ArrayList<PacketEntity> entities = new ArrayList<>();
    private final Hologram hologram;

    public CircleAnimation(Player player, Crate crate, Location location, boolean silent, boolean force) {
        super(player, 180, crate, location, silent, force);

        super.generateRewards();
        for (CrateReward reward : super.getCompactRewards()) {
            PacketEntity entity = NMSHandlers.getNmsHandler().createEntity(EntityType.ITEM_DISPLAY, location);
            EntityMeta meta = entity.meta();
            meta.name(StringUtils.format(ItemUtils.getFormattedItemName(reward.getDisplay())));
            Metadata metadata = entity.meta().metadata();
            metadata.define(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(new ItemStack(Material.AIR)));
            metadata.set(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(reward.getDisplay()));
            metadata.define(EntityDataSerializers.INT.createAccessor(10), 0);
            metadata.set(EntityDataSerializers.INT.createAccessor(10), 1);
            metadata.define(EntityDataSerializers.BYTE.createAccessor(24), (byte) 0);
            metadata.set(EntityDataSerializers.BYTE.createAccessor(24), (byte) 7);

            meta.hasNoGravity(true);
            meta.customNameVisible(true);
            entity.spawn();

            entities.add(entity);
        }

        hologram = new Hologram(location.clone().add(0, 4, 0));

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
        for (String line : LANG.getStringList("animation-hologram")) {
            lines.add(line.replace("%crate%", crate.displayName)
                          .replace("%player%", player.getName())
            );
        }
        page.setContent(String.join("<reset><br>", lines));
        page.spawn();

        totalFrames = totalFrames + (entities.size() * 15);
    }

    @Override
    protected void run() {
        if (frame > 165) {
            if (frame % 15 != 0) return;
            int c = (frame - 180) / 15;

            getCompactRewards().get(c).run(player);
            final PacketEntity entity = entities.get(c);
            final Location loc = entity.location();
            Scheduler.get().runAt(loc, scheduledTask -> loc.getWorld().strikeLightningEffect(loc));
            entity.remove();
        } else {
            double radius = 5;
            double angle = frame * 3;

            boolean update = frame % 21 == 0;

            if (update) {
                super.generateRewards();
            }
            for (int i = 0; i < entities.size(); i++) {
                final Location loc = location.clone();
                double x, z;
                x = Math.cos(Math.PI * 2 * angle / 360) * radius;
                z = Math.sin(Math.PI * 2 * angle / 360) * radius;
                loc.add(x, 4f, z);

                if (update) {
                    final PacketEntity entity = entities.get(i);
                    EntityMeta meta = entity.meta();
                    meta.name(StringUtils.format(ItemUtils.getFormattedItemName(getCompactRewards().get(i).getDisplay())));
                    Metadata metadata = entity.meta().metadata();
//                    metadata.define(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(new ItemStack(Material.AIR)));
                    metadata.set(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(getCompactRewards().get(i).getDisplay()));
                    loc.getWorld().spawnParticle(Version.getServerVersion().isNewerThanOrEqualTo(Version.v1_20_4) ? Particle.valueOf("FIREWORK") : Particle.valueOf("FIREWORKS_SPARK"), loc, 3, 0, 0, 0, 0.5);
                }

//                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).hide(player);
                entities.get(i).teleport(loc);
//                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).show(player);

                angle += 360D / entities.size();
            }
        }
    }

    protected void end() {
        for (PacketEntity entity : entities) {
            entity.remove();
        }
        hologram.remove();
    }
}
