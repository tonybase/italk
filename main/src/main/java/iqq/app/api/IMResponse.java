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
    private JsonObject data;
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

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public static IMResponse parseJson(String json) {
        try {
            return GsonUtils.fromJson(json, IMResponse.class);
        } catch (Exception e) {
            JsonObject object = new JsonParser().parse(json).getAsJsonObject();
            IMResponse response = new IMResponse();
            response.setStatus(object.get("status").getAsInt());
            response.setMsg(object.get("msg").getAsString());
            response.setRefer(object.get("refer").getAsString());
            return response;
        }
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
