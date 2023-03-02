package com.example.decrypt.netty;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.decrypt.common.CommonConstant;
import com.example.decrypt.common.exception.CommonException;
import com.example.decrypt.pojo.TaskInfo;
import com.example.decrypt.service.FileService;
import com.example.decrypt.service.TaskService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Netty客户端业务处理类
 *
 * @author HeYiyu
 * @date 2023/2/13
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final TaskService taskService = SpringUtil.getBean(TaskService.class);
    private final byte[] data = new byte[CommonConstant.DATA_SLICE];
    private final TaskInfo taskInfo;
    private final File file;
    private File tempFile;
    private RandomAccessFile randomAccessFile;
    private int times;

    @SneakyThrows
    public NettyClientHandler(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
        file = new File(taskInfo.getFromPath());
        if (!file.exists()) {
            throw new CommonException(StrUtil.format("文件不存在：{}", file.getAbsolutePath()));
        }
        randomAccessFile = new RandomAccessFile(file, "r");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            // 读取数据发送
            if (StrUtil.equals((String) msg, CommonConstant.ACK)) {
                sendData(ctx);
            } else {
                taskInfo.setErrorMessage(StrUtil.format("服务端错误：{}", msg));
                ctx.channel().close();
            }
        } else if (msg instanceof TaskInfo) {
            // 准备将解密文件写回本地
            TaskInfo backTaskInfo = (TaskInfo) msg;
            taskInfo.setTotalBackLength(backTaskInfo.getTotalBackLength());
            randomAccessFile.close();
            tempFile = FileUtil.touch(SpringUtil.getBean(FileService.class).getTempFile(file));
            randomAccessFile = new RandomAccessFile(tempFile, "rw");
            ctx.channel().writeAndFlush(CommonConstant.ACK);
        } else {
            // 接收数据，写到解密数据写回本地并返回确认信号
            byte[] bytes = (byte[]) msg;
            randomAccessFile.write(bytes);
            taskInfo.updateSendBackLength(bytes.length);
            if (randomAccessFile.length() == taskInfo.getTotalBackLength()) {
                randomAccessFile.close();
                taskService.handleDecryptFile(file, tempFile);
                ctx.channel().close();
                return;
            }
            if (++times % CommonConstant.TRANSMIT_TIMES == 0) {
                ctx.channel().writeAndFlush(CommonConstant.ACK);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Netty客户端关闭连接");
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        log.info(taskInfo.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        taskInfo.setErrorMessage("客户端错误：" + cause.getMessage());
        ctx.channel().close();
    }

    /**
     * 发送数据
     */
    private void sendData(ChannelHandlerContext ctx) {
        for (int i = 0; i < CommonConstant.TRANSMIT_TIMES; i++) {
            ctx.channel().writeAndFlush(readFile());
            if (taskInfo.getSendLength() == taskInfo.getTotalLength()) {
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
        taskInfo.updateSendLength(sendLength);
        if (sendLength < CommonConstant.DATA_SLICE) {
            byte[] lastSlice = new byte[sendLength];
            ArrayUtil.copy(data, lastSlice, sendLength);
            return lastSlice;
        }
        return data;
    }
}
