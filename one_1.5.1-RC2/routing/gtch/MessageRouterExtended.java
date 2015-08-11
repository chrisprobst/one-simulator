package routing.gtch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import routing.MessageRouter;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

/**
 * This is an extension of the MessageRouter
 * 
 * @author Andre Ippisch
 */
public abstract class MessageRouterExtended extends MessageRouter {
	/**
	 * mapping from id of Message ({@link core.Message.id}) to Message itself (@link {@link core.Message}
	 */
	protected Map<String, Message> mapIdMessage;
	/**
	 * mapping from message (@link {@link Message}) to forwarding time amount of this node
	 */
	protected Map<Message, Integer> forwardTimes;
	/**
	 * priority queue for queuing strategies
	 */
	protected Queue<Message> priorityQueue;
	/**
	 * Queuing strategy - setting id ({@value} ). String valued.
	 */
	public static final String QUEUING_STRATEGY_S = "queuingStrategy";

	protected QUEUING_STRATEGY_POSSIBILITIES QUEUING_STRATEGY;

	protected enum QUEUING_STRATEGY_POSSIBILITIES {
		AGE, TTL, HOPCOUNT, MOFO, SIZE
	}

	public MessageRouterExtended(Settings s) {
		super(s);
		setQueuingStrategy(s);
	}

	public MessageRouterExtended(MessageRouterExtended r) {
		super(r);
		this.QUEUING_STRATEGY = r.QUEUING_STRATEGY;
	}

	@Override
	public void init(DTNHost host, List<MessageListener> mListeners) {
		super.init(host, mListeners);
		this.forwardTimes = new HashMap<Message, Integer>();
		this.mapIdMessage = new HashMap<String, Message>();
		this.priorityQueue = new PriorityQueue<Message>(1, getQueuingStrategyComparator());
	}

	@Override
	protected void addToMessages(Message m, boolean newMessage) {
		this.mapIdMessage.put(m.getId(), m);
		this.forwardTimes.put(m, 0);
		super.addToMessages(m, newMessage);
	}

	@Override
	protected Message removeFromMessages(String id) {
		this.forwardTimes.remove(this.mapIdMessage.get(id));
		this.mapIdMessage.remove(id);
		return super.removeFromMessages(id);
	}

	private final void setQueuingStrategy(Settings s) {
		String queuingStrategy = s.getSetting(QUEUING_STRATEGY_S);
		if (queuingStrategy.equals("TTL")) {
			this.QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.TTL;
		} else if (queuingStrategy.equals("HOPCOUNT")) {
			this.QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.HOPCOUNT;
		} else if (queuingStrategy.equals("MOFO")) {
			this.QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.MOFO;
		} else if (queuingStrategy.equals("SIZE")) {
			this.QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.SIZE;
		} else {
			this.QUEUING_STRATEGY = QUEUING_STRATEGY_POSSIBILITIES.AGE;
		}
	}

	private final Comparator<Message> getQueuingStrategyComparator() {
		switch (this.QUEUING_STRATEGY) {
		case TTL:
			return new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					// least remaining time to live first
					return (int) Math.signum(o1.getTtl() - o2.getTtl());
				}
			};
		case HOPCOUNT:
			return new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					// highest hop count first
					return (int) -Math.signum(o1.getHopCount() - o2.getHopCount());
				}
			};
		case MOFO:
			return new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					// most forwarded first
					return (int) -Math.signum(forwardTimes.get(o1) - forwardTimes.get(o2));
				}
			};
		case SIZE:
			return new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					// biggest message first
					return (int) -Math.signum(o1.getSize() - o2.getSize());
				}
			};
		case AGE:
		default:
			return new Comparator<Message>() {
				public int compare(Message o1, Message o2) {
					// oldest message first
					return (int) Math.signum(o1.getReceiveTime() - o2.getReceiveTime());
				}
			};
		}
	}
}
