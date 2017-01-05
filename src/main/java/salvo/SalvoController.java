package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private PlayerRepository players;

    @Autowired
    private ParticipationRepository views;

    @RequestMapping("/games")
    public Map<String, Object> getGamesInfo(Authentication auth) {
        Map<String, Object> gamesInfo = new LinkedHashMap<>();
        gamesInfo.put("player", getUser(auth));
        gamesInfo.put("games", games.findAll().stream().map(game -> getGameDTO(game)).collect(toList()));
        return gamesInfo;
    }

    @RequestMapping("/players")
    public List<Object> getPlayers() {
        return players
                .findAll()
                .stream()
                .map(player -> getPlayerDTO(player))
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
        dto.put("score", participation.getScore());
        dto.put("salvos", participation.getSalvos().stream().map(salvo -> getSalvoDTO(salvo)).collect(toList()));
        return dto;
    }

    public Map<String, Object> getPlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("username", player.getUsername());
        dto.put("total", player.getParticipations().stream().mapToDouble(participation -> participation.getScore()).sum());
        dto.put("won", player.getParticipations().stream().filter(participation -> participation.getScore() == 1).collect(toList()).size());
        dto.put("lost", player.getParticipations().stream().filter(participation -> participation.getScore() == 0 && participation.getfinishDate() != null).collect(toList()).size());
        dto.put("tied", player.getParticipations().stream().filter(participation -> participation.getScore() == 0.5).collect(toList()).size());
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

    public Map<String, Object> getSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("locations", salvo.getLocations());
        return dto;
    }

    private String getUser(Authentication auth) {
        if (auth != null) {
            return players.findByUsername(auth.getName()).getUsername();
        } else {
            return "guest";
        }
    }
}
