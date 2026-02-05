package uk.ac.gold.ade.passwordapp;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import uk.ac.gold.ade.passwordapp.utils.Generator;

public class PasswordApp {

    private static final int DEFAULT_LENGTH = 10;
    private static final int DEFAULT_COUNT = 1;

    public static void main(String[] args) {
        Options options = buildOptions();

        CommandLine cmd;

        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error" + e.getMessage());
            printHelp(options);
            return;
        }

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        int length = getPositiveInt(cmd, "l", DEFAULT_LENGTH, "length", options);
        if (length < 8)
            return;
        int count = getPositiveInt(cmd, "c", DEFAULT_COUNT, "count", options);
        if (count < 0)
            return;
        boolean strong = cmd.hasOption("x");
        boolean special = cmd.hasOption("s") || strong;

        Generator generator = new Generator();

        try {

            for (int i = 0; i < count; i++) {
                String password;

                if (strong) {
                    password = generator.generateStrong(length);
                } else if (special) {
                    password = generator.generateWithSymbols(length);
                } else {
                    password = generator.generate(length);
                }
                System.out.println(password);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printHelp(options);
        }
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(Option.builder("l")
                .longOpt("length")
                .hasArg()
                .argName("N")
                .desc("Password length (default 10)")
                .get());

        options.addOption(Option.builder("c")
                .longOpt("count")
                .hasArg()
                .argName("N")
                .desc("Number of passwords (default 8)")
                .get());

        options.addOption(Option.builder("s")
                .longOpt("special")
                .desc("Allow special characters (@#$%!&*)")
                .get());

        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show help")
                .get());

        return options;
    }

    private static void printHelp(Options options) {
        String cmdLine = "java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar";
        HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
        String header = "Generate random passwords.\n\n";
        String footer = "\nExamples:\n" +
                "  " + cmdLine + "\n" +
                "  " + cmdLine + " -l 16\n" +
                "  " + cmdLine + " -l 16 -s -c 5\n" +
                "  " + cmdLine + " -l 16 -x\n";
        try {
            formatter.printHelp(cmdLine, header, options, footer, true);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to print help", e);
        }
    }

    private static int getPositiveInt(CommandLine cmd, String opt, int def, String name, Options options) {
        if (!cmd.hasOption(opt))
            return def;

        String raw = cmd.getOptionValue(opt);

        try {
            int val = Integer.parseInt(raw);
            if (val < 8) {
                System.err.println("Error: " + name + " must be >= 8");
                printHelp(options);
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.err.println("Error: " + name + " must be a whole number.");
            printHelp(options);
            return -1;
        }

    }
}
