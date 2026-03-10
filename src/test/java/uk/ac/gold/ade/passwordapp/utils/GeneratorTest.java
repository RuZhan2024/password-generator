package uk.ac.gold.ade.passwordapp.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Password Generator - Unit Tests")
public class GeneratorTest {

    @Test
    @DisplayName("generate(): should return a password of the requested length")
    void generate_shouldReturnCorrectLength() {
        Generator generator = new Generator();
        String password = generator.generate(16);
        assertEquals(16, password.length());
    }

    @Test
    @DisplayName("generate(): should reject length below 8")
    void generate_shouldRejectLengthBelowEight() {
        Generator generator = new Generator();
        assertThrows(IllegalArgumentException.class, () -> generator.generate(5));
    }

    @Test
    @DisplayName("generate(): should accept length 8 (boundary)")
    void generate_shouldAcceptLengthEight() {
        Generator generator = new Generator();
        String password = generator.generate(8);
        assertEquals(8, password.length());
    }

    @Test
    @DisplayName("generate(): should reject non-positive length")
    void generate_shouldRejectNonPositiveLength() {
        Generator generator = new Generator();
        assertThrows(IllegalArgumentException.class, () -> generator.generate(0));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    @DisplayName("generateStrong(): should reject length below 8")
    void generateStrong_shouldRejectLengthBelowEight() {
        Generator generator = new Generator();
        assertThrows(IllegalArgumentException.class, () -> generator.generateStrong(5));
    }

    @Test
    @DisplayName("generateStrong(): should accept length 8 (boundary)")
    void generateStrong_shouldAcceptLengthEight() {
        Generator generator = new Generator();
        String password = generator.generateStrong(8); // ✅ fixed (was generate(8))
        assertEquals(8, password.length());
    }

    private boolean contains(String password, String regex) {
        return Pattern.compile(regex).matcher(password).find();
    }

    @Test
    @DisplayName("generateStrong(): should contain uppercase, digit, and symbol")
    void generateStrong_shouldAcceptLengthEight_shouldContainRequiredCategories() {
        Generator generator = new Generator();

        for (int i = 0; i < 100; i++) {
            String password = generator.generateStrong(8);
            assertTrue(contains(password, "[A-Z]"), "Should contain at least 1 uppercase letter");
            assertTrue(contains(password, "[0-9]"), "Should contain at least 1 digit");
            assertTrue(contains(password, "[@#$%!&*]"), "Should contain at least 1 symbol");
        }
    }

    @Test
    @DisplayName("generateStrong(): should reject non-positive length")
    void generateStrong_shouldRejectNonPositiveLength() {
        Generator generator = new Generator();
        assertThrows(IllegalArgumentException.class, () -> generator.generateStrong(0));
        assertThrows(IllegalArgumentException.class, () -> generator.generateStrong(-1));
    }

    @Test
    @DisplayName("generateWithSymbols(): should reject length below 8")
    void generateWithSymbols_shouldRejectLengthBelowEight() {
        Generator generator = new Generator();
        assertThrows(IllegalArgumentException.class, () -> generator.generateWithSymbols(7));
    }

    @Test
    @DisplayName("generateWithSymbols(): should accept length 8 (boundary)")
    void generateWithSymbols_shouldAcceptLengthEight() {
        Generator generator = new Generator();
        String password = generator.generateWithSymbols(8);
        assertEquals(8, password.length());
    }

    @Test
    @DisplayName("generateWithSymbols(): should only use characters from the allowed set")
    void generateWithSymbols_shouldOnlyUseAllowedCharacters() {
        CharacterSet characterSet = new CharacterSet();
        String allChars = characterSet.getAll();
        Generator generator = new Generator();

        String password = generator.generateWithSymbols(200);

        for (char _char : password.toCharArray()) {
            assertTrue(allChars.indexOf(_char) >= 0, "Unexpected character found: " + _char);
        }
    }
}
