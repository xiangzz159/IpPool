package timer;

import Cache.RedisCachePro;
import com.alibaba.fastjson.JSONObject;
import ipproxy.htmlparse.IpPool;
import ipproxy.htmlparse.IpThread;
import ipproxy.htmlparse.URLFecter;
import ipproxy.ipfilter.IPFilter;
import ipproxy.ipfilter.IPUtils;
import model.IpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 * <p>
 * ip代理池里面最少保存200个代理ip
 * <p>
 * 多线程主要考虑的就是合理的任务分配以及线程安全性。
 */
@Service
public class IpProxySpiderTask {
    private Logger logger = LoggerFactory.getLogger(IpProxySpiderTask.class);

    @Autowired
    private RedisCachePro redisCachePro;

    private static final String CacheName = "IPPool";

    public IpMessage getIPByList(List<IpMessage> list) {
        if (list.size() <= 0) {
            return null;
        }
        int rand = (int) (Math.random() * list.size());
        IpMessage ipMessage = list.get(rand);
        return ipMessage;

    }

    public void run() {
        logger.info("开始IP代理抓取");
        //存放爬取下来的ip信息
        List<IpMessage> ipMessages = new ArrayList<IpMessage>();
        List<String> urls = new ArrayList<String>();
        //对创建的子线程进行收集
        List<Thread> threads = new ArrayList<Thread>();

        //首先使用本机ip爬取xici代理网第一页
        ipMessages = URLFecter.urlParse(ipMessages);

        //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
        ipMessages = IPFilter.Filter(ipMessages);

        //对拿到的ip进行质量检测，将质量不合格的ip在List里进行删除
        IPUtils.IPIsable(ipMessages);
        logger.info("爬取IP代理页：{}, 更新了{}个代理", "http://www.xicidaili.com/nn/1", ipMessages.size());

        //构造种子url(4000条ip)
        for (int i = 2; i <= 41; i++) {
            urls.add("http://www.xicidaili.com/nn/" + i);
        }
         /* 对urls进行解析并进行过滤,拿到所有目标IP(使用多线程)
          基本思路是给每个线程分配自己的任务，在这个过程中List<IPMessage> ipMessages
          应该是共享变量，每个线程更新其中数据的时候应该注意线程安全*/
        IpPool ipPool = new IpPool(ipMessages);
        for (int i = 0; i < 5; i++) {
            //给每个线程进行任务的分配
            Thread IPThread = new IpThread(urls.subList(i * 8, i * 8 + 8), ipPool);
            threads.add(IPThread);
            IPThread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ipMessages.size() > 0) {
            redisCachePro.del(CacheName);
            redisCachePro.set(CacheName, JSONObject.toJSON(ipMessages));
        }
        logger.info("结束IP代理抓取, 更新了{}个IP代理", ipMessages.size());
        System.gc();
    }
}
