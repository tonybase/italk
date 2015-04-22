package iqq.app.util.date;

/**
 * Created by Tony on 3/28/15.
 */
public class BaseDate extends java.util.Date {

    public BaseDate() {
        super();
    }

    public BaseDate(long milliseconds) {
        super(milliseconds);
    }

    public BaseDate(java.util.Date date) {
        super(date.getTime());
    }

    /**
     * yyyy-MM-dd
     *
     * @return
     */
    public String toDate() {
        return Date.formatFor(this);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String toDateTime() {
        return DateTime.formatFor(this);
    }

    /**
     * yyyy年MM月dd日
     *
     * @return
     */
    public String toDateCN() {
        return Date.FORMATTER_CN.format(this);
    }

    /**
     * yyyy年MM月dd日 HH:mm:ss
     *
     * @return
     */
    public String toDateTimeCN() {
        return DateTime.FORMATTER_CN.format(this);
    }

    /**
     * {0}秒之前
     * {0}分钟之前
     * {0}小时之前
     * {0}天之前
     * MM月dd日 HH:mm
     * yyyy年MM月dd日 HH:mm
     *
     * @return
     */
    public String toFriendly() {
        return new FriendlyDate(getTime()).toFriendlyDate(true);
    }
}
