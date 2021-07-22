package wiki.dwx.allinone.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import wiki.dwx.allinone.service.QweatherService;
import wiki.dwx.allinone.service.TimorHolidayService;
import wiki.dwx.allinone.service.WeatherSMSService;
import wiki.dwx.allinone.utils.DateUtils;
import wiki.dwx.allinone.utils.JsonUtils;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeatherSMSServiceImpl implements WeatherSMSService {

    @Resource
    private QweatherService qweatherService;
    @Resource
    private TimorHolidayService timorHolidayService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map getWeather4Wc(String location) {
        DateTime dateTime = new DateTime(DateUtils.getNowDate());
        log.info(location + ":" + dateTime.getHourOfDay());
        HashMap<String, String> res = new HashMap<>();

        Gson gson = new GsonBuilder().create();

        Map cityLookup = qweatherService.getCityLookup(location);
        JsonObject cityLookupObj = gson.fromJson(JsonUtils.toString(cityLookup), JsonObject.class);
        JsonObject locationObj = cityLookupObj.getAsJsonArray("location").get(0).getAsJsonObject();

        res.put("title", locationObj.get("adm2").getAsString() + locationObj.get("name").getAsString() + "天气预报");

        String sms = dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日星期" + DateUtils.getDayOfWeek(dateTime.getDayOfWeek());
        sms += locationObj.get("adm2").getAsString() + locationObj.get("name").getAsString();

        Map weatherNow = qweatherService.getWeatherNow(location);
        JsonObject weatherNowObj = gson.fromJson(JsonUtils.toString(weatherNow), JsonObject.class);
        JsonObject now = weatherNowObj.getAsJsonObject("now");
        sms += " 实时天气 " + now.get("text").getAsString() + " " + now.get("windDir").getAsString() + now.get("windScale").getAsString() + "级";
        sms += "\n气温" + now.get("temp").getAsString() + "℃ 体感温度" + now.get("feelsLike").getAsString() + "℃ 湿度" + now.get("humidity").getAsString() + "%";

        String fxLink = weatherNowObj.get("fxLink").getAsString();
        res.put("fxLink", fxLink);

        Map minutely5m = qweatherService.getMinutely5m(locationObj.get("lon").getAsString() + "," + locationObj.get("lat").getAsString());
        JsonObject minutely5mObj = gson.fromJson(JsonUtils.toString(minutely5m), JsonObject.class);
        if (minutely5mObj.has("summary")) {
            sms += " " + minutely5mObj.get("summary").getAsString();
        }

        Map warningNow = qweatherService.getWarningNow(location);
        JsonObject warningNowObj = gson.fromJson(JsonUtils.toString(warningNow), JsonObject.class);
        JsonArray warnings = warningNowObj.getAsJsonArray("warning");
        for (int i = 0; i < warnings.size(); i++) {
            JsonObject warning = warnings.get(i).getAsJsonObject();
            String text = warning.get("text").getAsString();
            sms += "\n" + text;
        }

        Map weather7d = qweatherService.getWeather7d(location);
        JsonObject weather7dObj = gson.fromJson(JsonUtils.toString(weather7d), JsonObject.class);
        JsonArray dailys = weather7dObj.getAsJsonArray("daily");
        JsonObject daily = dailys.get(0).getAsJsonObject();
        sms += "\n最高温度:" + daily.get("tempMax").getAsString() + "℃";
        sms += " 最低温度:" + daily.get("tempMin").getAsString() + "℃";
        sms += " 湿度:" + daily.get("humidity").getAsString() + "%";
        sms += "\n白天 " + daily.get("textDay").getAsString() + " " + daily.get("windDirDay").getAsString() + daily.get("windScaleDay").getAsString() + "级";
        sms += "\n夜间 " + daily.get("textNight").getAsString() + " " + daily.get("windDirNight").getAsString() + daily.get("windScaleNight").getAsString() + "级";

        Map airNow = qweatherService.getAirNow(location);
        JsonObject airNowObj = gson.fromJson(JsonUtils.toString(airNow), JsonObject.class);
        airNowObj = airNowObj.getAsJsonObject("now");
        sms += "\n空气质量" + airNowObj.get("category").getAsString() + " PM2.5:" + airNowObj.get("pm2p5").getAsString() + " PM10:" + airNowObj.get("pm10").getAsString() + " AQI:" + airNowObj.get("aqi").getAsString();

        Map indices1d = qweatherService.getIndices1d(location);
        JsonObject indices1dObj = gson.fromJson(JsonUtils.toString(indices1d), JsonObject.class);
        dailys = indices1dObj.getAsJsonArray("daily");
        for (int i = 0; i < dailys.size(); i++) {
            JsonObject index = dailys.get(i).getAsJsonObject();
            sms += "\n" + index.get("name").getAsString() + ":" + index.get("category").getAsString();
        }

        res.put("msg", sms);
        res.put("img", this.getWeatherImgUrl(location, cityLookup, weatherNow, warningNow, weather7d, airNow, 1068, 455));
        return res;
    }

    private String getWeatherImgUrl(String location,
                                    Map cityLookup,
                                    Map weatherNow,
                                    Map warningNow,
                                    Map weather7d,
                                    Map airNow,
                                    int scaleW,
                                    int scaleH) {
        DateTime dateTime = new DateTime(DateUtils.getNowDate());

        String wRoot = "/www/wwwroot/img.dwx.wiki";
//        String wRoot = "/Users/wenxuan.ding/fsdownload";

        String descPath01 = wRoot + "/";
        String descPath02 = dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth();
        String descPath03 = "/" + location + "-" + dateTime.getMillisOfDay() + ".png";

        List<String> fileList = cn.hutool.core.io.FileUtil.listFileNames(wRoot + "/bkimg/");
        String path = wRoot + "/bkimg/" + fileList.get(RandomUtil.randomInt(0, fileList.size()));

        String fontName = "WenQuanYi Micro Hei";
//        String fontName = "Arial";

        Gson gson = new GsonBuilder().create();

        List<String> listd = new ArrayList<>();
        JsonObject typeObj = null;
        try {
            for (int i = 0; i < 7; i++) {
                Date newDate = DateUtil.offset(DateUtils.getNowDate(), DateField.DAY_OF_MONTH, i);
                String d = DateUtil.format(newDate, "yyyy-MM-dd");
                listd.add(d);
            }

            String redisKey = DateUtil.format(DateUtils.getNowDate(), "yyyy-MM-dd");
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNoneBlank(redisValue)) {
                typeObj = gson.fromJson(redisValue, JsonObject.class);
                log.info("命中缓存:" + redisKey + ":" + redisValue);
            } else {
                Map holidayBatch = timorHolidayService.getHolidayBatch(listd);
                JsonObject holidayBatchObj = gson.fromJson(JsonUtils.toString(holidayBatch), JsonObject.class);
                typeObj = holidayBatchObj.get("type").getAsJsonObject();
                redisTemplate.opsForValue().set(redisKey, JsonUtils.toString(typeObj), 25, TimeUnit.HOURS);
                log.info("保存到缓存:" + redisKey);
            }
        } catch (Exception e) {
            log.error("getHolidayBatch:" + location, e);
        }

        try {
            // 读出文件 修改图片尺寸
            Img img = Img.from(FileUtil.file(path));
            img.setPositionBaseCentre(false);
            if (scaleW > 0 && scaleH > 0) {
                img.scale(scaleW, scaleH);
            }

            float scaleFont = 1.2f;

            JsonObject cityLookupObj = gson.fromJson(JsonUtils.toString(cityLookup), JsonObject.class);
            JsonObject locationObj = cityLookupObj.getAsJsonArray("location").get(0).getAsJsonObject();

            String pressText = locationObj.get("adm2").getAsString() + locationObj.get("name").getAsString();
            pressText += +dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日星期" + DateUtils.getDayOfWeek(dateTime.getDayOfWeek());
            Font smallFont = new Font(fontName, Font.PLAIN, (int) (24 * scaleFont));

            Color color = Color.WHITE;
            if (typeObj != null) {
                color = DateUtils.getDayOfColor(typeObj.get(listd.get(0)).getAsJsonObject().get("type").getAsInt());
            }
            img.pressText(pressText, color, smallFont, 20, 30, 1.0f);

            img.pressText("by 睡神", Color.WHITE, smallFont, scaleW - 105, 30, 1.0f);

            JsonObject weatherNowObj = gson.fromJson(JsonUtils.toString(weatherNow), JsonObject.class);
            JsonObject now = weatherNowObj.getAsJsonObject("now");

            String icon = now.get("icon").getAsString();
            int xOffset = 20;
            int yOffset = 20;

            String iconPathBase = wRoot + "/WeatherIcon/weather-icon-S2/";
            String iconPath = iconPathBase + "256/" + icon + ".png";
            img.pressImage(Img.from(FileUtil.file(iconPath)).getImg(), xOffset, yOffset + scaleH / 3 - 128, 1.0f);

            String temp = now.get("temp").getAsString() + "°";
            Font tempFont = new Font(fontName, Font.PLAIN, (int) (110 * scaleFont));
            int tempX = xOffset + 226;
            int tempY = yOffset / 2 + scaleH / 2 - 20;
            img.pressText(temp, Color.WHITE, tempFont, tempX, tempY, 1.0f);

            Rectangle2D tempFontRect = tempFont.getStringBounds(temp, ((Graphics2D) img.getImg().getGraphics()).getFontRenderContext());

            String text = now.get("text").getAsString();
            int textX = (int) (tempX + tempFontRect.getWidth());
            int textY = (int) (tempY - tempFontRect.getHeight() / 2) + 10;
            Font font = new Font(fontName, Font.BOLD, (int) (32 * scaleFont));
            img.pressText(text, Color.WHITE, font, textX, textY, 1.0f);
            Rectangle2D fontRect = font.getStringBounds(temp, ((Graphics2D) img.getImg().getGraphics()).getFontRenderContext());

            JsonObject warningNowObj = gson.fromJson(JsonUtils.toString(warningNow), JsonObject.class);
            JsonArray warnings = warningNowObj.getAsJsonArray("warning");
            int warOffx = (int) fontRect.getWidth() + 10;
            for (int i = 0; i < warnings.size(); i++) {
                JsonObject warning = warnings.get(i).getAsJsonObject();
//                text += " " + warning.get("typeName").getAsString() + warning.get("level").getAsString() + "预警";

                String war = warning.get("typeName").getAsString();
                img.pressText(war, DateUtils.getWarColor(warning.get("level").getAsString()), font, textX + warOffx, textY, 1.0f);
                Rectangle2D warRect = font.getStringBounds(war, ((Graphics2D) img.getImg().getGraphics()).getFontRenderContext());
                warOffx += warRect.getWidth() + 10;
            }

            JsonObject weather7dObj = gson.fromJson(JsonUtils.toString(weather7d), JsonObject.class);
            JsonArray dailys = weather7dObj.getAsJsonArray("daily");
            JsonObject daily = dailys.get(0).getAsJsonObject();

            String tempMaxtempMin = daily.get("tempMax").getAsString() + "/" + daily.get("tempMin").getAsString() + "度";

            JsonObject airNowObj = gson.fromJson(JsonUtils.toString(airNow), JsonObject.class);
            airNowObj = airNowObj.getAsJsonObject("now");

            tempMaxtempMin += " " + airNowObj.get("category").getAsString();

            img.pressText(tempMaxtempMin, Color.WHITE, font, textX, textY + (int) fontRect.getHeight() + 20, 1.0f);

            // iconDay 时间 tempMax tempMin
            String sunrise = daily.get("sunrise").getAsString();  // 07:23
            int sunriseI = Integer.parseInt(sunrise.split(":")[0]);

            String sunset = daily.get("sunset").getAsString();    // 17:31
            int sunsetI = Integer.parseInt(sunset.split(":")[0]);
            int dailyX = 10;
            int dailyY = scaleH - 80;
            for (int i = 0; i < 6; i++) {
                daily = dailys.get(i + 1).getAsJsonObject();

                int offset = i * 178;
                if (offset > 0) {
                    offset = offset - 15;
                }
                iconPath = iconPathBase + "64/" + daily.get("iconDay").getAsString() + ".png";
                if (dateTime.getHourOfDay() > sunsetI || dateTime.getHourOfDay() <= sunriseI) {
                    iconPath = iconPathBase + "64/" + daily.get("iconNight").getAsString() + ".png";
                }
                img.pressImage(Img.from(FileUtil.file(iconPath)).getImg(), dailyX + offset, dailyY - 10, 1.0f);

                Date newDate = DateUtil.offset(DateUtils.getNowDate(), DateField.DAY_OF_MONTH, i + 1);
                dateTime = new DateTime(newDate);

                String day = i == 0 ? "明天" : dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日";
                if (typeObj != null) {
                    img.pressText(day, DateUtils.getDayOfColor(typeObj.get(listd.get(i + 1)).getAsJsonObject().get("type").getAsInt()), smallFont, dailyX + 64 + offset, dailyY + 15, 1.0f);
                } else {
                    img.pressText(day, Color.WHITE, smallFont, dailyX + 64 + offset, dailyY + 15, 1.0f);
                }
                temp = daily.get("tempMax").getAsString() + "/" + daily.get("tempMin").getAsString() + "°";
                img.pressText(temp, Color.WHITE, smallFont, dailyX + 64 + offset, dailyY + 50, 1.0f);
            }

            FileUtil.mkdir(descPath01 + descPath02);
            img.round(0.1f).write(FileUtil.file(descPath01 + descPath02 + descPath03));
        } catch (Exception e) {
            log.error("index", e);
        }
        return "https://img.dwx.wiki/" + descPath02 + descPath03;
    }
}
