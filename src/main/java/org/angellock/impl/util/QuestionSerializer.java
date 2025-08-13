package org.angellock.impl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionSerializer {
    private String question;
    private Map<String, String> answers = new HashMap<>();
    private boolean validQuestion;
    private String stringQuestion;
    public QuestionSerializer(String stringQuestion) {
        this.stringQuestion = stringQuestion;
    }

    public void build(){
        String[] strings = stringQuestion.split("丨");
        this.validQuestion = (strings.length == 2);

        if (this.validQuestion) {
            this.question = strings[0];
            this.serializeAnswer(strings[1]);
        }
    }

    public boolean isValid(){
        return this.validQuestion;
    }

    private void serializeAnswer(String stringAnswer){
        Pattern answerPattern = Pattern.compile("([A-Z])\\.([\\u4e00-\\u9fa5\\w.]+)"); // [\u4e00-\u9fa5] Chinese character scope
        Matcher answerMatcher = answerPattern.matcher(stringAnswer);

        while (answerMatcher.find()){
            String key = answerMatcher.group(1);
            String value = answerMatcher.group(2);
            this.answers.put(value, key);
        }
    }

    public String getQuestion(){
        return this.question;
    }

    public String getAnswer(){
        for (String key: this.answers.keySet()){
            if (key.contains("54")){
                return this.answers.get(key);
            }
        }
        return "";
    }
}
