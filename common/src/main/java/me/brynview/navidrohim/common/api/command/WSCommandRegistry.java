package me.brynview.navidrohim.common.api.command;

public interface WSCommandRegistry {
    void register();
    void argParse(CallbackContext callbackContext);
}
