package wiki.dwx.allinone.utils;

import java.awt.*;
import java.util.Date;

public class DateUtils {

    private final static String[] week = {"", "一", "二", "三", "四", "五", "六", "日"};

    public static Date getNowDate() {
        return new Date();
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
