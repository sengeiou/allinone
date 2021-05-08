package wiki.dwx.allinone;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class AllinoneApplicationTests {

    class Iptv {
        public String f;
        public String url;
    }

    @Test
    void contextLoads() throws IOException {
        HashMap<Integer, Iptv> end = new HashMap<>();
        Pattern r = Pattern.compile("(\\d+)");

        List<String> list = FileUtils.readLines(new ClassPathResource("/other/all(内网).m3u").getFile(), Charset.forName("utf-8"));
        for (int i = 1; i < list.size(); i += 2) {
            String str = list.get(i);
            Matcher m = r.matcher(str.replace("-1", ""));
            m.find();
            Iptv iptv = new Iptv();
            iptv.f = str;
            iptv.url = list.get(i + 1);
            if ("35".equals(m.group(0))) {
                System.out.println(m.group(0) + " " + str);
            }
            end.put(Integer.valueOf(m.group(0)), iptv);
        }

        List<Integer> keyList = new ArrayList<>();
        keyList.addAll(end.keySet());
        Collections.sort(keyList);
        System.out.println("频道数:" + keyList.size());
        System.out.println(list.get(0));
        for (int i = 0; i < keyList.size(); i++) {
            Iptv iptv = end.get(keyList.get(i));
            String ext = String.format("#EXTINF:0 channelID=\"x-ID.%d\" tvg-chno=\"%d\"", keyList.get(i), keyList.get(i));
            System.out.println(iptv.f.replace("#EXTINF:-1", ext));
            System.out.println(iptv.url);
        }
    }
}
