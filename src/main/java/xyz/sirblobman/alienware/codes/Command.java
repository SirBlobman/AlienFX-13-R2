package xyz.sirblobman.alienware.codes;

/**
 * The Dell 'Alienware 13 R2' has ten known packet commands.
 * All packets must be written as nine bytes. To start using any other commands, a RESET command must be sent first.
 * Then you can use GET_STATUS to check if the device is ready to receive commands.
 * The status response packet will only be 8 bytes.
 * @author SirBlobman
 */
public enum Command {
    /**
     * Morph Color. Packet format is '02:01:nn:00:zz:zz:rg:bR:GB'
     * The color will change from 'rgb' to 'RGB' smoothly. Not controlled by tempo.
     * 02: All write packets start with 02.
     * 01: Morph Color command.
     * nn: Sequence ID.
     * 00: No data.
     * zz: Zone (2 bytes)
     * rg: Red 1 (0-15) + Green 1 (0-15)
     * bR: Blue 1 (0-15) + Red 2 (0-15)
     * GB: Green 2 (0-15) + Blue 2 (0-15)
     * @see Zone
     * @see xyz.sirblobman.alienware.BasicColor
     */
    MORPH_COLOR(0x01),

    /**
     * Pulse Color. Packet format is '02:02:nn:00:zz:zz:rg:b0:00'
     * The color will blink between turned off and the selected color.
     * Speed is controlled by tempo.
     * 02: All write packets start with 02.
     * 02: Pulse Color command.
     * nn: Sequence ID.
     * zz: Zone (2 bytes)
     * rg: Red (0-15) + Green (0-15)
     * b0: Blue (0-15) + no data.
     * 00: no data.
     * @see Zone
     * @see xyz.sirblobman.alienware.BasicColor
     */
    PULSE_COLOR(0x02),

    /**
     * Fixed Color. Packet format is '02:03:nn:00:zz:zz:rg:b0:00'
     * The color will be fixed, no pulsing or morphing.
     * 02: All write packets start with 02.
     * 03: Set Color command.
     * nn: Sequence ID.
     * zz: Zone (2 bytes)
     * rg: Red (0-15) + Green (0-15)
     * b0: Blue (0-15) + no data.
     * 00: no data.
     * @see Zone
     * @see xyz.sirblobman.alienware.BasicColor
     */
    SET_COLOR(0x03),

    /**
     * Loop. Without this, LEDs will go off after walking through the user-specified color sequence.
     * Packet format is '02:04:00:00:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 03: Loop command.
     * 00: No data.
     */
    LOOP_SEQUENCE(0x04),

    /**
     * Execute. This will start following the sequence that was defined.
     * Packet format is '02:05:00:00:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 05: Execute command.
     * 00: No data.
     */
    EXECUTE(0x05),

    /**
     * Update status. This needs to be followed up with a read packet for 8 bytes.
     * Packet format for the command is '02:05:00:00:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 06: Get Status command.
     * 00: No data.
     * Packet format for the reading response is 'ss:00:00:00:00:00:00:00'
     * ss: Status code.
     * 00: No data.
     * @see Status
     */
    GET_STATUS(0x06),

    /**
     * Reset. This must be sent before setting up any sequence, tempo, or saving.
     * Packet format is '02:07:tt:00:00:00:00:00:00'.
     * 02: All write packets start with 02.
     * 07: Reset command.
     * tt: Reset type.
     * 00: No data.
     * @see Reset
     */
    RESET(0x07),

    /**
     * Save Next. This will save the next command to the specified slot.
     * Packet format is '02:08:ss:00:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 08: Save Next command.
     * ss: Power state save slot.
     * 00: No data.
     * @see PowerState
     */
    SAVE_NEXT(0x08),

    /**
     * Permanent Save. This will permanently save the data currently set in all slots.
     * Packet format is '02:09:00:00:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 09: Permanent Save command.
     * 00: No data.
     */
    SAVE(0x09),

    /**
     * Tempo. This effects how quick the change is between parts of a sequence.
     * Official AlienFX drivers use values between 00:1e and 03:ae. Lower is faster.
     * Using values too low or too high may cause glitches.
     * Packet format is '02:0E:tt:tt:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 0E: Tempo command.
     * tt: Tempo value (2 bytes, milliseconds)
     * 00: No data.
     */
    SET_TEMPO(0x0E),

    /**
     * Untested command. Seems to affect light dimming.
     * Packet format is '02:1C:oo:bb:00:00:00:00:00'
     * 02: All write packets start with 02.
     * 1C: Dim command.
     * oo: 32 (Enable), 64 (Disable)
     * bb: 01 (Always), 00 (in Battery Mode Only)
     * 00: No data.
     */
    DIM(0x1C),

    /**
     * Hacked/untested command. Still being reverse engineered.
     * This command was found on a hack-alienfx GitHub page for the Alienware 13.
     * This command may not apply to the Alienware 13 R2.
     * Packet format is '02:1D:dd:00:00:00:00:00:00'.
     * 02: All write packets start with 02.
     * 0D: Command 0D (unknown)
     * dd: 03 (On Apply), 81 (On go-dark)
     * 00: No data.
     */
    UNKNOWN(0x1D);

    private final byte code;

    Command(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return this.code;
    }
}
