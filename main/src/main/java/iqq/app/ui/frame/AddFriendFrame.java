package iqq.app.ui.frame;

import com.alee.extended.panel.AlignPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.VerticalPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.tree.WebTree;
import iqq.api.bean.IMBuddy;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.renderer.RecentTreeCellRenderer;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.util.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Tony on 5/18/15.
 */
public class AddFriendFrame extends IMFrame {
    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = headerPanel();
    private WebPanel contentPanel = new WebPanel();

    private WebTree userTree;
    private DefaultTreeModel userModel;

    public AddFriendFrame() {
        initUI();
        initContent();
    }

    private void initUI() {
        setTitle("添加好友");
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(350, 240));        // 首选大小
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

        contentPanel.add(findPanel());
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

    private WebPanel findPanel() {
        WebPanel middlePanel = new WebPanel();
        WebLabel userIdLbl = new WebLabel("请输入昵称：");
        WebTextField inputFld = new WebTextField();
        userIdLbl.setMargin(new Insets(0, 0, 20, 20));
        inputFld.setPreferredSize(200, 35);

        VerticalPanel verticalPanel = new VerticalPanel(userIdLbl, inputFld);
        verticalPanel.setMargin(new Insets(20, 20, 20, 20));
        middlePanel.setOpaque(true);
        middlePanel.add(verticalPanel, BorderLayout.PAGE_START);

        WebButton findBtn = new WebButton("查找");
        findBtn.setPreferredSize(80, 32);
        AlignPanel alignPanel = new AlignPanel(findBtn, SwingConstants.RIGHT, SwingConstants.CENTER);
        alignPanel.setMargin(20);
        middlePanel.add(alignPanel, BorderLayout.PAGE_END);

        findBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.remove(0);
                contentPanel.add(resultPanel());
                contentPanel.revalidate();

                List<IMBuddy> buddies = new ArrayList<IMBuddy>();
                for (int i = 0; i < 10; i++) {
                    IMBuddy buddy = new IMBuddy();
                    buddy.setNick("nick-" + i);
                    buddy.setSign("sign-" + i);
                    buddies.add(buddy);
                }
                updateRecentList(buddies);
            }
        });
        return middlePanel;
    }

    private WebPanel resultPanel() {
        userTree = new WebTree();
        userTree.setCellRenderer(new RecentTreeCellRenderer());
        WebScrollPane treeScroll = new WebScrollPane(userTree, false, false);
        // 背景色
        treeScroll.getViewport().setBackground(new Color(250, 250, 250));
        // 滚动速度
        treeScroll.getVerticalScrollBar().setUnitIncrement(30);
        treeScroll.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        WebButton backBtn = new WebButton("返回");
        WebButton addBtn = new WebButton("添加");

        backBtn.setPreferredSize(60, 30);
        addBtn.setPreferredSize(60, 30);
        GroupPanel buttons = new GroupPanel(10, true, backBtn, addBtn);
        buttons.setMargin(10, 5, 0, 6);

        WebPanel resultPanel = new WebPanel();
        WebLabel title = new WebLabel("查找结果：");
        title.setMargin(0, 0, 4, 0);
        resultPanel.setOpaque(true);
        resultPanel.add(title, BorderLayout.PAGE_START);
        resultPanel.add(treeScroll, BorderLayout.CENTER);
        resultPanel.add(new AlignPanel(buttons, SwingConstants.RIGHT, SwingConstants.CENTER), BorderLayout.PAGE_END);
        resultPanel.setMargin(6);

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.remove(0);
                contentPanel.add(findPanel());
                contentPanel.revalidate();

            }
        });
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        return resultPanel;
    }

    public void updateRecentList(List<IMBuddy> buddies) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        BufferedImage defaultAvatar = getDefaultAvatar();
        for (IMBuddy buddy : buddies) {
            if (buddy.getAvatar() == null) {
                buddy.setAvatar(defaultAvatar);
            }
            root.add(new BuddyNode(buddy));
        }

        userModel = new DefaultTreeModel(root);
        userTree.setModel(userModel);
    }

    private BufferedImage getDefaultAvatar() {
        try {
            File file = getResourceService().getFile("icons/login/avatar2.png");
            return ImageIO.read(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
