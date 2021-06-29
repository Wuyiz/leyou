package com.leyou;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: suhai
 * @create: 2020-10-07 18:34
 */
@SpringBootApplication
@EnableDiscoveryClient
//mapperscan必须是通用mapper中的注解
@MapperScan("com.leyou.item.mapper")
public class LeyouItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class);
    }
}
