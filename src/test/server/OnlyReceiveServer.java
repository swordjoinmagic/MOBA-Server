package test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import test.server.decoder.MessageEncoder;
import test.server.decoder.MyDecoder;


import java.net.InetSocketAddress;

public class OnlyReceiveServer {

    public static void main(String[] args){
        new OnlyReceiveServer(8081).start();
    }

    private int port;

    public OnlyReceiveServer(int port){
        this.port = port;
    }

    public void start(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new SimpleChatServerInitializer(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyDecoder(1<<20, 10, 4));
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new OnlyReceiveServerHandler());
                            System.out.println("channel初始化完成");
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("服务器启动！正在监听端口");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
