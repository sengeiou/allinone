package wiki.dwx.allinone.service.impl;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import wiki.dwx.allinone.service.QweatherService;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class QweatherServiceImpl implements QweatherService {
    @Override
    public Map getCityLookup(String location) {
        try {
            URI uri = new URIBuilder("https://geoapi.qweather.com/v2/city/lookup")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getCityLookup:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getCityLookup:" + location, e);
            }
        } catch (Exception e) {
            log.error("getCityLookup:" + location, e);
        }

        return null;
    }

    @Override
    public Map getWeatherNow(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/weather/now")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getWeatherNow:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getWeatherNow:" + location, e);
            }
        } catch (Exception e) {
            log.error("getWeatherNow:" + location, e);
        }
        return null;
    }

    @Override
    public Map getMinutely5m(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/minutely/5m")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getMinutely5m:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getMinutely5m:" + location, e);
            }
        } catch (Exception e) {
            log.error("getMinutely5m:" + location, e);
        }

        return null;
    }

    @Override
    public Map getWarningNow(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/warning/now")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getWarningNow:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getWarningNow:" + location, e);
            }
        } catch (Exception e) {
            log.error("getWarningNow:" + location, e);
        }
        return null;
    }

    @Override
    public Map getWeather7d(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/weather/7d")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getWeather3d:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getWeather7d:" + location, e);
            }
        } catch (Exception e) {
            log.error("getWeather7d:" + location, e);
        }
        return null;
    }

    @Override
    public Map getAirNow(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/air/now")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getAirNow:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getAirNow:" + location, e);
            }
        } catch (Exception e) {
            log.error("getAirNow:" + location, e);
        }
        return null;
    }

    @Override
    public Map getIndices1d(String location) {
        try {
            URI uri = new URIBuilder("https://devapi.qweather.com/v7/indices/1d")
                    .addParameter("key", "85a5d49b41d84b178152cbfb02cfe894")
                    .addParameter("location", location)
                    .addParameter("type", "1,2,3,5,7,8,9,10,13")
                    .build();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
//                log.info("getIndices1d:" + location + " " + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getIndices1d:" + location, e);
            }
        } catch (Exception e) {
            log.error("getIndices1d:" + location, e);
        }
        return null;
    }
}
