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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iqq.api.bean.IMAccount;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMCategory;
import iqq.api.bean.IMStatus;
import iqq.app.api.IMResponse;
import iqq.app.core.context.IMContext;
import iqq.app.core.module.LogicModule;
import iqq.app.core.service.HttpService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 5/18/15.
 */
public class GetFriendRequestFrame extends IMFrame {
    private Logger logger = LoggerFactory.getLogger(GetFriendRequestFrame.class);

    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = headerPanel();
    private WebPanel contentPanel = new WebPanel();
    private WebComboBox comboBox = new WebComboBox();
    private List<IMCategory> categories = new LinkedList<>();
    private WebTree userTree;
    private DefaultTreeModel userModel;
    private IMBuddy buddy;
    private String friendRequestId;

    public GetFriendRequestFrame(IMBuddy buddy, String buddyRequestId) {
        this.buddy = buddy;
        this.friendRequestId = buddyRequestId;
        logger.info("friendRequestId=" + friendRequestId);
        logger.info(buddy.toString());
        initUI();
        initContent();
        loadData();
        loadData();
    }

    private void loadData() {
        IMAccount account = IMContext.getBean(LogicModule.class).getOwner();
        getHttpService().doGet("http://127.0.0.1:8080/users/category/query?id=" + account.getId(), new HttpService.StringCallback() {
            @Override
            public void onSuccess(String content) {
                logger.info(content);
                IMResponse response = GsonUtils.fromJson(content, IMResponse.class);
                JsonArray jsonArray = response.getData().get("categories").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    IMCategory category = new IMCategory();
                    category.setId(jsonObject.get("id").getAsString());
                    category.setName(jsonObject.get("name").getAsString());
                    categories.add(category);
                }
                contentPanel.add(chooseCatePanel());
                contentPanel.revalidate();
            }

            @Override
            public void onFailure(int statusCode, String content) {
                logger.error("statusCode=" + statusCode + " " + content);
            }
        });
    }

    private void initUI() {
        setTitle("好友请求");
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(320, 230));        // 首选大小
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

    private void initContent() {
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(contentPanel, BorderLayout.CENTER);
        setIMContentPane(contentPane);
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
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(new BuddyNode(buddy));
        userModel = new DefaultTreeModel(root);
        userTree.setModel(userModel);
        WebScrollPane treeScroll = new WebScrollPane(userTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        WebPanel chooseCatePanel = new WebPanel();

        chooseCatePanel.setOpaque(true);

        WebButton acceptBtn = new WebButton("接受");
        WebButton refuseBtn = new WebButton("拒绝");
        WebLabel label = new WebLabel("好友分组:");
        label.setPreferredSize(60, 32);


        comboBox.setRenderer(new CategoryComboxCellRenderer());
        for (IMCategory category : categories) {
            comboBox.addItem(category);
        }
        comboBox.setPreferredSize(80, 25);
        acceptBtn.setPreferredSize(60, 30);
        refuseBtn.setPreferredSize(60, 30);
        GroupPanel buttons = new GroupPanel(10, true, acceptBtn, refuseBtn);
        VerticalPanel verticalPanel = new VerticalPanel(label, comboBox);
        verticalPanel.setMargin(new Insets(10, 20, 20, 20));
        buttons.setMargin(10, 5, 0, 6);
        chooseCatePanel.add(verticalPanel, BorderLayout.PAGE_START);
        chooseCatePanel.add(treeScroll, BorderLayout.CENTER);
        chooseCatePanel.add(new AlignPanel(buttons, SwingConstants.RIGHT, SwingConstants.CENTER), BorderLayout.PAGE_END);
        chooseCatePanel.setMargin(6);
        refuseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                broadcastUIEvent(UIEventType.REFUSE_FRIEND_REQUEST, friendRequestId);
                dispose();
            }
        });
        acceptBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = comboBox.getSelectedItem();//返回当前所选的项。
                IMCategory category = (IMCategory) obj;
                UIEvent event = new UIEvent(UIEventType.ACCEPT_FRIEND_REQUEST, null);
                event.putData("receiver_category_id", category.getId());
                event.putData("buddy_request_id", friendRequestId);
                broadcastUIEvent(event);
                dispose();
            }
        });
        return chooseCatePanel;
    }

    @UIEventHandler(UIEventType.REFUSE_FRIEND_SUCCESS)
    public void processRefuseFriendRequest(UIEvent uiEvent) {
        this.dispose();
    }

    @UIEventHandler(UIEventType.ACCEPT_FRIEND_SUCCESS)
    public void processAcceptFriendRequest(UIEvent uiEvent) {
        this.dispose();
    }
}
