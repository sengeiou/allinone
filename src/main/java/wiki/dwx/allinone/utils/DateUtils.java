package wiki.dwx.allinone.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.awt.*;
import java.util.Date;

public class DateUtils {

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static String[] week = {"", "一", "二", "三", "四", "五", "六", "日"};

    public static Date getNowDate() {
        return new Date();
    }

    public static String toTimeString(Date date) {
        return toTimeString(date, DATE_TIME_FORMAT);
    }

    public static String toTimeString(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static String getDayOfWeek(int d) {
        if (d < 0 || d >= week.length) {
            return "";
        }
        return week[d];
    }

    public static Color getDayOfColor(int type) {
        return type == 0 || type == 3 ? Color.WHITE : Color.GREEN;
    }
}
