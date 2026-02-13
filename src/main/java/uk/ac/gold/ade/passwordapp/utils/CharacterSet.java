package uk.ac.gold.ade.passwordapp.utils;

// This class stores the different character groups we can use to build passwords.
// It helps keep the character lists in one place.

public class CharacterSet {

    // Uppercase letters A-Z
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Lowercase letters a-z
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    // Number characters 0-9
    private static final String DIGITS = "0123456789";

    // Allowed symbol characters
    private static final String SYMBOLS = "@#$%!&*";

    // Return uppercase letters
    public String getUppercase() {
        return UPPER;
    }

    // Return lowercase letters
    public String getLowercase() {
        return LOWER;
    }

    // Return digits
    public String getDigits() {
        return DIGITS;
    }

    // Return symbols
    public String getSymbols() {
        return SYMBOLS;
    }

    // Return all letters (uppercase + lowercase)
    public String getLetters() {
        return UPPER + LOWER;
    }

    // Return letters + digits
    public String getAlphaNumeric() {
        return UPPER + LOWER + DIGITS;
    }

    // Return letters + digits + symbols (full set)
    public String getAll() {
        return UPPER + LOWER + DIGITS + SYMBOLS;
    }

}
