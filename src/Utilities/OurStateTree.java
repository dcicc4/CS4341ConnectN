package Utilities;

import java.util.LinkedList;

public class OurStateTree extends StateTree{
	public boolean isMyTurn;


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
		// TODO Auto-generated method stub
		return null;
	}

	

}
