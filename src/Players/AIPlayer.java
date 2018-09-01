package Players;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import Referee.Referee;
import Utilities.Move;
import Utilities.OurStateTree;
import Utilities.StateTree;

public class AIPlayer extends Player {

	public AIPlayer(String n, int t, int l) {
		super(n, t, l);
	}

	/**
	 * @see Players.Player#getMove(Utilities.StateTree)
	 */
	@Override
	public Move getMove(StateTree state) {
		HashMap<Integer, OurStateTree> map = new HashMap<Integer, OurStateTree>();
		OurStateTree myState = new OurStateTree(state, this.turn);
		for (OurStateTree aState: myState.getStatesAfterValidMoves()) {
			map.put(this.getValueOfState(aState, null, null,0), aState);
		}
		Integer max = Collections.max(map.keySet());
		System.out.println(max);
		return map.get(max).lastMove;
	}
	public int getValueOfState(OurStateTree state, Integer min, Integer max, int depth) {

			if (this.containsWinForPlayerNumber(Math.abs(3-state.turn), state)) { // containsDefeat checks if the state
				//state.display();
				if (0==Referee.checkForWinner(state)) {
					return 0;
				}
				return state.turn == this.turn ? -1 : 1;
			}


		LinkedList<Integer> values = new LinkedList<Integer>();
		LinkedList<OurStateTree> states = state.getStatesAfterValidMoves();
		if(states.isEmpty()) { //if tie return 0
			return 0;
		}
		if (true){} //TODO add heuristic
		Integer minLower = min;
		Integer maxLower = max;

		for (OurStateTree aState : states) {
			if (aState.turn != this.turn) {
				int currentValue = getValueOfState(aState, minLower, maxLower, depth++);
				values.push(currentValue);
				if (minLower != null && currentValue < minLower) {
					minLower = currentValue;
				}
				if ((min != null && currentValue <= min)|| (max != null && currentValue >= max)) {
					return currentValue;
				}
			} else {
				int currentValue = getValueOfState(aState, minLower, maxLower, depth++);
				values.push(currentValue);
				if (maxLower != null && currentValue > maxLower) {
					maxLower = currentValue;
				}
				if ((min != null && currentValue <= min) || (max != null && currentValue >= max)) {
					return currentValue;
				}
			}
			
		}
		return (state.turn != this.turn) ? Collections.min(values) : Collections.max(values);
	}

	private boolean containsWinForPlayerNumber(int playerNumber, OurStateTree state) {
		int row = state.getRowForLastMove();
		if (state.lastMove.getPop()) { //for pop check if each piece in that column is now a victory
			boolean popWin = false;
			for (int i = 0;  i < row; i++) {
				popWin = popWin || checkHorizontal(playerNumber, state, i)|| checkUpDiagnol(playerNumber, state, i)|| checkDownDiagnol(playerNumber, state, i);
			}
			return popWin;
		} else {
			boolean val = checkVertical(playerNumber, state, row) || checkHorizontal(playerNumber, state, row) || checkUpDiagnol(playerNumber, state, row)|| checkDownDiagnol(playerNumber, state, row);
		return val;
		}

	}
	

	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the up right from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkDownDiagnol(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		int i = column;  
		int j = row;
		while ( i< state.getBoardMatrix()[0].length && j>=0 && state.getBoardMatrix()[j][i] == playerNumber) {
				i++;
				j--;
			}
		int sum = i-column;
		i=column;
		j = row;
		while ( i>=0 && j < state.getBoardMatrix().length && state.getBoardMatrix()[j][i] == playerNumber) {
				i--;
				j++;
		}
		sum+=(column - i);
		return (sum-1) >= state.winNumber;
		
		
	}
	
	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the up right from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkUpDiagnol(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		int i = column;  
		int j = row;
		while ( i< state.getBoardMatrix()[0].length && j < state.getBoardMatrix().length && state.getBoardMatrix()[j][i] == playerNumber) {
				i++;
				j++;
			}
		int sum = i-column;
		i=column;
		j = row;
		while ( i>=0 && j>=0 && state.getBoardMatrix()[j][i] == playerNumber) {
				i--;
				j--;
		}
		sum+=(column - i);
		return (sum-1) >= state.winNumber;
		
		
	}
	
	/**
	 * Checks if the player with number playerNumber has won in the state with horizontal 
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkHorizontal(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();

		int i = column;  
		while ( i< state.getBoardMatrix()[0].length && state.getBoardMatrix()[row][i] == playerNumber) {
				i++;
			}
		int sum = i-column;
		i=column;
		while ( i>=0 && state.getBoardMatrix()[row][i] == playerNumber) {
				i--;
		}
		sum+=(column - i);
		return (sum-1) >= state.winNumber;
	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a vertical connect
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkVertical(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (row < state.winNumber - 1) {
			return false;
		}
		for (int i = row; i > (row - state.winNumber); i--) {
			if (state.getBoardMatrix()[i][column] != playerNumber) {
				return false;
			}
		}

		return true;
	}

}
