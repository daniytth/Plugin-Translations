package com.artillexstudios.axcrates.hooks;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import com.artillexstudios.axcrates.hooks.other.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class HookManager {
    private static final List<ModelHook> modelHooks = new ArrayList<>();

    public static void setupHooks() {
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            if (ClassUtils.INSTANCE.classExists("com.ticxo.modelengine.api.generator.Hitbox")) {
                try {
                    ModelHook modelHook = (ModelHook) Class.forName("com.artillexstudios.axcrates.hooks.models.ModelEngine3Hook").getDeclaredConstructor().newInstance();
                    modelHooks.add(modelHook);
                    AxCrates.getInstance().getServer().getPluginManager().registerEvents((Listener) modelHook, AxCrates.getInstance());
                    Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hookolva ModelEngine3ra!"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                try {
                    ModelHook modelHook = (ModelHook) Class.forName("com.artillexstudios.axcrates.hooks.models.ModelEngine4Hook").getDeclaredConstructor().newInstance();
                    modelHooks.add(modelHook);
                    AxCrates.getInstance().getServer().getPluginManager().registerEvents((Listener) modelHook, AxCrates.getInstance());
                    Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hookolva ModelEngine4re!"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hookolva PlaceholderAPIra!"));
        }
    }

    public static List<ModelHook> getModelHooks() {
        return modelHooks;
    }
}
