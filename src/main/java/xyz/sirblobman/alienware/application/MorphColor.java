package xyz.sirblobman.alienware.application;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import xyz.sirblobman.alienware.AlienFxController;
import xyz.sirblobman.alienware.BasicColor;
import xyz.sirblobman.alienware.application.converter.BasicColorConverter;
import xyz.sirblobman.alienware.codes.PowerState;
import xyz.sirblobman.alienware.codes.Zone;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "morph-color",
        description = "Set a light zone to smoothly morph between two colors"
)
public final class MorphColor implements Callable<Integer> {
    @CommandLine.Option(
            names={"-z", "--zone"},
            description = "The light zone that will be changed.",
            required = true
    )
    private Zone zone;

    @CommandLine.Option(
            names={"-c", "--color"},
            description = "The main color for the morph. Format is 'R,G,B'. Each color is between 0-F.",
            converter = BasicColorConverter.class,
            required = true
    )
    private BasicColor color;

    @CommandLine.Option(
            names={"-a", "--alt-color"},
            description = "The alternative color for the morph. Format is 'R,G,B'. Each color is between 0-F.",
            converter = BasicColorConverter.class,
            required = true
    )
    private BasicColor color2;

    @CommandLine.Option(
            names={"-s", "--slot"},
            description = "The power slot that will be used for temporary saving."
    )
    private PowerState slot;

    @Override
    public Integer call() {
        return AlienFxController.getDefaultReadyController(this::setColor);
    }

    private void setColor(@NotNull AlienFxController controller) {
        controller.sendMorphColor(this.zone, this.slot, this.color, this.color2);
    }
}
