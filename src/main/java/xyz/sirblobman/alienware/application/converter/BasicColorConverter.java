package xyz.sirblobman.alienware.application.converter;

import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;
import xyz.sirblobman.alienware.BasicColor;

import java.util.Locale;
import java.util.regex.Pattern;

public final class BasicColorConverter implements CommandLine.ITypeConverter<BasicColor> {
    @Override
    public BasicColor convert(@Nullable String value) throws IllegalArgumentException {
        if (value == null) {
            String message = String.format(Locale.US, "Invalid format: must be 'r,g,b' but was '%s'", "null");
            throw new IllegalArgumentException(message);
        }

        String[] split = value.split(Pattern.quote(","), 3);
        if (split.length != 3) {
            String message = String.format(Locale.US, "Invalid format: must be 'r,g,b' but was '%s'", value);
            throw new IllegalArgumentException(message);
        }

        String redPart = split[0];
        String greenPart = split[1];
        String bluePart = split[2];

        int red = Integer.parseInt(redPart, 16);
        int green = Integer.parseInt(greenPart, 16);
        int blue = Integer.parseInt(bluePart, 16);

        if (red < 0x0 || red > 0xF) {
            String message = String.format(Locale.US, "Invalid number: r must be between 0-F but was %01x", red);
            throw new NumberFormatException(message);
        }

        if (green < 0x0 || green > 0xF) {
            String message = String.format(Locale.US, "Invalid number: g must be between 0-F but was %01x", green);
            throw new NumberFormatException(message);
        }

        if (blue < 0x0 || blue > 0xF) {
            String message = String.format(Locale.US, "Invalid number: b must be between 0-F but was %01x", blue);
            throw new NumberFormatException(message);
        }

        return new BasicColor(red, green, blue);
    }
}
