package uk.ac.gold.ade.passwordapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;

public class Generator {

    private final SecureRandom random = new SecureRandom();
    private final CharacterSet characterSet = new CharacterSet();

    public String generate(int length) {
        return generateInternal(
                length, characterSet.getAlphaNumeric(),
                false,
                false,
                false);
    }

    public String generateWithSymbols(int length) {
        return generateInternal(
                length,
                characterSet.getAll(),
                false,
                false,
                false);
    }

    public String generateStrong(int length) {
        return generateInternal(
                length,
                characterSet.getAll(),
                true,
                true,
                true);
    }

    private String generateInternal(
            int length,
            String pool,
            boolean requireUpper,
            boolean requireDigit,
            boolean requireSymbol) {
        if (length < 8) {
            throw new IllegalArgumentException("Length muxt be >= 8.");
        }

        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("Character pool is empty");
        }

        char[] out = new char[length];

        int[] pos = shuffledIndices(length);

        int k = 0;

        if (requireUpper) {
            out[pos[k++]] = randomChar(characterSet.getUppercase());
        }
        if (requireDigit) {
            out[pos[k++]] = randomChar(characterSet.getDigits());
        }
        if (requireSymbol) {
            out[pos[k]] = randomChar(characterSet.getSymbols());
        }

        for (int i = 0; i < length; i++) {
            if (out[i] == '\0') {
                out[i] = randomChar(pool);
            }
        }
        return new String(out);
    }

    private int[] shuffledIndices(int length) {
        int[] idx = new int[length];

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(i + 1);
            int tmp = idx[i];
            idx[i] = idx[j];
            idx[j] = tmp;
        }
        return idx;
    }

    private char randomChar(String chars) {
        int index = random.nextInt(chars.length());
        return chars.charAt(index);
    }
}
