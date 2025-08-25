package org.angellock.impl.win32terminal;

import org.jline.jansi.Ansi;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Win32ColorSerializer implements Serializable {
    private static final Pattern foreground_pattern = Pattern.compile("[&§]([0-9a-flonNRU])");
    private static final Ansi RESET_ALL = Ansi.ansi().reset();
    private static final Ansi[] colorList = new Ansi[]{Ansi.ansi().fgBlack(), Ansi.ansi().fgBlue(), Ansi.ansi().fgGreen(), Ansi.ansi().fgCyan(), Ansi.ansi().fgRed(), Ansi.ansi().fgMagenta(), Ansi.ansi().fgYellow(), Ansi.ansi().fg(Ansi.Color.WHITE), Ansi.ansi().fgBrightBlack(), Ansi.ansi().fgBrightBlue()};
    public static String serialize(String text){

        Matcher matcher = foreground_pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            char code = matcher.group(1).charAt(0);
            Ansi AnsiResult = serializeWindowsColor(code);
            matcher.appendReplacement(result, AnsiResult.toString());
        }
        matcher.appendTail(result);
        return result.toString() + RESET_ALL;
    }
    public static Ansi serializeWindowsColor(char code) {
        if (Character.isDigit(code)){
            return colorList[Character.digit(code, 10)];
        }
        Ansi text = Ansi.ansi();
        return switch (code) {
            case 'l' -> text.bold().a(text);
            case 'o' -> text.a(Ansi.Attribute.ITALIC).a(text);
            case 'f' -> text.fgBright(Ansi.Color.WHITE).a(text);
            case 'a' -> text.fgBrightGreen().a(text);
            case 'b' -> text.fgBrightCyan().a(text);
            case 'c' -> text.fgBrightRed().a(text);
            case 'd' -> text.fgBrightMagenta().a(text);
            case 'e' -> text.fgBrightYellow().a(text);
            case 'r' -> text.fgDefault().a(text);
            default -> text;
        };
    }
}
