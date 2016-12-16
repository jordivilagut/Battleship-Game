package salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Entity
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timeStamp;

    public Participation() {}

    public Participation(Date timeStamp, Game game, Player player) {
        this.timeStamp = timeStamp;
        this.game = game;
        this.player = player;
    }

    public Date getTimeStamp() { return timeStamp; }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameId")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "playerId")
    private Player player;

    public Game getGame(){
        return game;
    }

    public Player getPlayer(){ return player; }

    public void setGame(Game game){
        this.game = game;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("player", player.getMap());
        return map;
    }
}

// player -> wrong display
// this.getPlayer -> wrong display
// player.getMap() -> Whitelabel error page. Error 500.
// player.toString() -> Whitelabel error page. Error 500.
// player.getId() -> Whitelabel error page. Error 500.