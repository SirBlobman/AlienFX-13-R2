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
        name = "set-color",
        description = "Set a light zone to a single color."
)
public final class SetColor implements Callable<Integer> {
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

    @Override
    public Integer call() {
        return AlienFxController.getDefaultReadyController(this::setColor);
    }

    private void setColor(@NotNull AlienFxController controller) {
        controller.sendSetColor(this.zone, this.slot, this.color);
    }
}
