package wiki.dwx.allinone.schedule;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wiki.dwx.allinone.config.WxCpConfig;
import wiki.dwx.allinone.service.WeatherSMSService;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class ScheduleTask {
    @Resource
    private WeatherSMSService weatherSMSService;

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void doTasks() throws WxErrorException {
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);

        try {
            Map res = weatherSMSService.getWeather4Wc("101010300");
            NewArticle article = NewArticle.builder()
                    .title(res.get("title").toString())
                    .description(res.get("msg").toString())
                    .picUrl(res.get("img").toString())
                    .url(res.get("fxLink").toString())
                    .build();
            WxCpMessage message = WxCpMessage.NEWS()
                    .agentId(1000002)
                    .toUser("DingWenXuan")
                    .addArticle(article)
                    .build();
            wxCpService.getMessageService().send(message);
        } catch (Exception e) {
            Map res = weatherSMSService.getWeather4Wc("101010300");
            NewArticle article = NewArticle.builder()
                    .title(res.get("title").toString())
                    .description(res.get("msg").toString())
                    .picUrl(res.get("img").toString())
                    .url(res.get("fxLink").toString())
                    .build();
            WxCpMessage message = WxCpMessage.NEWS()
                    .agentId(1000002)
                    .toUser("DingWenXuan")
                    .addArticle(article)
                    .build();
            wxCpService.getMessageService().send(message);
        }
    }
}
