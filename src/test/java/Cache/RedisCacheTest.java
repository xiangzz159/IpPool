package Cache;

import com.alibaba.fastjson.JSONObject;
import model.IpMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 */
@RunWith(SpringJUnit4ClassRunner.class) // 表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:/spring/spring-jedis.xml" })
public class RedisCacheTest {
    @Autowired
    private RedisCachePro cachePro;

    private String CacheName = "IPPool";
    @Test
    public void putListIntoCache() {
        List<IpMessage> list = new ArrayList<IpMessage>();
        IpMessage ipMessage = new IpMessage();
        ipMessage.setIPSpeed("2.15秒");
        ipMessage.setIPType("HTTP");
        ipMessage.setIPPort("80");
        ipMessage.setIPAddress("127.0.0.1");
        list.add(ipMessage);
        list.add(ipMessage);
        list.add(ipMessage);
        list.add(ipMessage);
        list.add(ipMessage);
        cachePro.set(CacheName, JSONObject.toJSON(list));

    }

    @Test
    public void getListFromCache() {
        List<IpMessage> list = new ArrayList<IpMessage>();
        list = JSONObject.parseArray(cachePro.get(CacheName).toString(), IpMessage.class);
        for (IpMessage str : list) {
            System.out.println(str.toString());
        }

    }
}
