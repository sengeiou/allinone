package wiki.dwx.allinone.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wiki.dwx.allinone.config.WxCpConfig;
import wiki.dwx.allinone.service.WeatherSMSService;
import wiki.dwx.allinone.utils.DateUtils;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Controller
public class IndexController {

    @Resource
    private WeatherSMSService weatherSMSService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping(value = {"", "/"})
    public String index(Model model) {
        model.addAttribute("time", DateUtils.toTimeString(DateUtils.getNowDate()));
        model.addAttribute("bj_img", redisTemplate.opsForValue().get("bj_img"));
        return "index";
    }

    @ResponseBody
    @GetMapping(value = {"/send"})
    public String send() {
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);

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
        try {
            wxCpService.getMessageService().send(message);
        } catch (WxErrorException e) {
            log.error("send:", e);
        }
        return res.get("img").toString();
    }
}
