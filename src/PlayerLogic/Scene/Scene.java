package PlayerLogic.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存一个场景中的所有角色
 */
public class Scene {
    private List<ScenePlayer> players;
    public Scene(){
        players = new ArrayList<>();
    }

    private ScenePlayer GetScenePlayer(String id){
        for(ScenePlayer player : players){
            if(player.getId().equals(id)){
                return player;
            }
        }
        return null;
    }

    // 向场景中添加玩家
    public void AddPlayer(String id){
        synchronized (players){
            ScenePlayer p = new ScenePlayer();
            p.setId(id);
            players.add(p);
        }
    }
}
