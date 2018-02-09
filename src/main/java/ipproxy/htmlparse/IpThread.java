package ipproxy.htmlparse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 */
public class IpThread extends Thread {
    Logger logger = LoggerFactory.getLogger(IpThread.class);
    private List<String> urls;
    private IpPool ipPool;

    public IpThread(List<String> urls, IpPool ipPool) {
        this.urls = urls;
        this.ipPool = ipPool;
    }

    @Override
    public void run() {
        //进行ip的抓取
//        for (String url : urls) {
//            logger.info(Thread.currentThread().getName() + "爬取的地址为：" + url);
//        }
        ipPool.getIp(urls);
    }
}
