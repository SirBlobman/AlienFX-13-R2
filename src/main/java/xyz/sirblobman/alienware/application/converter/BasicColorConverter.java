package xyz.sirblobman.alienware.application.converter;

import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;
import xyz.sirblobman.alienware.BasicColor;

import java.util.regex.Pattern;

public final class BasicColorConverter implements CommandLine.ITypeConverter<BasicColor> {
    @Override
    public BasicColor convert(@Nullable String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("color must not be null.");
        }

        String[] split = value.split(Pattern.quote(","), 3);
        if (split.length != 3) {
            throw new IllegalArgumentException("color must must be in format 'r,g,b'.");
        }

        String redPart = split[0];
        String greenPart = split[1];
        String bluePart = split[2];

        int red = Integer.parseInt(redPart, 16);
        int green = Integer.parseInt(greenPart, 16);
        int blue = Integer.parseInt(bluePart, 16);

        if (red < 0x0 || red > 0xF) {
            throw new NumberFormatException("r must be between 0-F.");
        }

        if (green < 0x0 || green > 0xF) {
            throw new NumberFormatException("g must be between 0-F.");
        }

        if (blue < 0x0 || blue > 0xF) {
            throw new NumberFormatException("b must be between 0-F.");
        }

        return new BasicColor(red, green, blue);
    }
}
