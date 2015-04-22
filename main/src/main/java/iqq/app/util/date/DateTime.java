package iqq.app.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 日期时间类
 * yyyy-MM-dd HH:mm:ss
 * <p/>
 * Created by Tony on 1/5/15.
 */
public class DateTime extends BaseDate {
    public final static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat FORMATTER_CN = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    /**
     * string to date
     *
     * @param string
     * @return
     */
    public static DateTime parseFor(String string) {
        java.util.Date date = null;
        try {
            date = FORMATTER.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new DateTime(date);
    }

    /**
     * date to string
     *
     * @param date
     * @return
     */
    public static String formatFor(BaseDate date) {
        return FORMATTER.format(date);
    }

    /**
     * 新建一个日期时间类
     *
     * @return
     */
    public static DateTime newInstance() {
        // now to yyyy-MM-dd
        String nowDate = FORMATTER.format(new java.util.Date());
        try {
            return new DateTime(FORMATTER.parse(nowDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new DateTime(System.currentTimeMillis());
    }

    public DateTime(long readLong) {
        super(readLong);
    }

    public DateTime(java.util.Date date) {
        super(date.getTime());
    }

}
