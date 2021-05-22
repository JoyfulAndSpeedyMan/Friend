package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.UserFriendRelationService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/user/friend")
public class UserFriendRelationController {
    private final UserFriendRelationService friendRelationService;

    public UserFriendRelationController(UserFriendRelationService friendRelationService) {
        this.friendRelationService = friendRelationService;
    }

    @GetMapping("")
    Mono<ResponseResult> getAllFriend(@Token ObjectId userId){
        return friendRelationService.getAllFriend(userId);
    }
    @GetMapping("preAdd")
    Mono<ResponseResult> preAddFriend(@RequestParam ObjectId friendId,
                                      @Token ObjectId userId){
        return friendRelationService.preAddFriend(friendId, userId);

    }

    @GetMapping("request")
    public Mono<ResponseResult> getFriendRequest(@Token ObjectId userId){
        return friendRelationService.getFriendRequest(userId);
    }

    @PutMapping("request")
    Mono<ResponseResult> addFriend(ObjectId friendId,String content,@Token ObjectId userId){
        return friendRelationService.addFriend(friendId, content, userId);
    }
    @PutMapping("accept")
    Mono<ResponseResult> acceptFriend(ObjectId friendId,@Token ObjectId userId){
        return friendRelationService.acceptFriend(friendId, userId);
    }
    @PutMapping("reject")
    Mono<ResponseResult> rejectFriend(ObjectId friendId,String content,@Token ObjectId userId){
        return friendRelationService.rejectFriend(friendId, content, userId);

    }
    @PutMapping("blacklist")
    Mono<ResponseResult> blacklistFriend(ObjectId friendId,@Token ObjectId userId){
        return friendRelationService.blacklistFriend(friendId, userId);

    }
    @DeleteMapping("blacklist")
    public Mono<ResponseResult> cancelBlacklistFriend(ObjectId friendId,@Token ObjectId userId){
        return friendRelationService.cancelBlacklistFriend(friendId, userId);

    }
    @DeleteMapping()
    Mono<ResponseResult> deleteFriend(ObjectId friendId,@Token ObjectId userId){
        return friendRelationService.deleteFriend(friendId, userId);

    }

    @GetMapping("/findByPhone")
    Mono<ResponseResult> findUserByPhone(
            @RequestParam
            @NotBlank(message = "不能为空")
            @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$",
                    message = "格式错误")
            String phone,
            @Token ObjectId userId){
        return friendRelationService.findUserByPhone(phone, userId);
    }

    @GetMapping("/friendInfo")
    Mono<ResponseResult> getFriendInfo(@Token ObjectId userId,
                                       @RequestParam ObjectId friendId){
        return friendRelationService.getFriendInfo(userId, friendId);
    }

}
