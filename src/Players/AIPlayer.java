package Players;

import Utilities.Move;
import Utilities.OurStateTree;
import Utilities.StateTree;

public class AIPlayer extends Player {

	public AIPlayer(String n, int t, int l) {
		super(n, t, l);
	}

	/* (non-Javadoc)
	 * @see Players.Player#getMove(Utilities.StateTree)
	 */
	@Override
	public Move getMove(StateTree state) {
		OurStateTree myState = new OurStateTree (state, this.turn);
		// TODO Auto-generated method stub
		return null;
	}

}
