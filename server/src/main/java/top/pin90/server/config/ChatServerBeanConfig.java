package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.server.chat.ChatServer;

@Configuration
public class ChatServerBeanConfig {
    final ChatServerConfig chatServerConfig;

    public ChatServerBeanConfig(ChatServerConfig chatServerConfig) {
        this.chatServerConfig = chatServerConfig;
    }

    @Bean
    public ChatServer chatServer(@Value("${os.name}") String osName){
        return new ChatServer(chatServerConfig.getPort(), osName);
    }
}
