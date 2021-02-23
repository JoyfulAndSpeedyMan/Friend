package top.pin90.server.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.ChatServerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
@Service
public class ChatServerServiceImpl implements ChatServerService {
    private final ReactiveHashOperations<String, String, Integer> opsForHash;
    private ServerSelector serverSelector;
    public ChatServerServiceImpl(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate redisTemplate) {
        this.opsForHash = redisTemplate.opsForHash();
        this.serverSelector=new RandomServerSelector();

    }

    public interface ServerSelector {
        Map.Entry<String,Integer> select(List<Map.Entry<String,Integer>> infos) ;
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
        public Map.Entry<String,Integer> select(List<Map.Entry<String,Integer>> infos) {
            return infos.get(rand.nextInt(infos.size()));
        }
    }
    @Override
    public Mono<ResponseResult> getServer() {
        return opsForHash.entries("chat-server")
                .collectList()
                .defaultIfEmpty(new ArrayList<>())
                .map(list->{
                    if(list.isEmpty())
                        return ResponseResult.of(Code.SERVER_EXE_ERROR,"no chat server available");
                    return ResponseResult.ok(serverSelector.select(list));
                });
    }

    @Override
    public Mono<ResponseResult> getAllServer() {
        return opsForHash.entries("chat-server")
                .collectList()
                .flatMap(ResponseResult::monoOk);
    }
}
