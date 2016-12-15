package salvo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timeStamp;

    public Participation() {}

    public Participation(Date timeStamp) {
        this.timeStamp = timeStamp;
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

    public void setGame(Game game){
        this.game = game;
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}