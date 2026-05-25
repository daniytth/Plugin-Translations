package com.artillexstudios.axcrates.hooks.models;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.listeners.InteractListener;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.AnimationHandler;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.generator.Hitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ModelEngine3Hook implements ModelHook, Listener {
    private final HashMap<PlacedCrate, ModeledEntity> cache = new HashMap<>();

    private WeakReference<PlacedCrate> lastCrate = null;

    @Override
    public String getName() {
        return "modelengine";
    }

    @Override
    public void spawnCrate(PlacedCrate crate) {
        crate.getLocation().getLocation().getBlock().setType(Material.BARRIER);

        final Dummy dummy = ModelEngineAPI.createDummy();
        dummy.setLocation(crate.getLocation().getLocation().clone().add(0.5, 0, 0.5));
        dummy.setYBodyRot(crate.getCrate().placedTextureRotation);

        final ActiveModel activeModel = ModelEngineAPI.createActiveModel(crate.getCrate().placedTextureModel);
        if (activeModel == null) return; // model not found, modelengine already sends an error so we don't have to
        final ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(dummy);
        modeledEntity.setModelRotationLock(true);

        dummy.wrapRangeManager(modeledEntity);
        final Hitbox hitbox = activeModel.getBlueprint().getMainHitbox();

        // no idea why do I have to expand the hitbox, but it didn't work with the original hitbox
        dummy.setHitbox(new Hitbox(hitbox.getWidth() + 0.3f, hitbox.getHeight() + 0.3f, hitbox.getDepth(), hitbox.getEyeHeight()));

        modeledEntity.addModel(activeModel, false);
        // i don't think this does anything?
        modeledEntity.getRangeManager().setRenderDistance(crate.getLocation().getLocation().getWorld().getViewDistance());

        // why can't it handle this by itself?
        for (Player player : Bukkit.getOnlinePlayers()) {
            modeledEntity.getRangeManager().forceSpawn(player);
        }

        // super sketchy solution, because this seems to be broken (kinda like half of the things in modelengine)
        dummy.setOnHurtCallback((source, dmg) -> {
            lastCrate = new WeakReference<>(crate);
            return false;
        });

        dummy.setOnInteractAtCallback((player, equipmentSlot, vector) -> {
            InteractListener.interact(player, crate, Action.RIGHT_CLICK_BLOCK);
        });

        dummy.setOnInteractCallback((player, equipmentSlot) -> {
            InteractListener.interact(player, crate, Action.RIGHT_CLICK_BLOCK);
        });

        cache.put(crate, modeledEntity);
    }

    private WeakReference<Player> lastHit = null;

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        lastHit = new WeakReference<>(event.getPlayer());
    }

    // the idea behind this: if someone clicked on a crate, however there was no interact event, but there was a swing event, that means that the player must be the crate opener?
    // if crate previews randomly open to players, it might be caused by this code!
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(PlayerAnimationEvent event) {
        if (lastCrate == null) return;
        if (lastCrate.get() == null) {
            lastCrate = null;
            return;
        }
        PlacedCrate last = lastCrate.get();
        lastCrate = null;
        if (last == null) return;
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;
        if (lastHit == null) {
            InteractListener.interact(event.getPlayer(), last, Action.LEFT_CLICK_BLOCK);
            return;
        }
        final Player player = lastHit.get();
        if (player != null) {
            InteractListener.interact(player, last, Action.LEFT_CLICK_BLOCK);
        }
    }

    @Override
    public void removeCrate(PlacedCrate crate) {
        final ModeledEntity model = cache.get(crate);
        if (model == null) return;
        model.destroy();
    }

    @Override
    public void open(Player player, PlacedCrate crate) {
        if (crate.getCrate().placedTextureOpenAnimation.isBlank()) return;
        final ModeledEntity entity = cache.get(crate);
        if (entity == null) return;
        final AnimationHandler handler = entity.getModels().values().stream().findFirst().get().getAnimationHandler();
        if (handler.isPlayingAnimation(crate.getCrate().placedTextureOpenAnimation)) return;
        handler.playAnimation(crate.getCrate().placedTextureOpenAnimation, 0, 0, 1, true);
    }

    @Override
    public void close(Player player, PlacedCrate crate) {
        if (crate.getCrate().placedTextureOpenAnimation.isBlank()) return;
        final ModeledEntity entity = cache.get(crate);
        if (entity == null) return;
        final AnimationHandler handler = entity.getModels().values().stream().findFirst().get().getAnimationHandler();
        if (crate.getCrate().placedTextureCloseAnimation.isBlank()) {
            handler.forceStopAllAnimations();
            return;
        }
        handler.playAnimation(crate.getCrate().placedTextureCloseAnimation, 0, 0, 1, true);
    }

    @Override
    public void join(Player player) {
        for (ModeledEntity entity : cache.values()) {
            entity.getRangeManager().forceSpawn(player);
        }
    }

    @Override
    public void leave(Player player) {
        for (ModeledEntity entity : cache.values()) {
            entity.getRangeManager().removePlayer(player);
            entity.getRangeManager().removeTrackedPlayer(player);
        }
    }
}
