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
			if (this.containsWinForPlayerNumber(Math.abs(3-this.turn), state)) // containsDefeat checks if the state has connectedNeeded number of tiles in row
										// for opponent
				return -1;
		}
		int value = 0;

		LinkedList<Integer> values = new LinkedList<Integer>();

		for (OurStateTree aState : state.getStatesAfterValidMoves()) {

			int currentValue = getValueOfState(aState, min, max); // TODO: Do we have to reset min and max at some
																	// point?

			values.push(currentValue);

			if (state.isMyTurn) {

				if (min != null && value >= min) {

					return currentValue;
				}

				max = Collections.max(values);

			} else {

				if (max != null && value <= max)

					return currentValue;

				min = Collections.min(values);
			}

			return state.isMyTurn ? max : min;
		}
		return value;
	}

	private boolean containsWinForPlayerNumber(int playerNumber, OurStateTree state) {
		if (state.lastMove.getPop()) {
			//TODO implement pop check
		}else {
			//TODO check diagnols 
			return checkVertical(playerNumber, state) || checkLeftHorizontal(playerNumber, state) || checkRightHorizontal(playerNumber,state);
		}

		return false;
	}
private boolean checkLeftHorizontal(int playerNumber, OurStateTree state) {
	int column = state.lastMove.getColumn();
	int row = state.getRowForLastMove();
	if (column < state.winNumber - 1 ) {
		return false;
	}
	for (int i = column; i>(column - state.winNumber); i--) {
		if(state.getBoardMatrix()[row][i]!= playerNumber) {
			return false;
		}
	}
	return true;
	}

private boolean checkRightHorizontal(int playerNumber, OurStateTree state) {
	int column = state.lastMove.getColumn();
	int row = state.getRowForLastMove();
	if (state.columns - column < state.winNumber) {
		return false;
	}
	for (int i = column; i<(column + state.winNumber); i++) {
		if(state.getBoardMatrix()[row][i]!= playerNumber) {
			return false;
		}
	}
	return true;
	}


	private boolean checkVertical(int playerNumber, OurStateTree state) {
		int column = state.lastMove.getColumn();
		int row = state.getRowForLastMove();
		if (row < state.winNumber - 1 ) {
			return false;
		}
		for (int i = row; i>(row - state.winNumber); i--) {
			if(state.getBoardMatrix()[i][column]!= playerNumber) {
				return false;
			}
		}
		return true;
	}
	

}
