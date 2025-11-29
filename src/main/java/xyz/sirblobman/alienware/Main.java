package xyz.sirblobman.alienware;

import net.codecrete.usb.Usb;
import net.codecrete.usb.UsbDevice;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sirblobman.alienware.codes.PowerState;
import xyz.sirblobman.alienware.codes.Reset;
import xyz.sirblobman.alienware.codes.Zone;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class Main {
    static void main(String... args) {
        println("[Debug] Submitted CLI arguments:");
        println(Arrays.toString(args));

        Options options = new Options();
        options.addOption("z", "zone", true, "The zone to change.");
        options.addOption("s", "slot", true, "The slot that will be saved.");
        options.addOption("c", "color", true, "The color to use for set/pulse. Must be in 'r,g,b' hex format with each color being a value from 0-F.");
        options.addOption("a", "alt-color", true, "An alternative color used for morph.");

        OptionGroup changeGroup = new OptionGroup();
        changeGroup.addOption(new Option("C", "set-color", false, "Set color command. Requires color. Slot is optional."));
        changeGroup.addOption(new Option("P", "pulse-color", false, "Pulse color command. Requires color. Slot is optional."));
        changeGroup.addOption(new Option("M", "morph-color", false, "Morph color command. Requires color and alt color. Slot is optional."));
        changeGroup.addOption(new Option("T", "load-theme", true, "Load a theme, which is a json file."));
        changeGroup.addOption(new Option("h", "help", false, "Print this message."));
        changeGroup.setRequired(true);
        options.addOptionGroup(changeGroup);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cli = parser.parse(options, args, true);
            println("Parsed Arguments:");
            println(Arrays.toString(cli.getOptions()));

            if (cli.hasOption('T')) {
                String jsonPathString = cli.getOptionValue('T');
                loadTheme(jsonPathString);
            } else if (cli.hasOption('C')) {
                if (!cli.hasOption('c')) {
                    throw new ParseException("color is required for the set-color command.");
                }

                if (!cli.hasOption('z')) {
                    throw new ParseException("zone is required for the set-color command.");
                }

                String colorString = cli.getOptionValue('c');
                BasicColor color = parseColor(colorString);

                String zoneString = cli.getOptionValue('z');
                Zone zone = parseZone(zoneString);

                if (cli.hasOption('s')) {
                    String slotString = cli.getOptionValue('s');
                    PowerState slot = parseSlot(slotString);
                    setColor(zone, slot, color);
                } else {
                    setColor(zone, null, color);
                }
            } else if (cli.hasOption('P')) {
                if (!cli.hasOption('c')) {
                    throw new ParseException("color is required for the pulse-color command.");
                }

                if (!cli.hasOption('z')) {
                    throw new ParseException("zone is required for the pulse-color command.");
                }

                String colorString = cli.getOptionValue('c');
                BasicColor color = parseColor(colorString);

                String zoneString = cli.getOptionValue('z');
                Zone zone = parseZone(zoneString);

                if (cli.hasOption('s')) {
                    String slotString = cli.getOptionValue('s');
                    PowerState slot = parseSlot(slotString);
                    pulseColor(zone, slot, color);
                } else {
                    pulseColor(zone, null, color);
                }
            } else if (cli.hasOption('M')) {
                if (!cli.hasOption('c')) {
                    throw new ParseException("color is required for the morph-color command.");
                }

                if (!cli.hasOption('a')) {
                    throw new ParseException("alt-color is required for the morph-color command.");
                }

                if (!cli.hasOption('z')) {
                    throw new ParseException("zone is required for the morph-color command.");
                }

                String colorString = cli.getOptionValue('c');
                BasicColor color = parseColor(colorString);

                String altColorString = cli.getOptionValue('a');
                BasicColor altColor = parseColor(altColorString);

                String zoneString = cli.getOptionValue('z');
                Zone zone = parseZone(zoneString);

                if (cli.hasOption('s')) {
                    String slotString = cli.getOptionValue('s');
                    PowerState slot = parseSlot(slotString);
                    morphColor(zone, slot, color, altColor);
                } else {
                    morphColor(zone, null, color, altColor);
                }
            } else if(cli.hasOption('h')) {
                try {
                    String syntax = "java -jar alienfx.jar";
                    String header = "Control light zones for the Dell Alienware 13 R2 laptop.";
                    String footer = "Please report issues to GitHub: https://github.com/SirBlobman/AlienFX-13-R2/issues";
                    HelpFormatter formatter = HelpFormatter.builder().get();
                    formatter.printHelp(syntax, header, options, footer, true);
                } catch (IOException ex) {
                    println("An error occurred while printing help.");
                    ex.printStackTrace();
                }
            } else {
                throw new ParseException("No command selected.");
            }
        } catch (ParseException ex) {
            println("Failed to parse command line arguments. Please correct any mistakes:");
            println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void println(@NotNull String line) {
        System.out.println(line);
    }

    private static @NotNull PowerState parseSlot(@Nullable String name) throws ParseException {
        if (name == null) {
            throw new ParseException("Slot is not set.");
        }

        try {
            String uppercaseName = name.toUpperCase(Locale.US);
            return PowerState.valueOf(uppercaseName);
        } catch(IllegalArgumentException ex) {
            String slots = Arrays.toString(PowerState.values());
            String message = String.format(Locale.US, "Invalid slot '%s'. Use one of %s", name, slots);
            throw new ParseException(message);
        }
    }

    private static @NotNull Zone parseZone(@Nullable String name) throws ParseException {
        if (name == null) {
            throw new ParseException("Zone is not set.");
        }

        try {
            String uppercaseName = name.toUpperCase(Locale.US);
            return Zone.valueOf(uppercaseName);
        } catch(IllegalArgumentException ex) {
            String slots = Arrays.toString(Zone.values());
            String message = String.format(Locale.US, "Invalid zone '%s'. Use one of %s", name, slots);
            throw new ParseException(message);
        }
    }

    private static @NotNull BasicColor parseColor(@Nullable String string) throws ParseException {
        if (string == null) {
            throw new ParseException("Invalid color.");
        }

        String[] split = string.split(Pattern.quote(","), 3);
        if (split.length != 3) {
            throw new ParseException("Invalid color. Must be in format 'r,g,b'.");
        }

        String redPart = split[0];
        String greenPart = split[1];
        String bluePart = split[2];

        try {
            int red = Integer.parseInt(redPart, 16);
            int green = Integer.parseInt(greenPart, 16);
            int blue = Integer.parseInt(bluePart, 16);

            if (red < 0x0 || red > 0xF) {
                throw new NumberFormatException("r must be between 0-F.");
            }

            if (green < 0x0 || green > 0xF) {
                throw new NumberFormatException("g must be between 0-F.");
            }

            if (blue < 0x0 || blue > 0xF) {
                throw new NumberFormatException("b must be between 0-F.");
            }

            return new BasicColor(red, green, blue);
        } catch (IllegalArgumentException ex) {
            throw new ParseException(ex);
        }
    }

    private static void loadTheme(@NotNull String json) {
        println("Selected JSON: " + json);
        println("Themes are not implemented yet. Please try again in a future version.");
        System.exit(1);
    }

    private static void getReadyController(Consumer<AlienFxController> callback) {
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
            println("Running requested command...");
            callback.accept(controller);
        }

        println("Releasing control back to OS.");
        driver.releaseControl();

        println("Exiting with code 0 (success).");
        System.exit(0);
    }

    private static void setColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color) {
        getReadyController(controller -> controller.setColorSingle(zone, slot, color));
    }

    private static void pulseColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color) {
        getReadyController(controller -> controller.setColorPulse(zone, slot, color));
    }

    private static void morphColor(@NotNull Zone zone, @Nullable PowerState slot, @NotNull BasicColor color, @NotNull BasicColor color2) {
        getReadyController(controller -> controller.setColorMorph(zone, slot, color, color2));
    }
}