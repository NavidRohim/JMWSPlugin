package me.brynview.navidrohim.sponge;

import com.google.inject.Inject;
import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.events.CommonEvents;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.sponge.commands.SpongeCommands;
import me.brynview.navidrohim.sponge.commands.impl.GroupCommandCommonEncoder;
import me.brynview.navidrohim.sponge.commands.impl.PlayerCommandCommonEncoder;
import me.brynview.navidrohim.sponge.commands.impl.WaypointCommandCommonEncoder;
import me.brynview.navidrohim.sponge.impl.PluginMetadata;
import me.brynview.navidrohim.sponge.impl.game.SpongePlayer;
import me.brynview.navidrohim.sponge.impl.game.SpongeServer;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin("jmws")
public class JMWSSponge {

    private final Logger logger;
    private static Server serverObj;
    public static SpongeServer commonServer;

    private static JMWSSponge instance;
    private static PluginMetadata pluginMetadata;

    @Inject
    JMWSSponge(final PluginContainer container) {
        this.logger = Constants.getLogger();
        JMWSSponge.instance = this;
        JMWSSponge.pluginMetadata = new PluginMetadata(JMWSSponge.instance, container);
    }

    public static Server getServer()
    {
        return serverObj;
    }

    public static PluginMetadata getPlugin()
    {
        return JMWSSponge.pluginMetadata;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        // Perform any one-time setup
        this.logger.info("Constructing sponge");
    }

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {
        // Any setup per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
        JMWSSponge.serverObj = event.engine();
        JMWSSponge.commonServer = new SpongeServer(JMWSSponge.serverObj);
        CommonClass.init(commonServer);
    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        // Any tear down per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
    }

    @Listener
    public void onPlayerJoin(final ServerSideConnectionEvent.Join event)
    {
        CommonEvents.handleJoin(new SpongePlayer(event.player()));
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        SpongeCommands.registerArgument(ArgumentTypes.WAYPOINT, new  WaypointCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.GROUP, new  GroupCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.PLAYER, new  PlayerCommandCommonEncoder());

        CommonClass.COMMON_COMMANDS.forEach(command -> {
            SpongeCommands.registerCommand(command, event);});

    }

    public void onPluginMessage(String channel, SpongePlayer spongePlayer, byte[] bytes) {

        if (channel.equalsIgnoreCase(ActionPacket.CHANNEL)) {
            new ActionPacket(bytes, spongePlayer).process();
        }
    }


}
