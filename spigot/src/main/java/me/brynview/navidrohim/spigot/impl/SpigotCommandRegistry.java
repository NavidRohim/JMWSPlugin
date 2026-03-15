package me.brynview.navidrohim.spigot.impl;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.commandsenders.BukkitPlayer;
import dev.jorel.commandapi.executors.ExecutionInfo;
import me.brynview.navidrohim.common.api.command.ArgumentCastable;
import me.brynview.navidrohim.common.api.command.ArgumentCasterRegistry;
import me.brynview.navidrohim.common.api.command.CallbackContext;
import me.brynview.navidrohim.common.api.command.WSCommandRegistry;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.commands.Arguments;
import me.brynview.navidrohim.common.commands.Commands;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.common.objects.ServerWaypoint;
import me.brynview.navidrohim.spigot.commands.SharingCommands;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class SpigotCommandRegistry implements WSCommandRegistry {

    private static final Map<Class<?>, Class<?>> PROPRIETARY_TO_COMMON = Map.of(PlayerProfile.class, SpigotPlayer.class);

    private ExecutionInfo<Player, BukkitPlayer> ei = null;

    public void register() {
        Commands.commands.forEach((cmd) -> {
            new CommandAPICommand(cmd.commandName()).withArguments().executesPlayer((info) -> {
                WSPlayer cmPlayer = new SpigotPlayer(info.sender());
                CallbackContext context = new CallbackContext(this, null, cmPlayer);
                this.ei = info;
                context.processArgs();
                context.callback();
            });
        });
        SpigotCommandRegistry.registerAdmin();
    }

    @Override
    public void argParse(CallbackContext callbackContext) {
        if (ei == null) {
            throw new IllegalStateException("The command registry has not been initialized yet");
        }

        ei.args().argsMap().forEach((key, value) -> {
            if (key != null && value != null) {
                @Nullable Class<?> commonType = PROPRIETARY_TO_COMMON.get(key);

                if (ArgumentCasterRegistry.hasCastFor(commonType)) {
                    callbackContext.args.put(key, ArgumentCasterRegistry.cast(PROPRIETARY_TO_COMMON.get(value.getClass()), value));
                } else if (key.toLowerCase().contains("waypoint"))
                {
                    HashMap<String, Path> userObjPaths = JMWSServerIO.getNameHashmapLookup(callbackContext.sender.getUUID(), ObjectType.WAYPOINT);
                    Path specifiedObj = userObjPaths.get(value.toString());

                    callbackContext.args.put(key, ServerWaypoint.getFromPath(specifiedObj, callbackContext.sender.getUUID()));
                } else if (key.toLowerCase().contains("group"))
                {
                    HashMap<String, Path> userObjPaths = JMWSServerIO.getNameHashmapLookup(callbackContext.sender.getUUID(), ObjectType.GROUP);
                    Path specifiedObj = userObjPaths.get(value.toString());

                    callbackContext.args.put(key, ServerGroup.getGroupFromFile(specifiedObj, callbackContext.sender.getUUID(), false));
                } else {
                    callbackContext.args.put(key, value.toString());
                }
            }
        });
    }

    private static void registerAdmin() {

        CommandAPICommand adminCommandBase = new CommandAPICommand("jmws_admin");
        adminCommandBase.setPermission(CommandPermission.OP);
        Commands.adminCommands.forEach((cmd) -> {
            adminCommandBase.withSubcommand(new CommandAPICommand(cmd.commandName()).withPermission(CommandPermission.OP).withArguments(cmd.arguments()).executesPlayer(cmd.executor()));
        });
        adminCommandBase.register();
    }
}
