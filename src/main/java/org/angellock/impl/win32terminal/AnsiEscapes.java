package org.angellock.impl.win32terminal;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AnsiEscapes {
    private static final int TERMINAL_PROCESSING = 0x0004;

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
            Terminal winTerminal = TerminalBuilder.builder()
                    .system(true)
                    .encoding(StandardCharsets.UTF_8)
                    .build();

        } catch (IOException e) {
            System.out.println("Could not enable ansi escapes: " + e.getMessage());
        }
    }
}
