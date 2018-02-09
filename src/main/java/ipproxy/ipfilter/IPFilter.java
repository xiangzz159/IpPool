package ipproxy.ipfilter;

import model.IpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 * 对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
 */
public class IPFilter {
    private static Logger logger = LoggerFactory.getLogger(IPFilter.class);

    // 对IP进行过滤
    public static List<IpMessage> Filter(List<IpMessage> ipMessages1) {
        List<IpMessage> newIpMessages = new ArrayList<IpMessage>();

        for (int i = 0; i < ipMessages1.size(); i++) {
            String ipType = ipMessages1.get(i).getIPType();
            String ipSpeed = ipMessages1.get(i).getIPSpeed();

            ipSpeed = ipSpeed.substring(0, ipSpeed.indexOf("秒"));
            double Speed = Double.parseDouble(ipSpeed);

            if (ipType.equals("HTTPS") && Speed <= 3.0) {
                newIpMessages.add(ipMessages1.get(i));
            }
        }
        return newIpMessages;
    }


}
