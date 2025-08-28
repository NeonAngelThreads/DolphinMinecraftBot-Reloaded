package org.angellock.impl.win32terminal;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.angellock.impl.util.ConsoleTokens;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AnsiEscapes {
    private static final int TERMINAL_PROCESSING = 0x0004;
    private static Terminal winTerminal;
    private static LineReader reader;

    private interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        Pointer GetStdHandle(int nStdHandle);

        int GetConsoleMode(Pointer hConsoleInput, int[] lpMode);

        boolean SetConsoleMode(Pointer hConsoleOutput, int dwMode);
    }

    public static void enableAnsiSupport() {
        Pointer stdHandle = Kernel32.INSTANCE.GetStdHandle(-11);
        int[] consoleMode = new int[1];
        if (Kernel32.INSTANCE.GetConsoleMode(stdHandle, consoleMode) > 0) {
            int newMode = consoleMode[0] | TERMINAL_PROCESSING;
            Kernel32.INSTANCE.SetConsoleMode(stdHandle, newMode);
        } else {
            System.out.println("Failed to set console mode.");
        }
        try {
            winTerminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .build();

            reader = LineReaderBuilder.builder()
                    .terminal(AnsiEscapes.getTerminal())
                    .parser(new DefaultParser())
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M> ")
                    .build();

        } catch (IOException e) {
            System.out.println("Could not enable ansi escapes: " + e.getMessage());
        }
    }

    public static void printArt(String ARCHIVE_VERSION) {
        System.out.print(ConsoleTokens.colorizeText("\n\n" + "&l" +
                "&b /\\/|_____      _       _     _      ______       _    " + "&1 __  &9__&b__  \n" +
                "&b|/\\/|  _  \\    | |     | |   (_)     | ___ \\     | |   " + "&5  -- &9\\ \\ &b\\ \n" +
                "&b    | | | |___ | |_ __ | |__  _ _ __ | |_/ / ___ | |_  " + "&5   -- &9\\ \\ &b\\\n" +
                "&b    | | | / _ \\| | '_ \\| '_ \\| | '_ \\| ___ \\/ _ \\| __| " + "&d   -- &9/ / &b/\n" +
                "&b    | |/ / (_) | | |_) | | | | | | | | |_/ / (_) | |_  " + "&5  -- &9/ / &b/ \n" +
                "&b    |___/ \\___/|_| .__/|_| |_|_|_| |_\\____/ \\___/ \\__| " + "&1 -- &9/_/&b_/ \n" +
                "&b                 | |                                   \n" + "&1--" +
                "&b                 |_|                                 " + "&5VERSION  &d" + ARCHIVE_VERSION + "\n\n\n")
        );
    }

    public static Terminal getTerminal() {
        return winTerminal;
    }

    public static LineReader getReader() {
        return reader;
    }
}
