package top.pin90.friend.routeserver.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableDubbo(scanBasePackages = "top.pin90.friend.routeserver.dubbo.service.impl")
@ImportResource("classpath:route-server.xml")
public class DubboConfig {
}
