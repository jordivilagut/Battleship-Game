package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository games;

    //define getId in Game to return id
    //find all games
    //stream games (forEach)
    //map (for each game getId)
    //collect (put them toList)

    @RequestMapping("/games")
    public List<Object> getGames() {
        return games
                .findAll()
                .stream()
                .map(game -> game.getMap())
                .collect(toList());
    }





}
