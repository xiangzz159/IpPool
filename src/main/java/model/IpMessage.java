package model;

import java.io.Serializable;

/**
 * Created by xgy <515357706@qq.com> on 2018/2/9.
 */
public class IpMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String IPAddress;
    private String IPPort;
    private String IPType;
    private String IPSpeed;

    public IpMessage() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getIPPort() {
        return IPPort;
    }

    public void setIPPort(String IPPort) {
        this.IPPort = IPPort;
    }

    public String getIPType() {
        return IPType;
    }

    public void setIPType(String IPType) {
        this.IPType = IPType;
    }

    public String getIPSpeed() {
        return IPSpeed;
    }

    public void setIPSpeed(String IPSpeed) {
        this.IPSpeed = IPSpeed;
    }

    @Override
    public String toString() {
        return "IpMessage{" +
                "IPAddress='" + IPAddress + '\'' +
                ", IPPort='" + IPPort + '\'' +
                ", IPType='" + IPType + '\'' +
                ", IPSpeed='" + IPSpeed + '\'' +
                '}';
    }
}
