package wiki.dwx.allinone.service;

import java.util.Map;

public interface QweatherService {

    // 城市信息
    Map getCityLookup(String location);

    // 实况天气
    Map getWeatherNow(String location);

    // 分钟级降水
    Map getMinutely5m(String location);

    // 灾害预警
    Map getWarningNow(String location);

    // 7天预报
    Map getWeather7d(String location);

    // 空气质量实况
    Map getAirNow(String location);

    // 当天生活指数预报
    Map getIndices1d(String location);
}
