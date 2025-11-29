package xyz.sirblobman.alienware;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.codes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AlienFxController {
    private final AlienFxDriver driver;

    public AlienFxController(@NotNull AlienFxDriver driver) {
        this.driver = driver;
    }

    private @NotNull AlienFxDriver getDriver() {
        return this.driver;
    }

    public @NotNull Status getStatus() {
        AlienFxDriver driver = getDriver();
        byte[] packet = createStatusPacket();
        driver.writePacket(packet);

        byte[] response = driver.readPacket(8);
        if (response == null) {
            System.out.println("Invalid response. Null read packet.");
            return Status.UNKNOWN;
        }

        byte statusCode = response[0];
        if (statusCode == Status.READY.getCode()) {
            return Status.READY;
        }

        return Status.BUSY;
    }

    public void reset(Reset reset) {
        AlienFxDriver driver = getDriver();
        byte[] packet = createResetPacket(reset);
        driver.writePacket(packet);
    }

    public boolean waitUntilControllerReady() {
        boolean isReady = false;
        int errorCount = 0;

        while(!isReady) {
            Status status = getStatus();
            if (status != Status.READY) {
                errorCount++;
                System.out.println("No 'READY' status received yet. Failed tries: " + errorCount);
            } else {
                isReady = true;
            }

            if (errorCount > 50) {
                System.out.println("Could not get READY status. Is the device already in use?");
                return false;
            }
        }

        System.out.println("Controller Ready");
        return true;
    }

    public void setColorMorph(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color1, @NotNull BasicColor color2) {
        List<byte[]> commandList = new ArrayList<>();
        if (slot != null) {
            commandList.add(createSaveToPacket(slot));
            commandList.add(createMorphColorPacket(1, zone, color1, color2));
            commandList.add(createSaveToPacket(slot));
            commandList.add(createLoopSequencePacket());
        }

        commandList.add(createMorphColorPacket(1, zone, color1, color2));
        commandList.add(createLoopSequencePacket());
        commandList.add(createExecutePacket());

        AlienFxDriver driver = getDriver();
        for (byte[] packet : commandList) {
            driver.writePacket(packet);
        }
    }

    public void setColorSingle(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color) {
        List<byte[]> commandList = new ArrayList<>();
        if (slot != null) {
            commandList.add(createSaveToPacket(slot));
            commandList.add(createSetColorPacket(1, zone, color));
            commandList.add(createSaveToPacket(slot));
            commandList.add(createLoopSequencePacket());
        }

        commandList.add(createSetColorPacket(1, zone, color));
        commandList.add(createLoopSequencePacket());
        commandList.add(createExecutePacket());

        AlienFxDriver driver = getDriver();
        for (byte[] packet : commandList) {
            driver.writePacket(packet);
        }
    }

    public void setColorPulse(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color) {
        List<byte[]> commandList = new ArrayList<>();
        if (slot != null) {
            commandList.add(createSaveToPacket(slot));
            commandList.add(createPulseColorPacket(1, zone, color));
            commandList.add(createSaveToPacket(slot));
            commandList.add(createLoopSequencePacket());
        }

        commandList.add(createPulseColorPacket(1, zone, color));
        commandList.add(createLoopSequencePacket());
        commandList.add(createExecutePacket());

        AlienFxDriver driver = getDriver();
        for (byte[] packet : commandList) {
            driver.writePacket(packet);
        }
    }

    private byte[] createBasicPacket() {
        byte[] packet = new byte[9];
        Arrays.fill(packet, (byte) 0);
        packet[0] = 0x02;
        return packet;
    }

    private byte[] createStatusPacket() {
        byte[] packet = createBasicPacket();
        packet[1] = Command.GET_STATUS.getCode();
        return packet;
    }

    private byte[] createResetPacket(Reset reset) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.RESET.getCode();
        packet[2] = (byte) (reset.getCode() & 0xFF);
        return packet;
    }

    private byte[] createMorphColorPacket(int sequence, Zone zone, BasicColor color1, BasicColor color2) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.MORPH_COLOR.getCode();
        packet[2] = (byte) (sequence & 0xFF);

        int zoneCode = zone.getCode();
        packet[4] = (byte) ((zoneCode >> 8) & 0xFF);
        packet[5] = (byte) (zoneCode & 0xFF);

        packet[6] = (byte) ((color1.red() << 4) | color1.green());
        packet[7] = (byte) ((color1.blue() << 4) | color2.red());
        packet[8] = (byte) ((color2.green() << 4) | color2.blue());
        return packet;
    }

    private byte[] createPulseColorPacket(int sequence, Zone zone, BasicColor color) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.PULSE_COLOR.getCode();
        packet[2] = (byte) (sequence & 0xFF);

        int zoneCode = zone.getCode();
        packet[4] = (byte) ((zoneCode >> 8) & 0xFF);
        packet[5] = (byte) (zoneCode & 0xFF);

        packet[6] = (byte) ((color.red() << 4) | color.green());
        packet[7] = (byte) (color.blue() << 4);
        return packet;
    }

    private byte[] createSetColorPacket(int sequence, Zone zone, BasicColor color) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.SET_COLOR.getCode();
        packet[2] = (byte) (sequence & 0xFF);

        int zoneCode = zone.getCode();
        packet[4] = (byte) ((zoneCode >> 8) & 0xFF);
        packet[5] = (byte) (zoneCode & 0xFF);

        packet[6] = (byte) ((color.red() << 4) | color.green());
        packet[7] = (byte) (color.blue() << 4);
        return packet;
    }

    private byte[] createSetTempoPacket(int tempo) {
        if (tempo < 30) {
            tempo = 30;
        }

        if (tempo > 942) {
            tempo = 942;
        }

        byte[] packet = createBasicPacket();
        packet[1] = Command.SET_TEMPO.getCode();
        packet[2] = (byte) ((tempo >> 8) & 0xFF);
        packet[3] = (byte) (tempo & 0xFF);
        return packet;
    }

    private byte[] createLoopSequencePacket() {
        byte[] packet = createBasicPacket();
        packet[1] = Command.LOOP_SEQUENCE.getCode();
        return packet;
    }

    private byte[] createExecutePacket() {
        byte[] packet = createBasicPacket();
        packet[1] = Command.EXECUTE.getCode();
        return packet;
    }

    private byte[] createSaveToPacket(PowerState state) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.SAVE_NEXT.getCode();
        packet[2] = state.getCode();
        return packet;
    }
}
