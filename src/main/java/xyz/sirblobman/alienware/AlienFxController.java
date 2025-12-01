package xyz.sirblobman.alienware;

import net.codecrete.usb.Usb;
import net.codecrete.usb.UsbDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.codes.*;
import xyz.sirblobman.alienware.theme.Sequence;
import xyz.sirblobman.alienware.theme.SequenceList;
import xyz.sirblobman.alienware.theme.SlotData;
import xyz.sirblobman.alienware.theme.Theme;

import java.util.*;
import java.util.function.Consumer;

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

    public void sendMorphColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color1, @NotNull BasicColor color2) {
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

    public void sendSetColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color) {
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

    public void sendPulseColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color, int tempo) {
        List<byte[]> commandList = new ArrayList<>();
        if (slot != null) {
            commandList.add(createSaveToPacket(slot));
            commandList.add(createSetTempoPacket(tempo));
            commandList.add(createSaveToPacket(slot));
            commandList.add(createPulseColorPacket(1, zone, color));
            commandList.add(createSaveToPacket(slot));
            commandList.add(createLoopSequencePacket());
        }

        commandList.add(createSetTempoPacket(tempo));
        commandList.add(createPulseColorPacket(1, zone, color));
        commandList.add(createLoopSequencePacket());
        commandList.add(createExecutePacket());

        AlienFxDriver driver = getDriver();
        for (byte[] packet : commandList) {
            driver.writePacket(packet);
        }
    }

    public void sendTheme(@NotNull Theme theme) {
        List<byte[]> saveCommandList = new ArrayList<>();
        List<byte[]> normalCommandList = new ArrayList<>();
        Map<PowerState, SlotData> slotMap = theme.getSlotMap();
        Set<Map.Entry<PowerState, SlotData>> slotMapEntrySet = slotMap.entrySet();
        for (Map.Entry<PowerState, SlotData> slotMapEntry : slotMapEntrySet) {
            PowerState slot = slotMapEntry.getKey();
            SlotData slotData = slotMapEntry.getValue();

            Map<Zone, SequenceList> zoneMap = slotData.getZoneMap();
            Set<Map.Entry<Zone, SequenceList>> zoneMapEntrySet = zoneMap.entrySet();
            for (Map.Entry<Zone, SequenceList> zoneMapEntry : zoneMapEntrySet) {
                SequenceList sequenceListData = zoneMapEntry.getValue();
                int tempo = sequenceListData.getTempo();
                saveCommandList.add(createSaveToPacket(slot));
                saveCommandList.add(createSetTempoPacket(tempo));

                if (slot == PowerState.BOOT) {
                    normalCommandList.add(createSetTempoPacket(tempo));
                }

                Zone zone = zoneMapEntry.getKey();
                List<Sequence> sequenceList = sequenceListData.getSequenceList();

                int sequenceId = 0;
                for (Sequence sequence : sequenceList) {
                    sequenceId++;
                    Command command = sequence.command();
                    BasicColor color = sequence.color();

                    switch (command) {
                        case SET_COLOR -> {
                            saveCommandList.add(createSaveToPacket(slot));
                            saveCommandList.add(createSetColorPacket(sequenceId, zone, color));
                            if (slot == PowerState.BOOT) {
                                normalCommandList.add(createSetColorPacket(sequenceId, zone, color));
                            }
                        }

                        case PULSE_COLOR -> {
                            saveCommandList.add(createSaveToPacket(slot));
                            saveCommandList.add(createPulseColorPacket(sequenceId, zone, color));
                            if (slot == PowerState.BOOT) {
                                normalCommandList.add(createPulseColorPacket(sequenceId, zone, color));
                            }
                        }

                        case MORPH_COLOR -> {
                            BasicColor color2 = sequence.color2();
                            if (color2 == null) {
                                IO.println("Invalid morph command with missing color found in theme. Skipping");
                                continue;
                            }
                            saveCommandList.add(createSaveToPacket(slot));
                            saveCommandList.add(createMorphColorPacket(sequenceId, zone, color, color2));
                            if (slot == PowerState.BOOT) {
                                normalCommandList.add(createMorphColorPacket(sequenceId, zone, color, color2));
                            }
                        }

                        default -> IO.println("Invalid command found in theme. Skipping");
                    }
                }

                saveCommandList.add(createSaveToPacket(slot));
                saveCommandList.add(createLoopSequencePacket());
                if (slot == PowerState.BOOT) {
                    normalCommandList.add(createLoopSequencePacket());
                }
            }

        }

        saveCommandList.add(createPermanentSavePacket());
        AlienFxDriver driver = getDriver();
        for (byte[] packet : saveCommandList) {
            driver.writePacket(packet);
        }

        normalCommandList.add(createExecutePacket());
        for (byte[] packet : normalCommandList) {
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

    private byte[] createSaveToPacket(@NotNull PowerState state) {
        byte[] packet = createBasicPacket();
        packet[1] = Command.SAVE_NEXT.getCode();
        packet[2] = state.getCode();
        return packet;
    }

    private byte[] createPermanentSavePacket() {
        byte[] packet = createBasicPacket();
        packet[1] = Command.SAVE.getCode();
        return packet;
    }

    public static int getDefaultReadyController(Consumer<AlienFxController> callback) {
        int vendorId = 0x187C; // Alienware Corporation
        int productId = 0x0527; // AW13 USB Device
        Optional<UsbDevice> possibleDevice = Usb.findDevice(vendorId, productId);
        if (possibleDevice.isEmpty()) {
            IO.println("Failed to find Alienware AW13 USB Device.");
            return 1;
        }

        UsbDevice device = possibleDevice.get();
        IO.println("Successfully found default Alienware AW13 USB Device.");
        IO.println("Attempting to acquire control...");

        AlienFxDriver driver = new AlienFxDriver(device);
        driver.acquireControl();

        if (!driver.isControlTaken()) {
            IO.println("Failed to take control for AlienFX device.");
            IO.println("Is there another program controlling it?");
            return 2;
        }

        AlienFxController controller = new AlienFxController(driver);
        IO.println("Control of AlienFX device initiated.");
        IO.println("Sending reset command...");
        controller.reset(Reset.ALL_TURN_OFF_2);

        IO.println("Waiting for controller to become ready...");
        if (controller.waitUntilControllerReady()) {
            IO.println("Running requested command...");
            callback.accept(controller);
        }

        IO.println("Releasing control back to OS.");
        driver.releaseControl();

        IO.println("Exiting with code 0 (success).");
        return 0;
    }
}
