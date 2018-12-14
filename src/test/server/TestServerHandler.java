package test.server;

import com.sun.corba.se.impl.encoding.CodeSetConversion;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf result = (ByteBuf) msg;
        System.out.println("服务器接受到信息："+result.readableBytes());
        int i = result.readInt();
        System.out.println("result.readerIndex():"+result.readerIndex());

//        System.out.println(new String(result.array(),result.readerIndex(),i));

        byte[] strBytes = new byte[i];
        result.readBytes(strBytes);
        System.out.println(new String(strBytes));


        float a = result.readFloat();
        System.out.println("a:"+a);
    }
}
