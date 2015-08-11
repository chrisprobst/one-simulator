package gtch;

import routing.ActiveRouter;
import core.Settings;

/**
 * This is an extension of the ActiveRouter
 * 
 * @author Andre Ippisch
 */
public abstract class ActiveRouterExtended extends ActiveRouter {

	public ActiveRouterExtended(Settings s) {
		super(s);
	}

	public ActiveRouterExtended(ActiveRouter r) {
		super(r);
	}
}
