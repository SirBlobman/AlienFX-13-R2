package xyz.sirblobman.alienware;

public record BasicColor(int red, int green, int blue) {
    public BasicColor {
        if (red < 0 || red > 15) {
            throw new IllegalArgumentException("red is out of range 0-15");
        }

        if (green < 0 || green > 15) {
            throw new IllegalArgumentException("green is out of range 0-15");
        }

        if (blue < 0 || blue > 15) {
            throw new IllegalArgumentException("blue is out of range 0-255");
        }
    }
}
