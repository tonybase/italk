package iqq.app.ui.frame;

import com.alee.extended.layout.HorizontalFlowLayout;
import com.alee.extended.panel.AlignPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.VerticalPanel;
import com.alee.extended.panel.WebComponentPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
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
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony on 5/18/15.
 */
public class ChooseCateFrame extends IMFrame {
    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = headerPanel();
    private WebPanel contentPanel = new WebPanel();

    public ChooseCateFrame() {
        initUI();
        initContent();
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
        setTitle("选择好友分组");
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
        WebPanel chooseCatePanel = new WebPanel();



        chooseCatePanel.setOpaque(true);

        WebButton confirmBtn = new WebButton("确定");
        WebButton cancelBtn = new WebButton("取消");
        WebLabel label=new WebLabel("好友分组:");
        label.setPreferredSize(60, 32);

        WebComboBox comboBox=new WebComboBox();
        comboBox.addItem("我的好友");
        comboBox.addItem("黑名单");
        comboBox.setPreferredSize(100,32);

        confirmBtn.setPreferredSize(60, 30);
        cancelBtn.setPreferredSize(60, 30);
        GroupPanel buttons = new GroupPanel(10, true, confirmBtn, cancelBtn);
        VerticalPanel verticalPanel = new VerticalPanel(label, comboBox);
        verticalPanel.setMargin(new Insets(20, 20, 20, 20));

        buttons.setMargin(10, 5, 0, 6);
        chooseCatePanel.add(verticalPanel,BorderLayout.PAGE_START);
        chooseCatePanel.add(new AlignPanel(buttons, SwingConstants.RIGHT, SwingConstants.CENTER), BorderLayout.PAGE_END);
        chooseCatePanel.setMargin(6);
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        return chooseCatePanel;
    }


}
