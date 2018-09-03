package Players;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import Utilities.Move;
import Utilities.OurStateTree;
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
			if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
				return 0; //it is a draw as both get connect N
			}
			return state.turn == this.turn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
		
		if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
			return state.turn == this.turn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
		
		LinkedList<Integer> values = new LinkedList<Integer>();
		LinkedList<OurStateTree> states = state.getStatesAfterValidMoves();
		if (states.isEmpty()) { // if tie return 0
			return 0;
		}
		if (true) {
		} // TODO add heuristic
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

	   /*
    * Checks if the player is guaranteed to win on their next turn
    * Takes in the player's number and the current tree, returns true if they'll win on their next
    * turn no matter what.
    *
    * Returns false if either the
    *
    * */

    private boolean checkGuaranteedWin(OurStateTree state) {
        int us = state.turn, them = Math.abs(3 - state.turn);
        int[][] board = state.getBoardMatrix();
        int square;
        boolean inStreak = false;
        LinkedList<Streak> streaks = new LinkedList<>();
        Streak curS = new Streak();

        //order is [i][j] -> for this, [y][x] or [row][column]

        //HORIZONTAL TRAVEL
        for (int i = 0; i < state.rows; i++) {
            if (inStreak){
                curS.setRight(state.columns-1, i-1);
                curS.capR = true;
                if (curS.sized(state.winNumber)) {
                    streaks.add(curS);
                }
                inStreak = false;
            }
            for (int j = 0; j < state.columns; j++) {
                square = board[i][j];
                if (square != 0)
                    //System.out.println("square (" + j + ", " + i + "), = " + square);
                    if (square == us) {
                        if (!inStreak) {
                            curS = new Streak();
                            curS.setLeft(j, i);
                            inStreak = true;
                            if (j - 1 >= 0) {
                                if (board[i][j - 1] == 0)
                                    curS.capL = false;
                                else
                                    curS.capL = true;
                            } else {
                                curS.capL = true;
                            }
                        }

                    }//square==us end
                    else if (inStreak) {
                        if (square == them) {
                            curS.setRight(j-1, i);
                            curS.capR = true;

                            if (curS.sized(state.winNumber)) {
                                streaks.add(curS);
                            }
                            inStreak = false;
                        }//square==them end
                        if (square == 0) {
                            if ((j + 1) < state.columns) {
                                if (board[i][j + 1] == us && !curS.gap) {
                                    curS.setGap(j, i);
                                } else {
                                    curS.setRight(j-1, i);
                                    curS.capR = false;
                                    if (curS.sized(state.winNumber)) {
                                        streaks.add(curS);
                                    }
                                    inStreak = false;
                                }
                            }
                        }//square==0 end
                    }
            } //end of j for
        } //end of i for

        //System.out.println("\n-------\n");
        for (Streak s : streaks) {
            System.out.println("left (x, y): (" + s.xL + "," + s.yL + ")"
                    + "| right (x, y): (" + s.xR + "," + s.yR + ")");
            System.out.println("Capped on left, right, gap? " + s.capL + ", " + s.capR + ", " + s.gap);
            System.out.println("Length: " + s.length);
            System.out.println("-------");
        }
        //System.out.println("\n\n--------\n\n");
        return true;
    }

}
