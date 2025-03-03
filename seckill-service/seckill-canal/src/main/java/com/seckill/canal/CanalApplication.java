package com.seckill.canal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author http://www.itheima.com
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.seckill.search.feign","com.seckill.page.feign"})
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}
