package wiki.dwx.allinone.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import wiki.dwx.allinone.service.QweatherService;
import wiki.dwx.allinone.service.TimorHolidayService;
import wiki.dwx.allinone.service.WeatherSMSService;
import wiki.dwx.allinone.utils.DateUtils;
import wiki.dwx.allinone.utils.JsonUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class WeatherSMSServiceImpl implements WeatherSMSService {

    @Resource
    private QweatherService qweatherService;
    @Resource
    private TimorHolidayService timorHolidayService;

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

        String descPath01 = wRoot + "/";
        String descPath02 = dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth();
        String descPath03 = "/" + location + "-" + dateTime.getMillisOfDay() + ".png";

        List<String> fileList = cn.hutool.core.io.FileUtil.listFileNames(wRoot + "/bkimg/");
        String path = wRoot + "/bkimg/" + fileList.get(RandomUtil.randomInt(0, fileList.size()));

        String fontName = "WenQuanYi Micro Hei";

        Gson gson = new GsonBuilder().create();

        List<String> listd = new ArrayList<>();
        JsonObject typeObj = null;
        try {
            for (int i = 0; i < 5; i++) {
                Date newDate = DateUtil.offset(DateUtils.getNowDate(), DateField.DAY_OF_MONTH, i);
                String d = DateUtil.format(newDate, "yyyy-MM-dd");
                listd.add(d);
            }
            Map holidayBatch = timorHolidayService.getHolidayBatch(listd);
            JsonObject holidayBatchObj = gson.fromJson(JsonUtils.toString(holidayBatch), JsonObject.class);
            typeObj = holidayBatchObj.get("type").getAsJsonObject();
        } catch (Exception e) {
            log.error("getHolidayBatch:" + location, e);
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(FileUtil.file(path));
            BufferedImage bkImg = ImgUtil.copyImage(bufferedImage, BufferedImage.TYPE_INT_RGB);
            FileUtil.mkdir(descPath01 + descPath02);

            JsonObject cityLookupObj = gson.fromJson(JsonUtils.toString(cityLookup), JsonObject.class);
            JsonObject locationObj = cityLookupObj.getAsJsonArray("location").get(0).getAsJsonObject();

            String pressText = locationObj.get("adm2").getAsString() + locationObj.get("name").getAsString();
            pressText += +dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日星期" + DateUtils.getDayOfWeek(dateTime.getDayOfWeek());
            Font smallFont = new Font(fontName, Font.PLAIN, 24);
            int h = bkImg.getHeight();
            int x0 = (bkImg.getWidth() - 40) / -2 + smallFont.getSize() * pressText.length() / 2 - 20;
            int y0 = (bkImg.getHeight() - 40) / -2 + smallFont.getSize();
            Color color = Color.WHITE;
            if (typeObj != null) {
                color = DateUtils.getDayOfColor(typeObj.get(listd.get(0)).getAsJsonObject().get("type").getAsInt());
            }
            ImgUtil.pressText(Img.from(bkImg).round(0.1f).getImg(),
                    FileUtil.file(descPath01 + descPath02 + descPath03),
                    pressText,
                    color, smallFont, x0, y0, 1.0f);
            Rectangle2D smallFontRect = smallFont.getStringBounds(pressText, ((Graphics2D) bkImg.getGraphics()).getFontRenderContext());

            Img img = Img.from(FileUtil.file(descPath01 + descPath02 + descPath03));
            img.setPositionBaseCentre(false);

            if (scaleW > 0 && scaleH > 0) {
                img.scale(750, 320);
            }

            img.pressText("by 睡神", Color.WHITE, smallFont, bkImg.getWidth() - 85, 25, 1.0f);

            JsonObject weatherNowObj = gson.fromJson(JsonUtils.toString(weatherNow), JsonObject.class);
            JsonObject now = weatherNowObj.getAsJsonObject("now");

            String icon = now.get("icon").getAsString();
            int xOffset = 20;
            int yOffset = 20;

            String iconPathBase = wRoot + "/WeatherIcon/weather-icon-S2/";
            String iconPath = iconPathBase + "128/" + icon + ".png";
            img.pressImage(Img.from(FileUtil.file(iconPath)).getImg(), xOffset, yOffset + h / 3 - 64, 1.0f);

            String temp = now.get("temp").getAsString() + "°";
            Font tempFont = new Font(fontName, Font.PLAIN, 110);
            int tempX = xOffset + 128;
            int tempY = yOffset / 2 + h / 2 - 10;
            img.pressText(temp, Color.WHITE, tempFont, tempX, tempY, 1.0f);

            Rectangle2D tempFontRect = tempFont.getStringBounds(temp, ((Graphics2D) img.getImg().getGraphics()).getFontRenderContext());

            String text = now.get("text").getAsString();
            int textX = (int) (tempX + tempFontRect.getWidth());
            int textY = (int) (tempY - tempFontRect.getHeight() / 2) + 10;

            JsonObject warningNowObj = gson.fromJson(JsonUtils.toString(warningNow), JsonObject.class);
            JsonArray warnings = warningNowObj.getAsJsonArray("warning");
            for (int i = 0; i < warnings.size(); i++) {
                JsonObject warning = warnings.get(i).getAsJsonObject();
                text += " " + warning.get("typeName").getAsString() + warning.get("level").getAsString() + "预警";
            }

            Font font = new Font(fontName, Font.BOLD, 32);
            img.pressText(text, Color.WHITE, font, textX, textY, 1.0f);

            JsonObject weather7dObj = gson.fromJson(JsonUtils.toString(weather7d), JsonObject.class);
            JsonArray dailys = weather7dObj.getAsJsonArray("daily");
            JsonObject daily = dailys.get(0).getAsJsonObject();

            Rectangle2D fontRect = font.getStringBounds(temp, ((Graphics2D) img.getImg().getGraphics()).getFontRenderContext());

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
            int dailyX = 20;
            int dailyY = bufferedImage.getHeight() - 64 - 20;
            for (int i = 0; i < 4; i++) {
                daily = dailys.get(i + 1).getAsJsonObject();

                int offset = i * 180;
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
                img.pressText(temp, Color.WHITE, smallFont, dailyX + 64 + offset, dailyY + 15 + (int) smallFontRect.getHeight(), 1.0f);
            }
            if (scaleW > 0 && scaleH > 0) {
                img.scale(scaleW, scaleH);
            }
            img.write(FileUtil.file(descPath01 + descPath02 + descPath03));
        } catch (Exception e) {
            log.error("index", e);
        }
        return "https://img.dwx.wiki/" + descPath02 + descPath03;
    }
}
