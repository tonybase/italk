package iqq.app.ui.frame;

import com.alee.extended.panel.AlignPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.VerticalPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tree.WebTree;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMCategory;
import iqq.api.bean.IMStatus;
import iqq.app.core.context.IMContext;
import iqq.app.core.module.LogicModule;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.renderer.CategoryComboxCellRenderer;
import iqq.app.ui.renderer.RecentTreeCellRenderer;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.util.UIUtils;
import iqq.app.util.gson.GsonUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 5/18/15.
 */
public class GetFriendRequestFrame extends IMFrame {
    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = headerPanel();
    private WebPanel contentPanel = new WebPanel();
    private WebComboBox comboBox = new WebComboBox();
    private List<IMCategory> categories = new LinkedList<>();
    private WebTree userTree;
    private DefaultTreeModel userModel;
    private IMBuddy buddy = new IMBuddy();
    private String friendRequestId;

    public GetFriendRequestFrame(Map<String, Object> data) {
        System.out.println("数据跟踪");
        System.out.println(GsonUtils.toJson(data));
        buddy.setId(data.get("id").toString());
        buddy.setNick(data.get("nick").toString());
        buddy.setSign(data.get("sign").toString());
        buddy.setStatus((IMStatus.valueOfRaw(Integer.valueOf(data.get("status").toString()))));
        buddy.setAvatar(data.get("avatar").toString());
        buddy.setAvatarBuffered(UIUtils.getDefaultAvatarBuffer());
        friendRequestId = data.get("buddyRequestId").toString();
        String id = IMContext.getBean(LogicModule.class).getOwner().getId();
        broadcastUIEvent(UIEventType.QUERY_CATEGORY_BY_USER_ID, id);

    }

    /**
     * 广播 UIEvent
     *
     * @param type
     * @param target
     */
    protected void broadcastUIEvent(UIEventType type, Object target) {
        eventService.broadcast(new UIEvent(type, target));
    }

    private void initUI() {
        setTitle("好友请求");
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(320, 200));        // 首选大小
        setLocation(UIUtils.getLocationForCenter(this));
        pack();
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        // 背景
        contentPane.setPainter(skinService.getPainterByKey("skin/background"));
        setIconImage(getSkinService().getIconByKey("skin/skinIcon").getImage());
    }

    public void setUser(IMBuddy buddy) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(new BuddyNode(buddy));
        userModel = new DefaultTreeModel(root);
        userTree.setModel(userModel);
    }

    private void initContent() {
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(contentPanel, BorderLayout.CENTER);
        setIMContentPane(contentPane);
        contentPanel.add(chooseCatePanel());
    }

    private WebPanel headerPanel() {
        WebPanel headerPanel = new WebPanel();
        headerPanel.setOpaque(false);
        TitleComponent titleComponent = new TitleComponent(this);
        titleComponent.setShowSettingButton(false);
        titleComponent.setShowMaximizeButton(false);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowMinimizeButton(false);
        headerPanel.add(titleComponent, BorderLayout.NORTH);
        return headerPanel;
    }

    private WebPanel chooseCatePanel() {
        userTree = new WebTree();
        userTree.setCellRenderer(new RecentTreeCellRenderer());
        WebScrollPane treeScroll = new WebScrollPane(userTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        WebPanel chooseCatePanel = new WebPanel();

        chooseCatePanel.setOpaque(true);

        WebButton confirmBtn = new WebButton("接受");
        WebButton cancelBtn = new WebButton("拒绝");
        WebLabel label = new WebLabel("好友分组:");
        label.setPreferredSize(60, 32);


        comboBox.setRenderer(new CategoryComboxCellRenderer());
        for (IMCategory category : categories) {
            comboBox.addItem(category);
        }
        comboBox.setPreferredSize(80, 25);
        confirmBtn.setPreferredSize(60, 30);
        cancelBtn.setPreferredSize(60, 30);
        GroupPanel buttons = new GroupPanel(10, true, confirmBtn, cancelBtn);
        VerticalPanel verticalPanel = new VerticalPanel(label, comboBox);
        verticalPanel.setMargin(new Insets(10, 20, 20, 20));
        buttons.setMargin(10, 5, 0, 6);
        chooseCatePanel.add(verticalPanel, BorderLayout.PAGE_START);
        chooseCatePanel.add(treeScroll, BorderLayout.CENTER);
        chooseCatePanel.add(new AlignPanel(buttons, SwingConstants.RIGHT, SwingConstants.CENTER), BorderLayout.PAGE_END);
        chooseCatePanel.setMargin(6);
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //broadcastUIEvent(UIEventType.PUSH_FRIEND_REQUEST, map);

            }
        });
        return chooseCatePanel;
    }

    @UIEventHandler(UIEventType.QUERY_CATEGORY_BY_USER_ID_CALLBACK)
    public void processQueryCategoryByUserId(UIEvent uiEvent) {
        categories = (List<IMCategory>) uiEvent.getTarget();
        initUI();
        initContent();
    }

    @UIEventHandler(UIEventType.PUSH_FRIEND_REQUEST_RETURN)
    public void processPushFriendRequest(UIEvent uiEvent) {
        dispose();
        contentPane.getParent();
    }

}
