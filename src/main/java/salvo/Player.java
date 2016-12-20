package salvo;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;

    @OneToMany(mappedBy="player", fetch = FetchType.EAGER)
    private Set<Participation> participations;

    public Player() {}

    public Player(String username) {this.username = username;}

    public long getId() {return id;}

    public String getUsername() {return username;}

    public Set<Participation> getParticipations() {return participations;}

    public void setUsername(String username) {this.username = username;}
}
