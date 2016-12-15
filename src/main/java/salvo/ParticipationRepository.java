package salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findById(long id);
}
