package gtch;

import routing.MessageRouter;
import core.Connection;
import core.Settings;

/**
 * This is an extension of the MessageRouter
 * 
 * @author Andre Ippisch
 */
public class MessageRouterExtended extends MessageRouter {

	public MessageRouterExtended(Settings s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	public MessageRouterExtended(MessageRouter r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void changedConnection(Connection con) {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageRouter replicate() {
		// TODO Auto-generated method stub
		return null;
	}

}
