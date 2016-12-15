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
			Player player1 = new Player("j.bauer@ctu.gov");
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("t.almeida@ctu.gov");
			Player player4 = new Player("d.palmer@whitehouse.gov");
			Game game1 = new Game(date);
			Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(date.toInstant().plusSeconds(3600*2)));
			Game game4 = new Game(Date.from(date.toInstant().plusSeconds(3600*3)));
			Game game5 = new Game(Date.from(date.toInstant().plusSeconds(3600*4)));
			Game game6 = new Game(Date.from(date.toInstant().plusSeconds(3600*5)));
			Participation participation1 = new Participation(date);
			Participation participation2 = new Participation(date);
			Participation participation3 = new Participation(date);
			Participation participation4 = new Participation(date);
			Participation participation5 = new Participation(date);
			Participation participation6 = new Participation(date);
			Participation participation7 = new Participation(date);
			Participation participation8 = new Participation(date);
			Participation participation9 = new Participation(date);
			Participation participation10  = new Participation(date);
			Participation participation11  = new Participation(date);

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);

			participation1.setPlayer(player1);
			participation1.setGame(game1);
			participationRepository.save(participation1);

			participation2.setGame(game1);
			participationRepository.save(participation2);

			participation3.setPlayer(player1);
			participation3.setGame(game2);
			participationRepository.save(participation3);

			participation4.setPlayer(player2);
			participation4.setGame(game2);
			participationRepository.save(participation4);

			participation5.setPlayer(player2);
			participation5.setGame(game3);
			participationRepository.save(participation5);

			participation6.setPlayer(player3);
			participation6.setGame(game3);
			participationRepository.save(participation6);

			participation7.setPlayer(player1);
			participation7.setGame(game4);
			participationRepository.save(participation7);

			participation8.setPlayer(player2);
			participation8.setGame(game4);
			participationRepository.save(participation8);

			participation9.setPlayer(player3);
			participation9.setGame(game5);
			participationRepository.save(participation9);

			participation10.setPlayer(player1);
			participation10.setGame(game5);
			participationRepository.save(participation10);

			participation11.setPlayer(player4);
			participation11.setGame(game6);
			participationRepository.save(participation11);
		};
	}
}
