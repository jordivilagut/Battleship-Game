package salvo;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;

    public Player() {}

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @OneToMany(mappedBy="player", fetch = FetchType.EAGER)
    Set<Participation> participations;

    public Set<Participation> getParticipations() {
        return participations;
    }

    public String toString() {
        return "Player: " + id;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("username", username);
        return map;
    }
}
