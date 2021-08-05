package top.pin90.friend.routeserver.dubbo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.info.ServerInfo;
import top.pin90.common.unti.DubboUtils;
import top.pin90.friend.chatserver.api.service.MessageService;
import top.pin90.friend.routeserver.api.service.ClusterService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 写错了，暂时无用
 */
@Slf4j
//@DubboService(version = "0.0.1")
@PropertySource("classpath:route-server.xml")
@Order
public class ClusterServiceImpl implements ClusterService {
    private Map<Class<?>, List<ServerInfo>> infoMap;
    private Map<Class<?>, ServerSelector> serverSelectorMap;
    private ServerSelector defaultServerSelector;
    private CuratorFramework client;
    @Value("${zookeeper.address}")
    private String connectionString;
    private final Class<?> chatServerInterface = MessageService.class;

    public ClusterServiceImpl() {
    }

    public ClusterServiceImpl(ServerSelector defaultServerSelector) {
        this.defaultServerSelector = defaultServerSelector;
    }

    @PostConstruct
    public void init() {
        if (defaultServerSelector == null)
            defaultServerSelector = new RandomServerSelector();
        infoMap = new ConcurrentHashMap<>();
        serverSelectorMap = new ConcurrentHashMap<>();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(6000)
                // etc. etc.
                .build();
        client.start();
    }

    @PreDestroy
    public void destroy() {
        if (client != null)
            client.close();
    }

    class UpdateWatcher implements Watcher {
        private final String path;
        private final Class<?> interface0;

        public UpdateWatcher(String path, Class<?> interface0) {
            this.path = path;
            this.interface0 = interface0;
        }

        @Override
        public void process(WatchedEvent event) {
            try {
                List<String> strings = client.getChildren().usingWatcher(this).forPath(path);
                List<ServerInfo> infos = DubboUtils.parseInfo(strings);
                infoMap.put(interface0, infos);
                log.debug("update chat server list to : {}", infos.toString());
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public interface ServerSelector {
        ServerInfo select(List<ServerInfo> infos);
    }

    public static class RandomServerSelector implements ServerSelector {
        private final Random rand;

        public RandomServerSelector() {
            this(new Random());
        }

        public RandomServerSelector(Random rand) {
            this.rand = rand;
        }

        @Override
        public ServerInfo select(List<ServerInfo> infos) {
            return infos.get(rand.nextInt(infos.size()));
        }
    }

    @Override
    public CompletableFuture<List<ServerInfo>> getAllChatServer() {
        return getAllServer(chatServerInterface);
    }

    @Override
    public CompletableFuture<List<ServerInfo>> getAllServer(Class<?> interface0) {
        if (!infoMap.containsKey(interface0)) {
            return Mono.<List<ServerInfo>>create(monoSink -> {
                try {
                    String className = interface0.getName();
                    String path = "/dubbo/" + className + "/providers";
                    List<String> childrenInfos = client
                            .getChildren()
                            .usingWatcher(new UpdateWatcher(path, interface0))
                            .forPath(path);
                    List<ServerInfo> serverInfos = DubboUtils.parseInfo(childrenInfos);
                    monoSink.success(serverInfos);

                } catch (Exception e) {
                    monoSink.error(e);
                }
            })
                    .map(list -> {
                        infoMap.put(interface0, list);
                        return list;
                    })
                    .toFuture();
        }
        return CompletableFuture.completedFuture(infoMap.get(interface0));
    }

    @Override
    public CompletableFuture<ServerInfo> getChatServer() {
        return getServer(chatServerInterface);
    }

    @Override
    public CompletableFuture<ServerInfo> getServer(Class<?> interface0) {
        ServerSelector selector = serverSelectorMap.getOrDefault(interface0, defaultServerSelector);
        return Mono.fromFuture(getAllServer(interface0))
                .map(selector::select)
                .toFuture();
    }
}
