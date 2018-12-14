package test.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.concurrent.GlobalEventExecutor;
import javafx.scene.control.cell.CheckBoxTreeTableCell;

@Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

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
		channels.writeAndFlush(s);
	}

	@Override
	public  void  handlerRemoved(ChannelHandlerContext ctx) throws Exception{
		Channel incommint = ctx.channel();
		System.out.println("[SERVER] - "+incommint.remoteAddress()+"离开\n");


		channels.writeAndFlush("[SERVER] - "+incommint.remoteAddress()+"离开\n");
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
