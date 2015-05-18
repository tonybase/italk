package iqq.app.ui.frame;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextField;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.util.UIUtils;

import java.awt.*;

/**
 * Created by Tony on 5/18/15.
 */
public class AddFriendFrame extends IMFrame {
    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = new WebPanel();
    private WebPanel middlePanel = new WebPanel();

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
        contentPane.add(createHeader(), BorderLayout.NORTH);
        contentPane.add(createMiddle(), BorderLayout.CENTER);
        setIMContentPane(contentPane);
    }

    private WebPanel createHeader() {
        headerPanel.setOpaque(false);
        TitleComponent titleComponent = new TitleComponent(this);
        titleComponent.setShowSettingButton(false);
        titleComponent.setShowMaximizeButton(false);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowMinimizeButton(false);
        headerPanel.add(titleComponent, BorderLayout.NORTH);
        return headerPanel;
    }

    private WebPanel createMiddle() {
        WebLabel userIdLbl = new WebLabel("用户ID：");
        WebTextField inputFld = new WebTextField();
        userIdLbl.setMargin(new Insets(20, 20, 20, 20));
        inputFld.setPreferredSize(200, 35);
        middlePanel.setOpaque(true);
        middlePanel.add(userIdLbl, BorderLayout.PAGE_START);
        middlePanel.add(inputFld, BorderLayout.CENTER);

        return middlePanel;
    }
}
