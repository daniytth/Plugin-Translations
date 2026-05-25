package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrateSettings {
    public final Config settings;
    public String displayName;
    public Material material;
    public String previewTemplate;
    public boolean placedTextureEnabled;
    public String placedTextureMode;
    public String placedTextureModel;
    public String placedTextureOpenAnimation;
    public String placedTextureCloseAnimation;
    public float placedTextureRotation;
    public boolean placedHologramEnabled;
    public float placedHologramOffsetX;
    public float placedHologramOffsetY;
    public float placedHologramOffsetZ;
    public List<String> placedHologramLines;
    public boolean placedParticleEnabled;
    public String placedParticleAnimation;
    public boolean placedParticleReverse;
    public boolean placedParticleBackwards;
    public float placedParticleSpeed;
    public String placedParticleParticle;
    public boolean placedKnockback;
    public List<DynamicLocation> placedLocations;
    public List<String> openRequirements;
    public List<String> openActions;
    public String openAnimation;
    public String keyMode;
    public Set<Key> keysAllowed;

    public CrateSettings(Config settings) {
        this.settings = settings;
        reload();
    }

    public void refreshSettings() {
        settings.reload();
        reload();
    }

    private void reload() {
        displayName = settings.getString("name");
        material = Material.matchMaterial(settings.getString("material"));
        previewTemplate = settings.getString("preview-template");
        placedTextureEnabled = settings.getBoolean("placed.texture.enabled");
        placedTextureMode = settings.getString("placed.texture.mode");
        placedTextureModel = settings.getString("placed.texture.model");
        placedTextureOpenAnimation = settings.getString("placed.texture.open-animation");
        placedTextureCloseAnimation = settings.getString("placed.texture.close-animation");
        placedTextureRotation = settings.getFloat("placed.texture.rotation", 0f);
        placedHologramEnabled = settings.getBoolean("placed.hologram.enabled");
        placedHologramOffsetX = settings.getFloat("placed.hologram.location-offset.x");
        placedHologramOffsetY = settings.getFloat("placed.hologram.location-offset.y");
        placedHologramOffsetZ = settings.getFloat("placed.hologram.location-offset.z");
        placedHologramLines = settings.getStringList("placed.hologram.lines");
        placedParticleEnabled = settings.getBoolean("placed.particles.enabled");
        placedParticleAnimation = settings.getString("placed.particles.animation");
        placedParticleReverse = settings.getBoolean("placed.particles.reverse");
        placedParticleBackwards = settings.getBoolean("placed.particles.backwards");
        placedParticleSpeed = settings.getFloat("placed.particles.speed");
        placedParticleParticle = settings.getString("placed.particles.particle");
        placedKnockback = settings.getBoolean("placed.knockback");
        placedLocations = settings.getStringList("placed.locations").stream().map(DynamicLocation::deserialize).toList();
        openRequirements = settings.getStringList("open-requirements", new ArrayList<>());
        openActions = settings.getStringList("open-actions", new ArrayList<>());
        openAnimation = settings.getString("open-animation");
        keyMode = settings.getString("key.mode");

        final HashSet<Key> set = new HashSet<>();
        for (String k : settings.getStringList("key.allowed")) {
            final Key key = KeyManager.getKey(k);
            if (key == null) continue; // made we should delete unused keys from files?
            set.add(key);
        }
        keysAllowed = set;
    }
}
