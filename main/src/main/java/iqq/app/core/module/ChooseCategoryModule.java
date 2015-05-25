package iqq.app.core.module;
 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMCategory;
import iqq.app.api.IMResponse;
import iqq.app.core.service.EventService;
import iqq.app.core.service.HttpService;
import iqq.app.core.service.TaskService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.util.UIUtils;
import iqq.app.util.gson.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import iqq.im.QQException;

/**
 * QQ主模块，负责底层和QQ核心通信，如QQ登陆，发送消息，接受消息等
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
@Service
public class ChooseCategoryModule {

    private Logger logger = LoggerFactory.getLogger(ChooseCategoryModule.class);

    @Resource
    private EventService eventService;
    @Resource
    private HttpService httpService;

    @PostConstruct
    public void init() {
        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    /**
     * 根据昵称查找好友
     *
     * @param uiEvent
     */
    @UIEventHandler(UIEventType.QUERY_CATEGORY_BY_USER_ID)
    private void queryCategoryByUserId(UIEvent uiEvent) {
        String id = (String) uiEvent.getTarget();

        Map<String, String> map = new HashMap<>();
        map.put("id", id == null ? "" : id);
        httpService.doPost("http://127.0.0.1:8080/users/category/query", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                logger.info(content);
                IMResponse response = GsonUtils.fromJson(content, IMResponse.class);
                JsonArray jsonArray = response.getData().get("categories").getAsJsonArray();
                List<IMCategory> categories = new LinkedList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    IMCategory category = new IMCategory();
                    category.setId(jsonObject.get("id").getAsString());
                    category.setName(jsonObject.get("name").getAsString());
                    categories.add(category);

                }
                eventService.broadcast(new UIEvent(UIEventType.QUERY_CATEGORY_BY_USER_ID_CALLBACK, categories));
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }
    @UIEventHandler(UIEventType.PUSH_FRIEND_REQUEST)
    private void pushFriendRequest(UIEvent uiEvent) {
        Map<String, String> map= (Map) uiEvent.getTarget();
        httpService.doPost("http://127.0.0.1:8080/users/relation/push", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                System.out.println(content);
                eventService.broadcast(new UIEvent(UIEventType.PUSH_FRIEND_REQUEST_RETURN, ""));
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }
}
