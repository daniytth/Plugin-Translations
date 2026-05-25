package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.animation.opening.impl.CircleAnimation;
import com.artillexstudios.axcrates.animation.opening.impl.NoAnimation;
import com.artillexstudios.axcrates.crates.rewards.CrateRewards;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class Crate extends CrateSettings {
    public final String name;
    private final ArrayList<PlacedCrate> placedCrates = new ArrayList<>();
    private final CrateRewards crateRewards = new CrateRewards(settings);

    public Crate(Config settings, String name) {
        super(settings);
        this.name = name;
        reloadPlaced();
    }

    public ArrayList<PlacedCrate> getPlacedCrates() {
        return placedCrates;
    }

    public CrateRewards getCrateRewards() {
        return crateRewards;
    }

    public void open(Player player, int amount, boolean silent, boolean force, @Nullable PlacedCrate placed, @Nullable Location loc) {
        if (!crateRewards.hasRewards()) {
            if (!silent) MESSAGEUTILS.sendLang(player, "errors.no-rewards", Map.of("%crate%", displayName));
            return;
        }

        if (!force) {
            Key virtualKey = null;
            if (!CONFIG.getBoolean("allow-opening-with-full-inventory", false) && player.getInventory().firstEmpty() == -1) {
                if (!silent) MESSAGEUTILS.sendLang(player, "errors.inventory-full");
                return;
            }

            // todo: check for requirements here
            var keyItems = KeyManager.hasKey(player, this);
            if (keyItems == null) {

                // virtual key handling
                for (Key key : keysAllowed) {
                    int virtualAm = AxCrates.getDatabase().getVirtualKeys(player, key);
                    if (virtualAm > 0) {
                        amount = Math.min(amount, virtualAm);
                        virtualKey = key;
                        break;
                    }
                }

                if (virtualKey == null) {
                    if (!silent) MESSAGEUTILS.sendLang(player, "errors.no-key", Map.of("%crate%", displayName));
                    if (placed != null && placedKnockback) {
                        final Location location = placed.getLocation().getLocation().clone();
                        location.add(0.5, 0, 0.5);
                        final Vector diff = location.toVector().subtract(player.getLocation().toVector());
                        diff.subtract(diff.clone().multiply(2));
                        player.setVelocity(diff.normalize()
                                .multiply(CONFIG.getFloat("knockback-strength.forwards"))
                                .setY(CONFIG.getFloat("knockback-strength.upwards")));
                        player.setFallDistance(0);
                    }
                    return;
                }
            }

            if (virtualKey != null) {
                AxCrates.getDatabase().takeVirtualKey(player, virtualKey, amount);
            } else {
                int newAmount = amount;
                for (ItemStack it : keyItems) {
                    if (it.getAmount() >= newAmount) {
                        it.setAmount(it.getAmount() - newAmount);
                        newAmount = 0;
                    } else {
                        newAmount -= it.getAmount();
                        it.setAmount(0);
                    }
                    if (newAmount == 0) break;
                }

                if (newAmount != 0)
                    amount = amount - newAmount;
            }
        }

        if (placed != null) placed.open(player);

        for (int i = 0; i < amount; i++) {
            if (openAnimation.isBlank() || amount > 1 || (placed == null && loc == null)) {
                new NoAnimation(player, this, placed == null ? player.getLocation() : placed.getLocation().getLocation(), silent, force);
            } else {
                Location l = loc;
                if (l == null) l = placed.getLocation().getLocation();
                switch (openAnimation.toLowerCase()) {
                    case "circle" -> new CircleAnimation(player, this, l, silent, force);
                    default -> new NoAnimation(player, this, l, silent, force);
                }
            }
        }
    }

    public Config getSettings() {
        return settings;
    }

    public void reload() {
        refreshSettings();
        crateRewards.updateTiers();
        reloadPlaced();
    }

    public void reloadPlaced() {
        for (PlacedCrate crate : placedCrates) {
            crate.remove();
        }
        placedCrates.clear();
        for (DynamicLocation location : placedLocations) {
            placedCrates.add(new PlacedCrate(location, this));
        }
    }

    public void remove() {
        for (PlacedCrate crate : placedCrates) {
            crate.remove();
        }
    }
}
