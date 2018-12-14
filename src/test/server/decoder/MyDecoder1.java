package test.server.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import sun.java2d.pipe.BufferedTextPipe;

import java.nio.ByteOrder;

public class MyDecoder1 extends LengthFieldBasedFrameDecoder {
    public MyDecoder1(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if(buffer==null) return null;

        if(buffer.readableBytes() > 4){
            // 标记
            buffer.markReaderIndex();

            // 长度
            int length = buffer.readInt();
            System.out.println("信息长度:"+length);

            if(buffer.readableBytes() < length){
                buffer.resetReaderIndex();
                // 缓存当前剩余的Buffer数据，等待剩下数据包到来
                return  null;
            }

            // 读数据
            byte[] bytes = new byte[length];

            buffer.readBytes(bytes);
            System.out.println("解析信息是："+new String(bytes));
            return Unpooled.copiedBuffer(bytes);
        }
        // 缓存当前剩余的buffer数据，等待剩下数据包到来
        return null;
    }
}
