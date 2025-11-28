package xyz.sirblobman.alienware.codes;

/**
 * The Dell 'Alienware 13 R2' has nine known save slots for power states.
 * When the device changes power state, the slot data will be loaded.
 * These are only used to define the available save slots, not to get the status of the laptop.
 * To persist the power state after a restart, a 'permanent save' packet must be sent.
 * This ensures the state is reloaded after a reboot, but needs to be explicitly saved.
 * @author SirBlobman
 */
public enum PowerState {
    /**
     * Boot / Initial. The initial state of the device.
     * Used as a fallback when the laptop is still initializing and doesn't know its power state.
     */
    BOOT(0x01),

    /**
     * Power + Sleep. The laptop is connected to a charger and is in sleep mode.
     */
    AC_SLEEP(0x02),

    /**
     * Power + Full. The laptop is connected to a charger and the battery is fully charged (100%).
     */
    AC_FULL(0x05),

    /**
     * Power + Charging. The laptop is connected to a charger and the battery is being charged (1-99%).
     */
    AC_CHARGING(0x06),

    /**
     * Battery + Sleep. The laptop is on battery power and is in sleep mode.
     */
    BATTERY_SLEEP(0x07),

    /**
     * Battery + On. The laptop is on battery power and turned on.
     */
    BATTERY_ON(0x08),

    /**
     * Battery + Critical. The laptop is on battery power and turned on.
     * The battery has reached critical percentage and should be charged (BIOS version dependant, usually 5% or less)
     * The laptop will shut down soon if not plugged in to a charger.
     */
    BATTERY_CRITICAL(0x09);

    private final byte code;

    PowerState(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return this.code;
    }
}
