package top.pin90.common.unti.zookeeper;

import com.alibaba.fastjson.JSON;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.api.CreateOption;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.unti.Sets;

import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public abstract class ZkUtils<T> {
    private final AsyncCuratorFramework asyncClient;
    private final Charset defaultCharset = StandardCharsets.UTF_8;

    public ZkUtils(AsyncCuratorFramework asyncClient) {
        this.asyncClient = asyncClient;
    }

    public Mono<String> register(String path, T info) {
        String jsonString = JSON.toJSONString(info);
        return Mono.create(monoSink -> {
            Set<CreateOption> options = Sets.of(CreateOption.createParentsIfNeeded, CreateOption.setDataIfExists);
            asyncClient
                    .create()
                    .withOptions(options, CreateMode.EPHEMERAL)
                    .forPath(path, jsonString.getBytes(defaultCharset))
                    .whenComplete((name, exception) -> {
                        if (exception != null) {
                            monoSink.error(exception);
                        } else {
                            monoSink.success(name);
                        }
                    });
        });
    }

    public Mono<String> register(T info){
        return register(getThisPath(),info);
    }

    public Mono<Stat> update(String path, T info) {
        String jsonString = JSON.toJSONString(info);
        return Mono.create(monoSink -> {
            asyncClient
                    .setData()
                    .forPath(path, jsonString.getBytes(defaultCharset))
                    .whenComplete((stat, throwable) -> {
                        if (throwable != null) {
                            monoSink.error(throwable);
                        } else {
                            monoSink.success(stat);
                        }
                    });
        });
    }

    public Mono<Stat> update(T info){
        return update(getThisPath(),info);
    }

    public Mono<String> getRawData(String path) {
        return Mono.create(monoSink -> {
            asyncClient
                    .getData()
                    .forPath(path)
                    .whenComplete(((bytes, throwable) -> {
                        if (throwable == null)
                            monoSink.success(new String(bytes, defaultCharset));
                        else
                            monoSink.error(throwable);
                    }));
        });
    }

    public Mono<T> getData(String path) {
        return getRawData(path)
                .map(s->JSON.parseObject(s,getTClass()));
    }

    public Mono<T> getData(){
        return getData(getThisPath());
    }
    public Flux<String> getRawChildren(String path) {
        return Flux.create(fluxSink -> {
            asyncClient
                    .getChildren()
                    .forPath(path)
                    .whenComplete((strings, throwable) -> {
                        if (throwable == null) {
                            strings.forEach(fluxSink::next);
                            fluxSink.complete();
                        } else {
                            fluxSink.error(throwable);
                        }
                    });
        });
    }

    public Mono<List<T>> getChildren(String path) {
        return getRawChildren(path)
                .map(s->JSON.parseObject(s,getTClass()))
                .collectList();
    }

    public Mono<Boolean> hasNode(String path) {
        return Mono.create(monoSink -> {
            asyncClient
                    .checkExists()
                    .forPath(path)
                    .whenComplete((stat, throwable) -> {
                        if (throwable == null)
                            monoSink.success(true);
                        else
                            monoSink.error(throwable);

                    });

        });
    }

    protected Class<T> getTClass(){
        @SuppressWarnings("unchecked")
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    public abstract String getThisPath();
}
