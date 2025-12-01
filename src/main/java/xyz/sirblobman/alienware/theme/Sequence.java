package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.BasicColor;
import xyz.sirblobman.alienware.codes.Command;

import java.util.Locale;

public record Sequence(@NotNull Command command, @NotNull BasicColor color, @Nullable BasicColor color2) {
    @Override
    public @NotNull String toString() {
        String className = getClass().getSimpleName();
        return String.format(Locale.US, "%s{command=%s,color=%s,color2=%s}", className, command, color, color2);
    }
}
