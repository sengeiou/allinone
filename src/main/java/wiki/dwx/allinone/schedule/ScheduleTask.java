package wiki.dwx.allinone.schedule;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wiki.dwx.allinone.config.WxCpConfig;
import wiki.dwx.allinone.service.WeatherSMSService;
import wiki.dwx.allinone.utils.DateUtils;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class ScheduleTask {
    @Resource
    private WeatherSMSService weatherSMSService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 7,9,11,13,15,17,19,21 * * ? ")
    public void doTasks() throws WxErrorException {
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);
        log.info("定时任务时间: " + DateUtils.toTimeString(DateUtils.getNowDate()));
        try {
            Map res = weatherSMSService.getWeather4Wc("101010300");

            redisTemplate.opsForValue().set("bj_img", res.get("img").toString());

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
            WxCpMessageSendResult result = wxCpService.getMessageService().send(message);
            log.info(result.toString());
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
            WxCpMessageSendResult result = wxCpService.getMessageService().send(message);
            log.info(result.toString());
        }
    }

    @Scheduled(cron = "0 1 7,12,18,22 * * ?")
    public void configureTasks() throws WxErrorException {
        log.info("定时任务时间: " + DateUtils.toTimeString(DateUtils.getNowDate()));
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);
        try {
            Map res = weatherSMSService.getWeather4Wc("101220110");
            NewArticle article = NewArticle.builder()
                    .title(res.get("title").toString())
                    .description(res.get("msg").toString())
                    .picUrl(res.get("img").toString())
                    .url(res.get("fxLink").toString())
                    .build();
            WxCpMessage message = WxCpMessage.NEWS()
                    .agentId(1000002)
                    .toUser("chloed|davidlu")
                    .addArticle(article)
                    .build();
            log.info(res.get("msg").toString());
            WxCpMessageSendResult result = wxCpService.getMessageService().send(message);
            log.info(result.toString());
        } catch (Exception e) {
            Map res = weatherSMSService.getWeather4Wc("101220110");
            NewArticle article = NewArticle.builder()
                    .title(res.get("title").toString())
                    .description(res.get("msg").toString())
                    .picUrl(res.get("img").toString())
                    .url(res.get("fxLink").toString())
                    .build();
            WxCpMessage message = WxCpMessage.NEWS()
                    .agentId(1000002)
                    .toUser("chloed|davidlu")
                    .addArticle(article)
                    .build();
            log.info(res.get("msg").toString());
            WxCpMessageSendResult result = wxCpService.getMessageService().send(message);
            log.info(result.toString());
        }

//        try {
//            String sms2 = weatherSMSService.getWeatherSMS("101010700");
//            log.info(sms2);
//            sctFtqqService.sendMSG("北京昌平天气预报", sms2, "SCT4256T1qVLRM9qwwAQ45wUqeY1HE91");
//        } catch (Exception e) {
//            String sms2 = weatherSMSService.getWeatherSMS("101010700");
//            log.info(sms2);
//            sctFtqqService.sendMSG("北京昌平天气预报", sms2, "SCT4256T1qVLRM9qwwAQ45wUqeY1HE91");
//        }
    }
}
