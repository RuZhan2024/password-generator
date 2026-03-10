package uk.ac.gold.ade.passwordapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordApp CLI Tests")
public class PasswordAppTest {

    @Test
    @DisplayName("main(): should print an error and help when length is below 8")
    void main_shouldPrintErrorAndHelpWhenLengthIsBelowEight() {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try (
                PrintStream captureOut = new PrintStream(outBuffer, true, StandardCharsets.UTF_8);
                PrintStream captureErr = new PrintStream(errBuffer, true, StandardCharsets.UTF_8)) {
            System.setOut(captureOut);
            System.setErr(captureErr);

            PasswordApp.main(new String[] { "-l", "7" });
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String stdout = outBuffer.toString(StandardCharsets.UTF_8);
        String stderr = errBuffer.toString(StandardCharsets.UTF_8);
        assertTrue(stderr.contains("Error: length must be >= 8"));
        assertTrue(stdout.contains("usage:  java -jar target/password-generator-1.0-SNAPSHOT-shaded.jar"));
    }
}
