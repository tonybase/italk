package iqq.app.ui.manager;

import com.alee.utils.SystemUtils;
import iqq.api.bean.IMEntity;
import iqq.app.core.service.EventService;
import iqq.app.core.service.ResourceService;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.TimerService;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.frame.MainFrame;
import iqq.app.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-12
 * License  : Apache License 2.0
 */
@Component
public class MainManager {
    private final Logger logger = LoggerFactory.getLogger(MainManager.class);
    private SystemTray tray;
    private TrayIcon icon;
    private PopupMenu menu;
    private MainFrame mainFrame;

    private Runnable flashTimer;
    private Image flashImage;    //当前闪动的头像
    private Image defaultImage;    //默认头像
    private Image blankImage;    //空白的头像
    private IMEntity flashOwner;    //当前闪烁的用户
    private Deque<IMEntity> flashQueue;    //带闪烁的对象列表

    @Resource
    private ChatManager chatManager;
    @Resource
    private EventService eventService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private SkinService skinService;
    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init() {
        flashQueue = new LinkedList<>();
        flashTimer = new FlashTrayTimer();
        timerService.setInterval(flashTimer, 500);

        UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);
        eventService.register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    @UIEventHandler(UIEventType.FLASH_USER_START)
    protected void processIMFlashUserStart(UIEvent event) {
        if (flashQueue == null) return;
        if (flashQueue.contains(event.getTarget())) {
            flashQueue.remove(event.getTarget());
        }
        if (flashOwner != null && flashOwner != event.getTarget()) {
            flashQueue.addFirst(flashOwner);
        }
        flashOwner = (IMEntity) event.getTarget();
        flashImage = getTrayFace(flashOwner);
        flashTimer.run();
    }

    @UIEventHandler(UIEventType.FLASH_USER_STOP)
    protected void processIMFlashUserStop(UIEvent event) {
        if (flashQueue == null) return;
        if (flashQueue.isEmpty()) {
            flashOwner = null;
            flashImage = null;
            icon.setImage(defaultImage);
        } else if (flashOwner != event.getTarget()) {
            flashQueue.remove(flashOwner);
        } else {
            flashOwner = flashQueue.poll();
            flashImage = getTrayFace(flashOwner);
            flashTimer.run();
        }
    }

    public void show() {
        if (mainFrame == null) {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            enableTray();
        }
        if (!mainFrame.isVisible()) {
            mainFrame.setVisible(true);// 设置为可见
            // 设置窗口状态(在最小化状态弹出显示)
            mainFrame.setExtendedState(Frame.NORMAL);
        }
    }

    public void hide() {
        if (mainFrame.isVisible()) {
            mainFrame.setVisible(false);
        }
    }

    public void enableTray() {
        if (SystemTray.isSupported() && tray == null) {
            menu = new PopupMenu();
            MenuItem restore = new MenuItem("  显示主窗口  ");
            restore.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    show();
                }
            });
            MenuItem exit = new MenuItem("  退出程序  ");
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            menu.add(restore);
            menu.addSeparator();
            menu.add(exit);

            if (SystemUtils.isMac()) {
                defaultImage = skinService.getIconByKey("window/titleWIconBlack").getImage();
            } else {
                defaultImage = mainFrame.getIconImage();
            }
            blankImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            tray = SystemTray.getSystemTray();
            icon = new TrayIcon(defaultImage, "IQQ");
            icon.setImageAutoSize(true);
            if (!SystemUtils.isMac()) {
                icon.setPopupMenu(menu);
            }
            try {
                tray.add(icon);
                icon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        logger.debug("MouseEvent " + e.getButton() + " " + e.getClickCount());
                        //弹出左键菜单
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // 存在未读消息，点击显示
                            if (flashOwner != null) {
                                chatManager.addChat(flashOwner);
                            } else {
                                show();
                            }
                        }
                    }
                });
            } catch (AWTException e) {
                logger.error("SystemTray add icon.", e);
            }
        }

    }

    private Image getTrayFace(IMEntity owner) {
        BufferedImage avatar = null;
        if (owner.getAvatar() != null) {
            avatar = owner.getAvatarBuffered();
        } else {
            avatar = UIUtils.getDefaultAvatarBuffer();
        }
        return avatar.getScaledInstance(32, 32, 100);
    }

    private class FlashTrayTimer implements Runnable {
        @Override
        public void run() {
            if (flashOwner != null
                    && tray != null
                    && icon != null
                    && flashImage != null) {
                Image curImg = icon.getImage();
                icon.setImage(curImg == flashImage ? blankImage : flashImage);
            }
        }
    }
}
