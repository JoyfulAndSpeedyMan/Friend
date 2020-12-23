package top.pin90.server.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.FormData;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.UserRepository;
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
    public Mono<ResponseResult> login(@NotBlank(message = "不能为空")
                                         @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$",
                                                 message = "格式错误")
                                                 String phone,
                                         @NotBlank(message = "不能为空")
                                         @Size(min = 6, max = 6, message = "格式错误")
                                                 String code) {

        return userService.smsCodeLogin(phone, code);
    }

    @GetMapping("/sendLoginCode")
    public Mono<ResponseResult> sendRegisterCode(
            @NotBlank(message = "不能为空")
            @Pattern(regexp = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$",
                    message = "格式错误")
                    String phone) {
        return userService.sendLoginSmsCode(phone);
    }
    @GetMapping("baseInfo")
    public Mono<ResponseResult> getBaseInfoById(@Token String userId){
        return userService.getUserBaseInfo(userId);
    }




    @PutMapping("/testFormData")
    @FormData
    public Mono<ResponseResult> testFormData(@NotBlank String s1, @NotBlank String s2){
        return Mono.fromSupplier(()->{
            return ResponseResult.ok(s1+" "+s2);
        });
    }

/*
    @PostMapping("/testRegister")
    @Validated
    public Mono<ResponseResult> testRegister(Mono<User> user,@NotNull String s){
        return user
                .map(ResponseResult::ok)
                .onErrorResume(Exception.class, e -> Mono.just(ResponseResult.of("testRegister 失败")));
    }

    @PostMapping("/testRegister0")
    public Mono<ResponseResult> testRegister0(@Valid Mono<User> user){
        return user
                .map(ResponseResult::ok)
                .onErrorResume(Exception.class, e -> Mono.just(ResponseResult.of("testRegister0 失败")));
    }
    @PostMapping("/testRegister00")
    public ResponseResult testRegister00(@Valid User user){
        return ResponseResult.ok(user);
    }

    @PostMapping("/testRegister2")
    public Mono<ResponseResult> testRegister2(@NotNull Mono<String> s1,@NotNull Mono<String> s2){
        final LocalValidatorFactoryBean bean = SpringBeanFactory.getBean(LocalValidatorFactoryBean.class);
        return s1
                .zipWith(s2,(ss1,ss2)->ss1+" "+ss2)
                .map(ResponseResult::ok)
                .onErrorResume(Exception.class, e -> Mono.just(ResponseResult.ok()));
    }

    @GetMapping("/testRegister3")
    public Mono<ResponseResult> testRegister3(@Valid @NotNull String s1,@Valid @NotNull String s2){
        return Mono.just(s1+" "+s2)
                .map(ResponseResult::ok)
                .onErrorResume(Exception.class, e -> Mono.just(ResponseResult.of("testRegister3 失败")));
    }

*/


}
