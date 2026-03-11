package me.brynview.navidrohim.sponge;

import com.google.inject.Inject;
import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.events.CommonEvents;
import me.brynview.navidrohim.common.network.ServerPacketHandler;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.sponge.impl.SpongePlayer;
import me.brynview.navidrohim.sponge.impl.SpongeServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.Event;
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

    private final PluginContainer container;
    private final Logger logger;
    private static Server serverObj;
    public static SpongeServer commonServer;
    private static JMWSSponge instance;

    @Inject
    JMWSSponge(final PluginContainer container) {
        this.container = container;
        this.logger = Constants.getLogger();
        instance = this;
    }

    public static Server getServer()
    {
        return serverObj;
    }
    public static JMWSSponge getPlugin()
    {
        return instance;
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
        // Register a simple command
        // When possible, all commands should be registered within a command register event
        final Parameter.Value<String> nameParam = Parameter.string().key("name").build();
        event.register(this.container, Command.builder()
            .addParameter(nameParam)
            .permission("sponge.command.greet")
            .executor(ctx -> {
                final String name = ctx.requireOne(nameParam);
                ctx.sendMessage(Identity.nil(), LinearComponents.linear(
                    NamedTextColor.AQUA,
                    Component.text("Hello "),
                    Component.text(name, Style.style(TextDecoration.BOLD)),
                    Component.text("!")
                ));

                return CommandResult.success();
            })
            .build(), "greet", "wave");
    }

    public void onPluginMessage(String channel, SpongePlayer spongePlayer, byte[] bytes) {
        Constants.getLogger().info(channel);
        Constants.getLogger().info(ActionPacket.CHANNEL);
        if (channel.equalsIgnoreCase(ActionPacket.CHANNEL)) {
            new ActionPacket(bytes, spongePlayer).process();
        }
    }
}
