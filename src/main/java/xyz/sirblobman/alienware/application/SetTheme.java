package xyz.sirblobman.alienware.application;

import picocli.CommandLine;
import xyz.sirblobman.alienware.AlienFxController;
import xyz.sirblobman.alienware.theme.Theme;
import xyz.sirblobman.alienware.theme.ThemeParseException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "set-theme",
        description = "Load a theme from a file and save it to the AlienFX memory."
)
public final class SetTheme implements Callable<Integer> {
    @CommandLine.Parameters(
            index = "0",
            description = "The path to the file that will be loaded as a theme."
    )
    private File file;

    @Override
    public Integer call() throws IOException, ThemeParseException {
        Path filePath = this.file.toPath();
        String json = Files.readString(filePath, StandardCharsets.UTF_8);
        Theme theme = Theme.loadTheme(json);
        return AlienFxController.getDefaultReadyController(controller -> controller.sendTheme(theme));
    }
}
