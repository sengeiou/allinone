package wiki.dwx.allinone.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.awt.*;
import java.util.*;

public class DateUtils {

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static String[] week = {"", "一", "二", "三", "四", "五", "六", "日"};

    // 白色、蓝色、黄色、橙色、红色
    private static Map<String, Color> warColor = new HashMap();

    static {
        warColor.put("白色", Color.WHITE);
        warColor.put("蓝色", Color.blue);
        warColor.put("黄色", Color.yellow);
        warColor.put("橙色", Color.orange);
        warColor.put("红色", Color.red);
    }

    public static Color getWarColor(String name) {
        Color color = warColor.get(name);
        if (color == null) {
            return Color.white;
        }
        return color;
    }

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

    public static String getImgTime4Url(String url) {
        String[] strs = url.split("/");
        String day = strs[strs.length - 2];

        String str = StrUtil.removeSuffix(strs[strs.length - 1], ".png");
        strs = str.split("-");
        str = strs[strs.length - 1];
        int time = Integer.valueOf(str) / 1000;
        int h = time / 3600;
        int m = time % 3600 / 60;
        int s = time % 3600 % 60;

        return String.format("%s %02d:%02d:%02d", day, h, m, s);
    }
}
