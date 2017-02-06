package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ShipRepository ships;

    @Autowired
    private SalvoRepository salvos;

    @RequestMapping("/games")
    public Map<String, Object> getGamesInfo(Authentication auth) {
        Map<String, Object> gamesInfo = new LinkedHashMap<>();
        gamesInfo.put("player", getUsername(auth));
        gamesInfo.put("games", games.findAll().stream().map(game -> getGameDTO(game)).collect(toList()));
        return gamesInfo;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGame(@RequestParam Date timeStamp, Authentication auth) {
        Game game = new Game(timeStamp);
        Player user = getUser(auth);
        Participation participation = new Participation(timeStamp, game, user);
        games.save(game);
        participations.save(participation);
        return new ResponseEntity<>(makeMap("participation", participation.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable("id") Long id, @RequestParam Date timeStamp, Authentication auth) {
        Player user = getUser(auth);
        Game game = games.findOne(id);
        String username = user.getUsername();

        int playersInGame = game.getParticipations().size();

        if (username == null || username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No user."), HttpStatus.UNAUTHORIZED);
        }

        if (id == null) {
            return new ResponseEntity<>(makeMap("error", "Game doesn't exist.."), HttpStatus.FORBIDDEN);
        }

        if (playersInGame > 2) {
            return new ResponseEntity<>(makeMap("error", "Game is full."), HttpStatus.FORBIDDEN);
        }

        Participation participation = new Participation(timeStamp, game, user);
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

        Player player = players.findByUsername(username);
        if (player != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }

        players.save(new Player(username, password));
        return new ResponseEntity<>("Named added", HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable("id") long id, Authentication auth) {
        Participation participation = participations.findOne(id);
        String player = participation.getPlayer().getUsername();
        String user = getUsername(auth);

        if(user == player){return new ResponseEntity<>(getViewDTO(participation), HttpStatus.ACCEPTED);}
        else{return new ResponseEntity<>(makeMap("error", "Unauthorized path."), HttpStatus.UNAUTHORIZED);}
    }

    @RequestMapping(path = "/game_view/{id}/ships", method = RequestMethod.POST)
    public ResponseEntity<String> placeShip(@PathVariable("id") long id, @RequestBody Ship ship, Authentication auth) {
        Participation participation = participations.findOne(id);
        Player player = participation.getPlayer();
        Player user = getUser(auth);
        List<String> occupiedLocations = participation.getShips().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        List<String> newLocations = ship.getLocations().stream().collect(toList());
        List<String> placedShips = participation.getShips().stream().map(s -> s.getCategory()).collect(toList());
        String newShip = ship.getCategory();

        if(user == null || player == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.UNAUTHORIZED);
        }

        if(user != player) {
            return new ResponseEntity<>("Unauthorized user.", HttpStatus.UNAUTHORIZED);
        }

        if(!Collections.disjoint(newLocations, occupiedLocations)) {
            return new ResponseEntity<>("Invalid location.", HttpStatus.FORBIDDEN);
        }

        if(placedShips.stream().anyMatch(s -> s.trim().equals(newShip))) {
            return new ResponseEntity<>("Already placed", HttpStatus.FORBIDDEN);
        }

        if(participation.getShips().size() > 4) {
            return new ResponseEntity<>("Ships already placed.", HttpStatus.FORBIDDEN);
        }

        participation.addShip(ship);
        ships.save(ship);
        return new ResponseEntity<>("Ship placed successfully.", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game_view/{id}/salvos", method = RequestMethod.POST)
    public ResponseEntity<String> placeSalvo(@PathVariable("id") long id, @RequestBody Salvo salvo, Authentication auth) {
        Participation participation = participations.findOne(id);
        Participation opponent = participation.getGame().getParticipations().stream().filter(p ->p.getId() != participation.getId()).findFirst().get();
        Player player = participation.getPlayer();
        Player user = getUser(auth);
        List<String> occupiedLocations = participation.getSalvos().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        List<String> newLocations = salvo.getLocations().stream().collect(toList());
        List<Integer> registeredTurns = participation.getSalvos().stream().map(s -> s.getTurn()).collect(toList());
        int opponentTurns = opponent.getSalvos().size();
        int newTurn = salvo.getTurn();

        if(user == null || player == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.UNAUTHORIZED);
        }

        if(user != player) {
            return new ResponseEntity<>("Unauthorized user.", HttpStatus.UNAUTHORIZED);
        }

        if(!Collections.disjoint(newLocations, occupiedLocations)) {
            return new ResponseEntity<>("Invalid location.", HttpStatus.FORBIDDEN);
        }

        if(registeredTurns.contains(newTurn) || newTurn > opponentTurns + 1) {
            return new ResponseEntity<>("Invalid turn.", HttpStatus.FORBIDDEN);
        }

        if(participation.getShips().size() < 5 || opponent.getShips().size() < 5) {
            return new ResponseEntity<>("Ships aren't placed.", HttpStatus.FORBIDDEN);
        }

        participation.addSalvo(salvo);
        salvos.save(salvo);
        return new ResponseEntity<>("Salvos placed successfully.", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game_view/{id}/scores", method = RequestMethod.POST)
    public ResponseEntity<String> registerScore(@PathVariable("id") long id, Authentication auth) {
        Participation participation = participations.findOne(id);
        Participation opponent = participation.getGame().getParticipations().stream().filter(p ->p.getId() != participation.getId()).findFirst().get();
        List<String> opponentSalvos = opponent.getSalvos().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        int sunkShips = participation.getShips().stream().filter(s -> getHitsNr(s, opponentSalvos) >= s.getLocations().size()).collect(toList()).size();
        int opponentSunkShips = opponent.getShips().stream().filter(s -> getHitsNr(s, opponentSalvos) >= s.getLocations().size()).collect(toList()).size();
        double score;

        if (sunkShips < opponentSunkShips) {
            score = 1;
        }else if(sunkShips == opponentSunkShips) {
            score = 0.5;
        }else {
            score = 0;
        }

        participation.setScore(score);
        participations.save(participation);
        return new ResponseEntity<>("Game Over", HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    private Player getUser(Authentication auth) {
        if (auth != null) {
            return players.findByUsername(auth.getName());
        }
        return null;
    }

    private String getUsername(Authentication auth) {
        if (auth != null) {
            return getUser(auth).getUsername();
        }
        return "guest";
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
        dto.put("hitShips", participation.getGame().getParticipations().stream().filter(p ->p.getId() != participation.getId()).map(p -> p.getShips().stream().map(s -> getHitShipDTO(s, participation)).collect(toList())).flatMap(p -> p.stream()).collect(toList()));
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

    public int getHitsNr (Ship ship, List<String> opponentSalvos) {
        List<String> hits = ship.getLocations().stream().collect(toList());
        hits.retainAll(opponentSalvos);
        return hits.size();
    }

    public Map<String, Object> getViewDTO(Participation participation) {

        Participation user = participation;
        Participation opponent = participation.getGame().getParticipations().stream().filter(p ->p.getId() != participation.getId()).findFirst().get();
        int userTurn = participation.getSalvos().size();
        int opponentTurn = opponent.getSalvos().size();
        List<String> opponentSalvos = opponent.getSalvos().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        List<String> userSalvos = participation.getSalvos().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        int sunkShips = participation.getShips().stream().filter(s -> getHitsNr(s, opponentSalvos) >= s.getLocations().size()).collect(toList()).size();
        int opponentSunkShips = opponent.getShips().stream().filter(s -> getHitsNr(s, userSalvos) >= s.getLocations().size()).collect(toList()).size();
        int lastTurn = 16;
        int shipsNr = 5;

        //STATUS CODE chart
        // 1 - Place ships.
        // 2 - Wait for opponent to place ships.
        // 3 - Enter salvo.
        // 4 - Wait for opponent to enter salvos.
        // 5 - Game over.
        System.out.println("Sunkships: " + sunkShips);
        System.out.println("Opponent Sunkships: " + opponentSunkShips);

        int statusCode;

        if (userTurn > opponentTurn) {
            statusCode = 4;
        } else {
            statusCode = 3;
        }

        if (sunkShips >= shipsNr || opponentSunkShips >= shipsNr) {
            statusCode = 5;
        }

        if (userTurn == lastTurn && opponentTurn == lastTurn) {
            statusCode = 5;
        }

        if (opponent.getShips().size() < shipsNr) {
            statusCode = 2;
        }

        if (user.getShips().size() < shipsNr) {
            statusCode = 1;
        }

        System.out.println("Code: " + statusCode);

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", participation.getId());
        dto.put("created", participation.getTimeStamp());
        dto.put("players", participation.getGame().getParticipations().stream().map(p -> getParticipationDTO(p)).collect(toList()));
        dto.put("ships", participation.getShips().stream().map(ship -> getShipDTO(ship)).collect(toList()));
        dto.put("status", statusCode);
        return dto;
    }

    public Map<String, Object> getHitShipDTO(Ship ship, Participation participation) {

        List<String> hits = participation.getSalvos().stream().map(s -> s.getLocations()).flatMap(l -> l.stream()).collect(toList());
        List<String> opponentShipPositions = ship.getLocations();
        hits.retainAll(opponentShipPositions);
        int leftLocations = ship.getLocations().size() - hits.size();

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("category", ship.getCategory());
        dto.put("locations", hits);
        dto.put("left", leftLocations);
        dto.put("sunk", leftLocations == 0);

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
}
