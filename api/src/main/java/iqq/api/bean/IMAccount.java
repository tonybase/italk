package iqq.api.bean;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-4
 * License  : Apache License 2.0
 */
public class IMAccount extends IMUser {
    private String account;
    private String password;
    private boolean isRememberPwd;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberPwd() {
        return isRememberPwd;
    }

    public void setRememberPwd(boolean isRememberPwd) {
        this.isRememberPwd = isRememberPwd;
    }
}
