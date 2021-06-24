package net.foxtam.warpathlorry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class JsonMap {
    public static final java.lang.reflect.Type type =
            new TypeToken<Map<String, String>>() {
            }.getType();

    private final Map<String, String> map;

    public JsonMap(String jsonString) {
        this.map = new Gson().fromJson(jsonString, type);
    }

    public String get(String key) {
        return map.get(key);
    }
}
