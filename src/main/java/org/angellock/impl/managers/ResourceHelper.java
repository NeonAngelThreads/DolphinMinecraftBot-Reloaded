package org.angellock.impl.managers;

import org.angellock.impl.Start;
import org.angellock.impl.managers.utils.AbstractJsonAccessor;
import org.angellock.impl.util.ConsoleTokens;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class ResourceHelper extends AbstractJsonAccessor {
    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class.getCanonicalName());
    private final String fileType;
    public ResourceHelper(@Nullable String defaultPath, String fileType) {
        this.fileType = fileType;

        File outFile = new File((defaultPath != null) ? defaultPath : "");
        if (!outFile.exists()){
            log.warn(ConsoleTokens.colorizeText("&eSpecified config file &5" + defaultPath + "&e not found, &6reading from the default file: &3" + getFullFileName()));
            outFile = new File(this.getBaseConfigRoot());
        }
        if (outFile.isDirectory()) {
            outFile = new File(defaultPath, getFullFileName());
        }
        try {
            this.autoCopy(outFile);
        } catch (IOException e) {
            log.info(ConsoleTokens.colorizeText("&8" + e));
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
                log.error(ConsoleTokens.colorizeText("&dCould not extract the fallback config file &3" + getFullFileName()));
            }
        }
//        this.configPath = Path.of(output, getFullFileName());
    }

    public String getFullFileName(){
        return getFileName() + this.fileType;
    }

}
