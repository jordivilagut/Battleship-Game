package salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, ParticipationRepository participationRepository) {
		return (args) -> {
			Date date = new Date();
			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov"));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov"));
			Player player3 = playerRepository.save(new Player("t.almeida@ctu.gov"));
			Player player4 = playerRepository.save(new Player("d.palmer@whitehouse.gov"));
			Game game1 = gameRepository.save(new Game(date));
			Game game2 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600))));
			Game game3 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600*2))));
			Game game4 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600*3))));
			Game game5 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600*4))));
			Game game6 = gameRepository.save(new Game(Date.from(date.toInstant().plusSeconds(3600*5))));
			Participation participation1 = participationRepository.save(new Participation(date, game1, player1));
			Participation participation2 = participationRepository.save(new Participation(date, game1, player2));
			Participation participation3 = participationRepository.save(new Participation(date, game2, player1));
			Participation participation4 = participationRepository.save(new Participation(date, game2, player2));
			Participation participation5 = participationRepository.save(new Participation(date, game3, player2));
			Participation participation6 = participationRepository.save(new Participation(date, game3, player3));
			Participation participation7 = participationRepository.save(new Participation(date, game4, player1));
			Participation participation8 = participationRepository.save(new Participation(date, game4, player2));
			Participation participation9 = participationRepository.save(new Participation(date, game5, player3));
			Participation participation10  = participationRepository.save(new Participation(date, game5, player1));
			Participation participation11  = participationRepository.save(new Participation(date, game6, player4));
		};
	}
}
