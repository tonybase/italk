package iqq.app.core.module;

import iqq.api.bean.IMMsg;
import iqq.app.core.query.MsgQuery;
import iqq.app.core.service.EventService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 消息处理模块
 * <p/>
 * Created by Tony on 5/3/15.
 */
@Service
public class MsgModule implements MsgQuery {
    @Resource
    private EventService eventService;
    private Map<String, List<IMMsg>> msgsMap = new HashMap<>();

    @PostConstruct
    public void init() {

        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    @UIEventHandler(UIEventType.RECV_RAW_MSG)
    private void onMsgRecvEvent(UIEvent uiEvent) {
        IMMsg msg = (IMMsg) uiEvent.getTarget();
        String id = msg.getSender().getId();
        if (!msgsMap.containsKey(id)) {
            msgsMap.put(id, new LinkedList<>());
        }
        msgsMap.get(id).add(msg);
    }

    @UIEventHandler(UIEventType.SEND_MSG_REQUEST)
    private void onSendMsgEvent(UIEvent uiEvent) {
        IMMsg msg = (IMMsg) uiEvent.getTarget();
        String id = msg.getOwner().getId();
        if (!msgsMap.containsKey(id)) {
            msgsMap.put(id, new LinkedList<>());
        }
        msgsMap.get(id).add(msg);
    }

    @Override
    public List<IMMsg> getMsgs(String uid) {
        if (!msgsMap.containsKey(uid)) {
            msgsMap.put(uid, new LinkedList<>());
        }
        return msgsMap.get(uid);
    }
}
