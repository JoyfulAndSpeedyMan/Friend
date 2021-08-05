package top.pin90.friend.chatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDubbo(scanBasePackages = "top.pin90.friend.chatserver.serviceImpl")
//@ImportResource("classpath:dubbo.xml")
public class ChatServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChatServerApplication.class, args);
    }

}
