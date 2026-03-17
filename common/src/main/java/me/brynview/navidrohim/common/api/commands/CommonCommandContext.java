package me.brynview.navidrohim.common.api.commands;

import me.brynview.navidrohim.common.api.game.WSPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record CommonCommandContext(HashMap<String, Optional<?>> arguments, WSPlayer commandSender)
{
    public <T> Optional<T> getArgumentAsType(String key, Class<T> clazz) {
        Optional<?> value = arguments.get(key);

        if (value.isPresent()) {
            Object valueObj = value.get();
            return Optional.of(clazz.cast(valueObj));
        }

        return Optional.empty();
    }
}
