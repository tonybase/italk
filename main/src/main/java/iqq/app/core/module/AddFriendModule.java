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
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import iqq.api.bean.*;
import iqq.api.bean.content.IMTextItem;
import iqq.app.api.IMRequest;
import iqq.app.api.IMResponse;
import iqq.app.core.query.AccountQuery;
import iqq.app.core.query.BuddyQuery;
import iqq.app.core.query.GroupQuery;
import iqq.app.core.service.EventService;
import iqq.app.core.service.HttpService;
import iqq.app.core.service.TaskService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.util.UIUtils;
import iqq.app.util.gson.GsonUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

//import iqq.im.QQException;

/**
 * QQ主模块，负责底层和QQ核心通信，如QQ登陆，发送消息，接受消息等
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
@Service
public class AddFriendModule {

    private Logger logger = LoggerFactory.getLogger(AddFriendModule.class);

    @Resource
    private EventService eventService;
    @Resource
    private HttpService httpService;
    @Resource
    private TaskService taskService;

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
    @UIEventHandler(UIEventType.QUERY_FRIEND_BY_NICK)
    private void queryFriendEvent(UIEvent uiEvent) {
        String nick = (String) uiEvent.getTarget();
        logger.info("nick: " + nick);
        Map<String, String> map = new HashMap<>();
        map.put("nick", nick == null ? "" : nick);
        httpService.doPost("http://127.0.0.1:8080/query", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                logger.info(content);
                IMResponse response = GsonUtils.fromJson(content, IMResponse.class);
                JsonArray jsonArray = response.getData().get("users").getAsJsonArray();
                List<IMBuddy> buddies = new LinkedList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    IMBuddy buddy = new IMBuddy();
                    buddy.setId(jsonObject.get("id").getAsString());
                    buddy.setNick(jsonObject.get("nick").getAsString());
                    buddy.setSign(jsonObject.get("sign").getAsString());
                    buddy.setAvatar(jsonObject.get("avatar").getAsString());
                    buddy.setStatus((IMStatus.valueOfRaw(jsonObject.get("status").getAsInt())));
                    buddy.setAvatarBuffered(UIUtils.getDefaultAvatarBuffer());
                    buddies.add(buddy);

                    taskService.submit(new Runnable() {
                        @Override
                        public void run() {
                            buddy.setAvatarBuffered(UIUtils.getBufferedImage(jsonObject.get("avatar").getAsString()));
                            eventService.broadcast(new UIEvent(UIEventType.USER_FACE_UPDATE, buddy));
                        }
                    });
                }

                eventService.broadcast(new UIEvent(UIEventType.QUERY_FRIEND_BY_NICK_CALLBACK, buddies));
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }

    @UIEventHandler(UIEventType.PUSH_FRIEND_REQUEST)
    private void pushFriendRequest(UIEvent uiEvent) {
        Map<String, String> map = (Map) uiEvent.getTarget();
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

    @UIEventHandler(UIEventType.ADD_FRIEND_REQUEST)
    private void pushFriendRequestAdd(UIEvent uiEvent) {
        IMUser user = (IMUser) uiEvent.getTarget();
        String cateId = uiEvent.getData("category_id");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", user.getId());
        map.put("category_id", cateId);
        httpService.doPost("http://127.0.0.1:8080/users/relation/add", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                System.out.println(content);
                eventService.broadcast(new UIEvent(UIEventType.ADD_FRIEND_SUCCESS, ""));
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }

    @UIEventHandler(UIEventType.REFUSE_FRIEND_REQUEST)
    private void pushFriendRequestRefuse(UIEvent uiEvent) {
        IMUser user = (IMUser) uiEvent.getTarget();
        String cateId = uiEvent.getData("category_id");

        Map<String, String> map = new HashMap<>();
        map.put("user_id", user.getId());
        map.put("category_id", cateId);
        httpService.doPost("http://127.0.0.1:8080/users/relation/del", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                System.out.println(content);
                eventService.broadcast(new UIEvent(UIEventType.DELETE_FRIEND_SUCCESS, ""));
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }
}
