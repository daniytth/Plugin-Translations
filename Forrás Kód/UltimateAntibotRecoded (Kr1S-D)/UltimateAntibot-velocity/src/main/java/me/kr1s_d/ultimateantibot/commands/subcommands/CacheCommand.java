package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.service.CheckService;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheCommand implements SubCommand {
    @Override
    public String getSubCommandId() {
        return "cache";
    }

    @Override
    public void execute(CommandSource commandSender, String[] strings) {
        switch (strings[1]) {
            case "status":
                for (String s : CheckService.getInformationAsMessage()) {
                    commandSender.sendMessage(Utils.colora(s));
                }
                break;
            case "clear":
                CheckService.clearCheckCache();
                commandSender.sendMessage(Utils.colora(MessageManager.prefix + "&fAz &c&lUAB &fcache törölve lett!"));
                commandSender.sendMessage(Utils.colora(MessageManager.prefix + "&7PS: A cache A különböző ellenőrzések adatai rendszeresen törlődnek, kivéve az első csatlakozáskor rögzített adatokat, amelyek véglegesen megmaradnak. A parancs futtatása semmilyen módon nem javítja vagy rontja az antibot működését!"));
        }
    }

    @Override
    public String getPermission() {
        return "uab.command.cache";
    }

    @Override
    public int argsSize() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, Arrays.asList("clear", "status"));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }
}
