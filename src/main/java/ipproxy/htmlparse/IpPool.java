package ipproxy.htmlparse;

import ipproxy.ipfilter.IPFilter;
import ipproxy.ipfilter.IPUtils;
import model.IpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 */
public class IpPool {
    Logger logger = LoggerFactory.getLogger(IpPool.class);
    private List<IpMessage> ipMessages;

    public IpPool(List<IpMessage> ipMessages) {
        this.ipMessages = ipMessages;
    }

    public void getIp(List<String> urls) {
        String ipAddress;
        String ipPort;
        int times = 0;
        for (int i = 0; i < urls.size(); i++) {
            /** 随机挑选代理IP(仔细想了想，本步骤由于其他线程有可能在位置确定之后对ipMessages数量进行
             * 增加，虽说不会改变已经选择的ip代理的位置，但合情合理还是在对共享变量进行读写的时候要保证
             * 其原子性，否则极易发生脏读)
             *
             * 每个线程先将自己抓取下来的ip保存下来并进行过滤与检测
             */
            List<IpMessage> ipMessages1 = new ArrayList<IpMessage>();
            String url = urls.get(i);
            synchronized (ipMessages) {
                int rand = (int) (Math.random() * ipMessages.size());
                /*logger.info("当前线程：" + Thread.currentThread().getName() + " rand值：" + rand +
                " ipMessages 大小:" + ipMessages.size());*/
                ipAddress = ipMessages.get(rand).getIPAddress();
                ipPort = ipMessages.get(rand).getIPPort();
            }
            boolean status = URLFecter.urlParse(url, ipAddress, ipPort, ipMessages1);
            //如果ip代理池里面的ip不能用，测试三次,不通过则跳过
            if (status == false && times++ < 10) {
                i--;
                continue;
            }
            // 若还是不行使用本服务器爬取
            if (!status) {
                status = URLFecter.urlParse(url, null, null,ipMessages1);
            }
            if (status) {
                //对ip重新进行过滤，只要速度在两秒以内的并且类型为HTTPS的
                ipMessages1 = IPFilter.Filter(ipMessages1);
                //对ip进行质量检测，将质量不合格的ip在List里进行删除
                IPUtils.IPIsable(ipMessages1);
                //将质量合格的ip合并到共享变量ipMessages中，进行合并的时候保证原子性
                synchronized (ipMessages) {
                    logger.info("线程" + Thread.currentThread().getName() + "已进入合并区 " +
                            "待合并大小 ipMessages.size：" + ipMessages1.size());
                    ipMessages.addAll(ipMessages1);
                    times = 0;
                }
            }
        }

    }
}
