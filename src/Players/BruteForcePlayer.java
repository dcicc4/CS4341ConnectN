package Players;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import Utilities.Move;
import Utilities.StateTreeDCiccarelli;
import Utilities.StateTree;
import Utilities.Streak;

public class BruteForcePlayer extends Player {

	public BruteForcePlayer(String n, int t, int l) {
		super(n, t, l);
	}

	/**
	 * @see Players.Player#getMove(Utilities.StateTree)
	 */
	@Override
	public Move getMove(StateTree state) {
		HashMap<Integer, StateTreeDCiccarelli> map = new HashMap<Integer, StateTreeDCiccarelli>();
		StateTreeDCiccarelli myState = new StateTreeDCiccarelli(state, this.turn);
		for (StateTreeDCiccarelli aState : myState.getStatesAfterValidMoves()) {
			map.put(this.getValueOfState(aState, null, null, 0), aState);
		}
		Integer max = Collections.max(map.keySet());
		System.out.println(max);
		return map.get(max).lastMove;
	}

	public int getValueOfState(StateTreeDCiccarelli state, Integer min, Integer max, int depth) {

		if (this.containsWinForPlayerNumber(Math.abs(3 - state.turn), state)) { // containsDefeat checks if the state
			if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
				return 0; //it is a draw as both get connect N
			}
			return state.turn == this.turn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
		
		if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
			return state.turn == this.turn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
		
		LinkedList<Integer> values = new LinkedList<Integer>();
		LinkedList<StateTreeDCiccarelli> states = state.getStatesAfterValidMoves();
		if (states.isEmpty()) { // if tie return 0
			return 0;
		}
		if (true) {
		} // TODO add heuristic
		Integer minLower = min;
		Integer maxLower = max;

		for (StateTreeDCiccarelli aState : states) {
			if (aState.turn != this.turn) {
				int currentValue = getValueOfState(aState, minLower, maxLower, depth++);
				values.push(currentValue);
				if (minLower != null && currentValue < minLower) {
					minLower = currentValue;
				}
				if ((min != null && currentValue <= min) || (max != null && currentValue >= max)) {
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

	private boolean containsWinForPlayerNumber(int playerNumber, StateTreeDCiccarelli state) {
		int row = state.getRowForLastMove();
		if (state.lastMove.getPop()) { // for pop check if each piece in that column is now a victory
			boolean popWin = false;
			for (int i = 0; i < row; i++) {
				popWin = popWin || checkHorizontal(playerNumber, state, i) || checkUpDiagnol(playerNumber, state, i)
						|| checkDownDiagnol(playerNumber, state, i);
			}
			return popWin;
		} else {
			boolean val = checkVertical(playerNumber, state, row) || checkHorizontal(playerNumber, state, row)
					|| checkUpDiagnol(playerNumber, state, row) || checkDownDiagnol(playerNumber, state, row);
			return val;
		}

	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a
	 * diagnol to the up right from piece placed
	 * 
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkDownDiagnol(int playerNumber, StateTreeDCiccarelli state, int row) {
		int column = state.lastMove.getColumn();
		int i = column;
		int j = row;
		while (i < state.getBoardMatrix()[0].length && j >= 0 && state.getBoardMatrix()[j][i] == playerNumber) {
			i++;
			j--;
		}
		int sum = i - column;
		i = column;
		j = row;
		while (i >= 0 && j < state.getBoardMatrix().length && state.getBoardMatrix()[j][i] == playerNumber) {
			i--;
			j++;
		}
		sum += (column - i);
		return (sum - 1) >= state.winNumber;

	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a
	 * diagnol to the up right from piece placed
	 * 
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkUpDiagnol(int playerNumber, StateTreeDCiccarelli state, int row) {
		int column = state.lastMove.getColumn();
		int i = column;
		int j = row;
		while (i < state.getBoardMatrix()[0].length && j < state.getBoardMatrix().length
				&& state.getBoardMatrix()[j][i] == playerNumber) {
			i++;
			j++;
		}
		int sum = i - column;
		i = column;
		j = row;
		while (i >= 0 && j >= 0 && state.getBoardMatrix()[j][i] == playerNumber) {
			i--;
			j--;
		}
		sum += (column - i);
		return (sum - 1) >= state.winNumber;

	}

	/**
	 * Checks if the player with number playerNumber has won in the state with
	 * horizontal
	 * 
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkHorizontal(int playerNumber, StateTreeDCiccarelli state, int row) {
		int column = state.lastMove.getColumn();

		int i = column;
		while (i < state.getBoardMatrix()[0].length && state.getBoardMatrix()[row][i] == playerNumber) {
			i++;
		}
		int sum = i - column;
		i = column;
		while (i >= 0 && state.getBoardMatrix()[row][i] == playerNumber) {
			i--;
		}
		sum += (column - i);
		return (sum - 1) >= state.winNumber;
	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a
	 * vertical connect
	 * 
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkVertical(int playerNumber, StateTreeDCiccarelli state, int row) {
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
