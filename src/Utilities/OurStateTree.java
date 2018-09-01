package Utilities;

import java.util.LinkedList;

public class OurStateTree extends StateTree {
	public Move lastMove;
	private Integer lastMoveRow;

	public OurStateTree(StateTree t, int playerNumber) {
		super(t.rows, t.columns, t.winNumber, t.turn, t.pop1, t.pop2, t);
		this.copyIntoBoardMatrix(t.getBoardMatrix());
	}

	public OurStateTree(OurStateTree t) {
		super(t.rows, t.columns, t.winNumber, t.turn, t.pop1, t.pop2, t);
		this.copyIntoBoardMatrix(t.boardMatrix);


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
		//loops through each column. First makes drop moves, then checks and makes pop moves if possible.
		for (int i=0; i < this.columns; i++){
			//if the column is a valid move, drop a piece there and add that state to the list
			if (validMove(new Move(false, i))){
				stateList.add(new OurStateTree(this, turn));
				stateList.getLast().lastMove = new Move(false, i);
				stateList.getLast().makeMove(new Move(false, i));
			}
			//if the column is a valid move, pop the bottom piece there and add that state to the list
			if (validMove(new Move(true, i))){
				stateList.add(new OurStateTree(this, turn));
				stateList.getLast().lastMove = new Move(true, i);
				stateList.getLast().makeMove(new Move(true, i));
			}
		}
		return stateList;
	}

	public int getRowForLastMove() {
		if (lastMoveRow == null) {
			int tempVal;
			int index = 0;
			do {
				if (index != boardMatrix.length) {
					tempVal = boardMatrix[index][lastMove.getColumn()];
				}else {
					tempVal = 0;
				}
				index ++;
			} while (tempVal != 0);
			lastMoveRow = index - 2;
		}
		return lastMoveRow;
	}

	/* (non-Javadoc)
	 * @see Utilities.StateTree#makeMove(Utilities.Move)
	 */
	@Override
	public void makeMove(Move move) {
		super.makeMove(move);
	}

}
