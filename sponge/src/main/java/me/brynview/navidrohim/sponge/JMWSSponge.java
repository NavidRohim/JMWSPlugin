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
import me.brynview.navidrohim.sponge.commands.impl.SharedWaypointCommandCommonEncoder;
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

        /* How to add new common argument encoder (made for myself because I will forget)
        *
        * Make a new argument type, name it whatever you like.
        * Make a class which implements SpongeCommandCommonEncoder
        * Override buildParameterForNative, make it return SpongeArgumentType with the class of the value you want the parameter,
        * and the builder (just use the default constuctor it's very easy)
        *
        * Also override getCommonParameterValue and make it return whatever object will be in the common namespace.
        * Can be anything at all as long as it works in the common namespace. Can be a player, entity, location, anything.
        * There is one parameter, "value" of which the annotation is just Object. This is whatever you definied the class to be in buildParameterForNative. For example, it might ServerPlayer
        * if you specified Parameter::player. But once again, ServerPlayer isn't in the common namespace so you cannot directly return that.
        *
        * Register the class you made here with the new ArgumentType and an instance of your encoder.
        * */
        SpongeCommands.registerArgument(ArgumentTypes.WAYPOINT, new WaypointCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.SHARED_WAYPOINT, new SharedWaypointCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.GLOBAL_GROUP, new SharedWaypointCommandCommonEncoder());

        SpongeCommands.registerArgument(ArgumentTypes.GROUP, new GroupCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.SHARED_GROUP, new GroupCommandCommonEncoder());
        SpongeCommands.registerArgument(ArgumentTypes.GLOBAL_GROUP, new GroupCommandCommonEncoder());

        SpongeCommands.registerArgument(ArgumentTypes.PLAYER, new PlayerCommandCommonEncoder());

        CommonClass.COMMON_COMMANDS.forEach(command -> {
            SpongeCommands.registerCommand(command, event);});

    }

    public void onPluginMessage(String channel, SpongePlayer spongePlayer, byte[] bytes) {

        if (channel.equalsIgnoreCase(ActionPacket.CHANNEL)) {
            new ActionPacket(bytes, spongePlayer).process();
        }
    }


}
