package ipproxy.ipfilter;

import model.IpMessage;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 * 测试此IP是否有效
 */
public class IPUtils {
    private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static void IPIsable(List<IpMessage> ipMessages1) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        for (int i = 0; i < ipMessages1.size(); i++) {
            String ip = ipMessages1.get(i).getIPAddress();
            String port = ipMessages1.get(i).getIPPort();

            HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
            RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(5000).
                    setSocketTimeout(5000).build();
            HttpGet httpGet = new HttpGet("https://www.baidu.com");
            httpGet.setConfig(config);

            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                    "q=0.9,image/webp,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                    "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

            try {
                response = httpClient.execute(httpGet);
            } catch (IOException e) {
                ipMessages1.remove(ipMessages1.get(i));
                i--;
            }
        }

        try {
            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static IpMessage IpMessageIsable(IpMessage ipMessages) {
        if (ipMessages == null) {
            return null;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        String ip = ipMessages.getIPAddress();
        String port = ipMessages.getIPPort();

        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(5000).
                setSocketTimeout(5000).build();
        HttpGet httpGet = new HttpGet("https://www.baidu.com");
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        try {
            response = httpClient.execute(httpGet);
//            logger.info("【使用代理】IpAddress:{}, port:{}", ipMessages.getIPAddress(), ipMessages.getIPPort());
        } catch (IOException e) {
//            logger.info("代理已失效");
            ipMessages = null;
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ipMessages;
        }
    }
}
