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
                .map(game -> getGameMap(game))
                .collect(toList());
    }

    public Map<String, Object> getGameMap(Game game) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", game.getId());
        map.put("created", game.getTimeStamp());
        map.put("participants", game.participations.stream().map(participation -> getParticipationsMap(participation)).collect(toList()));
        return map;
    }

    public Map<String, Object> getParticipationsMap(Participation participation) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", participation.getId());
        map.put("player", getPlayerMap(participation.getPlayer()));
        return map;
    }

    public Map<String, Object> getPlayerMap(Player player) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", player.getId());
        map.put("username", player.getUsername());
        return map;
    }
}
