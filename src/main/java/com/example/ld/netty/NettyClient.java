package com.example.ld.netty;

import com.example.ld.pojo.TaskInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HeYiyu
 * @date 2023/2/13
 */
@Slf4j
public class NettyClient {
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    @SneakyThrows
    public void init() {
        log.info("启动Netty客户端");
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new BaseEncoder());
                        ch.pipeline().addLast(new BaseDecoder());
                    }
                });
    }

    public void close() {
        log.info("关闭Netty客户端");
        group.shutdownGracefully();
    }

    /**
     * 1. 建立Netty连接
     * 2. 发送任务信息
     * 注：taskId为null代表建立直连测试
     *
     * @param taskInfo 任务信息
     * @return 建立的Netty连接
     */
    @SneakyThrows
    public Channel send(TaskInfo taskInfo) {
        String host = taskInfo.getServerHost();
        int port = taskInfo.getServerPort();
        log.debug("与{}建立Netty连接", host + ":" + port);
        Channel channel = bootstrap.connect(host, port).sync().channel();
        channel.pipeline().addLast(new NettyClientHandler(taskInfo));
        channel.writeAndFlush(taskInfo);
        return channel;
    }

    /**
     * 检测能否与目标节点建立Netty连接
     *
     * @param host 目标IP
     * @param port 目标端口
     */
    @SneakyThrows
    public void connect(String host, int port) {
        log.debug("尝试与{}建立连接", host + ":" + port);
        bootstrap.connect(host, port).sync().channel().close();
    }
}
