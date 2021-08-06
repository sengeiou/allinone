package wiki.dwx.allinone.schedule;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wiki.dwx.allinone.config.WxCpConfig;
import wiki.dwx.allinone.service.WeatherSMSService;
import wiki.dwx.allinone.utils.DateUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduleTask {
    @Resource
    private WeatherSMSService weatherSMSService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 7,9,11,13,15,17,19,21,23 * * ? ")
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
        }
    }

    @Scheduled(cron = "0 1 7,12,18,22 * * ?")
    public void configureTasks() throws WxErrorException {
        log.info("定时任务时间: " + DateUtils.toTimeString(DateUtils.getNowDate()));
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);
        try {
            Map res = weatherSMSService.getWeather4Wc("101220110");
            redisTemplate.opsForValue().set("ah_img", res.get("img").toString());

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
            redisTemplate.opsForValue().set("ah_img", res.get("img").toString());

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

    @Scheduled(cron = "0 0/10 * * * ?")
    public void myCron() {
        log.info("cron:" + DateUtils.toTimeString(DateUtils.getNowDate()));

        // 101220303
        String whxImgUrl = redisTemplate.opsForValue().get("whxImgUrl");
        if (StringUtils.isBlank(whxImgUrl)) {
            Map res = weatherSMSService.getWeather4Wc("101220303");
            whxImgUrl = res.get("img").toString();
            redisTemplate.opsForValue().set("whxImgUrl", whxImgUrl, 60, TimeUnit.MINUTES);
            return;
        }

        // 101221406
        String gdImgUrl = redisTemplate.opsForValue().get("gdImgUrl");
        if (StringUtils.isBlank(gdImgUrl)) {
            Map res = weatherSMSService.getWeather4Wc("101221406");
            gdImgUrl = res.get("img").toString();
            redisTemplate.opsForValue().set("gdImgUrl", gdImgUrl, 60, TimeUnit.MINUTES);
            return;
        }

        // 101080608
        String ksktImgUrl = redisTemplate.opsForValue().get("ksktImgUrl");
        if (StringUtils.isBlank(ksktImgUrl)) {
            Map res = weatherSMSService.getWeather4Wc("101080608");
            ksktImgUrl = res.get("img").toString();
            redisTemplate.opsForValue().set("ksktImgUrl", ksktImgUrl, 60, TimeUnit.MINUTES);
            return;
        }

        // 101160808
        String dhImgUrl = redisTemplate.opsForValue().get("dhImgUrl");
        if (StringUtils.isBlank(dhImgUrl)) {
            Map res = weatherSMSService.getWeather4Wc("101160808");
            dhImgUrl = res.get("img").toString();
            redisTemplate.opsForValue().set("dhImgUrl", dhImgUrl, 60, TimeUnit.MINUTES);
        }
    }

}
