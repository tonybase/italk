package iqq.app.ui.frame;

import com.alee.extended.panel.CenterPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextField;
import iqq.api.bean.IMCategory;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.renderer.node.CategoryNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tony on 5/12/15.
 */
public class AddCategoryFrame extends IMFrame {
    private Logger logger = LoggerFactory.getLogger(AddCategoryFrame.class);
    private IAddCategoryCallback addedCallback;
    private AddCategoryPane contentPanel;
    private IMCategory category;

    public AddCategoryFrame() {
        initUI();
        initContent();
    }

    public AddCategoryFrame(IMCategory category) {
        this.category = category;

        initUI();
        initContent();

        logger.debug(category + "");
    }

    private void initUI() {
        setTitle(category != null ? "重名分类" : "添加分组");
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(240, 125));        // 首选大小
        pack();
    }

    private void initContent() {
        contentPanel = new AddCategoryPane();
        setIMContentPane(contentPanel);
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        // 背景
        contentPanel.setPainter(skinService.getPainterByKey("skin/background"));
    }

    public class AddCategoryPane extends IMContentPane {

        WebLabel nameLbl = new WebLabel("名称");
        WebTextField inputFld = new WebTextField();
        WebButton okBtn = new WebButton(category != null ? "确定" : "添加");

        public AddCategoryPane() {
            // 上面是标题栏，下面为内容显示
            TitleComponent titleComponent = new TitleComponent(AddCategoryFrame.this);
            titleComponent.setShowSkinButton(false);
            titleComponent.setShowMaximizeButton(false);
            titleComponent.setShowSettingButton(false);
            add(titleComponent, BorderLayout.PAGE_START);

            add(createContent(), BorderLayout.CENTER);
        }

        private WebPanel createContent() {
            inputFld.setText(category != null ? category.getName() : "");

            Insets insets = new Insets(5, 10, 5, 10);
            nameLbl.setMargin(insets);
            okBtn.setMargin(insets);
            okBtn.setPreferredWidth(80);
            okBtn.setPreferredHeight(30);
            okBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addCategory(inputFld.getText());
                }
            });

            WebPanel contentPanel = new WebPanel();
            contentPanel.add(nameLbl, BorderLayout.PAGE_START);
            contentPanel.add(inputFld, BorderLayout.CENTER);
            contentPanel.add(new CenterPanel(okBtn), BorderLayout.PAGE_END);
            return contentPanel;
        }

    }

    /**
     * 添加分类
     *
     * @param text
     */
    private void addCategory(String text) {
        if (addedCallback != null) {
            IMCategory category = new IMCategory();
            category.setId(text);
            category.setName(text);
            addedCallback.onAddedEvent(category);
        }

        dispose();
    }

    public void setAddedCallback(IAddCategoryCallback callback) {
        this.addedCallback = callback;
    }

    public interface IAddCategoryCallback {
        void onAddedEvent(IMCategory category);
    }
}
