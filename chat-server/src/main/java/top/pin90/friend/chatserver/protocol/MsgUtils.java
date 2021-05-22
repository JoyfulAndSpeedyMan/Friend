package top.pin90.friend.chatserver.protocol;

import top.pin90.friend.chatserver.protocol.res.WsResCode;

import java.util.function.Supplier;

public class MsgUtils {
    public static WSMsg.Msg.Builder otherOps() {
        return WSMsg.Msg.newBuilder()
                .setCode(WsOps.OTHER);
    }

    public static WSMsg.Msg.Builder okCode() {
        return otherOps().setCode(WsCode.OK);
    }

    public static Supplier<WSMsg.Msg> serverErrorSupplier() {
        return () ->
                WSMsg.Msg.newBuilder()
                        .setCode(WsResCode.SERVER_EXE_ERROR)
                        .setMsg("服务器错误")
                        .build();
    }

    public static WSMsg.Msg serverError(){
        return                 WSMsg.Msg.newBuilder()
                .setCode(WsResCode.SERVER_EXE_ERROR)
                .setMsg("服务器错误")
                .build();
    }

}
