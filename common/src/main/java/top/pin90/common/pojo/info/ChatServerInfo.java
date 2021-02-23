package top.pin90.common.pojo.info;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ChatServerInfo extends ServerInfo {

    /**
     * 在线人数
     */
    private final AtomicInteger onlineNum=new AtomicInteger();
    /**
     * websocket 路径
     */
    private String wsPath;
    public ChatServerInfo(String host, int port, String wsPath) {
        super(host, port);
        this.wsPath = wsPath;
    }


}
