package xyz.sirblobman.alienware.codes;

/**
 * The Dell 'Alienware 13 R2' laptop has nine possible light zones.<br/>
 * The light zones are controlled with a 2-byte bitmask of zone data.<br/>
 * This means that we can change multiple zones in one command.<br/>
 * Please see the individual documentation for each zone.<br/>
 * It is not possible to change individual keys or letters on this device.
 * @author SirBlobman
 */
public enum Zone {
    /**
     * Keyboard Left. This is the left part of the keyboard.
     * The light may bleed into nearby keys, it is not perfectly defined.
     * Included Keys: ESC, ~`, TAB, CAPS LOCK, SHIFT, CTRL, FN, Meta, Alt, F1-F2, Q, A, S, Z, W, X, 1-3
     */
    KEYBOARD_LEFT(0b0000000000001000),

    /**
     * Keyboard Middle Left. This is the middle-left part of the keyboard.
     * The light may bleed into nearby keys, it is not perfectly defined.
     * Included Keys: F3-F7, 4-6, E, D, C, R, F, V, T, G, B
     */
    KEYBOARD_MIDDLE_LEFT(0b0000000000000100),

    /**
     * Keyboard Middle Right. This is the middle-right part of the keyboard.
     * The light may bleed into nearby keys, it is not perfectly defined.
     * Included Keys: F8-F10, 7-9, Y, H, N, U, J, M, I, K, O, L, <,
     */
    KEYBOARD_MIDDLE_RIGHT(0b0000000000000010),

    /**
     * Keyboard Right. This is the right part of the keyboard.
     * The light may bleed into nearby keys, it is not perfectly defined.
     * Included Keys: F10-F12, Home, End, Delete, 9, O, L, .>, ?/, :;, &quot;', {[, ]}, |\, Enter, Backspace, Shift, Arrows, PgUp, PgDown
     */
    KEYBOARD_RIGHT(0b0000000000000001),

    /**
     * Combined Zones: Keyboard Left, Left-Middle, Right-Middle, Right.
     * The entire keyboard.
     */
    KEYBOARD_ALL(0b0000000000001111),

    /**
     * Power Button. Looks like the Alienware logo or the head of an alien.
     * Requires special saving packets due to power states.
     * You can try to change it without the special packets, but it will quickly revert back to its original colors.
     */
    POWER_BUTTON(0b0000000100000000),

    /**
     * "ALIENWARE" Text. This is the text that is under the display.
     */
    ALIENWARE_TEXT(0b0000000001000000),

    /**
     * Lid + Logo Lines. These are on the laptop lid. Includes the Alienware logo head and two lines.
     */
    LID_LOGO_LINES(0b0000000000100000),

    /**
     * Wi-Fi Radio and Caps Lock Status. These indicator lights can't be changed separately.
     */
    WIFI_CAPS_STATUS(0b0000000010000000),

    /**
     * HDD Status. This is the light that shows HDD/SSD drive activity.
     */
    HDD_STATUS(0b0000001000000000),

    /**
     * Combined Zones: Keyboard All, Alienware Text, Lid Logo Lines, Wi-Fi Radio, Caps Lock Status, and HDD Status
     * Used to change the color of everything at once, excluding the power button due to the special packets required.
     */
    EVERYTHING(0b0000001011101111);

    private final int code;

    Zone(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
