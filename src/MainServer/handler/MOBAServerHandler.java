package MainServer.handler;

import Protocol.ProtocolBytes;
import ProtocolDispatcher.HandlePlayerMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MOBAServerHandler extends ChannelInboundHandlerAdapter {

    // 用于登录的ChannelGroup
    public static ChannelGroup loginChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private HandlePlayerMsg handlePlayerMsg = new HandlePlayerMsg();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel channel = ctx.channel();

        // 将新来的连接加入至登录的ChannelGroup中
        loginChannelGroup.add(channel);

        System.out.println("一个连接被加入了！ 目前登录ChannelGroup共有:"+loginChannelGroup.size()+"个连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf result = (ByteBuf) msg;

        // 解析获得的数据包（完整）
        ProtocolBytes protocolBytes = ProtocolBytes.Decode(result.array(),0,result.array().length);

        // 分发消息(基于反射),给其他方法处理这个协议
        HandleMsg(ctx,protocolBytes);
    }

    /**
     * 用于处理一个协议的方法,具体实现是通过获得协议的名字来进行反射
     * 获得处理对应协议的方法
     * @param ctx
     * @param protocolBytes
     */
    private void HandleMsg(ChannelHandlerContext ctx, ProtocolBytes protocolBytes){
        String name = protocolBytes.GetName();

        System.out.println("收到协议，协议名为："+name);

        try {
            // 消息分发至对应的处理方法
            HandlePlayerMsg.class.getMethod("Msg"+name,Channel.class,ProtocolBytes.class)
                    .invoke(handlePlayerMsg,ctx.channel(),protocolBytes);
        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incommint = ctx.channel();
        System.out.println("[SERVER] - "+incommint.remoteAddress()+"离开\n");
    }
}
