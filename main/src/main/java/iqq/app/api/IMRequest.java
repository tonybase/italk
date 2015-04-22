package iqq.app.api;

import iqq.app.util.gson.GsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony on 4/22/15.
 */
public class IMRequest {
    private String command;
    private Object data;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setData(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        data = map;
    }
}
