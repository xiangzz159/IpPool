package ipproxy.htmlparse;

import ipproxy.httpbrowser.MyHttpResponse;
import model.IpMessage;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 */
public class URLFecter {
    private static Logger logger = LoggerFactory.getLogger(URLFecter.class);

    //使用代理进行爬取
    public static boolean urlParse(String url, String ip, String port, List<IpMessage> ipMessage1) {
        //调用一个类使其返回html源码
        String html = null;
        if (StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(port)) {
            html = MyHttpResponse.getHtml(url, ip, port);
        } else {
            html = MyHttpResponse.getHtml(url);
        }

        if (html != null) {
            Document document = Jsoup.parse(html);
            Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");

            for (int i = 1; i < trs.size(); i++) {
                IpMessage ipMessage = new IpMessage();
                String ipAddress = trs.get(i).select("td").get(1).text();
                String ipPort = trs.get(i).select("td").get(2).text();
                String ipType = trs.get(i).select("td").get(5).text();
                String ipSpeed = trs.get(i).select("td").get(6).select("div[class=bar]")
                        .attr("title");

                ipMessage.setIPAddress(ipAddress);
                ipMessage.setIPPort(ipPort);
                ipMessage.setIPType(ipType);
                ipMessage.setIPSpeed(ipSpeed);

                ipMessage1.add(ipMessage);
            }
            return true;
        } else {
//            logger.info(ip+ ": " + port + " 代理不可用");
            return false;
        }
    }

    //使用本机IP爬取xici代理网站的第一页
    public static List<IpMessage> urlParse(List<IpMessage> ipMessages) {
        String url = "http://www.xicidaili.com/nn/1";
        String html = MyHttpResponse.getHtml(url);
        //将html解析成DOM结构
        Document document = Jsoup.parse(html);

        //提取所需要的数据
        Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");

        for (int i = 1; i < trs.size(); i++) {
            IpMessage ipMessage = new IpMessage();
            String ipAddress = trs.get(i).select("td").get(1).text();
            String ipPort = trs.get(i).select("td").get(2).text();
            String ipType = trs.get(i).select("td").get(5).text();
            String ipSpeed = trs.get(i).select("td").get(6).select("div[class=bar]").
                    attr("title");

            ipMessage.setIPAddress(ipAddress);
            ipMessage.setIPPort(ipPort);
            ipMessage.setIPType(ipType);
            ipMessage.setIPSpeed(ipSpeed);

            ipMessages.add(ipMessage);
        }
        return ipMessages;
    }
}