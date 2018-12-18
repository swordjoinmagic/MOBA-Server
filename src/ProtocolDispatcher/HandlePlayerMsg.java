package ProtocolDispatcher;

import MainServer.handler.MOBAServerHandler;
import PlayerLogic.RoomSystem.RoomModel;
import PlayerLogic.RoomSystem.RoomSystem;
import PlayerLogic.Scene.Scene;
import PlayerLogic.Scene.ScenePlayer;
import Protocol.ProtocolBytes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 *  **基于反射实现消息分发**
 *
 * 用于处理角色协议，角色登录成功后的消息处理
 * 一般用于处理游戏逻辑
 */
public class HandlePlayerMsg {

    RoomSystem roomSystem;

    public HandlePlayerMsg(){
        roomSystem = new RoomSystem();
    }

    // 处理位置更新的消息
    public void MsgUpdateInfo(Channel channel, ProtocolBytes protocolBytes){
        String id = protocolBytes.GetString();
        System.out.println("转发由"+id+"号玩家发来的信息");
        // 转发消息
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    // 处理创建房间信息
    public void MsgCreateRoom(Channel channel, ProtocolBytes protocolBytes){
        // 获得要创建房间的用户名
        String userName = protocolBytes.GetString();
        // 获得要创建的房间的名字
        String roomName = protocolBytes.GetString();

        System.out.println("接收到CreateRoom消息，用户名是："+userName+" 房间名："+roomName);

        // 查看此房间是否已经被创建
        if(roomSystem.GetRoomModel(roomName)!=null){
            // 此房间已被创建，返回创建失败信息给客户端
            ProtocolBytes protocol = CreateRoomResult(userName,roomName,"Fail", "该房间已被人创建~");

            // 将结果消息发送给发送创建消息的用户
            channel.writeAndFlush(protocol.Encde());
        }else {
            // 此房间尚未被创建,返回创建成功信息给客户端,同时更新服务端的房间列表
            ProtocolBytes protocol = CreateRoomResult(userName,roomName,"Success", "null");

            // 将结果消息发送给发送创建消息的用户
            channel.writeAndFlush(protocol.Encde());

            // 更新服务端房间列表
            RoomModel roomModel = roomSystem.AddRoomModel(roomName);
            roomModel.setRoomPerson(roomModel.getRoomPerson()+1);
            roomModel.getPlayers().add(userName);
        }
    }

    /**
     * 构造创建房间结果协议
     */
    public ProtocolBytes CreateRoomResult(String userName,String roomName,String result,String failReason){
        ProtocolBytes protocolBytes = new ProtocolBytes();
        // 协议名
        protocolBytes.AddString("CreateRoomResult");

        // 参数
        protocolBytes.AddString(userName);
        protocolBytes.AddString(roomName);
        protocolBytes.AddString(result);
        protocolBytes.AddString(failReason);

        return protocolBytes;
    }

    /**
     * 处理加入房间协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgAttendRoom(Channel channel, ProtocolBytes protocolBytes){
        // 获得要创建的房间的名字
        String roomName = protocolBytes.GetString();
        // 获得要创建房间的用户名
        String userName = protocolBytes.GetString();

        System.out.println("接收到MsgAttendRoom消息，用户名是："+userName+" 房间名："+roomName);

        RoomModel room = roomSystem.GetRoomModel(roomName);
        // 查看此房间是否已经被创建
        if(room != null){
            // 此房间已被创建

            // 判断该房间内人数是否满人(大于10即为满人)
            if(room.getRoomPerson() >= 10){
                // 满人
                ProtocolBytes protocol = CreateAttendRoomResult(roomName,userName,"Fail", "您要加入的房间人数已满~");

                // 将结果消息发送给发送加入消息的用户
                channel.writeAndFlush(protocol.Encde());
            }else {
                // 人数未满,发送加入成功的消息给客户端
                ProtocolBytes protocol = CreateAttendRoomResult(roomName,userName,"Success", "null");

                // 将结果消息发送给发送加入消息的用户
                channel.writeAndFlush(protocol.Encde());
            }

        }else {
            // 此房间尚未被创建
            ProtocolBytes protocol = CreateAttendRoomResult(roomName,userName,"Fail", "您要加入的房间尚未被任何人创建~");

            // 将结果消息发送给发送加入消息的用户
            channel.writeAndFlush(protocol.Encde());
        }
    }

    /**
     * 构造加入房间结果协议
     * @return
     */
    public ProtocolBytes CreateAttendRoomResult(String roomName,String userName,String roomResult,String failReason){
        ProtocolBytes protocolBytes = new ProtocolBytes();

        // 协议名
        protocolBytes.AddString("AttendRoomResult");

        // 参数
        protocolBytes.AddString(roomName);
        protocolBytes.AddString(userName);
        protocolBytes.AddString(roomResult);
        protocolBytes.AddString(failReason);

        return protocolBytes;
    }

    /**
     * 处理返回所有房间信息协议
     */
    public void MsgGetRoomList(Channel channel, ProtocolBytes protocolBytes){
        System.out.println("处理返回所有房间信息的消息");

        // 构造房间信息
        ProtocolBytes protocol = CreateRoomListProtocol();

        // 向请求者发送回信息
        channel.writeAndFlush(protocol.Encde());
    }

    public ProtocolBytes CreateRoomListProtocol(){
        ProtocolBytes protocol = new ProtocolBytes();

        // 获得目前服务器上房间的数量
        int count = roomSystem.getRoomList().size();

        // 房间数量
        protocol.AddInt(count);

        for(RoomModel roomModel : roomSystem.getRoomList()){
            // 房间名
            protocol.AddString(roomModel.getRoomName());
            // 房间人数
            protocol.AddInt(roomModel.getRoomPerson());
            // 房间游戏状态
            protocol.AddString(roomModel.getRoomStatus());
        }

        return protocol;

    }


    /**
     * 处理AniamtionOperation协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgAnimationOperation(Channel channel, ProtocolBytes protocolBytes){
        // 直接转发协议
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }


    public  void MsgDamage(Channel channel, ProtocolBytes protocolBytes){
        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgLevel(Channel channel, ProtocolBytes protocolBytes){
        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgSpellSkill(Channel channel, ProtocolBytes protocolBytes){
        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgAddItem(Channel channel, ProtocolBytes protocolBytes){
        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());

    }

    public void MsgDeleteItem(Channel channel, ProtocolBytes protocolBytes){
        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());

    }
}
