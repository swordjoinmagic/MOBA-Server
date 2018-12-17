package PlayerLogic.RoomSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间系统，
 * 用于表示服务器上一共有多少个房间被开启，
 * 用于在客户端发送GetRoomList协议时，返回房间系统信息
 */
public class RoomSystem {
    public List<RoomModel> getRoomList() {
        return roomList;
    }

    private List<RoomModel> roomList = new ArrayList<>();

    /**
     * 根据房间名，获得房间系统内一个指定的房间
     * @param roomName
     * @return
     */
    public RoomModel GetRoomModel(String roomName){
        for(RoomModel room : roomList){
            if(room.getRoomName().equals(roomName)){
                return room;
            }
        }
        return null;
    }

    /**
     * 向房间系统内添加房间
     * @param roomName
     */
    public RoomModel AddRoomModel(String roomName){
        RoomModel room = new RoomModel();
        synchronized (roomList){
            room.setRoomName(roomName);
            roomList.add(room);
        }
        return room;
    }
}
