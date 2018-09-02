package Players;

import java.util.Arrays;
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
		for (OurStateTree aState : myState.getStatesAfterValidMoves()) {
			map.put(this.getValueOfState(aState, null, null, 0), aState);
		}
		Integer max = Collections.max(map.keySet());
		System.out.println(max);
		return map.get(max).lastMove;
	}

	public int getValueOfState(OurStateTree state, Integer min, Integer max, int depth) {

		if (this.containsWinForPlayerNumber(Math.abs(3 - state.turn), state)) { // containsDefeat checks if the state
			if (0 == Referee.checkForWinner(state)) {
				return 0;
			}
			return state.turn == this.turn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}

		LinkedList<Integer> values = new LinkedList<Integer>();
		LinkedList<OurStateTree> states = state.getStatesAfterValidMoves();
		if (states.isEmpty()) { // if tie return 0
			return 0;
		}
		if (depth == 2) {
			return this.getHeuristicValue(state);
		}
		Integer minLower = min;
		Integer maxLower = max;

		for (OurStateTree aState : states) {
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

	private int getHeuristicValue(OurStateTree state) {
		int value = 0;
		int[][] board = state.getBoardMatrix();
		int[][] columns = new int[board[0].length][board.length];
		for (int i = 0; i < board.length; i++) {
			value += getValueOfVector(Arrays.stream( board[i] ).boxed().toArray( Integer[]::new ), state.winNumber, this.turn);
			value -= getValueOfVector(Arrays.stream( board[i] ).boxed().toArray( Integer[]::new ), state.winNumber, Math.abs(3 - this.turn));
			for (int j = 0; j < board[0].length; j++) {
				columns[j][i] = board[i][j];
			}
		}
		for (int i =0; i< columns.length; i ++) {
			value += getValueOfVector(Arrays.stream( columns[i] ).boxed().toArray( Integer[]::new ), state.winNumber, this.turn);
			value -= getValueOfVector(Arrays.stream( columns[i] ).boxed().toArray( Integer[]::new ), state.winNumber, Math.abs(3 - this.turn));
		}
		LinkedList<LinkedList<Integer>> upDiagnols = new LinkedList<LinkedList<Integer>>();
		LinkedList<LinkedList<Integer>> downDiagnols = new LinkedList<LinkedList<Integer>>();
		if (board.length > board[0].length) {
			for (int i = 0; i < board.length; i++) {
				upDiagnols.add(new LinkedList<Integer>());
				downDiagnols.add(new LinkedList<Integer>());

				upDiagnols.get(i).add(board[i][0]);
				downDiagnols.get(i).add(board[i][0]);
				for (int j = 1; j < board[0].length; j++) {
					if (i + j < board.length)
						upDiagnols.get(i).add(board[i + j][j]);
					if (i - j >= 0)
						downDiagnols.get(i).add(board[i - j][j]);
				}

			}
		} else {
			for (int i = 0; i < board[0].length; i++)
			{
				upDiagnols.add(new LinkedList<Integer>());
				downDiagnols.add(new LinkedList<Integer>());
				upDiagnols.get(i).add(board[0][i]);
				downDiagnols.get(i).add(board[0][i]);
				for (int j = 1; j < board.length; j++) {
					if (i + j < board[0].length)
						upDiagnols.get(i).add(board[j][i + j]);
					if (i - j >= 0)
						downDiagnols.get(i).add(board[j][i - j]);
				}
				
				Integer [] downD = Arrays.copyOf( downDiagnols.get(i).toArray(), downDiagnols.get(i).size(), Integer[].class);
				Integer [] upD = Arrays.copyOf(upDiagnols.get(i).toArray(), upDiagnols.get(i).size(), Integer[].class);
				value += this.getValueOfVector(downD, state.winNumber, this.turn);
				value += this.getValueOfVector(upD, state.winNumber, this.turn);
				
				value -= this.getValueOfVector(downD, state.winNumber, Math.abs(3-this.turn));
				value -= this.getValueOfVector(upD, state.winNumber,  Math.abs(3-this.turn));

			}
		}

		return value;
	}

	private int getValueOfVector(Integer[] vector, int winNum, int player) {
		int value = 0;
		int filledIn = 0;
		int leftSize = 0;
		int rightSize = 0;
		int holes = 0;
		for (int i = 0; i < vector.length; i++) {
			if (vector[i] != 0) {
				if (player != vector[i]) {
					value += tallyStreakValue(winNum, leftSize, rightSize, holes, filledIn, player);
					holes = 0;
					leftSize = 0;
					rightSize = 0;
					filledIn = 0;
				} else {
					filledIn++;
					holes += rightSize;
					rightSize = 0;
				}

			}
			if (filledIn == 0) {
				leftSize++;
			} else {
				rightSize++;
			}
		}
		value += tallyStreakValue(winNum, leftSize, rightSize, holes, filledIn, player);
		return value;
	}

	private int tallyStreakValue(int winNum, int leftSize, int rightSize, int holes, int filledIn, int player) {
		int streakSize = leftSize + rightSize + holes + filledIn;
		if (player == 0 || streakSize < winNum || filledIn == 0) {
			return 0;
		}
		int value = (Integer.MAX_VALUE / (winNum * 100)) * filledIn;
		double multiplier = 1;
		if (holes > 1) {
			multiplier -= (1 - (1.0 / (double) holes));
		}
		if (leftSize > 1) {
			multiplier += .1;
		}
		if (rightSize > 1) {
			multiplier += .1;
		}
		if (holes == 1) {
			multiplier += .1;
		}
		return (int) (value * multiplier);
	}

	private boolean containsWinForPlayerNumber(int playerNumber, OurStateTree state) {
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
	private boolean checkDownDiagnol(int playerNumber, OurStateTree state, int row) {
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
	private boolean checkUpDiagnol(int playerNumber, OurStateTree state, int row) {
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
	private boolean checkHorizontal(int playerNumber, OurStateTree state, int row) {
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
