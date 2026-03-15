package me.brynview.navidrohim.common.commands;

import me.brynview.navidrohim.common.api.command.CallbackContext;

public record Command(String commandName, CallbackContext executor, Arguments... arguments) {}
