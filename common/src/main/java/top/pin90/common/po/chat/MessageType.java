package top.pin90.common.po.chat;
/**
 * 消息的类型
 */
public interface MessageType {
    /**
     * 纯文本类型
     */
    public static final int TEXT=1;
    /**
     * 富文本类型
     */
    public static final int RICH_TEXT=2;
    /**
     * 图片类型
     */
    public static final int PHOTO=3;
    /**
     * 语音类型
     */
    public static final int VOICE=4;
    /**
     * 复合类型(富文本，图片)
     */
    public static final int COMPOUND=5;

    /**
     * 其他类型
     */
    public static final int OTHER=10;
}
