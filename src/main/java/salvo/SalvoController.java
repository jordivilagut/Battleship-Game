package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    private ParticipationRepository participations;

    @RequestMapping("/games")
    public Map<String, Object> getGamesInfo(Authentication auth) {
        Map<String, Object> gamesInfo = new LinkedHashMap<>();
        gamesInfo.put("player", getUser(auth));
        gamesInfo.put("games", games.findAll().stream().map(game -> getGameDTO(game)).collect(toList()));
        return gamesInfo;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createUser(@RequestParam Date timeStamp, Authentication auth) {
        Game game = new Game(timeStamp);
        Player user = players.findByUsername(auth.getName());
        Participation participation = new Participation(timeStamp, game, user);
        games.save(game);
        participations.save(participation);
        return new ResponseEntity<>(makeMap("participation", participation.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/players")
    public List<Object> getPlayers() {
        return players
                .findAll()
                .stream()
                .map(player -> getPlayerDTO(player))
                .collect(toList());
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }

        if (password == null || password.isEmpty()) {
            return new ResponseEntity<>("No password given", HttpStatus.FORBIDDEN);
        }

        Player user = players.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }

        players.save(new Player(username, password));
        return new ResponseEntity<>("Named added", HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable("id") long id, Authentication auth) {
        Participation participation = participations.findOne(id);
        String player = participation.getPlayer().getUsername();
        String user = getUser(auth);

        if(user == player){return new ResponseEntity<>(getViewDTO(participation), HttpStatus.ACCEPTED);}
        else{return new ResponseEntity<>(makeMap("error", "Unauthorized path."), HttpStatus.UNAUTHORIZED);}
    }

    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable("gameId") Long gameId, @RequestParam Date timeStamp, Authentication auth) {
        String username = getUser(auth);
        Game game = games.findOne(gameId);
        Player user = players.findByUsername(auth.getName());
        int playersInGame = game.getParticipations().size();

        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No user."), HttpStatus.UNAUTHORIZED);
        }

        if (gameId == null) {
            return new ResponseEntity<>(makeMap("error", "Game doesn't exist.."), HttpStatus.FORBIDDEN);
        }

        if (playersInGame > 2) {
            return new ResponseEntity<>(makeMap("error", "Game is full."), HttpStatus.FORBIDDEN);
        }

        Participation participation = new Participation(timeStamp, game, user);
        participations.save(participation);
        return new ResponseEntity<>(makeMap("participation", participation.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
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

    public Map<String, Object> getViewDTO(Participation participation) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", participation.getId());
        dto.put("created", participation.getTimeStamp());
        dto.put("players", participation.getGame().getParticipations().stream().map(p -> getParticipationDTO(p)).collect(toList()));
        dto.put("ships", participation.getShips().stream().map(ship -> getShipDTO(ship)).collect(toList()));
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
