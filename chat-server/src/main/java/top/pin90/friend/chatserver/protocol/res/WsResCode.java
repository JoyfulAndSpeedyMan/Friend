package top.pin90.friend.chatserver.protocol.res;

import top.pin90.friend.chatserver.protocol.WsCode;

public class WsResCode extends WsCode {
    public static final int CLIENT_ERROR=10; // 用户端错误

    public static final int PARAM_ERROR=30; // 参数错误
    public static final int OPERATION_ERROR=31; //用户操作错误
    public static final int SMS_CODE_ERROR=32; // 短信校验码错误

    public static final int USER_VERITY_EXPIRE=50; //用户验证过期
    public static final int USER_VERITY_ERROR=51; //用户身份校验失败

    public static final int USER_EALREADY_EXIST =70; //用户名已存在
    public static final int USER_NOT_EXIST=71; // 用户账户不存在

    public static final int SERVER_EXE_ERROR=500; //系统执行出错
}
