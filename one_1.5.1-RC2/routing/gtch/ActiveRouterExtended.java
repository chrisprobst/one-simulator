package routing.gtch;

import java.util.Iterator;
import java.util.List;

import routing.ActiveRouter;
import util.Tuple;
import core.Connection;
import core.Message;
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

	public ActiveRouterExtended(ActiveRouterExtended r) {
		super(r);
	}

	/**
	 * Returns the first message in the message priority queue (that is not being sent if excludeMsgBeingSent is true).
	 * 
	 * @param excludeMsgBeingSent
	 *            If true, excludes message(s) that are being sent from the oldest message check (i.e. if oldest message is being sent, the second oldest message is returned)
	 * @return The oldest message or null if no message could be returned (no messages in buffer or all messages in buffer are being sent and exludeMsgBeingSent is true)
	 */
	protected Message getNextMessageToRemove(boolean excludeMsgBeingSent) {
		Message message;
		Iterator<Message> iterator = priorityQueue.iterator();
		while (iterator.hasNext()) {
			message = iterator.next();
			if (excludeMsgBeingSent && isSending(message.getId())) {
				continue; // skip the message(s) that router is sending
			}
			return message;
		}
		return null;
	}

	@Override
	protected Tuple<Message, Connection> tryMessagesForConnected(List<Tuple<Message, Connection>> tuples) {
		Tuple<Message, Connection> tuple = super.tryMessagesForConnected(tuples);
		if (tuple != null) {
			int count = this.forwardTimes.get(tuple.getKey());
			this.forwardTimes.put(tuple.getKey(), ++count);
		}
		return tuple;
	}
}
