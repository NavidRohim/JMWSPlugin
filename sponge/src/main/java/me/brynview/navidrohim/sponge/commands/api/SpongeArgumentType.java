package me.brynview.navidrohim.sponge.commands.api;

import org.spongepowered.api.command.parameter.Parameter;

import java.util.function.Supplier;

public record SpongeArgumentType<T>(Class<T> type, Supplier<Parameter.Value.Builder<T>> builder) {

    public record ParameterAndKey<T>(Parameter parameter, Parameter.Key<T> key) {}

    public Parameter.Value.Builder<T> createBuilder() {
        return builder.get();
    }

    public static <T> ParameterAndKey<T> buildParameter(String name, SpongeArgumentType<T> type) {
        Parameter.Key<T> key = Parameter.key(name, type.type());
        return new ParameterAndKey<>(type.createBuilder().key(key).build(), key);
    }
}
