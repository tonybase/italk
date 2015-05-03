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
import iqq.api.bean.*;
import iqq.api.bean.content.IMTextItem;
import iqq.app.api.IMRequest;
import iqq.app.api.IMResponse;
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
import java.util.*;

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
    private Socket client;
    private String clientKey;
    private String clientToken;
    private Map<String, String> ticketMap = new HashMap<>();
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
                IMResponse response = GsonUtils.fromJson(content, IMResponse.class);
                JsonObject jsonObject = response.getData().get("user").getAsJsonObject();

                clientToken = jsonObject.get("token").getAsString();
                account.setId(jsonObject.get("id").getAsString());
                account.setNick(jsonObject.get("nick").getAsString());
                account.setStatus(IMStatus.ONLINE);

                contentToServer();
                logger.debug("loginSuccess content:" + jsonObject);
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
                try {
                    onReceived(GsonUtils.fromJson(line, IMResponse.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    JsonObject object = new JsonParser().parse(line).getAsJsonObject();
                    IMResponse response = new IMResponse();
                    response.setStatus(object.get("status").getAsInt());
                    response.setMsg(object.get("msg").getAsString());
                    response.setRefer(object.get("refer").getAsString());
                    onReceived(response);
                    break;
                }
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
        if (response == null) return;
        if (response.getRefer().equals("GET_KEY_RETURN")) {
            JsonObject jsonObject = response.getData().get("conn").getAsJsonObject();
            clientKey = jsonObject.get("key").getAsString();

            Map<String, String> map = new HashMap<>();
            map.put("id", account.getId());
            map.put("token", clientToken);
            map.put("key", clientKey);
            IMRequest request = new IMRequest();
            request.setCommand("GET_CONN");
            request.setData("user", map);
            sendRequest(request);
        } else if (response.getRefer().equals("GET_CONN_RETURN")) {
            Map<String, String> map = new HashMap<>();
            map.put("id", account.getId());
            map.put("token", clientToken);
            IMRequest request = new IMRequest();
            request.setCommand("GET_BUDDY_LIST");
            request.setData("user", map);
            sendRequest(request);
        } else if (response.getRefer().equals("GET_BUDDY_LIST_RETURN")) {
            eventService.broadcast(new UIEvent(UIEventType.LOGIN_SUCCESS, account));
            onBuddyListRecv(response.getData().get("categories").getAsJsonArray());
        } else if (response.getRefer().equals("CREATE_SESSION_RETURN")) {
            JsonObject jsonObject = response.getData().get("session").getAsJsonObject();
            String receiver = jsonObject.get("receiver").getAsString();
            String ticket = jsonObject.get("ticket").getAsString();
            ticketMap.put(receiver, ticket);
        } else if (response.getRefer().equals("PUSH_MSG")) {
            JsonObject jsonObject = response.getData().get("message").getAsJsonObject();

            IMMsg msg = new IMMsg();
            msg.setSender(findById(jsonObject.get("sender").getAsString()));
            msg.setContents(new LinkedList<>());
            msg.getContents().add(new IMTextItem(jsonObject.get("content").getAsString()));
            msg.setDate(new Date());
            msg.setState(IMMsg.State.UNREAD);
            msg.setDirection(IMMsg.Direction.RECV);
            msg.setOwner(account);
            eventService.broadcast(new UIEvent(UIEventType.RECV_RAW_MSG, msg));
        }
    }

    @UIEventHandler(UIEventType.SHOW_CHAT)
    private void onCreateChatEvent(UIEvent uiEvent) {
        IMEntity entity = (IMEntity) uiEvent.getTarget();
        String to = entity.getId();
        String ticket = ticketMap.get(to);
        if (ticket == null) {
            JsonObject msgJson = new JsonObject();
            msgJson.addProperty("sender", account.getId());
            msgJson.addProperty("receiver", to);
            msgJson.addProperty("token", clientToken);

            IMRequest request = new IMRequest();
            request.setCommand("CREATE_SESSION");
            request.setData("session", msgJson);
            sendRequest(request);
        }
    }

    /**
     * 发送消息
     * {"command":"CREATE_SESSION","data":{"session":{"sender":"1","receiver":"2","token":"11"}}}
     * {"command":"SEND_MSG","data":{"message":{"content":"xxx","ticket":"a865b423-a148-4464-b427-35f22f3b6811","token":"11"}}}
     *
     * @param uiEvent
     */
    @UIEventHandler(UIEventType.SEND_MSG_REQUEST)
    private void onSendMsgEvent(UIEvent uiEvent) {
        IMMsg msg = (IMMsg) uiEvent.getTarget();

        String to = msg.getOwner().getId();
        String ticket = ticketMap.get(to);
        JsonObject msgJson = new JsonObject();
        msgJson.addProperty("content", msg.getContents().toString());
        msgJson.addProperty("ticket", ticket);
        msgJson.addProperty("token", clientToken);
        IMRequest request = new IMRequest();
        request.setCommand("SEND_MSG");
        request.setData("message", msgJson);
        sendRequest(request);

        logger.debug(msgJson + "");
        logger.debug("to:" + to + " content:" + msg.getContents().toString() + " ticket:" + ticket);
    }

    private void onBuddyListRecv(JsonArray categories) {
        List<IMBuddyCategory> imCategories = new LinkedList<>();
        for (int i = 0; i < categories.size(); i++) {
            JsonObject cate = categories.get(i).getAsJsonObject();
            IMBuddyCategory buddyCategory = new IMBuddyCategory();
            buddyCategory.setName(cate.get("name").getAsString());
            JsonArray buddiesJson = cate.get("buddies").getAsJsonArray();
            for (int j = 0; j < buddiesJson.size(); j++) {
                JsonObject buddyJson = buddiesJson.get(i).getAsJsonObject();

                IMBuddy buddy = new IMBuddy();
                buddy.setId(buddyJson.get("id").getAsString());
                buddy.setNick(buddyJson.get("nick").getAsString());
                buddy.setSign(buddyJson.get("sign").getAsString());
                buddyCategory.getBuddyList().add(buddy);

                buddies.add(buddy);
            }
            imCategories.add(buddyCategory);
        }

        eventService.broadcast(new UIEvent(UIEventType.BUDDY_LIST_UPDATE, imCategories));

        doGetGroupList();
        doGetRecentList();
    }

    private void doGetGroupList() {
        List<IMRoomCategory> roomCategories = new LinkedList<>();
//        for (int i = 0; i < 8; i++) {
//            IMRoomCategory roomCategory = new IMRoomCategory();
//            roomCategory.setName("Category " + i);
//            for (int j = 0; j < 10; j++) {
//                IMRoom room = new IMRoom();
//                room.setId(j + "");
//                room.setNick("Hello World! " + j);
//                roomCategory.getRoomList().add(room);
//            }
//            roomCategories.add(roomCategory);
//        }

        eventService.broadcast(new UIEvent(UIEventType.GROUP_LIST_UPDATE, roomCategories));
    }

    private void doGetRecentList() {
        List<IMBuddy> buddies = new LinkedList<>();
//        for (int j = 0; j < 10; j++) {
//            IMBuddy buddy = new IMBuddy();
//            buddy.setId(j + "");
//            buddy.setNick("Tony " + j);
//            buddy.setSign("Hello World! " + j);
//            buddies.add(buddy);
//        }

        eventService.broadcast(new UIEvent(UIEventType.RECENT_LIST_UPDATE, buddies));
    }
}
