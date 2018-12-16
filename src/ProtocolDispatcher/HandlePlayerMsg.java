package ProtocolDispatcher;

import MainServer.handler.MOBAServerHandler;
import PlayerLogic.Scene.Scene;
import PlayerLogic.Scene.ScenePlayer;
import Protocol.ProtocolBytes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

/**
 *  **基于反射实现消息分发**
 *
 * 用于处理角色协议，角色登录成功后的消息处理
 * 一般用于处理游戏逻辑
 */
public class HandlePlayerMsg {
    public void MsgUpdateInfo(ChannelGroup channels, Channel channel, ProtocolBytes protocolBytes){
        String id = protocolBytes.GetString();
        System.out.println("转发由"+id+"号玩家发来的信息");
        // 转发消息
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }
}
