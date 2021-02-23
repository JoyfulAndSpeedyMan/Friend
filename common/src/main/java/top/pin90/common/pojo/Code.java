package top.pin90.common.pojo;

public class Code{
    public static final String OK="00000";    //一切OK
    public static final String CLIENT_ERROR="A0001"; // 用户端错误

    public static final String PARAM_ERROR="A0100"; // 参数错误
    public static final String OPERATION_ERROR="A0101"; //用户操作错误
    public static final String SMS_CODE_ERROR="A0131"; // 短信校验码错误

    public static final String USER_VERITY_EXPIRE="A0230"; //用户验证过期
    public static final String USER_VERITY_ERROR="A0220"; //用户身份校验失败

    public static final String USER_EALREADY_EXIST ="A0111"; //用户名已存在
    public static final String USER_NOT_EXIST="A0201"; // 用户账户不存在

    public static final String SERVER_EXE_ERROR="B0001"; //系统执行出错

}