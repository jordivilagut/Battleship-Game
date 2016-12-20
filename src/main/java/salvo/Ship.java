package salvo;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String category;

    @ElementCollection
    @Column(name="location")
    private List<String> locations;

    public Ship() {}

    public Ship(String category, List<String> locations) {
        this.category = category;
        this.locations = locations;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participation_id")
    private Participation participation;

    public Participation getParticipation() {return participation;}

    public long getId() {return id;}

    public String getCategory() {return category;}

    public List<String> getLocations() {return locations;}

    public void setParticipation(Participation participation) {this.participation = participation;}
}
