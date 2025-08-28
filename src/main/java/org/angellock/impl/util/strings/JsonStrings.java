package org.angellock.impl.util.strings;

import com.google.gson.JsonElement;

import java.util.List;

public class JsonStrings {
    public static String toListString(List<JsonElement> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (JsonElement element : list) {
            sb.append(element.getAsString());
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }
}
