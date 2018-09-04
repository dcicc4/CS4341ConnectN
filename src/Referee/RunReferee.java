package Referee;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Players.AIPlayerDCiccarelli;
import Players.BruteForcePlayer;
import Players.Player;

public class RunReferee {

	public static void main(String[] args) {
		
		// Match parameter
		// You can modify them
		int timeLimit = 60;
		int boardRows = 5;
		int boardColumns = 5;
		int winNumber = 4;
		int battleDurationLimit = 3600;
		// End of modifications
		
		
		//Player player1 = (Player) new BruteForcePlayer("BrutePlayer1", 1, timeLimit);
		//Player player2 = (Player) new  BruteForcePlayer("BrutePlayer2", 2, timeLimit);
		
		Player player1 = (Player) new AIPlayerDCiccarelli("AIPlayer1", 1, timeLimit);
		Player player2 = (Player) new AIPlayerDCiccarelli("AIPlayer2", 2, timeLimit);

		Referee referee = new Referee();
		referee.setOut(System.out);
		referee.initMatch(boardRows, boardColumns, winNumber, timeLimit, player1, player2);
		Callable<Object> judge1 = new Callable<Object>() {
		 	public Object call() {
			  return referee.judge();
		 	}
		};			
		ExecutorService service = Executors.newSingleThreadExecutor();
		
		final Future<Object> future1 = service.submit(judge1);
		int result = -1;
		try {
			result = (int) future1.get(battleDurationLimit, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
}
