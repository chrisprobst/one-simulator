package routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

/**
 * Implementation of GTCH with ProphetV2Router
 * 
 * @author Andre Ippisch
 */
public class ProphetV2RouterWithGTCH extends ProphetV2Router {
	/**
	 * mapping from id of Message ({@link core.Message.id}) to Message itself (@link {@link core.Message}
	 */
	private Map<String, Message> mapIdMessage;
	/**
	 * mapping from message (@link {@link Message}) to forwarding time amount of this node
	 */
	private Map<Message, Integer> forwardTimes;

	public ProphetV2RouterWithGTCH(Settings s) {
		super(s);
	}

	public ProphetV2RouterWithGTCH(ProphetV2RouterWithGTCH r) {
		super(r);
	}

	@Override
	public void init(DTNHost host, List<MessageListener> mListeners) {
		super.init(host, mListeners);
		this.forwardTimes = new HashMap<Message, Integer>();
		this.mapIdMessage = new HashMap<String, Message>();
	}

	@Override
	protected void addToMessages(Message m, boolean newMessage) {
		if (!this.forwardTimes.containsKey(m)) {
			this.mapIdMessage.put(m.getId(), m);
			this.forwardTimes.put(m, 0);
		}
		super.addToMessages(m, newMessage);
	}

	@Override
	protected Message removeFromMessages(String id) {
		this.forwardTimes.remove(this.mapIdMessage.get(id));
		this.mapIdMessage.remove(id);
		return super.removeFromMessages(id);
	}
}
