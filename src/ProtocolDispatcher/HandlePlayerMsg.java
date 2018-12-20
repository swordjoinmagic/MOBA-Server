package ProtocolDispatcher;

import MainServer.MOBAServer;
import MainServer.handler.MOBAServerHandler;
import PlayerLogic.RoomSystem.RoomModel;
import PlayerLogic.RoomSystem.RoomPlayer;
import PlayerLogic.RoomSystem.RoomSystem;
import PlayerLogic.Scene.Scene;
import PlayerLogic.Scene.ScenePlayer;
import Protocol.ProtocolBytes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import sun.java2d.pipe.PixelToParallelogramConverter;

import javax.rmi.PortableRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 *  **基于反射实现消息分发**
 *
 * 用于处理角色协议，角色登录成功后的消息处理
 * 一般用于处理游戏逻辑
 */
public class HandlePlayerMsg {

    static RoomSystem roomSystem = new RoomSystem();

    //=================================================
    // 房间系统
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
            roomModel.setRoomPerson(0);
            System.out.println("服务器创建房间,目前服务器房间数量为:"+roomSystem.getRoomList().size());
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
     * 处理加入房间协议,加入房间时,客户端要发送GetRoom协议来获得房间内玩家的具体信息
     * @param channel
     * @param protocolBytes
     */
    public void MsgAttendRoom(Channel channel, ProtocolBytes protocolBytes){
        // 获得要加入的房间的名字
        String roomName = protocolBytes.GetString();

        // 获得要加入房间的用户名
        String userName = protocolBytes.GetString();

        System.out.println("接收到MsgAttendRoom消息，用户名是："+userName+" 房间名："+roomName);

        // 根据房间名获得用户要加入的房间
        RoomModel room = roomSystem.GetRoomModel(roomName);

        System.out.println("要加入的房间已创建,房间名为:"+roomName);

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
                // 人数未满,构造加入成功的消息给客户端
                ProtocolBytes protocol = CreateAttendRoomResult(roomName,userName,"Success", "null");

                // 构造玩家对象
                RoomPlayer roomPlayer = new RoomPlayer();
                roomPlayer.setUserName(userName);
                if(room.getRoomPerson()==0)
                    roomPlayer.setUserStatus("HomeOwner");
                else
                    roomPlayer.setUserStatus("Prepare");
                roomPlayer.setUserFaction("Red");

                // 玩家对象加入房间
                room.getPlayers().add(roomPlayer);

                // 房间人数+1
                room.setRoomPerson(room.getRoomPerson()+1);


                // 将该消息群发给房间内所有单位
                ChannelGroup roomChannelGroup = MOBAServerHandler.roomsChannelGroup.get(roomName);
                if(roomChannelGroup==null){
                    // 如果roomChannelGroup为空,那就新建一个
                    roomChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                    MOBAServerHandler.roomsChannelGroup.put(roomName,roomChannelGroup);
                }

                // 将该用户加入roomChannelGroup
                MOBAServerHandler.roomsChannelGroup.get(roomName).add(channel);

                // 将该消息群发给房间内所有单位
                roomChannelGroup.writeAndFlush(protocol.Encde());
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
     * 需要注意的是,加入房间协议的结果是发送给房间内所有单位的
     * @return
     */
    public ProtocolBytes CreateAttendRoomResult(String roomName,String userName,String roomResult,String failReason){
        ProtocolBytes protocolBytes = new ProtocolBytes();

        // 协议名
        protocolBytes.AddString("AttendRoomResult");

        // 参数
        protocolBytes.AddString(roomName);
        System.out.println("在CreateAttendRoomResult 中 RooName是:"+roomName);
        protocolBytes.AddString(userName);
        protocolBytes.AddString(roomResult);
        protocolBytes.AddString(failReason);

        return protocolBytes;
    }

    /**
     * 客户端用于获得一个房间具体信息(房间人数,玩家是什么之类的)的协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgGetRoom(Channel channel, ProtocolBytes protocolBytes){
        System.out.println("服务端收到 GetRoom 协议");

        //======================================
        // 构造房间信息协议对客户端进行转发

        // 获得房间名
        String roomName = protocolBytes.GetString();

        System.out.println("房间名是:"+roomName);

        // 查找房间
        RoomModel room = roomSystem.GetRoomModel(roomName);

        // 构造协议
        ProtocolBytes protocol = CreateGetRoomProtocol(roomName,room);

        // 发送该协议回客户端
        channel.writeAndFlush(protocol.Encde());
    }

    /**
     * 构造GetRoom协议
     * @return
     */
    public ProtocolBytes CreateGetRoomProtocol(String roomName,RoomModel room){
        // 构造协议
        ProtocolBytes protocol = new ProtocolBytes();

        // 协议名
        protocol.AddString("GetRoom");

        // 协议参数
        protocol.AddString(roomName);   // 房间名

        protocol.AddInt(room.getRoomPerson());  // 房间人数

        for(int i=0;i<room.getRoomPerson();i++){
            protocol.AddString(room.getPlayers().get(i).getUserName()); // 用户名
            protocol.AddString(room.getPlayers().get(i).getUserFaction()); // 用户阵营
            protocol.AddString(room.getPlayers().get(i).getUserStatus()); // 用户状态
        }

        return protocol;
    }

    /**
     * 处理返回所有房间信息协议
     */
    public void MsgGetRoomList(Channel channel, ProtocolBytes protocolBytes){
        // 构造房间信息
        ProtocolBytes protocol = new ProtocolBytes();

        // 协议名
        protocol.AddString("GetRoomList");

        // 协议参数
        int roomCount = roomSystem.getRoomList().size();
        System.out.println("处理返回所有房间信息的消息,房间数量"+roomCount);
        protocol.AddInt(roomCount);
        for(RoomModel roomModel : roomSystem.getRoomList()){
            protocol.AddString(roomModel.getRoomName());
            protocol.AddInt(roomModel.getRoomPerson());
            protocol.AddString(roomModel.getRoomStatus());
        }


        // 向请求者发送回信息
        channel.writeAndFlush(protocol.Encde());
    }

    /**
     * 处理房间内阵营改变协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgChangeUserFaction(Channel channel, ProtocolBytes protocolBytes){
        // 获得房间名
        String roomName = protocolBytes.GetString();
        // 获得要改变阵营的用户
        String userName = protocolBytes.GetString();
        // 获得目标阵营
        String userFaction = protocolBytes.GetString();

        // 首先根据房间名找到房间
        RoomModel roomModel = roomSystem.GetRoomModel(roomName);
        // 根据用户名找到该用户
        RoomPlayer roomPlayer = roomModel.GetRoomPlayer(userName);
        // 更改用户的阵营
        roomPlayer.setUserFaction(userFaction);

        // 构造GetRoom协议
        ProtocolBytes protocol = CreateGetRoomProtocol(roomName,roomModel);

        // 向房间内所有客户端发送getRoom消息
        MOBAServerHandler.roomsChannelGroup.get(roomName).writeAndFlush(protocol.Encde());
    }

    /**
     * 处理房间内用户状态改变协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgChangeUserStatus(Channel channel, ProtocolBytes protocolBytes){
        // 获得房间名
        String roomName = protocolBytes.GetString();
        // 获得要改变阵营的用户
        String userName = protocolBytes.GetString();
        // 获得目标状态
        String userStatus = protocolBytes.GetString();

        // 首先根据房间名找到房间
        RoomModel roomModel = roomSystem.GetRoomModel(roomName);
        // 根据用户名找到该用户
        RoomPlayer roomPlayer = roomModel.GetRoomPlayer(userName);
        // 更改用户的准备状态
        roomPlayer.setUserStatus(userStatus);

        // 构造GetRoom协议
        ProtocolBytes protocol = CreateGetRoomProtocol(roomName,roomModel);

        // 向房间内所有客户端发送getRoom消息
        MOBAServerHandler.roomsChannelGroup.get(roomName).writeAndFlush(protocol.Encde());
    }

    // 处理开始游戏协议
    public void MsgStartGame(Channel channel, ProtocolBytes protocolBytes){
        // 获得房间名
        String roomName = protocolBytes.GetString();

        // 判断该房间内是否所有单位都准备好了

        // 转发信息
        MOBAServerHandler.roomsChannelGroup.get(roomName).writeAndFlush(protocolBytes.Encde());
    }

    //===============================================================================

    // 处理位置更新的消息
    public void MsgUpdateInfo(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        String id = protocolBytes.GetString();
//        System.out.println("转发由"+id+"号玩家发来的信息");
        // 转发消息
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }
    /**
     * 处理AniamtionOperation协议
     * @param channel
     * @param protocolBytes
     */
    public void MsgAnimationOperation(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 直接转发协议
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }


    public  void MsgDamage(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgLevel(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgSpellSkill(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());
    }

    public void MsgAddItem(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());

    }

    public void MsgDeleteItem(Channel channel, ProtocolBytes protocolBytes){

        // 此处应该为协议添加RoomName房间名,限制协议在房间内传递
        // 此处为了测试,直接向所有用户转发协议

        // 转发
        MOBAServerHandler.channels.writeAndFlush(protocolBytes.Encde());

    }
}
