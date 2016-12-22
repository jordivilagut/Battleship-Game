package salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timeStamp;
    private double score;
    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy="participation", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="participation", fetch = FetchType.EAGER)
    private Set<Salvo> salvos = new HashSet<>();

    public Participation() {}

    public Participation(Date timeStamp, Game game, Player player) {
        this.timeStamp = timeStamp;
        this.game = game;
        this.player = player;
    }

    public long getId() {return id;}

    public Date getTimeStamp() {return timeStamp;}

    public Date getfinishDate() {return finishDate;}

    public double getScore() {return score;}

    public Game getGame(){return game;}

    public Player getPlayer() {return player;}

    public Set<Ship> getShips() {return ships;}

    public Set<Salvo> getSalvos() {return salvos;}

    public void addShip(Ship ship) {
        ship.setParticipation(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setParticipation(this);
        salvos.add(salvo);
    }

    public void setScore(double score) {
        this.score = score;
    }
}