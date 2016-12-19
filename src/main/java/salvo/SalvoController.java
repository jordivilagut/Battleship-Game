package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository games;

    @RequestMapping("/games")
    public List<Object> getGames() {
        return games
                .findAll()
                .stream()
                .map(game -> getGameDTO(game))
                .collect(toList());
    }

    public Map<String, Object> getGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getTimeStamp());
        dto.put("participants", game.participations.stream().map(participation -> getParticipationDTO(participation)).collect(toList()));
        return dto;
    }

    public Map<String, Object> getParticipationDTO(Participation participation) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", participation.getId());
        dto.put("player", getPlayerDTO(participation.getPlayer()));
        return dto;
    }

    public Map<String, Object> getPlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("username", player.getUsername());
        return dto;
    }
}
