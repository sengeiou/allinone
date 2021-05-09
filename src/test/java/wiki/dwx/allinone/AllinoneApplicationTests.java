package wiki.dwx.allinone;

import cn.hutool.core.util.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        public Integer chno;
        public String ext;
        public String url;
        public String tvgid;
        public String tvgName;
        public String[] extList;

        public String extToString() {
            String s = "";
            for (int i = 0; i < extList.length; i++) {
                if (extList[i].contains("tvg-id=")) {
                    if (StringUtils.isNotBlank(tvgid)) {
                        s = s + "tvg-id=\"" + tvgid + "\" ";
                    }
                } else {
                    s = s + extList[i] + " ";
                }
            }
            return s.substring(0, s.length() - 1);
        }
    }

    @Test
    void egp2() throws IOException {
        HashMap<Integer, Iptv> end = new HashMap<>();

        List<String> iptvList = FileUtils.readLines(new ClassPathResource("/other/all(内网).m3u").getFile(), Charset.forName("utf-8"));
        for (int i = 1; i < iptvList.size(); i += 2) {
            String ext = iptvList.get(i);
            String url = iptvList.get(i + 1);

            Iptv iptv = new Iptv();
            iptv.ext = ext;
            iptv.url = url;
            iptv.extList = ext.split(" ");
            for (int j = 0; j < iptv.extList.length; j++) {
                if (iptv.extList[j].contains("tvg-name")) {
                    iptv.tvgName = iptv.extList[j]
                            .replace("高清", "")
                            .replace("tvg-name=", "")
                            .replace("\"", "")
                            .replace("CCTV-", "CCTV");
                    break;
                }
            }
//            for (int j = 0; j < iptv.extList.length; j++) {
//                if (iptv.extList[j].contains("tvg-id")) {
//                    iptv.tvgid = iptv.extList[j].replace("tvg-id=\"", "").replace("\"", "");
//                    break;
//                }
//            }
            for (int j = 0; j < iptv.extList.length; j++) {
                if (iptv.extList[j].contains("tvg-chno")) {
                    iptv.chno = Integer.valueOf(iptv.extList[j].replace("tvg-chno=\"", "").replace("\"", ""));
                    break;
                }
            }
            end.put(iptv.chno, iptv);
        }

        List<Integer> keyList = new ArrayList<>();
        keyList.addAll(end.keySet());
        Collections.sort(keyList);

        int find = 0;
        Document e = XmlUtil.readXML(new ClassPathResource("/other/e.xml").getFile());
        NodeList list = e.getElementsByTagName("channel");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            for (int j = 0; j < keyList.size(); j++) {
                Iptv iptv = end.get(keyList.get(j));
                if (iptv.tvgName.equalsIgnoreCase(node.getTextContent())) {
                    iptv.tvgid = node.getAttributes().getNamedItem("id").getNodeValue();
                    find++;
                    break;
                }
            }
        }


        System.out.println("匹配频道数:" + find);
        System.out.println(iptvList.get(0));
        for (int i = 0; i < keyList.size(); i++) {
            Iptv iptv = end.get(keyList.get(i));
            System.out.println(iptv.extToString());
            System.out.println(iptv.url);
        }

    }

    @Test
    void egp1() throws IOException {
        Document e = XmlUtil.readXML(new ClassPathResource("/other/e.xml").getFile());
        NodeList list = e.getElementsByTagName("channel");

        List<String> iptvList = FileUtils.readLines(new ClassPathResource("/other/all(内网).m3u").getFile(), Charset.forName("utf-8"));
        System.out.println(iptvList.get(0));
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String name = node.getTextContent();

            for (int j = 1; j < iptvList.size(); j += 2) {
                String ext = iptvList.get(j);
                String[] extList = ext.split(" ");
                String tvgName = "";
                int k = 0;
                for (; k < extList.length; k++) {
                    if (extList[k].contains("tvg-name")) {
                        tvgName = extList[k]
                                .replace("高清", "")
                                .replace("tvg-name=", "")
                                .replace("\"", "");
                        break;
                    }
                }
                if (tvgName.contains(name)) {
                    extList[k] = "tvg-name=\"" + tvgName + "\"";
                    for (int l = 0; l < extList.length; l++) {
                        System.out.print(extList[l]);
                        System.out.println();
                    }
                } else {
                    System.out.println(iptvList.get(j));
                }
                System.out.println(iptvList.get(j + 1));
            }
        }
    }

    @Test
    void egp() throws IOException {
        Document e = XmlUtil.readXML(new ClassPathResource("/other/e.xml").getFile());
        NodeList list = e.getElementsByTagName("channel");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String name = node.getTextContent();
            String id = node.getAttributes().getNamedItem("id").getNodeValue();
            System.out.println(id + " " + name);
        }
    }

    @Test
    void channel() throws IOException {
        List<String> list = FileUtils.readLines(new ClassPathResource("/other/all(内网).m3u").getFile(), Charset.forName("utf-8"));
        System.out.println(list.get(0));
        for (int i = 1; i < list.size(); i += 2) {
            String ext = list.get(i);
            System.out.println(ext);
            String url = list.get(i + 1);
            System.out.println(url);
        }
    }

    @Test
    void contextLoads() throws IOException {
        HashMap<Integer, Iptv> end = new HashMap<>();
        Pattern r = Pattern.compile("(\\d+)");

        List<String> list = FileUtils.readLines(new ClassPathResource("/other/all(内网).m3u").getFile(), Charset.forName("utf-8"));
        for (int i = 1; i < list.size(); i += 2) {
            String ext = list.get(i);
            Matcher m = r.matcher(ext.replace("-1", ""));
            m.find();
            Iptv iptv = new Iptv();
            iptv.ext = ext;
            iptv.url = list.get(i + 1);
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
            System.out.println(iptv.ext.replace("#EXTINF:-1", ext));
            System.out.println(iptv.url);
        }
    }
}
