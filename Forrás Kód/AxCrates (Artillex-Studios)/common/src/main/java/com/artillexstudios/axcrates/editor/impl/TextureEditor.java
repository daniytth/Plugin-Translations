package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TextureEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public TextureEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Szerkesztő > &lSzerkesztés " + crate.displayName))
                .create());
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        boolean enabled = crate.placedTextureEnabled;
        super.addInputBoolean(makeItem(
                        enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lTextura Támogatás Engedélyezve",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték: &f" + enabled
                ),
                enabled,
                bool -> {
                    crate.settings.set("placed.texture.enabled", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "4"
        );

        String placedTextureMode = crate.placedTextureMode;
        super.addInputEnum(makeItem(
                        Material.BEACON,
                        "&#FF4400&lTextura Plugin",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték: &f" + placedTextureMode.toLowerCase()
                ),
                List.of("modelengine"),
                placedTextureMode,
                val -> {
                    crate.settings.set("placed.texture.mode", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "19"
        );

        String model = crate.placedTextureModel;
        super.addInputText(makeItem(
                        Material.PAPER,
                        "&#FF4400&lModel Neve",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték: &f" + model
                ),
                "&#FF6600Írd le a model nevét: &#DDDDDD(írd be a &#FF6600mégse &#DDDDDDszót a megszakításhoz)",
                bool -> {
                    crate.settings.set("placed.texture.model", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        float rotation = crate.placedTextureRotation;
        super.addInputDouble(makeItem(
                        Material.ARMOR_STAND,
                        "&#FF4400&lModel Textura Forgatás (fok)",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték: &f" + String.format("%.1f", rotation)
                ),
                rotation,
                num -> {
                    crate.settings.set("placed.texture.rotation", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "23"
        );

        String openAnimation = crate.placedTextureOpenAnimation;
        super.addInputText(makeItem(
                        Material.GOLD_NUGGET,
                        "&#FF4400&lNyitási Animáció",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték:: &f" + (openAnimation.isBlank() ? "none" : openAnimation)
                ),
                "&#FF6600Írd ide a modell nevét: &#DDDDDD(írd azt, hogy &#FF6600none &#DDDDDDa kikapcsoláshoz, írd azt, hogy &#FF6600cancel &#DDDDDDa leállításhoz)",
                val -> {
                    crate.settings.set("placed.texture.open-animation", val.equals("none") ? "" : val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "24"
        );

        String closeAnimation = crate.placedTextureCloseAnimation;
        super.addInputText(makeItem(
                        Material.IRON_NUGGET,
                        "&#FF4400&lNyitásí Animáció",
                        " ",
                        "&#FF4400&l> &#FFCC00Jelenlegi Érték: &f" + (closeAnimation.isBlank() ? "none" : openAnimation)
                ),
                "&#FF6600Írd ide a modell nevét: &#DDDDDD(írd azt, hogy &#FF6600none &#DDDDDDa kikapcsoláshoz, írd azt, hogy &#FF6600cancel &#DDDDDDa leállításhoz)",
                val -> {
                    crate.settings.set("placed.texture.close-animation", val.equals("none") ? "" : val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "25"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lVissza",
                        " ",
                        "&#FF4400&l> &#FF4400Kattintás &8- &#FF4400Vissza az előző menűbe."
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
