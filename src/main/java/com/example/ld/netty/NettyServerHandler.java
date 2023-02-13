package com.example.ld.netty;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.example.ld.common.CommonConstant;
import com.example.ld.pojo.TaskInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Netty服务端业务处理类
 *
 * @author HeYiyu
 * @date 2023/2/13
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final byte[] data = new byte[CommonConstant.DATA_SLICE];
    private TaskInfo taskInfo;
    private File tempFile;
    private RandomAccessFile randomAccessFile;
    private int times;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (taskInfo == null) {
            // 首次接收到数据
            taskInfo = (TaskInfo) msg;
            log.debug("接收到任务数据：{}", taskInfo);
            tempFile = FileUtil.touch(taskInfo.getToPath());
            randomAccessFile = new RandomAccessFile(tempFile, "rw");
            ctx.channel().writeAndFlush(CommonConstant.ACK);
        } else if (msg instanceof byte[]) {
            // 接收数据，写到本地文件并返回确认信号
            randomAccessFile.write((byte[]) msg);
            if (randomAccessFile.length() == taskInfo.getTotalLength()) {
                randomAccessFile.close();
                tempFile = new File(taskInfo.getToPath());
                randomAccessFile = new RandomAccessFile(tempFile, "r");
                taskInfo.setTotalBackLength(tempFile.length());
                ctx.channel().writeAndFlush(taskInfo);
                return;
            }
            if (++times % CommonConstant.TRANSMIT_TIMES == 0) {
                ctx.channel().writeAndFlush(CommonConstant.ACK);
            }
        } else {
            // 读取文件进行解密，发回到请求方
            sendData(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Netty服务端关闭连接");
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        FileUtil.del(tempFile);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.channel().writeAndFlush(cause.getMessage());
    }

    /**
     * 发送数据
     */
    private void sendData(ChannelHandlerContext ctx) {
        for (int i = 0; i < CommonConstant.TRANSMIT_TIMES; i++) {
            ctx.channel().writeAndFlush(readFile());
            if (taskInfo.getSendBackLength() == taskInfo.getTotalBackLength()) {
                break;
            }
        }
    }

    /**
     * 读取文件到字节数组
     *
     * @return 文件字节数组
     */
    @SneakyThrows
    private byte[] readFile() {
        int sendLength = randomAccessFile.read(data);
        if (sendLength == -1) {
            sendLength = 0;
        }
        taskInfo.updateSendBackLength(sendLength);
        if (sendLength < CommonConstant.DATA_SLICE) {
            byte[] lastSlice = new byte[sendLength];
            ArrayUtil.copy(data, lastSlice, sendLength);
            return lastSlice;
        }
        return data;
    }
}
