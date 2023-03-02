package com.example.decrypt.netty;

import com.example.decrypt.util.JacksonUtil;
import com.example.decrypt.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 基础编码器，将对象转换为特定字节数组进行发送
 *
 * @author HeYiyu
 * @date 2023/2/13
 */
public class BaseEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        byte[] bytes;
        if (msg instanceof byte[]) {
            bytes = (byte[]) msg;
        } else {
            bytes = JacksonUtil.toBytes(msg);
        }
        out.writeInt(bytes.length);
        out.writeChar(NettyUtil.getCharacter(msg.getClass()));
        out.writeBytes(bytes);
    }
}
