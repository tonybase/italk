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

import iqq.api.bean.*;
import iqq.app.api.IMRequest;
import iqq.app.api.IMResponse;
import iqq.app.api.Conn;
import iqq.app.api.User;
import iqq.app.core.query.AccountQuery;
import iqq.app.core.query.BuddyQuery;
import iqq.app.core.query.GroupQuery;
import iqq.app.core.service.EventService;
import iqq.app.core.service.HttpService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.util.gson.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.net.Socket;
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
public class LogicModule implements AccountQuery, BuddyQuery, GroupQuery {

    private Logger logger = LoggerFactory.getLogger(LogicModule.class);

    @Resource
    private EventService eventService;
    @Resource
    private HttpService httpService;
    private IMAccount account;
    private User user;
    private Socket client;
    private String clientKey;
    private List<IMBuddy> buddies = new LinkedList<>();

    @PostConstruct
    public void init() {

        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    @Override
    public IMAccount getOwner() {
        return account;
    }

    @Override
    public IMBuddy findById(String id) {
        for (IMBuddy buddy : buddies) {
            if (buddy.getId().equals(id)) {
                return buddy;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<IMBuddy> findAll() {
        return buddies;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 登录
     *
     * @param uiEvent
     */
    @UIEventHandler(UIEventType.LOGIN_REQUEST)
    private void onLoginEvent(UIEvent uiEvent) {
        account = (IMAccount) uiEvent.getTarget();
        Map<String, String> map = new HashMap<>();
        map.put("account", account.getAccount());
        map.put("password", account.getPassword());
        map.put("device", "pc");
        httpService.doPost("http://127.0.0.1:8080/login", map, new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                IMResponse IMResponse = GsonUtils.fromJson(content, IMResponse.class);
                user = IMResponse.toBean("user", User.class);

                account.setId(user.getId());
                account.setNick(user.getNick());
                account.setStatus(IMStatus.ONLINE);

                contentToServer();
                logger.debug("loginSuccess content:" + user);
            }

            @Override
            public void onFailure(int statusCode, String content) {

            }
        });
    }

    private void contentToServer() {
        try {
            client = new Socket("127.0.0.1", 9090);
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (client.isConnected()) {
                String line = reader.readLine();
                logger.debug(line);
                onReceived(GsonUtils.fromJson(line, IMResponse.class));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRequest(IMRequest request) {
        String data = GsonUtils.toJson(request);
        logger.debug(data);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
            writer.write(data + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @SuppressWarnings("unchecked")
    private void onReceived(IMResponse response) {
        if (response.getRefer().equals("GET_KEY_RETURN")) {
            Conn conn = response.toBean("conn", Conn.class);
            clientKey = conn.getKey();

            Map<String, String> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("token", user.getToken());
            map.put("key", clientKey);
            IMRequest request = new IMRequest();
            request.setCommand("GET_CONN");
            request.setData("user", map);
            sendRequest(request);
        } else if (response.getRefer().equals("GET_CONN_RETURN")) {

            Map<String, String> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("token", user.getToken());
            IMRequest request = new IMRequest();
            request.setCommand("GET_BUDDY_LIST");
            request.setData("user", map);
            sendRequest(request);
        } else if (response.getRefer().equals("GET_BUDDY_LIST_RETURN")) {

        }

    }

    private void onConnected() {
        eventService.broadcast(new UIEvent(UIEventType.LOGIN_SUCCESS, account));

        doGetBuddyList();
        doGetGroupList();
        doGetRecentList();
    }

    /**
     * 发送消息
     *
     * @param uiEvent
     */
    @UIEventHandler(UIEventType.SEND_MSG_REQUEST)
    private void onSendMsgEvent(UIEvent uiEvent) {

    }

    private void doGetBuddyList() {
        List<IMBuddyCategory> imCategories = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            IMBuddyCategory buddyCategory = new IMBuddyCategory();
            buddyCategory.setName("Category " + i);
            for (int j = 0; j < 10; j++) {
                IMBuddy buddy = new IMBuddy();
                buddy.setId(j + "");
                buddy.setNick("Tony " + j);
                buddy.setSign("Hello World! " + j);
                buddyCategory.getBuddyList().add(buddy);
                buddies.add(buddy);
            }
            imCategories.add(buddyCategory);
        }

        eventService.broadcast(new UIEvent(UIEventType.BUDDY_LIST_UPDATE, imCategories));
    }

    private void doGetGroupList() {
        List<IMRoomCategory> roomCategories = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            IMRoomCategory roomCategory = new IMRoomCategory();
            roomCategory.setName("Category " + i);
            for (int j = 0; j < 10; j++) {
                IMRoom room = new IMRoom();
                room.setId(j + "");
                room.setNick("Hello World! " + j);
                roomCategory.getRoomList().add(room);
            }
            roomCategories.add(roomCategory);
        }

        eventService.broadcast(new UIEvent(UIEventType.GROUP_LIST_UPDATE, roomCategories));
    }

    private void doGetRecentList() {
        List<IMBuddy> buddies = new LinkedList<>();
        for (int j = 0; j < 10; j++) {
            IMBuddy buddy = new IMBuddy();
            buddy.setId(j + "");
            buddy.setNick("Tony " + j);
            buddy.setSign("Hello World! " + j);
            buddies.add(buddy);
        }

        eventService.broadcast(new UIEvent(UIEventType.RECENT_LIST_UPDATE, buddies));
    }
}
