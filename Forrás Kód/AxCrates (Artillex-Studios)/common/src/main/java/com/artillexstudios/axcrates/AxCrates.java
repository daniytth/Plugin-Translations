package com.artillexstudios.axcrates;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.dependencies.DependencyManagerWrapper;
import com.artillexstudios.axapi.executor.ThreadedQueue;
import com.artillexstudios.axapi.libs.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.database.Database;
import com.artillexstudios.axcrates.database.impl.H2;
import com.artillexstudios.axcrates.hooks.HookManager;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.lang.LanguageManager;
import com.artillexstudios.axcrates.libraries.Libraries;
import com.artillexstudios.axcrates.listeners.BreakListener;
import com.artillexstudios.axcrates.listeners.InteractListener;
import com.artillexstudios.axcrates.listeners.PlayerListeners;
import com.artillexstudios.axcrates.listeners.WorldListeners;
import com.artillexstudios.axcrates.scheduler.PlacedCrateTicker;
import com.artillexstudios.axcrates.utils.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import revxrsal.zapper.DependencyManager;
import revxrsal.zapper.relocation.Relocation;

import java.io.File;
import java.lang.reflect.Method;

public final class AxCrates extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;
    private static ThreadedQueue<Runnable> threadedQueue;
    private static Database database;
    private static AxMetrics metrics;

    public static ThreadedQueue<Runnable> getThreadedQueue() {
        return threadedQueue;
    }

    public static Database getDatabase() {
        return database;
    }

    public static AxPlugin getInstance() {
        return instance;
    }

    @Override
    public void dependencies(DependencyManagerWrapper manager) {
        instance = this;
        manager.repository("https://jitpack.io/");
        manager.repository("https://repo.codemc.org/repository/maven-public/");
        manager.repository("https://repo.papermc.io/repository/maven-public/");
        manager.repository("https://repo.artillex-studios.com/releases/");

        DependencyManager dependencyManager = manager.wrapped();
        for (Libraries lib : Libraries.values()) {
            dependencyManager.dependency(lib.fetchLibrary());
            for (Relocation relocation : lib.relocations()) {
                dependencyManager.relocate(relocation);
            }
        }
    }

    public void enable() {
        instance = this;

        new Metrics(this, 21234);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

        switch (CONFIG.getString("database.type").toLowerCase()) {
//            case "sqlite" -> database = new SQLite();
//            case "mysql" -> database = new MySQL();
//            case "postgresql" -> database = new PostgreSQL();
            default -> database = new H2();
        }

        database.setup();

        LanguageManager.reload();

        if (FileUtils.PLUGIN_DIRECTORY.resolve("crates/").toFile().mkdirs()) {
            FileUtils.copyFromResource("crates");
        }

        if (FileUtils.PLUGIN_DIRECTORY.resolve("keys/").toFile().mkdirs()) {
            FileUtils.copyFromResource("keys");
        }

        if (FileUtils.PLUGIN_DIRECTORY.resolve("previews/").toFile().mkdirs()) {
            FileUtils.copyFromResource("previews");
        }

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        threadedQueue = new ThreadedQueue<>("AxCrates-Datastore-thread");

        HookManager.setupHooks();

        KeyManager.refresh();
        CrateManager.refresh();

        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new BreakListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        getServer().getPluginManager().registerEvents(new WorldListeners(), this);

        PlacedCrateTicker.start();

        try { // temporary solution because the project is using lamp v4
            Class<?> clazz = Class.forName("com.artillexstudios.axcrates.commands.CommandManager");
            Method method = clazz.getMethod("load");
            method.invoke(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        PacketItemModifier.registerModifierListener(new ItemModifier());

        metrics = new AxMetrics(this, 47);
        metrics.start();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400[AxCrates] Plugin betöltve!"));
    }

    public void disable() {
        if (metrics != null) metrics.cancel();

        PlacedCrateTicker.stop();
        for (Crate crate : CrateManager.getCrates().values()) {
            crate.remove();
        }
        database.disable();
    }

    public void updateFlags() {
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(true);
        FeatureFlags.PACKET_ENTITY_TRACKER_ENABLED.set(true);
        FeatureFlags.HOLOGRAM_UPDATE_TICKS.set(5L);
        FeatureFlags.ENABLE_PACKET_LISTENERS.set(true);
    }
}