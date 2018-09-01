package Utilities;

import java.util.LinkedList;

public class OurStateTree extends StateTree {
	public boolean isMyTurn;
	public Move lastMove;
	private Integer lastMoveRow;

	public OurStateTree(StateTree t, int playerNumber) {
		super(t.rows, t.columns, t.winNumber, t.turn, t.pop1, t.pop2, t);
		this.copyIntoBoardMatrix(t.getBoardMatrix());
		isMyTurn = (t.turn == playerNumber);
	}

	public OurStateTree(OurStateTree t) {
		super(t.rows, t.columns, t.winNumber, t.turn, t.pop1, t.pop2, t);
		this.copyIntoBoardMatrix(t.boardMatrix);
		isMyTurn = t.isMyTurn;

	}

	private void copyIntoBoardMatrix(int[][] array) {
		boardMatrix = new int[boardMatrix.length][boardMatrix[0].length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				boardMatrix[i][j] = array[i][j];
			}
		}
	}

	public LinkedList<OurStateTree> getStatesAfterValidMoves() {
		// creating the list for all the  states
		LinkedList<OurStateTree> stateList = new LinkedList<>();
		//loops through each column
		//TODO swap to one big looop
		for (int i=0; i < this.columns; i++){
			//if at least the top row for that column is empty, drop a piece there and add that state to the list
			if (this.boardMatrix[this.rows][i] == 0){ //TODO swap for isvalid
				stateList.add(new OurStateTree(this, turn));
				stateList.getLast().makeMove(new Move(false, i));
				//call this.isValidMove or something like that
			}
		}
		//next, check if pop is allowed. If so, second loop for pop moves
		if ((!pop1 && turn == 1) || (!pop2 && turn == 2)) {
			for (int i = 0; i < this.columns; i++) {
				//if the bottom piece for the column is ours. If so, pop that and add that state to the list
				if (this.boardMatrix[0][i] == turn) {
					stateList.add(new OurStateTree(this, turn));
					stateList.getLast().makeMove(new Move(true, i));
				}
			}
		}
		return stateList;
	}

	public int getRowForLastMove() {
		if (lastMoveRow == null) {
			int tempVal;
			int index = 0;
			do {
				tempVal = boardMatrix[index][lastMove.getColumn()];
			} while (tempVal != 0);
			lastMoveRow = index - 1;
		}
		return lastMoveRow;
	}

}
