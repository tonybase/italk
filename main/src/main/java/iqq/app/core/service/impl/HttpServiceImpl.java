package iqq.app.core.service.impl;

import iqq.app.core.service.HttpService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tony on 4/21/15.
 */
@Service
public class HttpServiceImpl implements HttpService {
    private Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

    private ExecutorService poolExecutor = Executors.newCachedThreadPool();
    private HttpClient client = new DefaultHttpClient();

    @Override
    public void doGet(String url, StringCallback callback) {
        logger.debug("doGet " + url);
        poolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String content = EntityUtils.toString(response.getEntity());
                        callback.onSuccess(content);
                    } else {
                        callback.onFailure(response.getStatusLine().getStatusCode(), "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void doPost(String url, Map<String, String> params, StringCallback callback) {
        logger.debug("doPost " + url + " " + params);

        poolExecutor.submit(new Runnable() {
            @Override
            public void run() {
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
                try {
                    HttpResponse response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String content = EntityUtils.toString(response.getEntity());
                        callback.onSuccess(content);
                    } else {
                        callback.onFailure(response.getStatusLine().getStatusCode(), "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
