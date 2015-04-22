package iqq.app.api;

/**
 * Created by Tony on 4/22/15.
 */
public class User {
    private String id;
    private String nick;
    private String avatar;
    private String status;
    private String token;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nick='" + nick + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
