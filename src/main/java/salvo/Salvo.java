package salvo;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int turn;

    @ElementCollection
    @Column(name="location")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participation_id")
    private Participation participation;

    public Salvo() {}

    public Salvo(int turn, List<String> locations) {
        this.turn = turn;
        this.locations = locations;
    }

    public long getId() {return id;}

    public int getTurn() {return turn;}

    public List<String> getLocations() {return locations;}

    public Participation getParticipation() {return participation;}

    public void setParticipation(Participation participation) {this.participation = participation;}
}
