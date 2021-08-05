package top.pin90.friend.chatserver.server;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeData {
    /**
     * 用户ID和Channel的映射关系
     */
    public final static Map<Channel, ObjectId> channelMapId = new ConcurrentHashMap<>();
    public final static Map<ObjectId, UserInfoCache> idMapChannel = new ConcurrentHashMap<>();

    public static UserInfoCache buildCache(ObjectId id, String phone, Channel channel) {
        return new UserInfoCache(id, phone, channel);
    }

    public static ObjectId getUserId(Channel channel) {
        return channelMapId.get(channel);
    }


    public static UserInfoCache getCacheInfo(ObjectId userId) {
        return idMapChannel.get(userId);
    }

    public static UserInfoCache getCacheInfo(Channel channel) {
        return idMapChannel.get(getUserId(channel));
    }

    public static Channel getChannel(ObjectId userId) {
        UserInfoCache cacheInfo = getCacheInfo(userId);
        if(cacheInfo!=null)
            return cacheInfo.getChannel();
        return null;
    }

    public static void logout(Channel channel) {
        if(channelMapId.containsKey(channel)) {
            ObjectId id = channelMapId.remove(channel);
            idMapChannel.remove(id);
        }
    }

    public static void put(Channel channel, ObjectId userId) {
        channelMapId.put(channel, userId);
    }

    public static void put(ObjectId userId, UserInfoCache cache) {
        idMapChannel.put(userId, cache);
    }

    @Data
    @AllArgsConstructor
    public static class UserInfoCache {
        private ObjectId id;
        private String phone;
        private Channel channel;
    }
}
