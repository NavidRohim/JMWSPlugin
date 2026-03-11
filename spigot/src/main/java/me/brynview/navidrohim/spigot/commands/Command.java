package me.brynview.navidrohim.spigot.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;

public record Command(String commandName, PlayerCommandExecutor executor, Argument<?>... arguments) {}
