package iqq.app.ui.manager;

import iqq.api.bean.IMCategory;
import iqq.app.ui.frame.*;
import org.springframework.stereotype.Component;

/**
 * Created by Tony on 4/6/15.
 */
@Component
public class FrameManager {

    private LoginFrame loginFrame = null;
    private VerifyFrame verifyFrame = null;
    private AddFriendFrame addFriendFrame = null;
    private ChooseCateFrame chooseCateFrame =null;

    public void showLogin() {
        if (loginFrame == null) {
            loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        } else if (!loginFrame.isVisible()) {
            loginFrame.setVisible(true);
        }
    }

    public void hideLogin() {
        if (loginFrame != null) {
            loginFrame.setVisible(false);
        }
    }

    public void disposeLogin() {
        if (loginFrame != null) {
            loginFrame.dispose();
            loginFrame = null;
        }
    }

    public void showVerify() {
        if (verifyFrame == null) {
            verifyFrame = new VerifyFrame();
            verifyFrame.setVisible(true);
        } else if (!verifyFrame.isVisible()) {
            verifyFrame.setVisible(true);
        }
    }

    public void hideVerify() {
        if (verifyFrame != null) {
            verifyFrame.setVisible(false);
        }
    }

    public void showAddFriend() {
        if (addFriendFrame == null) {
            addFriendFrame = new AddFriendFrame();
            addFriendFrame.setVisible(true);
        } else if (!addFriendFrame.isVisible()) {
            addFriendFrame.setVisible(true);
        }
    }

    public void hideAddFriend() {
        if (addFriendFrame != null) {
            addFriendFrame.setVisible(false);
        }
    }
    public void showChooseCate() {
        if (chooseCateFrame == null) {
            chooseCateFrame = new ChooseCateFrame();
            chooseCateFrame.setVisible(true);
        } else if (!chooseCateFrame.isVisible()) {
            chooseCateFrame.setVisible(true);
        }
    }

    public void hideChooseCate() {
        if (chooseCateFrame != null) {
            chooseCateFrame.setVisible(false);
        }
    }
}
