package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.FormData;
import top.pin90.common.annotation.Token;
import top.pin90.common.po.user.User;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.server.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    final UserRepository userRepository;
    final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/all")
    public Mono<ResponseResult> findAllUser() {
        return userRepository.findAll()
                .collect(Collectors.toList())
                .map(ResponseResult::ok);
    }


    @PutMapping("/login")
    public Mono<ResponseResult> login(
                                        @NotBlank(message = "不能为空")
                                         @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$",
                                                 message = "格式错误")
            String phone,
                                         @NotBlank(message = "不能为空")
                                         @Size(min = 6, max = 6, message = "格式错误")
            String code) {
        System.out.println("login:\t"+Thread.currentThread().getName());
        return userService.smsCodeLogin(phone, code);
    }


    @GetMapping("/sendLoginCode")
    public Mono<ResponseResult> sendRegisterCode(
            @RequestParam
            @NotBlank(message = "不能为空")
            @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$",
                    message = "格式错误")
                    String phone) {
        return userService.sendLoginSmsCode(phone);
    }
    @GetMapping("baseInfo")
    public Mono<ResponseResult> getBaseInfoById(@Token ObjectId userId){
        return userService.getUserBaseInfo(userId);
    }

    @GetMapping("baseInfo2")
    public Mono<ResponseResult> getBaseInfoByFriendId(@RequestParam ObjectId fid,@Token ObjectId userId){
        return userService.getUserBaseInfo(fid);
    }

    @GetMapping("/refreshToken")
    public Mono<ResponseResult> refreshToken(String refreshToken){
        return userService.refreshToken(refreshToken);
    }

    @PutMapping("/testFormData")
    @FormData
    public Mono<ResponseResult> testFormData(@NotBlank String s1, @NotBlank String s2){
        return Mono.fromSupplier(()->{
            return ResponseResult.ok(s1+" "+s2);
        });
    }
    @PostMapping("/test")
    public Mono<String> testModelAttribute(@ModelAttribute Mono<Person> personMono){
        return personMono.map(Person::toString);
    }

    @PutMapping
    public Mono<ResponseResult> updateUserInfo(@Token ObjectId userId,@RequestBody User user){
        user.setId(userId);
        return userService.updateUserInfo(user);
    }

}
