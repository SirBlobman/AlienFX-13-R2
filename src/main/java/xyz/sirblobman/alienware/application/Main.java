package xyz.sirblobman.alienware.application;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

@CommandLine.Command(
        name = "AlienFX",
        subcommands = {
                CommandLine.HelpCommand.class,
                MorphColor.class,
                PulseColor.class,
                SetColor.class,
                SetTheme.class },
        description = "Modify light zones for the Dell Alienware 13 R2 laptop.",
        version = "AlienFX 13 R2 v1.0.0",
        footer = "Reverse-engineered by SirBlobman"
)
public final class Main implements Runnable {
    @CommandLine.Option(names = { "-V", "--version" }, versionHelp = true,
            description = "print version information and exit")
    boolean versionRequested;

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Specify a subcommand");
    }

    static void main(String @NotNull... args) {
        CommandLine commandLine = new CommandLine(new Main());
        if (args.length == 0) {
            commandLine.usage(System.out);
            return;
        }

        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }

        commandLine.execute(args);
    }
}
