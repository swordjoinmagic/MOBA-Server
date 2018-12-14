package test.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class OnlyReceiveServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        System.out.println("一个新的连接被加入了");
        Channel channel = ctx.channel();
        channels.writeAndFlush("[SERVER] - "+channel.remoteAddress()+"加入\n");
        channels.add(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("服务器收到消息:"+s);
        channels.writeAndFlush(s);
    }

    @Override
    public  void  handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incommint = ctx.channel();
        System.out.println("[SERVER] - "+incommint.remoteAddress()+"离开\n");


        channels.writeAndFlush("[SERVER] - "+incommint.remoteAddress()+"离开\n");
    }
}
