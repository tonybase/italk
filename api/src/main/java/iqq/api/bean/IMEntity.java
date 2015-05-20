package iqq.api.bean;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: solosky
 * Date: 4/19/14
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class IMEntity implements Serializable {
    private String id;
    private String nick;
    private String sign;
    private String avatar;
    private BufferedImage avatarBuffered;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BufferedImage getAvatarBuffered() {
        return avatarBuffered;
    }

    public void setAvatarBuffered(BufferedImage avatarBuffered) {
        this.avatarBuffered = avatarBuffered;
    }

    @Override
    public String toString() {
        return "IMEntity{" +
                "id='" + id + '\'' +
                ", nick='" + nick + '\'' +
                ", sign='" + sign + '\'' +
                ", avatar='" + avatar + '\'' +
                ", avatarBuffered=" + avatarBuffered +
                '}';
    }
}
