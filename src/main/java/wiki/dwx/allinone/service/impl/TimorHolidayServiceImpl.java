package wiki.dwx.allinone.service.impl;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import wiki.dwx.allinone.service.TimorHolidayService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TimorHolidayServiceImpl implements TimorHolidayService {
    @Override
    public Map getHolidayBatch(List<String> d) {
        try {
            URIBuilder uriBuilder = new URIBuilder("https://timor.tech/api/holiday/batch");
            for (int i = 0; i < d.size(); i++) {
                uriBuilder.addParameter("d", d.get(i));
            }
            uriBuilder.addParameter("type", "Y");
            URI uri = uriBuilder.build();

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri.toString()).build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
                log.info("getHolidayBatch:" + res);
                Map object = new GsonBuilder().create().fromJson(res, Map.class);
                return object;
            } catch (IOException e) {
                log.error("getHolidayBatch:" + d.toString(), e);
            }
        } catch (Exception e) {
            log.error("getHolidayBatch:" + d.toString(), e);
        }
        return null;
    }
}
