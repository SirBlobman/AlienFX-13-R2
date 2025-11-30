package xyz.sirblobman.alienware.application;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import xyz.sirblobman.alienware.AlienFxController;
import xyz.sirblobman.alienware.BasicColor;
import xyz.sirblobman.alienware.application.converter.BasicColorConverter;
import xyz.sirblobman.alienware.codes.PowerState;
import xyz.sirblobman.alienware.codes.Zone;

import java.util.Locale;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "pulse-color",
        description = "Set a light zone to flash between black and a color"
)
public final class PulseColor implements Callable<Integer> {
    @CommandLine.Option(
            names={"-z", "--zone"},
            description = "The light zone that will be changed.",
            required = true
    )
    private Zone zone;

    @CommandLine.Option(
            names={"-c", "--color"},
            description = "The color to set. Format is 'R,G,B'. Each color is between 0-F.",
            converter = BasicColorConverter.class,
            required = true
    )
    private BasicColor color;

    @CommandLine.Option(
            names={"-s", "--slot"},
            description = "The power slot that will be used for temporary saving."
    )
    private PowerState slot;

    private int tempo;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names={"-t", "--tempo"},
            description = "The tempo to set in milliseconds. Must be between 30 and 942.",
            defaultValue = "200",
            required = true
    )
    public void setTempo(int value) {
        if (value < 30 || value > 942) {
            String message = "Invalid value '%s' for tempo. Must be between 30 and 942.";
            throw new CommandLine.ParameterException(this.spec.commandLine(),
                    String.format(Locale.US, message, value));
        }

        this.tempo = value;
    }

    @Override
    public Integer call() {
        return AlienFxController.getDefaultReadyController(this::setPulse);
    }

    private void setPulse(@NotNull AlienFxController controller) {
        controller.sendPulseColor(this.zone, this.slot, this.color, this.tempo);
    }
}
