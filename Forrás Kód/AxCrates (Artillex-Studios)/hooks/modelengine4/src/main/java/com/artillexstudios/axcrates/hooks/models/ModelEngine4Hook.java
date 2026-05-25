package com.artillexstudios.axcrates.hooks.models;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.listeners.InteractListener;
import com.google.common.collect.HashBiMap;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public class ModelEngine4Hook implements ModelHook, Listener {
    private final HashBiMap<PlacedCrate, ModeledEntity> cache = HashBiMap.create();

    @Override
    public String getName() {
        return "modelengine";
    }

    @Override
    public void spawnCrate(PlacedCrate crate) {

        crate.getLocation().getLocation().getBlock().setType(Material.BARRIER);

        final Dummy<?> dummy = new Dummy<>();
        dummy.setLocation(crate.getLocation().getLocation().clone().add(0.5, 0, 0.5));

        ActiveModel activeModel;
        try {
            activeModel = ModelEngineAPI.createActiveModel(crate.getCrate().placedTextureModel);
        } catch (Exception ex) {
            ex.printStackTrace(); // todo: better error
            return;
        }
        final ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(dummy);

//        final Hitbox hitbox = activeModel.getBlueprint().getMainHitbox();

//        dummy.setHitbox(new Hitbox(hitbox.getWidth() + 0.3f, hitbox.getHeight() + 0.3f, hitbox.getDepth(), hitbox.getEyeHeight()));

        modeledEntity.addModel(activeModel, false);

//        for (Player player : Bukkit.getOnlinePlayers()) {
//            modeledEntity.getRangeManager().forceSpawn(player);
//        }

//        ModelEngineAPI.registerModeledEntity(dummy, modeledEntity);

        cache.put(crate, modeledEntity);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInteract(BaseEntityInteractEvent event) {
        final PlacedCrate crate = cache.inverse().get(event.getModel().getModeledEntity());
        if (crate == null) return;
        InteractListener.interact(event.getPlayer(), crate, event.getAction() == BaseEntityInteractEvent.Action.ATTACK ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public void removeCrate(PlacedCrate crate) {
        final ModeledEntity model = cache.get(crate);
        if (model == null) return;
        model.destroy();
    }

    @Override
    public void open(Player player, PlacedCrate crate) {

    }

    @Override
    public void close(Player player, PlacedCrate crate) {

    }

    @Override
    public void join(Player player) {
//        for (ModeledEntity entity : cache.values()) {
//            entity.getRangeManager().forceSpawn(player);
//        }
    }

    @Override
    public void leave(Player player) {
//        for (ModeledEntity entity : cache.values()) {
//            entity.getRangeManager().removePlayer(player);
//            entity.getRangeManager().removeTrackedPlayer(player);
//        }
    }
}
