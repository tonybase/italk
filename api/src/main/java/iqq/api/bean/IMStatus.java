package iqq.api.bean;

/**
 * Created with IntelliJ IDEA.
 * User: solosky
 * Date: 4/19/14
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 * <p/>
 * 状态 0离线，1在线，2离开，3请勿打扰，4忙碌，5Q我吧，6隐身
 */
public enum IMStatus {

    OFFLINE("offline", 0),
    ONLINE("online", 1),
    AWAY("away", 2),
    SILENT("silent", 3),
    MEETING("meeting", 4),
    CALLME("callme", 5),
    HIDDEN("hidden", 6);

    public String name;
    public int value;

    IMStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 原生值转枚举
     *
     * @param value
     * @return
     */
    public static IMStatus valueOfRaw(int value) {
        for (IMStatus status : IMStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return OFFLINE;
    }
}
