package me.kr1s_d.ultimateantibot.commands.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.kr1s_d.ultimateantibot.commands.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.LimitedList;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.IpEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NickNameEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ScoreTracker;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.TimeUtil;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConnectionProfileCommand implements SubCommand {
    private IAntiBotPlugin plugin;
    private UserDataService service;

    public ConnectionProfileCommand(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.service = plugin.getUserDataService();
    }

    @Override
    public String getSubCommandId() {
        return "profile";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        ConnectionProfile pro = getFromNickName(args[1]);
        if(pro == null) {
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&fNem sikerült megtalálni a játékost: &c" + args[1]));
            sender.sendMessage(Utils.colora("&7Ez a parancs támogatja az offline profilokat, ezért ügyeljen arra, hogy a játékos nevét helyesen írja be (a nagy- és kisbetűket is figyelembe véve)"));
            return;
        }


        LimitedList<NickNameEntry> nickHistory = pro.getLastNickNames();
        LimitedList<IpEntry> ipHistory = pro.getLastIPs();

        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Component.text("§c§lULTIMATE§F§L | ANTIBOT §r§7- V" + plugin.getVersion())) + "§f§lfutattása";
        sender.sendMessage(Component.text(""));
        sender.sendMessage(Utils.colora("&cIP &7» &c" + pro.getIP()));
        sender.sendMessage(Utils.colora("&cJelenleg Név &7» &c" + pro.getCurrentNickName()));
        sender.sendMessage(Utils.colora("&cElső Belépés &7» &c" + TimeUtil.convertSeconds(pro.getSecondsFromFirstJoin())));
        sender.sendMessage(Utils.colora("&cUtólsó Belépés &7» &c" + TimeUtil.convertSeconds(pro.getSecondsFromLastJoin())));
        sender.sendMessage(Utils.colora("&cPontszám &7» &c" + pro.getConnectionScore()));
        sender.sendMessage(Utils.colora("&cJátékidő &7» &c" + TimeUtil.convertSeconds(TimeUnit.MINUTES.toSeconds(pro.getMinutePlayed()))));
        sender.sendMessage(Utils.colora("&fUtólsó Becenevek: "));
        for (NickNameEntry s : nickHistory) {
            sender.sendMessage(Utils.colora("&f» &c" + s.getName() + " &7(" + TimeUtil.convertSeconds(s.getSecondsFromLastJoin()) + " előtt)"));
        }
        sender.sendMessage(Utils.colora("&fUtólsó IPk: "));
        for (IpEntry s : ipHistory) {
            sender.sendMessage(Utils.colora("&f» &c" + s.getIP() + " &7(" + TimeUtil.convertSeconds(s.getSecondsFromLastJoin()) + " előtt)"));
        }

        if(ConfigManger.isDebugModeOnline) {
            sender.sendMessage(Utils.colora("&fPontszám Statisztika:"));
            for (ScoreTracker.ScoreAddition s : pro.getScoreTracker().getAdditionList()) {
                sender.sendMessage(Utils.colora("&f» &c" + s.toString()));
            }
        }
        sender.sendMessage(Component.text("§8§l§n___________________________________________"));
        sender.sendMessage(Utils.colora("&7PS: Ha az elején azt látod, hogy botnak gyanúsítanak, az teljesen normális, ne aggódj! Az elején minden játékosra biztonsági okokból minimális gyanú szintet állítanak be, ami az alapbeállítások mellett nem okozhat problémát."));
    }

    @Override
    public String getPermission() {
        return "uab.command.profile";
    }

    @Override
    public int argsSize() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter() {
        Map<String, String> map2 = new HashMap<>();
        for (ConnectionProfile profile : service.getConnectedProfiles()) {
            map2.put(profile.getCurrentNickName(), profile.getIP());
        }

        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, new ArrayList<>(map2.keySet()));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }

    //Name -> IP mapping
    private Map<String, String> getProfilesMapping() {
        Map<String, String> map = new HashMap<>();
        for (ConnectionProfile profile : service.getProfiles()) {
            for (NickNameEntry name : profile.getLastNickNames()) {
                map.put(name.getName(), profile.getIP());
            }
        }

        return map;
    }

    private ConnectionProfile getFromNickName(String nick) {
        Map<String, String> mapping = getProfilesMapping();
        String s = mapping.get(nick);
        if(s == null) return null;
        return service.getProfile(s);
    }
}

