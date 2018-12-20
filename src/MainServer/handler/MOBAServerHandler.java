package MainServer.handler;

import Protocol.ProtocolBytes;
import ProtocolDispatcher.HandleConnMsg;
import ProtocolDispatcher.HandlePlayerMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

public class MOBAServerHandler extends ChannelInboundHandlerAdapter {

    // 用于划分房间系统区域的ChannelGroup字典,键是房间名，值是房间内所有Channel
    public static Map<String,ChannelGroup> roomsChannelGroup = new HashMap<>();

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static HandlePlayerMsg handlePlayerMsg = new HandlePlayerMsg();
    private static HandleConnMsg handleConnMsg = new HandleConnMsg();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel channel = ctx.channel();

        channels.add(channel);

        System.out.println("一个连接被加入了！ 目前登录ChannelGroup共有:"+channels.size()+"个连接");
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

//        System.out.println("收到协议，协议名为："+name);

        try {
            if(!name.contains("Conn"))
                // 消息分发至对应的处理方法
                HandlePlayerMsg.class.getMethod("Msg"+name,Channel.class,ProtocolBytes.class)
                        .invoke(handlePlayerMsg,ctx.channel(),protocolBytes);
            else
                // 协议名含有Conn字样,表示这是玩家尚未登录前的协议
                HandleConnMsg.class.getMethod("Msg"+name,Channel.class,ProtocolBytes.class)
                        .invoke(handleConnMsg,ctx.channel(),protocolBytes);
        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incommint = ctx.channel();
        System.out.println("[SERVER] - "+incommint.remoteAddress()+"离开\n");
    }
}
