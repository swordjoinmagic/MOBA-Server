package PlayerLogic.RoomSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于表示一个房间的状态，房间只有两种状态，
 *  · 等待玩家
 *  · 游戏中
 */
enum RoomStatus{
    Waiting,
    Playing
}
/**
 * 用于表示房间系统内的一个房间
 */
public class RoomModel {
    // 房间名，主键
    private String roomName;
    // 房间人数
    private int roomPerson;
    // 房间状态
    private RoomStatus roomStatus = RoomStatus.Waiting;
    // 房间内的所有玩家,使用list保存玩家的userName
    private List<String> players = new ArrayList<>();

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomPerson() {
        return roomPerson;
    }

    public void setRoomPerson(int roomPerson) {
        this.roomPerson = roomPerson;
    }

    public String getRoomStatus() {
        return roomStatus.toString();
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public List<String> getPlayers() {
        return players;
    }
}
