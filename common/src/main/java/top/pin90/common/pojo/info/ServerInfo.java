package top.pin90.common.pojo.info;

import lombok.Data;

@Data

public class ServerInfo {
    public final String host;
    public final int port;

    @Override
    public String toString() {
        return "ServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
