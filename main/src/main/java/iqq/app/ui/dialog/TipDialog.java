package iqq.app.ui.dialog;

import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.VerticalPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextArea;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMDialog;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tony on 5/22/15.
 */
public class TipDialog extends IMDialog {
    private IMContentPane contentPane = new IMContentPane();
    private WebTextArea messageTxt;

    public TipDialog(IMFrame owner) {
        super(owner);

        initUI();
        initContent();
    }

    private void initUI() {
        setTitle("提示");
        setModal(true);
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(240, 160));        // 首选大小
        setLocation(UIUtils.getLocationForCenter(owner));
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
        contentPane.add(headerPanel(), BorderLayout.NORTH);
        contentPane.add(contentPanel(), BorderLayout.CENTER);
        setIMContentPane(contentPane);
    }

    private WebPanel headerPanel() {
        WebPanel headerPanel = new WebPanel();
        headerPanel.setOpaque(false);
        TitleComponent titleComponent = new TitleComponent(owner);
        titleComponent.setShowSettingButton(false);
        titleComponent.setShowMaximizeButton(false);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowMinimizeButton(false);
        headerPanel.add(titleComponent, BorderLayout.NORTH);
        return headerPanel;
    }

    private WebPanel contentPanel() {
        WebPanel contentPanel = new WebPanel();
        messageTxt = new WebTextArea();
        messageTxt.setEditable(false);
        messageTxt.setLineWrap(true);
        messageTxt.setWrapStyleWord(true);
        messageTxt.setBackground(getBackground());
        messageTxt.setAlignmentX(SwingConstants.CENTER);
        messageTxt.setMargin(20);
        WebButton okBtn = new WebButton("确定");
        okBtn.setPreferredWidth(30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPanel.add(new CenterPanel(messageTxt), BorderLayout.CENTER);
        contentPanel.add(okBtn, BorderLayout.PAGE_END);
        return contentPanel;
    }

    /**
     * 设置提示信息
     *
     * @param text
     */
    public void setMessage(String text) {
        messageTxt.setText(text);
    }
}
