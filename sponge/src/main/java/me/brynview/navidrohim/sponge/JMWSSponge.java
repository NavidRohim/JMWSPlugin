package me.brynview.navidrohim.sponge;

import com.google.inject.Inject;
import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.commands.Argument;
import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.commands.SuggestionProvider;
import me.brynview.navidrohim.common.events.CommonEvents;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.sponge.impl.PluginMetadata;
import me.brynview.navidrohim.sponge.impl.game.SpongePlayer;
import me.brynview.navidrohim.sponge.impl.game.SpongeServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.*;
import java.util.function.Supplier;

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

    public void onPluginMessage(String channel, SpongePlayer spongePlayer, byte[] bytes) {
        Constants.getLogger().info(channel);
        Constants.getLogger().info(ActionPacket.CHANNEL);
        if (channel.equalsIgnoreCase(ActionPacket.CHANNEL)) {
            new ActionPacket(bytes, spongePlayer).process();
        }
    }
}
