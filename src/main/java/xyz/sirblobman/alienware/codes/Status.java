package xyz.sirblobman.alienware.codes;

/**
 * The Dell 'Alienware 13 R2' has three known status codes.
 * These are used when reading the current device status.
 * @author SirBlobman
 */
public enum Status {
    /**
     * Busy. The device is not ready to receive changes.<br/>
     * This could mean one of the following:<br/>
     * - sequence/animation is running<br/>
     * - the reset command was not sent<br/>
     * - a reset is currently being processed.
     */
    BUSY(0x11),

    /**
     * Ready. The device is ready to receive changes.
     */
    READY(0x10),

    /**
     * Unknown. DO NOT USE.
     * This status indicates a failure or error condition.
     * It is typically encountered when the program is no longer in control of the device,
     * or when there is a kernel/permission issue preventing communication.
     * This status should not be used for any checks or iterations.
     * If encountered, ensure proper initialization and permissions, or handle as an error condition.
     */
    UNKNOWN(0x12);

    private final byte code;

    Status(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return this.code;
    }
}
