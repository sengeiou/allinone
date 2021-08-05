package wiki.dwx.allinone.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.apache.commons.lang3.StringUtils;
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

        String bjImgUrl = redisTemplate.opsForValue().get("bj_img");
        model.addAttribute("bj_img", bjImgUrl);
//        model.addAttribute("img_time", DateUtils.getImgTime4Url(bjImgUrl));

        String ajImgUrl = redisTemplate.opsForValue().get("ah_img");
        model.addAttribute("ah_img", ajImgUrl);
//        model.addAttribute("an_img_time", DateUtils.getImgTime4Url(ajImgUrl));

        // 101220303
        String whxImgUrl = redisTemplate.opsForValue().get("whxImgUrl");
        if (StringUtils.isNotBlank(whxImgUrl)) {
            model.addAttribute("whx_img", whxImgUrl);
        }
//        model.addAttribute("whx_img_time", DateUtils.getImgTime4Url(whxImgUrl));

        // 101221406
        String gdImgUrl = redisTemplate.opsForValue().get("gdImgUrl");
        if (StringUtils.isNotBlank(gdImgUrl)) {
            model.addAttribute("gd_img", gdImgUrl);
        }
//        model.addAttribute("gd_img_time", DateUtils.getImgTime4Url(gdImgUrl));

        // 101080608
        String ksktImgUrl = redisTemplate.opsForValue().get("ksktImgUrl");
        if (StringUtils.isNotBlank(ksktImgUrl)) {
            model.addAttribute("ksktImgUrl", ksktImgUrl);
        }

        // 101160808
        String dhImgUrl = redisTemplate.opsForValue().get("dhImgUrl");
        if (StringUtils.isNotBlank(dhImgUrl)) {
            model.addAttribute("dhImgUrl", dhImgUrl);
        }
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
        redisTemplate.opsForValue().set("bj_img", res.get("img").toString());
        return res.get("img").toString();
    }
}
