package me.brynview.navidrohim.sponge.commands;

import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.commands.Argument;
import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.api.commands.CommonCommand;
import me.brynview.navidrohim.common.api.commands.CommonCommandContext;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.sponge.JMWSSponge;
import me.brynview.navidrohim.sponge.commands.api.SpongeArgumentType;
import me.brynview.navidrohim.sponge.commands.api.SpongeCommandCommonEncoder;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

import java.util.*;

public class SpongeCommands {

    private static final Map<ArgumentTypes, SpongeCommandCommonEncoder> ARGUMENT_TYPES_TO_ENCODERS = new HashMap<>();
    private static final Map<String, CommonCommand> COMMAND_NAME_TO_OBJ_MAP = new HashMap<>();
    private static final Map<String, Parameter.Key<?>> PARAM_KEYS = new HashMap<>();

    public static void registerCommand(CommonCommand commonCommand, final RegisterCommandEvent<Command.Parameterized> event)
    {
        Constants.getLogger().info("Registering JMWS command %s".formatted(commonCommand.commandName()));
        final List<Parameter> params = new ArrayList<>();

        for (Argument arg : commonCommand.commandArguments()) {
            SpongeArgumentType<?> spongeType = ARGUMENT_TYPES_TO_ENCODERS.get(arg.type()).buildParameterForNative();
            SpongeArgumentType.ParameterAndKey<?> param = SpongeArgumentType.buildParameter(arg.name(), spongeType);

            PARAM_KEYS.put(arg.name(), param.key());
            params.add(param.parameter());
        }

        Command.Builder nativeCommand = Command.builder().addParameters(params).executor((executor) -> {
            return SpongeCommands.executeCommand(executor, commonCommand.commandName());
        });

        if (commonCommand.opOnly())
        {
            nativeCommand.permission("minecraft.command.op");
        }

        event.register(JMWSSponge.getPlugin().container(), nativeCommand.build(), commonCommand.commandName());
        COMMAND_NAME_TO_OBJ_MAP.put(commonCommand.commandName(), commonCommand);
    }

    public static void registerArgument(ArgumentTypes argumentTypes, SpongeCommandCommonEncoder encoder) {
        SpongeCommands.ARGUMENT_TYPES_TO_ENCODERS.put(argumentTypes, encoder);
    }

    private static CommandResult executeCommand(CommandContext commandContext, String commandName)
    {
        if (commandContext.cause().audience().get(Identity.UUID).isPresent())
        {
            CommonCommand commonCommand = COMMAND_NAME_TO_OBJ_MAP.get(commandName);
            HashMap<String, Optional<?>> commonArgs = new HashMap<>();
            UUID UUID = commandContext.cause().audience().get(Identity.UUID).get(); // Sorry but why the name audience? Made no sense.
            WSPlayer commonPlayer = CommonClass.server.getWSPlayer(UUID);

            Arrays.asList(commonCommand.commandArguments()).forEach(arg -> {
                Object proprietaryArgValue = commandContext.requireOne(PARAM_KEYS.get(arg.name()));
                Optional<?> value;

                if (ARGUMENT_TYPES_TO_ENCODERS.containsKey(arg.type())) {
                    value = ARGUMENT_TYPES_TO_ENCODERS.get(arg.type()).getCommonParameterValue(proprietaryArgValue, UUID);
                } else {
                    value = Optional.ofNullable(proprietaryArgValue);
                }
                commonArgs.put(arg.name(), value);
            });

            CommonCommandContext ctx = new CommonCommandContext(commonArgs, commonPlayer);
            commonCommand.executeWithContext(ctx);

            return CommandResult.success();
        } else {
            Constants.getLogger().info("Cannot execute command. Debug: %s".formatted(commandContext.cause().audience().get(Identity.UUID).isPresent()));
            return CommandResult.error(Component.text("Cannot execute command. Internal error."));
        }
    }
}
