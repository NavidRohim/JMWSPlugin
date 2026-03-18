package me.brynview.navidrohim.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.brynview.navidrohim.common.api.commands.Argument;
import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.api.commands.CommonCommand;
import me.brynview.navidrohim.common.api.commands.CommonCommandContext;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.api.networking.PacketFlow;
import me.brynview.navidrohim.common.api.game.WSServer;
import me.brynview.navidrohim.common.commands.ServerCommands;
import me.brynview.navidrohim.common.config.ServerConfig;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.common.network.packets.HandshakePacket;
import me.brynview.navidrohim.common.objects.ServerWaypoint;

import java.io.File;
import java.util.List;
import java.util.Optional;

// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.

    public static final Gson gson = new Gson();
    public static final Gson gsonExcludeNoExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static WSServer server;

    public static final List<CommonCommand> COMMON_COMMANDS = List.of(
            new CommonCommand("share_waypoint", false, ServerCommands::share,
                    new Argument("player", ArgumentTypes.PLAYER),
                    new Argument("waypoint", ArgumentTypes.WAYPOINT)
            ),
            new CommonCommand("share_group", false, ServerCommands::shareGroup,
                    new Argument("player", ArgumentTypes.PLAYER),
                    new Argument("group", ArgumentTypes.GROUP)
            ),
            new CommonCommand("stop_sharing_group", false, ServerCommands::removeShareGroup,
                    new Argument("group", ArgumentTypes.GROUP)
            ),
            new CommonCommand("stop_sharing_waypoint", false, ServerCommands::removeShareWaypoint,
                    new Argument("waypoint", ArgumentTypes.WAYPOINT)
            ),

            new CommonCommand("create_global_waypoint", true, ServerCommands::globalWaypoint,
                    new Argument("waypoint", ArgumentTypes.WAYPOINT)
            ),
            new CommonCommand("create_global_group", true, ServerCommands::globalGroup,
                    new Argument("group", ArgumentTypes.GROUP)
            ),
            new CommonCommand("remove_global_waypoint", true, ServerCommands::nonGlobalWaypoint,
                    new Argument("waypoint", ArgumentTypes.WAYPOINT)
            ),
            new CommonCommand("remove_global_group", true, ServerCommands::nonGlobalGroup,
                    new Argument("group", ArgumentTypes.GROUP)
            )

    );

    public static void _createServerResources() {
        new File("./jmws").mkdir();
        new File("./jmws/groups").mkdir();
        new File("./jmws/users").mkdir();
    }


    public static void init(WSServer serverInstance) {

        Constants.getLogger().info("Creating server resources..");
        ServerConfig.ensureExistence(); // Create config
        _createServerResources(); // jmws folders

        CommonClass.server = serverInstance;

        // register packets
        serverInstance.registerPacket(PacketFlow.OUTGOING, HandshakePacket.CHANNEL);
        serverInstance.registerPacket(PacketFlow.OUTGOING, ActionPacket.CHANNEL);
        serverInstance.registerPacket(PacketFlow.INCOMING, ActionPacket.CHANNEL);


        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
    }
}
