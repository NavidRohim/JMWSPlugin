package me.brynview.navidrohim.JMWSSpigot;


import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPISpigotConfig;
import me.brynview.navidrohim.JMWSSpigot.commands.SharingCommands;
import me.brynview.navidrohim.JMWSSpigot.events.JMWSEvents;
import me.brynview.navidrohim.JMWSSpigot.impl.SpigotPlayer;
import me.brynview.navidrohim.JMWSSpigot.impl.SpigotServer;
import me.brynview.navidrohim.common.CommonClass;

import me.brynview.navidrohim.common.network.packets.ActionPacket;


import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jspecify.annotations.NonNull;

public final class JMWSSpigot extends JavaPlugin implements PluginMessageListener {

    public static SpigotServer server;
    public static JMWSSpigot plugin;

    @Override
    public void onLoad() {

        CommandAPI.onLoad(new CommandAPISpigotConfig(this));

        SharingCommands.register();
        SharingCommands.registerAdmin();
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        getServer().getPluginManager().registerEvents(new JMWSEvents(), this);
        JMWSSpigot.server = new SpigotServer(getServer());
        JMWSSpigot.plugin = this;

        CommonClass.init(JMWSSpigot.server);
    }



    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    public static JMWSSpigot getPluginInstance() {
        return JMWSSpigot.plugin;
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] message) {
        ActionPacket packet = new ActionPacket(message, new SpigotPlayer(player));
        packet.process();
    }
}
