package me.brynview.navidrohim.common.api.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ArgumentCasterRegistry {
    private static final Map<Class<?>, Function<Object, ?>> CASTERS = new HashMap<>();

    public static <T> void register(Class type, Function<Object, T> caster) {
        CASTERS.put(type, caster);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> type, Object value) {
        Function<Object, ?> caster = CASTERS.get(type);
        if (caster == null) {
            throw new IllegalArgumentException("No casts registered for " + type);
        }
        return (T) caster.apply(value);
    }

    public static boolean hasCastFor(Class<?> type) {
        return CASTERS.containsKey(type);
    }

}
