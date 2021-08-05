package top.pin90.friend.routeserver.api.service;

import top.pin90.common.pojo.info.ServerInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ClusterService {
    CompletableFuture<List<ServerInfo>> getAllChatServer();
    CompletableFuture<List<ServerInfo>> getAllServer(Class<?> interface0);

    CompletableFuture<ServerInfo> getChatServer();
    CompletableFuture<ServerInfo> getServer(Class<?> interface0);

}
