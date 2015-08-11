package gtch;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import routing.ProphetV2Router;

import util.Priority;
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
	private static final QUEUING_STRATEGY_POSSIBILITIES QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.TTL;

	private enum QUEUING_STRATEGY_POSSIBILITIES {
		TTL, HOPCOUNT, SIZE
	}

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

	@Override
	protected boolean makeRoomForMessage(int size) {
		if (size > this.getBufferSize()) {
			return false; // message too big for the buffer
		}
			
		int freeBuffer = this.getFreeBufferSize();
		if(freeBuffer > size) {
			return true;
		}
		int sizeToDelete = size - freeBuffer;
		
		Comparator<Message> comparator;
		switch(QUEUING_STRATEGY) {
		case TTL:
			comparator = Priority.getRemainingTimeToLiveComparator(true);
			break;
		case HOPCOUNT:
			comparator = Priority.getHopCountComparator(true);
			break;
		default:
			return super.makeRoomForMessage(size);
		}
		
		Collection<Message> unsortedMessages = this.getMessageCollection();
		Queue<Message> sortedMessages = Priority.getMessageQueue(unsortedMessages, comparator);
		Collection<String> messagesToDrop = Priority.getCollectionOfMessagesToDropForSize(sortedMessages, sizeToDelete);
		
		Iterator<String> iterator = messagesToDrop.iterator();
		while(iterator.hasNext()) {
			deleteMessage(iterator.next(), true);
		}
		
		return true;
	}
}
