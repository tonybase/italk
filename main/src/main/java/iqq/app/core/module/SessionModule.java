package iqq.app.core.module;

import iqq.api.bean.IMEntity;
import iqq.api.bean.IMMsg;
import iqq.api.bean.IMUser;
import iqq.app.core.service.EventService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * 会话模块
 * <p/>
 * Created by Tony on 5/3/15.
 */
@Service
public class SessionModule {

    @Resource
    private EventService eventService;
    private Set<IMEntity> entities = new HashSet<>();

    @PostConstruct
    public void init() {

        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    @UIEventHandler(UIEventType.SHOW_CHAT)
    private void onShowChatEvent(UIEvent uiEvent) {
        IMEntity entity = (IMEntity) uiEvent.getTarget();
        entities.add(entity);

        eventService.broadcast(new UIEvent(UIEventType.FLASH_USER_STOP, entity));
    }

    @UIEventHandler(UIEventType.CLOSE_CHAT)
    private void onCloseChatEvent(UIEvent uiEvent) {
        IMEntity entity = (IMEntity) uiEvent.getTarget();
        entities.remove(entity);
    }

    @UIEventHandler(UIEventType.RECV_RAW_MSG)
    private void onMsgRecvEvent(UIEvent uiEvent) {
        IMMsg msg = (IMMsg) uiEvent.getTarget();

        IMEntity sender = msg.getSender();
        if (!entities.contains(sender)) {
            eventService.broadcast(new UIEvent(UIEventType.FLASH_USER_START, sender));
        }
    }

}
