package Players;

import java.util.Collections;
import java.util.LinkedList;

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
		OurStateTree myState = new OurStateTree(state, this.turn);
		// TODO Auto-generated method stub
		return null;
	}

	public int getValueOfState(OurStateTree state, Integer min, Integer max) {

		if (state.isMyTurn) { // containsVictory checks if the state has connectedNeeded number of tiles in
								// row for AI
			if (this.containsWinForPlayerNumber(this.turn, state))
				return 1;
		} else {
			if (this.containsWinForPlayerNumber(Math.abs(3 - this.turn), state)) // containsDefeat checks if the state
																					// has connectedNeeded number of
																					// tiles in row
				// for opponent
				return -1;
		}


		LinkedList<Integer> values = new LinkedList<Integer>();
		LinkedList<OurStateTree> states = state.getStatesAfterValidMoves();
		if(states.isEmpty()) { //if tie return 0
			return 0;
		}
		for (OurStateTree aState : states) {
			if (state.isMyTurn) {
				int currentValue = getValueOfState(aState, min, max);
				values.push(currentValue);
				if ((min != null && currentValue >= min) || (max != null && currentValue <= max))
					return currentValue;
				max = Collections.max(values);
			} else {
				int currentValue = getValueOfState(aState, min, max);
				values.push(currentValue);
				if ((min != null && currentValue >= min) || (max != null && currentValue <= max))
					return currentValue;
				min = Collections.min(values);
			}
			
		}
		return state.isMyTurn ? max : min;
	}

	private boolean containsWinForPlayerNumber(int playerNumber, OurStateTree state) {
		int row = state.getRowForLastMove();
		if (state.lastMove.getPop()) { //for pop check if each piece in that column is now a victory
			boolean popValid = true;
			for (int i = 0;  i < row; i++) {
				popValid = popValid || checkLeftHorizontal(playerNumber, state, i)
						|| checkRightHorizontal(playerNumber, state, i) || checkLeftDown(playerNumber, state, i)
						|| checkLeftUp(playerNumber, state, i) || checkRightUp(playerNumber, state, i)
						|| checkRightDown(playerNumber, state, i);
			}
			return popValid;
		} else {
			return checkVertical(playerNumber, state, row) || checkLeftHorizontal(playerNumber, state, row)
					|| checkRightHorizontal(playerNumber, state, row) || checkLeftDown(playerNumber, state, row)
					|| checkLeftUp(playerNumber, state, row) || checkRightUp(playerNumber, state, row)
					|| checkRightDown(playerNumber, state, row);
		}

	}
	
	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the down left from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkLeftDown(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (column < state.winNumber - 1 || row < state.winNumber - 1) {
			return false;
		}
		int j = row;
		for (int i = column; i > (column - state.winNumber); i--) {
			if (state.getBoardMatrix()[j][i] != playerNumber) {
				return false;
			}
			j--;
		}
		return true;
	}
	
	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the down right from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkRightDown(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (state.columns - column < state.winNumber || row < state.winNumber - 1) {
			return false;
		}
		int j = row;
		for (int i = column; i < (column + state.winNumber); i++) {
			if (state.getBoardMatrix()[j][i] != playerNumber) {
				return false;
			}
			j--;
		}
		return true;
	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the up left from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkLeftUp(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (column < state.winNumber - 1 || state.rows - row < state.winNumber) {
			return false;
		}
		int j = row;
		for (int i = column; i > (column - state.winNumber); i--) {
			if (state.getBoardMatrix()[j][i] != playerNumber) {
				return false;
			}
			j++;
		}
		return true;
	}

	/**
	 * Checks if the player with number playerNumber has won in the state with a diagnol to the up right from piece placed
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkRightUp(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (state.columns - column < state.winNumber || state.rows - row < state.winNumber) {
			return false;
		}
		int j = row;
		for (int i = column; i < (column + state.winNumber); i++) {
			if (state.getBoardMatrix()[j][i] != playerNumber) {
				return false;
			}
			j++;
		}
		return true;
	}
	
	/**
	 * Checks if the player with number playerNumber has won in the state with horizontal to the left connect
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkLeftHorizontal(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (column < state.winNumber - 1) {
			return false;
		}
		for (int i = column; i > (column - state.winNumber); i--) {
			if (state.getBoardMatrix()[row][i] != playerNumber) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the player with number playerNumber has won in the state with horizontal to the right connect
	 * @param playerNumber
	 * @param state
	 * @param row
	 * @return
	 */
	private boolean checkRightHorizontal(int playerNumber, OurStateTree state, int row) {
		int column = state.lastMove.getColumn();
		if (state.columns - column < state.winNumber) {
			return false;
		}
		for (int i = column; i < (column + state.winNumber); i++) {
			if (state.getBoardMatrix()[row][i] != playerNumber) {
				return false;
			}
		}
		return true;
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
