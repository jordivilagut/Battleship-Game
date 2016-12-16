package salvo;

import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

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
    Set<Participation> participations;

    public Set<Participation> getParticipations() {
        return participations;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("created", timeStamp);
        map.put("participants", participations.stream().map(participation -> participation.getMap()).collect(toList()));
        return map;
    }
}
