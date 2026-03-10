package uk.ac.gold.ade.passwordapp;

// This class is the entry point of the program.
// It reads command-line arguments, decides which generator mode to use,
// and prints one or more passwords.

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import uk.ac.gold.ade.passwordapp.utils.Generator;

public class PasswordApp {

    // Default values used when the user does not pass -l or -c.
    private static final int DEFAULT_LENGTH = 10;
    private static final int DEFAULT_COUNT = 1;

    public static void main(String[] args) {
        // 1) Build all command line options we support (-l, -c, -s, -x, -h).
        Options options = buildOptions();

        // 2) Parse the user input (args) into a CommandLine object.
        CommandLine cmd;

        try {
            // DefaultParser reads the args and matches them with the options.
            // Example: "-l 16 -s -c 5" becomes:
            // length = 16, special = true, count = 5
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            // If parsing fails, it usually means:
            // - unknown option, or
            // - missing value for an option that needs an argument (like -l or -c),
            // - wrong format
            System.err.println("Error: " + e.getMessage());
            printHelp(options);
            return; // stop program
        }

        // If user asks for help, show help and exit.
        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        // Read the password length.
        // If user did not pass -l, we use DEFAULT_LENGTH (10).
        // getPositiveInt() also checks it is a valid whole number > 0.
        int length = getPositiveInt(cmd, "l", DEFAULT_LENGTH, "length", options);

        // If getPositiveInt() failed, it returns -1.
        // We stop early in that case.
        //
        // Extra rule: we also require length >= 8 for basic safety.
        // Your Generator methods may also have their own checks.
        if (length < 0) {
            return;
        }

        if (length < 8) {
            System.err.println("Error: length must be >= 8");
            printHelp(options);
            return;
        }

        // Read how many passwords to generate.
        // If user did not pass -c, we use DEFAULT_COUNT (1).
        int count = getPositiveInt(cmd, "c", DEFAULT_COUNT, "count", options);

        // If parsing/validation failed, getPositiveInt() returns -1.
        // We stop early so we do not run the loop with an invalid count.
        if (count < 0)
            return;

        // Strong mode: must include at least:
        // - 1 uppercase
        // - 1 digit
        // - 1 symbol
        // This depends on how Generator.generateStrong() is implemented.
        boolean strong = cmd.hasOption("x");

        // Special characters:
        // - If user passes -s, special is true
        // - If user passes -x (strong), special is also true automatically
        //   because strong mode needs symbols.
        boolean special = cmd.hasOption("s") || strong;

        // Create the generator that actually builds password strings.
        Generator generator = new Generator();

        try {
            // Generate and print 'count' passwords.
            for (int i = 0; i < count; i++) {
                String password;

                // Choose one generator method based on the flags.
                // Priority:
                // 1) strong mode first
                // 2) else special mode
                // 3) else normal letters/digits mode
                if (strong) {
                    password = generator.generateStrong(length);
                } else if (special) {
                    password = generator.generateWithSymbols(length);
                } else {
                    password = generator.generate(length);
                }

                // Print one password per line.
                System.out.println(password);
            }

        } catch (IllegalArgumentException e) {
            // If Generator throws an error (for example length too small),
            // we show the error and also show the help screen so the user knows how to fix it.
            System.err.println("Error: " + e.getMessage());
            printHelp(options);
        }
    }

    /**
     * Build all supported command line options.
     * We keep this in one method to make main() shorter and easier to read.
     */
    private static Options buildOptions() {
        Options options = new Options();

        // -l or --length needs a value (like 16).
        options.addOption(Option.builder("l")
                .longOpt("length")
                .hasArg()          // option expects an argument
                .argName("N")      // shows as "N" in the help output
                .desc("Password length (default 10)")
                .get());

        // -c or --count needs a value (like 5).
        options.addOption(Option.builder("c")
                .longOpt("count")
                .hasArg()
                .argName("N")
                .desc("Number of passwords (default 1)")
                .get());

        // -s or --special is a flag (no value).
        // It tells the program to allow symbols like @#$%!&*.
        options.addOption(Option.builder("s")
                .longOpt("special")
                .desc("Allow special characters (@#$%!&*)")
                .get());

        // -x or --strong is a flag (no value).
        // It means "strong password rules".
        // It also implies --special (so symbols are included).
        options.addOption(Option.builder("x")
                .longOpt("strong")
                .desc("Strong mode: require at least 1 uppercase, 1 digit, 1 symbol (implies --special)")
                .get());

        // -h or --help prints the help message.
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show help")
                .get());

        return options;
    }

    /**
     * Print a help screen showing usage, options, and examples.
     * This is called when:
     * - user passes -h
     * - parsing fails
     * - user enters an invalid number
     */
    private static void printHelp(Options options) {
        // This is the command shown in the help output.
        // It matches your shaded jar name.
        String cmdLine = "java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar";

        // HelpFormatter is a Commons CLI helper class.
        // It prints a nice "usage + options" layout.
        HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();

        // Text shown before the options list.
        String header = "Generate random passwords.\n\n";

        // Text shown after the options list.
        // We include a few examples so users can copy and run quickly.
        String footer = "\nExamples:\n" +
                "  " + cmdLine + "\n" +
                "  " + cmdLine + " -l 16\n" +
                "  " + cmdLine + " -l 16 -s -c 5\n" +
                "  " + cmdLine + " -l 16 -x\n";

        try {
            // The last 'true' means: also print the option descriptions.
            formatter.printHelp(cmdLine, header, options, footer, true);
        } catch (java.io.IOException e) {
            // This should be rare, but if printing fails we throw a runtime error.
            throw new RuntimeException("Failed to print help", e);
        }
    }

    /**
     * Read an integer option safely.
     *
     * @param cmd     parsed command line
     * @param opt     short option name, like "l" or "c"
     * @param def     default value to use if option is missing
     * @param name    friendly name used in error messages ("length" or "count")
     * @param options used to print help on error
     * @return a positive integer (> 0), or -1 if invalid
     */
    private static int getPositiveInt(CommandLine cmd, String opt, int def, String name, Options options) {
        // If user did not pass this option, return default value.
        if (!cmd.hasOption(opt))
            return def;

        // Read the raw string argument.
        // Example: "-l 16" -> raw = "16"
        String raw = cmd.getOptionValue(opt);

        try {
            // Convert string to int.
            int val = Integer.parseInt(raw);

            // We only accept values > 0.
            // (For count, this means you cannot generate 0 passwords using -c 0.
            // If you want to allow 0, you would need to change this check.)
            if (val <= 0) {
                System.err.println("Error: " + name + " must be > 0");
                printHelp(options);
                return -1;
            }

            return val;
        } catch (NumberFormatException e) {
            // If raw is not a number (like "abc"), Integer.parseInt fails.
            System.err.println("Error: " + name + " must be a whole number.");
            printHelp(options);
            return -1;
        }
    }
}
