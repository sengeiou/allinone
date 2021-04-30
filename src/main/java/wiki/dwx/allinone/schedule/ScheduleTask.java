package wiki.dwx.allinone.schedule;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wiki.dwx.allinone.config.WxCpConfig;

@Slf4j
@Component
public class ScheduleTask {

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void doTasks() throws WxErrorException {
        final WxCpService wxCpService = WxCpConfig.getCpService(1000002);

        WxCpMessage message = WxCpMessage.TEXT()
                .agentId(1000002)
                .toUser("DingWenXuan")
                .content(DateUtil.now())
                .build();
        wxCpService.getMessageService().send(message);
    }
}
