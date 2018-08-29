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
		int value = 0;

		LinkedList<Integer> values = new LinkedList<Integer>();

		for (OurStateTree aState : state.getStatesAfterValidMoves()) {
			if (state.isMyTurn) {
				int currentValue = getValueOfState(aState, null, max);
				values.push(currentValue);
				if (min != null && value >= min) {
					return currentValue;
				}
				max = Collections.max(values);
			} else {
				int currentValue = getValueOfState(aState, min, null);
				values.push(currentValue);
				if (max != null && value <= max)
					return currentValue;
				min = Collections.min(values);
			}
			return state.isMyTurn ? max : min;
		}
		return value;
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
