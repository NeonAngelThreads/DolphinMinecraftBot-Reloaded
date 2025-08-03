package org.angellock.impl.managers;

import org.angellock.impl.Start;
import org.angellock.impl.managers.utils.AbstractJsonAccessor;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class RobotConfig extends AbstractJsonAccessor {
    private static final Logger log = LoggerFactory.getLogger("RobotConfig");
    private final String fileType;
    public RobotConfig(@Nullable String defaultPath, String fileType) {
        this.fileType = fileType;

        File outFile = new File((defaultPath != null) ? defaultPath : "");
        if (!outFile.exists()){
            log.warn(ConsoleTokens.standardizeText(ConsoleTokens.LIGHT_PURPLE + "Specified config file "+ConsoleTokens.DARK_PURPLE + defaultPath + ConsoleTokens.LIGHT_PURPLE +" not found, reading from the default file."));
            outFile = new File(this.getBaseConfigRoot());
        }
        if (outFile.isDirectory()) {
            outFile = new File(defaultPath, getFullFileName());
        }
        try {
            this.autoCopy(outFile);
        } catch (IOException e) {
            log.info(ConsoleTokens.standardizeText(ConsoleTokens.GRAY+e.toString()));
        }
        this.configPath = outFile.toPath();
    }

    private void autoCopy(File outFile) throws IOException {
        if (!outFile.exists()) {
            InputStream in = Start.class.getClassLoader().getResourceAsStream(getFullFileName());
            if (in != null) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[512];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
            else {
                log.error(ConsoleTokens.standardizeText(ConsoleTokens.LIGHT_PURPLE + "Could not extract the fallback config file "+ConsoleTokens.DARK_PURPLE+getFullFileName()));
            }
        }
//        this.configPath = Path.of(output, getFullFileName());
    }

    public String getFullFileName(){
        return getFileName() + this.fileType;
    }

    @Override
    public String getFileName() {
        return "mc.bot.config";
    }
}
