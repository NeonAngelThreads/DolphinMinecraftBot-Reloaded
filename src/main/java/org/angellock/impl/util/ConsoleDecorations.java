package org.angellock.impl.util;

import org.angellock.impl.util.colorutil.SimpleColor;

public enum ConsoleDecorations {
    BOLD("\u001B[1m", 'L'),
    UNDERLINED("\u001B[4m", 'N'),
    REVERSE("\u001B[7m", 'R' ),
    STRIKETHROUGH("",'m'),
    ITALIC("",'o'),
    RESET_REVERSE("\u001B[27m", 'U'),
    RESET_ALL("\u001B[0m", '~'),
    NONE("", Character.MIN_VALUE);

    private final String colorToken;
    private final char colorCode;
    ConsoleDecorations(String ansiCode, char styleCode) {
        this.colorToken = ansiCode;
        this.colorCode = styleCode;
    }

    @Override
    public String toString() {
        return this.colorToken;
    }

    public char getColorCode() {
        return colorCode;
    }

    public static ConsoleDecorations parseColorFormCode(String name){
        for (ConsoleDecorations instance: values()){
            if (instance.name().equalsIgnoreCase(name)){
                return instance;
            }
        }
        return ConsoleDecorations.NONE; // return the default style
    }
}
