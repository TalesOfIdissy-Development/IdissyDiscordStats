package me.dahpr0gam3r.IdissyDiscordStats;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pzg.www.minecrafthook.main.PluginMain;
import com.pzg.www.minecrafthook.object.User;
import me.dahpr0gam3r.IdissyCore.IdissyCore;
import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;
import com.pzg.www.discord.object.CommandMethod;
import com.pzg.www.discord.object.Method;
import com.pzg.www.minecrafthook.events.UserVerifyEvent;
import com.pzg.www.minecrafthook.main.APILink;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class StatsMain extends JavaPlugin implements Listener {
    private APILink mchApi;

    private Config config;

    private StatsMain plugin;

    private List<String> commands;
    private Main lq = (Main) Bukkit.getPluginManager().getPlugin("LegendQuest");

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        mchApi = new APILink(this);
        reloadConfig();
        IdissyCore.sendStart(this);

        if (!config.getConfig().getBoolean("Verify.Active", false)) {
            commands = new ArrayList<String>();
            commands.add("/say Hello, this is a test command!");
            commands.add("/say Player: {PlayerName} just verified there discord: {DiscordName}!");
            config.getConfig().set("Verify.Active", true);
            config.getConfig().set("Verify.Commands", commands);
            config.saveConfig();
            reloadConfig();
        }

        mchApi.getBot().addCommand(new CommandMethod("stats", Permissions.READ_MESSAGES.toString(), new Method() {
            @Override
            public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args, IMessage message) {
                User discordUser = PluginMain.getInstance().getBot().getUsers().getUser(user.getLongID());
                UUID mcUUID;
                if (args.size() < 1) {
                    if (discordUser == null) return;
                    mcUUID = discordUser.getMinecraftUUID();
                } else {
                    mcUUID = Bukkit.getOfflinePlayer(args.get(0)).getUniqueId();
                }
                PC playerPC = lq.getPlayers().getPC(mcUUID);
                message.reply(playerPC.player + "'s Stats: Max Mana: " + playerPC.getMaxMana() + ", Max HP: " + playerPC.getMaxHealth() + ", INT: " + playerPC.getStatInt() + ", DEX: " + playerPC.getStatDex() + ", STR" + playerPC.getStatStr() + ", CHR:" + playerPC.getStatChr() + ", CON: " + playerPC.getStatCon() + ", WIS: " + playerPC.getStatWis());
            }
        }));
    }

    @EventHandler
    public void userVerifed(UserVerifyEvent event) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(event.getMinecraftPlayerUUID());
        IUser du = event.getUser();
        String playerName = "";
        if (op != null)
            playerName = op.getName();
        String discordUserName = "";
        if (du != null)
            discordUserName = du.getName();
        for (String command : commands) {
            String com = command.replace("{PlayerName}", playerName).replace("{DiscordName}", discordUserName).replace("/", "");
            System.out.println("Running command: \"" + com + "\".");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), com);
        }
    }

    public void reloadConfig() {
        config = new Config("plugins/Minecraft Hook", "Config.yml", this);
    }
}