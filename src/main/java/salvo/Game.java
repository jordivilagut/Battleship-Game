package salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timeStamp;

    public Game() {}

    public Game(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getTimeStamp() { return timeStamp; }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    Set<Participation> participations = new HashSet<>();

    public Set<Participation> getParticipations() {
        return participations;
    }
}
