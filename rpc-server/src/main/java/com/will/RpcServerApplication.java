package com.will;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author clewill
 * @create 2022:04:11 19:06
 **/
@SpringBootApplication
public class RpcServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(RpcServerApplication.class, args);
  }
}
