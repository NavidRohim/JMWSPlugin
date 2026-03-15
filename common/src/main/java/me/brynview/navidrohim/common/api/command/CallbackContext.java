package me.brynview.navidrohim.common.api.command;

import me.brynview.navidrohim.common.api.server.WSPlayer;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.function.Function;

public class CallbackContext {
    private final WSCommandRegistry registry;
    private final Function<CallbackContext, Void> callback;
    public final HashMap<String, Object> args = new HashMap<>();
    public final WSPlayer sender;

    public CallbackContext(WSCommandRegistry registry, Function<CallbackContext, Void> callback, WSPlayer sender)
    {
        this.sender = sender;
        this.registry = registry;
        this.callback = callback;
    }

    public void processArgs()
    {
        registry.argParse(this);
    }

    public void callback()
    {
        callback.apply(this);
    }
}
