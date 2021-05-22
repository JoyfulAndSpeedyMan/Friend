package top.pin90.friend.chatserver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private int type;
    private String content;
}
