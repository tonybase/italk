package iqq.app.core.service;

import java.util.Map;

/**
 * Created by Tony on 4/21/15.
 */
public interface HttpService {

    void doGet(String url, StringCallback callback);

    void doPost(String url, Map<String, String> params, StringCallback callback);

    interface StringCallback {
        void onSuccess(String content);

        void onFailure(int statusCode, String content);
    }
}
