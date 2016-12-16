package salvo;

import javax.persistence.*;
import java.util.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timeStamp;

    public Game() {}

    public Game(Date timeStamp) {this.timeStamp = timeStamp;}

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    Set<Participation> participations;

    public long getId() {return id;}

    public Date getTimeStamp() {return timeStamp;}

    public Set<Participation> getParticipations() {return participations;}

    public void setTimeStamp(Date timeStamp) {this.timeStamp = timeStamp;}
}
