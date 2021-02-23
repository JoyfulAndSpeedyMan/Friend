package top.pin90.common.unti.zookeeper;

import org.apache.curator.x.async.AsyncCuratorFramework;
import top.pin90.common.pojo.info.ChatServerInfo;

public class ChatServerZkUtils extends ZkUtils<ChatServerInfo> {
    public final String thisPath;
    public ChatServerZkUtils(AsyncCuratorFramework asyncClient, String thisPath) {
        super(asyncClient);
        this.thisPath = thisPath;
    }
    @Override
    public String getThisPath() {
        return thisPath;
    }
}
