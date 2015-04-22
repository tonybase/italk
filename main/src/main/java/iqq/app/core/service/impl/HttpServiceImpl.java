package iqq.app.core.service.impl;

import iqq.app.core.service.HttpService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 4/21/15.
 */
@Service
public class HttpServiceImpl implements HttpService {
    private CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();

    public HttpServiceImpl() {
        httpclient.start();
    }

    @Override
    public void doGet(String url, StringCallback callback) {
        HttpGet request = new HttpGet(url);
        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String content = "";
                try {
                    content = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(content);
                } else {
                    callback.onFailure(statusCode, content);
                }
            }

            public void failed(final Exception ex) {
                callback.onFailure(0, "");
            }

            public void cancelled() {

            }
        });
    }

    @Override
    public void doPost(String url, Map<String, String> params, StringCallback callback) {
        HttpPost request = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<>();
        for (String key : params.keySet()) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String content = "";
                try {
                    content = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(content);
                } else {
                    callback.onFailure(statusCode, content);
                }
            }

            public void failed(final Exception ex) {
                callback.onFailure(0, "");
            }

            public void cancelled() {

            }
        });
    }
}
