package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.UserFriendRelationService;

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
    public Mono<ResponseResult> getFriendRequest(@Token ObjectId userId,
                                                 @RequestParam int page,
                                                 @RequestParam int size){
        return friendRelationService.getFriendRequest(userId, page, size);
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


}
