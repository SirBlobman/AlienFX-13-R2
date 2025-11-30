package xyz.sirblobman.alienware.theme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.BasicColor;
import xyz.sirblobman.alienware.codes.Command;

public record Sequence(@NotNull Command command, @NotNull BasicColor color, @Nullable BasicColor color2) {
    // Empty Record
}
