package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import me.kr1s_d.ultimateantibot.UltimateAntiBotVelocity;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.PasteBinBuilder;
import me.kr1s_d.ultimateantibot.utils.Utils;

import java.util.List;
import java.util.Map;

public class DumpCommand implements SubCommand {

    private final IAntiBotPlugin plugin;

    public DumpCommand(IAntiBotPlugin iAntiBotManager){
        this.plugin = iAntiBotManager;
    }

    @Override
    public String getSubCommandId() {
        return "dump";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        PasteBinBuilder pasteBinBuilder = PasteBinBuilder.builder();
        pasteBinBuilder.addLine("Név: " + UltimateAntiBotVelocity.getInstance().getServer().getVersion().getName());
        pasteBinBuilder.addLine("Verzió: " + UltimateAntiBotVelocity.getInstance().getServer().getVersion().getVersion());
        pasteBinBuilder.addLine("Online Mennyiség: " + Utils.calculatePlayerNames().size());
        pasteBinBuilder.addLine("Pluginok:");
        for (PluginContainer plugin : UltimateAntiBotVelocity.getInstance().getServer().getPluginManager().getPlugins()) {
            if (plugin.getDescription().getName().orElse("").contains("Protocol") || plugin.getDescription().getName().orElse("").contains("Geyser")) {
                pasteBinBuilder.addLine(plugin.getDescription().getName() + " - " + plugin.getDescription().getVersion() + " - Problémákat okozhat!");
            } else {
                pasteBinBuilder.addLine(plugin.getDescription().getName() + " - " + plugin.getDescription().getVersion());
            }
        }
        pasteBinBuilder.addLine("Plugin Információ:");
        pasteBinBuilder.addLine("Verzió: " + plugin.getVersion());
        pasteBinBuilder.addLine("Whitelist Mérete: " + plugin.getAntiBotManager().getWhitelistService().size());
        pasteBinBuilder.addLine("Blacklist Mérete: " + plugin.getAntiBotManager().getBlackListService().size());
        pasteBinBuilder.addLine("Felhasznűlók: " + plugin.getUserDataService().size());
        plugin.runTask(pasteBinBuilder::pasteSync, true);
        pasteBinBuilder.pasteSync();
        sender.sendMessage(Utils.colora(MessageManager.prefix + "&fKérés küldése a szervernek, kérlek várj..."));
        plugin.scheduleDelayedTask(() -> {
            if (pasteBinBuilder.isReady()) {
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fDump készen áll: &n" + pasteBinBuilder.getUrl() + "&f &7(Vissza áll egy hét múlva!)"));
            }else{
                sender.sendMessage(Utils.colora(MessageManager.prefix + "&fA kérés nem hajtható végre, kérjük, próbálja meg később..."));
            }
        }, true, 2500L);
    }

    @Override
    public String getPermission() {
        return "uab.command.dump";
    }

    @Override
    public int argsSize() {
        return 1;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        return null;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
