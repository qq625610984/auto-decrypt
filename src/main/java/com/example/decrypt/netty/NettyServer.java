package com.example.decrypt.netty;

import com.example.decrypt.config.CustomConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author HeYiyu
 * @date 2023/2/13
 */
@Slf4j
public class NettyServer {
    @Resource
    private CustomConfig customConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @SneakyThrows
    public void init() {
        log.info("启动Netty服务端");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(customConfig.getNettyPort())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new BaseEncoder());
                        ch.pipeline().addLast(new BaseDecoder());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
        serverBootstrap.bind().sync();
    }

    public void close() {
        log.info("关闭Netty服务端");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
