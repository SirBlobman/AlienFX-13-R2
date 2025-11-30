package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;

public class ThemeParseException extends RuntimeException {
    public ThemeParseException(@NotNull String message) {
        super(message);
    }

    public ThemeParseException(@NotNull Throwable ex) {
        super(ex);
    }

    public ThemeParseException(@NotNull String message, @NotNull Throwable ex) {
        super(message, ex);
    }
}
