package iqq.app.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iqq.app.util.gson.GsonUtils;

/**
 * Created by Tony on 4/22/15.
 */
public class IMResponse {
    private int status;
    private String msg;
    private String data;
    private String refer;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public <V> V toBean(Class<V> type) {
        return GsonUtils.fromJson(data, type);
    }

    public <V> V toBean(String name, Class<V> type) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
        return GsonUtils.fromJson(jsonObject.get(name).getAsString(), type);
    }

    @Override
    public String toString() {
        return "ApiData{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                ", refer='" + refer + '\'' +
                '}';
    }
}
