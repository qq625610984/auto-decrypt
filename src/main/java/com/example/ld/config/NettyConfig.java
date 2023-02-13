package com.example.ld.config;

import com.example.ld.netty.NettyClient;
import com.example.ld.netty.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HeYiyu
 * @date 2023/2/13
 */
@Configuration
public class NettyConfig {
    @Bean(initMethod = "init", destroyMethod = "close")
    public NettyServer nettyServer() {
        return new NettyServer();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public NettyClient nettyClient() {
        return new NettyClient();
    }
}
