package top.pin90.server.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.CommonService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/common")
@Validated
public class CommonController {
    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("/pinyin/simple")
    public Mono<ResponseResult> getSimplePinyin(
            @RequestParam
            @Size(max = 50)
            @NotBlank String content) {
        return ResponseResult.monoOkString(commonService.getSimplePinyin(content));
    }

    @GetMapping("/pinyin/all")
    public Mono<ResponseResult> getAllPinyin(
            @RequestParam
            @Size(max = 50)
            @NotBlank String content) {
        return ResponseResult.monoOkString(commonService.getAllPinyin(content));
    }

    @GetMapping("/pinyin/groupName")
    public Mono<ResponseResult> groupName(
            @RequestParam
            @Size(max = 50)
            @NotBlank String content) {
        return ResponseResult.monoOkString(commonService.groupName(content));
    }
}
