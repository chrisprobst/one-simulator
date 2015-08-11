package gtch;

import routing.MessageRouter;
import core.Settings;

/**
 * This is an extension of the MessageRouter
 * 
 * @author Andre Ippisch
 */
public abstract class MessageRouterExtended extends MessageRouter {

	public MessageRouterExtended(Settings s) {
		super(s);
	}

	public MessageRouterExtended(MessageRouter r) {
		super(r);
	}
}
