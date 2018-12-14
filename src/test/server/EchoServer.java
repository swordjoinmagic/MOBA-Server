package test.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoServer {
	private final int port;
	
	public EchoServer(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
	}
	
	public static void main(String[] args) throws Exception {
		new EchoServer(8081).start();
	}
	
	public void start() throws Exception {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(port))
			.childHandler(new SimpleChatServerInitializer())
			.option(ChannelOption.SO_BACKLOG,128)
			.childOption(ChannelOption.SO_KEEPALIVE,true);

			System.out.println("SimpleChatServer 启动了");

			ChannelFuture f = b.bind().sync();
			System.out.println(EchoServer.class.getName()+ " started and listen on " + f.channel().localAddress());
			f.channel().closeFuture().sync();
		}catch (Exception e) {
			// TODO: handle exception
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

			System.out.println("SimpleChatServer 关闭了");
		}
	}
}
