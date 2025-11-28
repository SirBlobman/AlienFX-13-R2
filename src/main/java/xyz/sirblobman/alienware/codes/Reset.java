package xyz.sirblobman.alienware.codes;

/**
 * The Dell 'Alienware 13 R2' has five known reset codes.
 * Doing a reset is required to make tempo, color, and sequence changes.
 * A reset stops any running color sequences (i.e. a series of color changes, blinks, or morphs).
 * This stops the current animation or effect immediately.
 * I'm not 100% sure on the internal logic.
 * This class may be updated in the future if more codes are found or if their function is discovered to be different.
 * @author SirBlobman
 */
public enum Reset {
    /**
     * Keyboard and Lid: Turn Off.
     * This reset mode will only turn off the lid and keyboard lights.
     */
    KEYBOARD_LID_TURN_OFF_1(0x00),

    /**
     * Keyboard and Lid: Turn Off.
     * Same as {@link #KEYBOARD_LID_TURN_OFF_1}
     */
    KEYBOARD_LID_TURN_OFF_2(0x01),

    /**
     * All Zones: Turn Off.
     * This reset mode will turn off all the light zones.
     */
    ALL_TURN_OFF_1(0x02),

    /**
     * All Zones: Turn Off.
     * Same as {@link #ALL_TURN_OFF_1}
     */
    ALL_TURN_OFF_2(0x03),

    /**
     * All Zones: Stay On.
     * This mode keeps all zones on their last color (e.g., RED, BLUE, etc.).
     * Behavior with active sequences (color morphs or blinking patterns) is unconfirmed.
     */
    ALL_STAY_ON(0x04);

    private final byte code;

    Reset(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return this.code;
    }
}
