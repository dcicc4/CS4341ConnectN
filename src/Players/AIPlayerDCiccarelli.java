package Players;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import Utilities.Move;
import Utilities.StateTreeDCiccarelli;
import Utilities.StateTree;
import Utilities.Streak;

/**
 * This AI makes a move by dynamically determining the max depth it can reach in
 * a time limit, given a number of rows, then goes that deep in the tree using
 * min-max and alpha beta pruning, searching for win or lose cases, then runs
 * the heuristic when the depth limit for the given time is reached.
 *
 * @author Drew Ciccarelli
 */
public class AIPlayerDCiccarelli extends Player {
    final double DEPTH_RATIO = 2.75; // Converts the time-columns growth ratio to the depth the algorithm can search
    int maxDepth;

    /**
     * @see Players.Player#Player(String, int, int)
     */
    public AIPlayerDCiccarelli(String n, int t, int l) {
        super(n, t, l);
    }

    /**
     * @see Players.Player#getMove(Utilities.StateTree)
     */
    @Override
    public Move getMove(StateTree state) {
        //Get max depth using log functions as the worst case time efficienty is t=b^l so log(t)/log(b)*k = l
        this.maxDepth = (int) (DEPTH_RATIO * Math.log(this.timeLimit) / Math.log(state.columns));
        //Map to store values to moves
        HashMap<Integer, StateTreeDCiccarelli> map = new HashMap<Integer, StateTreeDCiccarelli>();
        StateTreeDCiccarelli myState = new StateTreeDCiccarelli(state, this.turn);
        for (StateTreeDCiccarelli aState : myState.getStatesAfterValidMoves()) {
            map.put(this.getValueOfState(aState, null, null, 0), aState);
        }
        //NOTE: chooses between equal value stages randomly
        Integer max = Collections.max(map.keySet());
        return map.get(max).lastMove;
    }

    /**
     * Gets the value of a given state by summing the value of lower states, seeing
     * if a player has won, or running a heuristic
     *
     * @param state The state to get the value of
     * @param min   the min value (alpha) for alpha-beta pruning
     * @param max   the max value (beta) for alpha-beta pruning
     * @param depth The current depth of the tree
     * @return
     */
    public int getValueOfState(StateTreeDCiccarelli state, Integer min, Integer max, int depth) {

        if (this.containsWinForPlayerNumber(Math.abs(3 - state.turn), state)) { // containsDefeat checks if the state
            if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
                return 0; // it is a draw as both get connect N
            }
            return state.turn == this.turn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        if (this.containsWinForPlayerNumber(Math.abs(state.turn), state)) {
            return state.turn == this.turn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        if (checkGuaranteedWin(state)) {
            //System.out.println("good");
            return state.turn == this.turn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            //TODO SET THIS TO RETURN A PROPER VALUE
        }

        LinkedList<Integer> values = new LinkedList<Integer>();
        LinkedList<StateTreeDCiccarelli> states = state.getStatesAfterValidMoves();
        if (states.isEmpty()) { // if tie return 0
            return 0;
        }
        if (depth == this.maxDepth) {
            return this.getHeuristicValue(state);
        }
        Integer minLower = min;
        Integer maxLower = max;

        for (StateTreeDCiccarelli aState : states) {
            if (aState.turn != this.turn) {
                int currentValue = getValueOfState(aState, minLower, maxLower, depth + 1);
                values.push(currentValue);
                if (minLower != null && currentValue < minLower) {
                    minLower = currentValue;
                }
                if ((min != null && currentValue <= min) || (max != null && currentValue >= max)) {
                    return currentValue;
                }
            } else {
                int currentValue = getValueOfState(aState, minLower, maxLower, depth + 1);
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

    /**
     * Gets the value of the current state using a heuristic
     *
     * @param state The state to evaluate
     * @return The value of a state
     */
    private int getHeuristicValue(StateTreeDCiccarelli state) {
        int value = 0;
        int[][] board = state.getBoardMatrix();
        int[][] columns = new int[board[0].length][board.length];
        for (int i = 0; i < board.length; i++) {
            value += getValueOfVector(Arrays.stream(board[i]).boxed().toArray(Integer[]::new), state.winNumber,
                    this.turn);
            value -= getValueOfVector(Arrays.stream(board[i]).boxed().toArray(Integer[]::new), state.winNumber,
                    Math.abs(3 - this.turn));
            for (int j = 0; j < board[0].length; j++) {
                columns[j][i] = board[i][j];
            }
        }
        for (int i = 0; i < columns.length; i++) {
            value += getValueOfVector(Arrays.stream(columns[i]).boxed().toArray(Integer[]::new), state.winNumber,
                    this.turn);
            value -= getValueOfVector(Arrays.stream(columns[i]).boxed().toArray(Integer[]::new), state.winNumber,
                    Math.abs(3 - this.turn));
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
            for (int i = 0; i < board[0].length; i++) {
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

                Integer[] downD = Arrays.copyOf(downDiagnols.get(i).toArray(), downDiagnols.get(i).size(),
                        Integer[].class);
                Integer[] upD = Arrays.copyOf(upDiagnols.get(i).toArray(), upDiagnols.get(i).size(), Integer[].class);
                value += this.getValueOfVector(downD, state.winNumber, this.turn);
                value += this.getValueOfVector(upD, state.winNumber, this.turn);

                value -= this.getValueOfVector(downD, state.winNumber, Math.abs(3 - this.turn));
                value -= this.getValueOfVector(upD, state.winNumber, Math.abs(3 - this.turn));

            }
        }

        return value;
    }

    /**
     * Gets the value of a vector on the board for a player
     *
     * @param vector A single straight line across the entire board, can be diagnol,
     *               horizontal, or vertical
     * @param winNum The number in a row
     * @param player The player to evaluate the value for
     * @return
     */
    private int getValueOfVector(Integer[] vector, int winNum, int player) {
        int value = 0;
        int filledIn = 0;
        int leftSize = 0;
        int rightSize = 0;
        int holes = 0;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] != 0) {
                if (player != vector[i]) {
                    value += tallyStreakValue(winNum, leftSize, rightSize, holes, filledIn);
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
        value += tallyStreakValue(winNum, leftSize, rightSize, holes, filledIn);
        return value;
    }

    /**
     * Gets the value of a single streak (between enemy pieces and or endges
     *
     * @param winNum    Number in row needed to win
     * @param leftSize  The number of empty spots to left of the player's pieces
     * @param rightSize The number of empty spots to right of the player's pieces
     * @param holes     The number of holes, that is empty spots between a player's pieces
     * @param filledIn  The number of the player's pieces in the streak
     * @return
     */
    private int tallyStreakValue(int winNum, int leftSize, int rightSize, int holes, int filledIn) {
        int streakSize = leftSize + rightSize + holes + filledIn;
        if (streakSize < winNum || filledIn == 0) {
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

    /**
     * Determines if the board contains a win for a player
     *
     * @param playerNumber the player to check the win for
     * @param state        The state to check for a win on
     * @return If playerNumber has won at this stage
     */
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
     * diagnol to the down left direction
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
     * diagnol to the up right directions
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
     * Checks if the player with number playerNumber has won in the state with a
     * horizontal chain
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


	   /*
    * Checks if the player is guaranteed to win on their next turn
    * Takes in the player's number and the current tree, returns true if they'll win on their next
    * turn no matter what.
    *
    * Returns false if either the
    *
    * */

    private boolean checkGuaranteedWin(StateTreeDCiccarelli state) {
        int us = state.turn, them = Math.abs(3 - state.turn);
        int[][] board = state.getBoardMatrix();
        int square;
        boolean inStreak = false;
        LinkedList<Streak> streaks = new LinkedList<>();
        Streak curS = new Streak();

        //order is [i][j] -> for this, [y][x] or [row][column]

        //HORIZONTAL TRAVEL
        for (int r = 0; r < state.rows; r++) {
            for (int c = 0; c < state.columns; c++) {
                square = board[r][c];
                //if (square != 0)
                //System.out.println("square (" + j + ", " + i + "), = " + square);
                if (square == us) {
                    if (!inStreak) {
                        curS = new Streak();
                        curS.setLeft(c, r);
                        inStreak = true;
                        if (c - 1 >= 0) {
                            if (board[r][c - 1] == 0)
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
                        curS.setRight(c - 1, r);
                        curS.capR = true;

                        if (curS.good(state.winNumber)) {
                            streaks.add(curS);
                        }
                        inStreak = false;
                    }//square==them end
                    if (square == 0) {
                        if ((c + 1) < state.columns) {
                            if (board[r][c + 1] == us && !curS.gap) {
                                curS.setGap(c, r);
                            } else {
                                curS.setRight(c - 1, r);
                                curS.capR = false;
                                if (curS.good(state.winNumber)) {
                                    streaks.add(curS);
                                }
                                inStreak = false;
                            }
                        }
                    }//square==0 end
                }
            } //end of c for
            if (inStreak) {
                curS.setRight(state.columns - 1, r);
                curS.capR = true;
                if (curS.good(state.winNumber)) {
                    streaks.add(curS);
                }
                inStreak = false;
            }
        } //end of r for

        //VERTICAL TRAVEL
        for (int c = 0; c < state.columns; c++) {
            for (int r = 0; r < state.rows; r++) {
                square = board[r][c];
                /*if (square != 0)
                System.out.println("square (" + j + ", " + i + "), = " + square);*/
                if (square == us) {
                    if (!inStreak) {
                        curS = new Streak();
                        curS.setLeft(c, r);
                        inStreak = true;
                        curS.capL = true;
                    }
                }//square==us end
                else if (inStreak) {
                    if (square == them) {
                        curS.setRight(c, r - 1);
                        curS.capR = true;

                        if (curS.good(state.winNumber)) {
                            streaks.add(curS);
                        }
                        inStreak = false;
                    }//square==them end
                    if (square == 0) {
                        curS.setRight(c, r - 1);
                        curS.capR = false;
                        if (curS.good(state.winNumber)) {
                            streaks.add(curS);
                        }
                        inStreak = false;
                    }//square==0 end
                }
            } //end of r for
            if (inStreak) {
                curS.setRight(c, state.rows);
                curS.capR = true;
                if (curS.good(state.winNumber)) {
                    streaks.add(curS);
                }
                inStreak = false;
            }
        } //end of c for

        /*
        //DIAGONAL TRAVEL UPLEFT->DOWNRIGHT
        //wip
        int offset = 0;
        for (int s = 1; s < (state.rows + state.columns - 1); s++) {
            //order is [i][j] -> for this, [y][x] or [row][column]
            int y = s - offset, x = s;
            while (y < state.rows && x < state.columns) {
                square = board[y][x];
                if (square == 1)
                    inStreak = true;
                if (square != 1) {
                    if (square == 2) {
                        ///end
                    }
                    if (square == 0) {
                        //test for gap, else end
                    }

                }
                if (inStreak) {
                    curS.setRight(0,0);
                    curS.capR = true;
                    if (curS.good(state.winNumber)) {
                        streaks.add(curS);
                    }
                    inStreak = false;
                }

            }
            if (s >= state.rows)
                offset++;
        }
*/
        /*for (Streak s : streaks) {
            System.out.println("left/start (x, y): (" + s.xL + "," + s.yL + ")"
                    + "| right/end (x, y): (" + s.xR + "," + s.yR + ")");
            System.out.println("Cap left: " + s.capL + ", right: " + s.capR + ", gap: " + s.gap);
            System.out.println("Length: " + s.length);
            System.out.println("-------");
        }*/

        for (Streak s : streaks) {
            if (!s.capL && !s.capR) {
                //System.out.println("Cap left: " + s.capL + ", right: " + s.capR + ", gap: " + s.gap);
                if (s.yL > 0) {
                    if (board[s.yL - 1][s.xL] != 0 || board[s.yR - 1][s.xR] != 0) {
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < streaks.size(); i++) {
            for (int j = i + 1; j < streaks.size(); j++) {
                Streak s1 = streaks.get(i);
                Streak s2 = streaks.get(j);
                if (s1.posPair(s2)) {
                    /*System.out.println("s1: (" + s1.xL + "," + s1.yL + ")"
                            + "- (" + s1.xR + "," + s1.yR + ")"
                            + " | s2: (" + s2.xL + "," + s2.yL + ")"
                            + "- (" + s2.xR + "," + s2.yR + ")");*/
                    if (!s1.capL && !s1.gap) {
                        if (!s2.capL) {
                            if (s1.xL == s2.xL)
                                return true;
                        }
                        if (!s2.capR) {
                            if (Math.abs(s1.xL - s2.xR) == 2)
                                return true;
                        }
                    }
                    if (!s1.capR && !s1.gap) {
                        if (!s2.capR) {
                            if (s1.xR == s2.xR)
                                return true;
                        }
                        if (!s2.capL) {
                            if (Math.abs(s1.xR - s2.xL) == 2)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
