package top.pin90.common.pojo;

import lombok.Data;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.Describe;

/**
 * http业务相应结果
 */
@Data
public class ResponseResult {
    @Describe("响应码")
    private String code;
    @Describe("提示消息")
    private String message;
    @Describe("返回的数据")
    private Object data;

    private ResponseResult(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseResult of(String code) {
        return new ResponseResult(code, null, null);
    }

    public static ResponseResult of(String code, String message) {
        return new ResponseResult(code, message, null);
    }

    public static ResponseResult of(String code, Object data) {
        return new ResponseResult(code, null, data);
    }

    public static ResponseResult ofString(String code, String data) {
        return new ResponseResult(code, null, data);
    }

    public static ResponseResult of(String code, String message, Object data) {
        return new ResponseResult(code, message, data);
    }

    public static ResponseResult ok() {
        return of(Code.OK);
    }

    public static ResponseResult ok(String message) {
        return of(Code.OK, message);
    }

    public static ResponseResult ok(Object data) {
        return of(Code.OK, null, data);
    }

    public static ResponseResult okString(String data) {
        return of(Code.OK, null, data);
    }

    public static ResponseResult ok(String message, Object data) {
        return of(Code.OK, message, data);
    }

    public static Mono<ResponseResult> toMono(String code, String message, Object data) {
        return Mono.fromSupplier(()->ResponseResult.of(code, message, data));
    }
    public static Mono<ResponseResult> toMono(String code, String message){
        return toMono(code, message,null);
    }

    public static Mono<ResponseResult> monoOk(String message){
        return toMono(Code.OK,message,null);
    }
    public static Mono<ResponseResult> monoOk(Object data){
        return toMono(Code.OK,null,data);
    }
    public static Mono<ResponseResult> monoOkString(String data){
        return toMono(Code.OK,null,data);
    }
    public static Mono<ResponseResult> monoOk(String message, Object data){
        return toMono(Code.OK,message,data);
    }

}
