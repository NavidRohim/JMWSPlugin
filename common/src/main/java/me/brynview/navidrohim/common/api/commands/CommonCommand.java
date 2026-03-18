package me.brynview.navidrohim.common.api.commands;

import java.util.function.Consumer;
import java.util.function.Function;

public record CommonCommand(String commandName, boolean opOnly, Consumer<CommonCommandContext> commandFunc, Argument... commandArguments)
{
    public void executeWithContext(CommonCommandContext context)
    {
        commandFunc.accept(context);
    }
}
