package routing;

import core.Settings;

/**
 * This is an extension of the ActiveRouter
 * 
 * @author Andre Ippisch
 */
public class ActiveRouterExtended extends ActiveRouter {

	public ActiveRouterExtended(Settings s) {
		super(s);
	}

	public ActiveRouterExtended(ActiveRouter r) {
		super(r);
	}

	@Override
	public MessageRouter replicate() {
		return null;
	}

}
