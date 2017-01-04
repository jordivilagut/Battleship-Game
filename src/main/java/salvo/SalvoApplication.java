package salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, ParticipationRepository participationRepository, ShipRepository shipRepository, SalvoRepository salvoRepository) {
		return (args) -> {
			Date date = new Date();

			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov"));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov"));
			Player player3 = playerRepository.save(new Player("t.almeida@ctu.gov"));
			Player player4 = playerRepository.save(new Player("d.palmer@whitehouse.gov"));

			player1.setPassword("24");
			player2.setPassword("42");
			player3.setPassword("kb");
			player4.setPassword("mole");
			//players need to be saved to repository again now!!!

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

			List<String> location1 = Arrays.asList("E1", "F1", "G1");
			List<String> location2 = Arrays.asList("B4", "B5");
			List<String> location3 = Arrays.asList("B10", "C10", "D10");
			List<String> location4 = Arrays.asList("F8", "F9");
			List<String> location5 = Arrays.asList("G5", "H5", "I5");
			List<String> location6= Arrays.asList("C6", "C7");
			List<String> location7 = Arrays.asList("A2", "A3", "A4");
			List<String> location8 = Arrays.asList("G8", "H8");
			List<String> location9 = Arrays.asList("B9", "C9", "D9");
			List<String> location10 = Arrays.asList("C1", "C2");


			Ship ship = new Ship("Carrier", location1);
			participation1.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Submarine", location2);
			participation1.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Destroyer", location3);
			participation1.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Submarine", location4);
			participation1.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Destroyer", location5);
			participation1.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Patrol", location6);
			participation2.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Submarine", location7);
			participation2.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Carrier", location8);
			participation2.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Submarine", location9);
			participation2.addShip(ship);
			shipRepository.save(ship);
			ship = new Ship("Battleship", location10);
			participation2.addShip(ship);
			shipRepository.save(ship);

			Salvo salvo = new Salvo(1, Arrays.asList("B4", "B5"));
			participation1.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(2, Arrays.asList("D4", "H5"));
			participation1.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(3, Arrays.asList("E3", "B1"));
			participation1.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(4, Arrays.asList("A1", "A6"));
			participation1.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(1, Arrays.asList("J1", "J9"));
			participation2.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(2, Arrays.asList("B4", "H10"));
			participation2.addSalvo(salvo);
			salvoRepository.save(salvo);

			salvo = new Salvo(3, Arrays.asList("A8", "A9"));
			participation2.addSalvo(salvo);
			salvoRepository.save(salvo);

			participation11.setScore(1);
			participationRepository.save(participation11);
		};
	}
}
