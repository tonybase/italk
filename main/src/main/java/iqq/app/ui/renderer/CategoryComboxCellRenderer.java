package iqq.app.ui.renderer;

import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMCategory;
import iqq.api.bean.IMMsg;
import iqq.app.ui.renderer.node.BuddyNode;
import iqq.app.ui.renderer.node.CategoryNode;
import javax.swing.*;
import java.awt.*;

public class CategoryComboxCellRenderer extends WebLabel implements ListCellRenderer {

    public CategoryComboxCellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index,  boolean isSelected,  boolean cellHasFocus) {
            IMCategory category = (IMCategory) value;
            setText(category.getName());
        return this;
    }
}
