package top.pin90.friend.chatserver.config.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.friend.chatserver.config.server.ChatServerBeanConfig;
import top.pin90.common.unti.zookeeper.ChatServerZkUtils;

@Configuration
public class ZookeeperBeanConfig {
    @Bean
    @Autowired
    public CuratorFramework curatorFramework(ZookeeperProperties properties){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client;
        client = CuratorFrameworkFactory.builder()
                .connectString(properties.getConnectString())
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(15000)
                .sessionTimeoutMs(6000)
                // etc. etc.
                .build();
        client.start();
        return client;
    }
    @Bean
    @Autowired
    public AsyncCuratorFramework asyncCuratorFramework(CuratorFramework client){
        return AsyncCuratorFramework.wrap(client);
    }
    @Bean
    @Autowired
    public ChatServerZkUtils chatServerZkUtils(AsyncCuratorFramework asyncCuratorFramework, ChatServerBeanConfig chatServerBeanConfig){
        String thisPath="/"+chatServerBeanConfig.getHost()+":"+chatServerBeanConfig.getPort();
        return new ChatServerZkUtils(asyncCuratorFramework,"/chat-server"+thisPath);
    }


}
