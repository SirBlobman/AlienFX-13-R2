package xyz.sirblobman.alienware;

import net.codecrete.usb.Usb;
import net.codecrete.usb.UsbDevice;
import org.jetbrains.annotations.NotNull;
import xyz.sirblobman.alienware.codes.Reset;
import xyz.sirblobman.alienware.codes.Zone;

import java.util.Arrays;
import java.util.Optional;

public final class Main {
    static void main(String... args) {
        println("[Debug] Submitted CLI arguments:");
        println(Arrays.toString(args));

        int vendorId = 0x187C; // Alienware Corporation
        int productId = 0x0527; // AW13 USB Device
        Optional<UsbDevice> possibleDevice = Usb.findDevice(vendorId, productId);
        if (possibleDevice.isEmpty()) {
            println("Failed to find Alienware AW13 USB Device.");
            println("Exiting with code 1.");
            System.exit(1);
        }

        UsbDevice device = possibleDevice.get();
        println("Successfully found Alienware AW13 USB Device.");
        println("Attempting to configure colors...");
        println("Current Mode: SET_ALL_RED");

        AlienFxDriver driver = new AlienFxDriver(device);
        driver.acquireControl();

        if (!driver.isControlTaken()) {
            println("Failed to take control for AlienFX device.");
            println("Is there another program controlling it?");
            println("Exiting with code 2.");
            System.exit(2);
        }

        AlienFxController controller = new AlienFxController(driver);
        println("Control of AlienFX device initiated.");

        println("Sending reset command...");
        controller.reset(Reset.ALL_TURN_OFF_2);

        println("Waiting for controller to become ready...");
        if (controller.waitUntilControllerReady()) {
            println("Sending color data...");
            controller.setColorSingle(Zone.POWER_BUTTON, BasicColors.BLUE);
            controller.setColorSingle(Zone.EVERYTHING, BasicColors.RED);
        }

        println("Releasing control back to OS.");
        driver.releaseControl();

        println("Exiting with code 0 (success).");
        System.exit(0);
    }

    private static void println(@NotNull String line) {
        System.out.println(line);
    }
}