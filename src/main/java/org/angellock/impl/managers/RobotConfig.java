package org.angellock.impl.managers;

import org.jetbrains.annotations.Nullable;

public class RobotConfig extends ResourceHelper{
    public RobotConfig(@Nullable String defaultPath, String fileType) {
        super(defaultPath, fileType);
    }

    @Override
    public String getFileName() {
        return "mc.bot.config";
    }
}
