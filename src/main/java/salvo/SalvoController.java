package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private ParticipationRepository views;

    @RequestMapping("/games")
    public List<Object> getGames() {
        return games
                .findAll()
                .stream()
                .map(game -> getGameDTO(game))
                .collect(toList());
    }

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> getGameView(@PathVariable("id") long id) {
        return getViewDTO(views.findOne(id));
    }

    public Map<String, Object> getGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("created", game.getTimeStamp());
        dto.put("participants", game.getParticipations().stream().map(participation -> getParticipationDTO(participation)).collect(toList()));
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

    public Map<String, Object> getViewDTO(Participation view) {

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", view.getId());
        dto.put("created", view.getTimeStamp());
        dto.put("players", view.getGame().getParticipations().stream().map(participation -> getParticipationDTO(participation)).collect(toList()));
        dto.put("ships", view.getShips().stream().map(ship -> getShipDTO(ship)).collect(toList()));
        return dto;
    }

    public Map<String, Object> getShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("category", ship.getCategory());
        dto.put("locations", ship.getLocations());
        return dto;
    }
}
