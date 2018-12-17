package ProtocolDispatcher;

import MainServer.handler.MOBAServerHandler;
import Protocol.ProtocolBytes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

/**
 *  **基于反射实现消息分发**
 *
 * 用于处理用户协议，也就是用户尚未登录时的逻辑处理
 */
public class HandleConnMsg {

    private enum LoginStatus{Success,Fail}

    /**
     * 用于处理登录协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgLoginConn(Channel channel, ProtocolBytes protocolBytes){

        System.out.println("正在处理登录协议");

        // 解析协议,获得用户名和密码
        String userName = protocolBytes.GetString();
        String password = protocolBytes.GetString();

        System.out.println("用户名是:"+userName);
        System.out.println("密码是:"+password);

        // 发送登录状态协议至客户端,原则上是，谁发送登录信息给我，我就把登录状态信息发送给谁，所以是发送到Channel中
        channel.writeAndFlush(CreateLoginStatusProtocol(LoginStatus.Success,userName).Encde());
    }


    /**
     * 构造关于登录状态的协议
     * @param loginStatus
     * @param userName
     * @return
     */
    private ProtocolBytes CreateLoginStatusProtocol(LoginStatus loginStatus,String userName){
        ProtocolBytes protocolBytes = new ProtocolBytes();

        // 协议名
        protocolBytes.AddString("LoginResultConn");
        // 用户名,用于标识唯一用户
        protocolBytes.AddString(userName);
        // 登录状态
        protocolBytes.AddString(loginStatus.toString());

        return protocolBytes;
    }
}
