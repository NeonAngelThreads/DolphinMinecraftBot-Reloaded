package org.angellock.impl.managers;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class QuestionManager extends ResourceHelper{
    private Map<String, JsonElement> questionMap;

    public QuestionManager(@Nullable String defaultPath, String fileType) {
        super(defaultPath, fileType);
    }

    public QuestionManager(String filename){
        super(null, filename);
    }

    @Override
    public String getFileName() {
        return "server.queue.questions";
    }

    public String fetchStringAnswer(String question){
        return this.questionMap.get(question).getAsString();
    }

    public QuestionManager load(){
        if (this.questionMap == null) {
            this.questionMap = readJSONContent();
        }
        return this;
    }
}
