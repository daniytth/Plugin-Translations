package net.flamestudios;

import com.tcoded.folialib.FoliaLib;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import net.flamestudios.command.*;
import net.flamestudios.config.*;
import net.flamestudios.listener.*;
import net.flamestudios.manager.*;
import net.flamestudios.service.*;

@Getter
public final class FarmSystem extends JavaPlugin {

    @Getter
    private static FarmSystem instance;

    private FoliaLib foliaLib;

    private FarmConfig farmConfig;
    private RegionService regionService;
    private FarmManager farmManager;

    public FarmSystem() {
        instance = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.foliaLib = new FoliaLib(this);

        this.farmConfig = new FarmConfig();
        farmConfig.load();

        this.regionService = new RegionService();
        regionService.init();

        this.farmManager = new FarmManager();

        new FarmListener();
        new FarmCommand();
    }

    @Override
    public void onDisable() {
        if (farmManager != null) farmManager.shutdown();
        if (foliaLib != null) foliaLib.getScheduler().cancelAllTasks();
    }
}