package uk.ac.gold.ade.passwordapp.utils;

import java.security.SecureRandom;

// This class generates random passwords using different rules:
// - normal (letters + digits)
// - with symbols
// - strong (must include uppercase, digit, symbol)
public class Generator {

    // SecureRandom is better than Random for passwords.
    private final SecureRandom random = new SecureRandom();

    // CharacterSet provides the allowed characters (letters, digits, symbols).
    private final CharacterSet characterSet = new CharacterSet();

    // Generate a basic password using letters + digits.
    public String generate(int length) {
        return generateInternal(
                length,
                characterSet.getAlphaNumeric(),
                false,
                false,
                false);
    }

    // Generate a password using letters + digits + symbols.
    public String generateWithSymbols(int length) {
        return generateInternal(
                length,
                characterSet.getAll(),
                false,
                false,
                false);
    }

    // Generate a strong password:
    // it must include at least 1 uppercase, 1 digit, and 1 symbol.
    public String generateStrong(int length) {
        return generateInternal(
                length,
                characterSet.getAll(),
                true,
                true,
                true);
    }

    // Core method that does the real work.
    // "pool" is the full list of characters we can choose from.
    // The require flags force some character types to appear at least once.
    private String generateInternal(
            int length,
            String pool,
            boolean requireUpper,
            boolean requireDigit,
            boolean requireSymbol) {

        // Basic safety rule: do not allow very short passwords.
        if (length < 8) {
            throw new IllegalArgumentException("Length must be >= 8.");
        }

        // Make sure we actually have characters to pick from.
        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("Character pool is empty");
        }

        // We build the password in a char array first, then convert to String.
        char[] out = new char[length];

        // Random positions for required characters, so they are not always at the start.
        int[] pos = shuffledIndices(length);

        // k is the pointer to the next free position in pos[] for required chars.
        int k = 0;

        // Place at least one uppercase if needed.
        if (requireUpper) {
            out[pos[k++]] = randomChar(characterSet.getUppercase());
        }

        // Place at least one digit if needed.
        if (requireDigit) {
            out[pos[k++]] = randomChar(characterSet.getDigits());
        }

        // Place at least one symbol if needed.
        if (requireSymbol) {
            out[pos[k]] = randomChar(characterSet.getSymbols());
        }

        // Fill all remaining empty slots with random characters from the pool.
        // Empty slots are '\0' because char arrays are zero-initialised.
        for (int i = 0; i < length; i++) {
            if (out[i] == '\0') {
                out[i] = randomChar(pool);
            }
        }

        // Convert the char array into a String password.
        return new String(out);
    }

    // Create a random order of indices (0..length-1).
    // This helps us place required characters in random positions.
    private int[] shuffledIndices(int length) {
        int[] idx = new int[length];

        for (int i = 0; i < length; i++) {
            idx[i] = i;
        }

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(i + 1);
            int tmp = idx[i];
            idx[i] = idx[j];
            idx[j] = tmp;
        }
        return idx;
    }

    // Pick one random character from the given string.
    private char randomChar(String chars) {
        int index = random.nextInt(chars.length());
        return chars.charAt(index);
    }
}
