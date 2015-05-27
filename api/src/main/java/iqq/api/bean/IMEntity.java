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

    private Object attachment;

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

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IMEntity entity = (IMEntity) o;

        return id.equals(entity.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
