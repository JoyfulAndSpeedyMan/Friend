package top.pin90.server.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface UserFriendRelationService {
    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    Mono<ResponseResult> getAllFriend(ObjectId userId);

    /**
     * 添加好友之前，获取添加好友所需要的信息
     * @param friendId 要添加好友的Id
     * @param userId 用户的Id
     * @return
     */
    Mono<ResponseResult> preAddFriend(ObjectId friendId, ObjectId userId);

    /**
     * 发送好友请求
     * @param friendId 要添加好友的Id
     * @param content 验证消息的内容
     * @param userId 用户的Id
     * @return
     */
    Mono<ResponseResult> addFriend(ObjectId friendId,String content,ObjectId userId);

    Mono<ResponseResult> getFriendRequest(ObjectId userId);
    /**
     * 通过好友请求
     * @param friendId 好友的Id
     * @param userId 用户Id
     * @return
     */
    Mono<ResponseResult> acceptFriend(ObjectId friendId,ObjectId userId);

    /**
     * 拒绝好友请求
     * @param friendId 好友的Id
     * @param userId 用户Id
     * @return
     */
    Mono<ResponseResult> rejectFriend(ObjectId friendId,String content,ObjectId userId);

    /**
     * 拉黑好友
     * @param friendId 好友的Id
     * @param userId 用户Id
     * @return
     */
    Mono<ResponseResult> blacklistFriend(ObjectId friendId,ObjectId userId);
    /**
     * 取消拉黑好友
     * @param friendId 好友的Id
     * @param userId 用户Id
     * @return
     */
    Mono<ResponseResult> cancelBlacklistFriend(ObjectId friendId,ObjectId userId);

    /**
     * 删除好友
     * @param friendId 好友的Id
     * @param userId 用户Id
     * @return
     */
    Mono<ResponseResult> deleteFriend(ObjectId friendId,ObjectId userId);

    /**
     * 根据手机号查找用户
     * @param phone
     * @param userId
     * @return
     */
    Mono<ResponseResult> findUserByPhone(String phone,ObjectId userId);

    Mono<ResponseResult> getFriendInfo(ObjectId userId ,ObjectId friendId);

}
