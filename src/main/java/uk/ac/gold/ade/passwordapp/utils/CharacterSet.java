package uk.ac.gold.ade.passwordapp.utils;

public class CharacterSet {
    
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "@#$%!&*";

    public String getUppercase() {
        return UPPER;
    }

    public String getLowercase() {
        return LOWER;
    }

    public String getDigits() {
        return DIGITS;
    }

    public String getSymbols() {
        return SYMBOLS;
    }

    public String getLetters() {
        return UPPER + LOWER;
    }

    public String getAlphaNumeric() {
        return UPPER + LOWER + DIGITS;
    }

    public String getAll() {
        return UPPER + LOWER + DIGITS + SYMBOLS;
    }

}
