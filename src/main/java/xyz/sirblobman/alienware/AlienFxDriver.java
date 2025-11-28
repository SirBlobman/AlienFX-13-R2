package xyz.sirblobman.alienware;

import net.codecrete.usb.UsbControlTransfer;
import net.codecrete.usb.UsbDevice;
import net.codecrete.usb.UsbRecipient;
import net.codecrete.usb.UsbRequestType;
import net.codecrete.usb.linux.LinuxUsbDevice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class AlienFxDriver {
    private final UsbDevice device;
    private boolean controlTaken;

    public AlienFxDriver(@NotNull UsbDevice device) {
        this.device = device;
        this.controlTaken = false;
    }

    private UsbDevice getDevice() {
        return this.device;
    }

    public boolean isControlTaken() {
        return this.controlTaken;
    }

    public void acquireControl() {
        if (isControlTaken()) {
            return;
        }

        UsbDevice device = getDevice();

        try {
            device.detachStandardDrivers();
        } catch(Exception ex) {
            System.out.println("Failed to detach kernel drivers:");
            ex.printStackTrace();
            return;
        }

        try {
            device.open();
            device.claimInterface(0);
        } catch (Exception ex) {
            System.out.println("Failed to claim device interface:");
            ex.printStackTrace();
            return;
        }

        this.controlTaken = true;
        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        String message = String.format(Locale.US,
                "Acquired control of USB device with VID: 0x%04x and PID: 0x%04x.", vendorId, productId);
        System.out.println(message);
    }

    public void releaseControl() {
        if(!isControlTaken()) {
            return;
        }

        UsbDevice device = getDevice();

        try {
            device.releaseInterface(0);
            device.close();
        } catch (Exception ex) {
            System.out.println("Failed to unclaim device interface:");
            ex.printStackTrace();
            return;
        }

        try {
            device.attachStandardDrivers();
        } catch (Exception ex) {
            System.out.println("Failed to attach kernel drivers:");
            ex.printStackTrace();
            return;
        }

        this.controlTaken = false;
        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        String message = String.format(Locale.US,
                "Released control of USB device with VID: 0x%04x and PID: 0x%04x.", vendorId, productId);
        System.out.println(message);
    }

    public void writePacket(byte[] packet) {
        if (!isControlTaken()) {
            return;
        }

        try {
            UsbDevice device = getDevice();
            UsbControlTransfer transfer = new UsbControlTransfer(
                    UsbRequestType.CLASS,
                    UsbRecipient.INTERFACE,
                    0x09,
                    0x202,
                    0
            );
            device.controlTransferOut(transfer, packet);
            System.out.println("wrote: " + hexPacket(packet) + ", " + packet.length + " bytes");
        } catch (Exception ex) {
            System.out.println("Failed to write a packet:");
            ex.printStackTrace();
        }
    }

    public byte[] readPacket(int length) {
        if(!isControlTaken()) {
            System.out.println("Can't read any packet, control is not taken!");
            return null;
        }

        try {
            UsbDevice device = getDevice();
            UsbControlTransfer transfer = new UsbControlTransfer(
                    UsbRequestType.CLASS,
                    UsbRecipient.INTERFACE,
                    0x01,
                    0x101,
                    0
            );

            byte[] buffer = device.controlTransferIn(transfer, length);
            System.out.println("read: " + hexPacket(buffer) + ", " + buffer.length + " bytes");
            return buffer;
        } catch (Exception ex) {
            System.out.println("Failed to read a packet:");
            ex.printStackTrace();
            return null;
        }
    }

    @Contract("null -> null")
    private String hexPacket(byte[] packet) {
        if (packet == null) {
            return null;
        }

        int indexLength = packet.length;
        int indexMax = (indexLength - 1);
        if (indexMax == -1) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('[');

        for (int index = 0; index < indexLength; index++) {
            byte data = packet[index];
            String dataString = String.format(Locale.US, "0x%02x", data);
            builder.append(dataString);

            if (index != indexMax) {
                builder.append(", ");
            }
        }

        builder.append("]");
        return builder.toString();
    }
}
