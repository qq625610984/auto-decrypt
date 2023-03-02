package com.example.decrypt.netty;

import com.example.decrypt.util.JacksonUtil;
import com.example.decrypt.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 基础解码器，将收到的字节数组解析为相应对象
 *
 * @author HeYiyu
 * @date 2023/2/13
 */
public class BaseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 数据长度小于6说明读取不到完整的消息长度和消息标志
        if (in.readableBytes() < 6) {
            return;
        }
        // 标记读取位置
        in.markReaderIndex();
        // 获得消息长度
        int length = in.readInt();
        // 获得消息标志
        char type = in.readChar();
        // 如果消息体长度小于记录的消息长度，说明消息体不完整
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        // 获取完整的消息并根据消息标记将数据转换为相应的对象
        byte[] buffer = new byte[length];
        in.readBytes(buffer);
        if (type == 'b') {
            out.add(buffer);
        } else {
            out.add(JacksonUtil.toObject(buffer, NettyUtil.getClazz(type)));
        }
    }
}
