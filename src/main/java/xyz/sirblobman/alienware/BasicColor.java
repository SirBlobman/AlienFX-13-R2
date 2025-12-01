package xyz.sirblobman.alienware;

import java.util.Locale;

public record BasicColor(int red, int green, int blue) {
    public BasicColor {
        if (red < 0 || red > 15) {
            throw new IllegalArgumentException("red is out of range 0-15");
        }

        if (green < 0 || green > 15) {
            throw new IllegalArgumentException("green is out of range 0-15");
        }

        if (blue < 0 || blue > 15) {
            throw new IllegalArgumentException("blue is out of range 0-15");
        }
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        return String.format(Locale.US, "%s{r=0x%01x,g=0x%01x,b=0x%01x}", className, this.red, this.green, this.blue);
    }
}
