package routing.gtch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routing.MessageRouter;
import routing.ProphetV2Router;
import util.GTCH;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.SimScenario;

/**
 * This is an extension of the ProphetV2Router
 * 
 * @author Andre Ippisch
 */
public class ProphetV2RouterExtended extends ProphetV2Router {
	/**
	 * max forward times alltogether - setting id ({@value} ). Integer valued.
	 */
	public static final String MAX_FORWARD_S = "maxForwardTimesTotal";
	/**
	 * maximum amount of forwarding times alltogether of any messages to other hosts;
	 */
	protected int maxForwardTimes;
	/**
	 * mapping from message (@link {@link Message}) to maximal calculated forwarding time amount of the message
	 */
	protected Map<Message, Integer> maxForwardTimesCalculated;
	/**
	 * mapping from message (@link {@link Message}) to maximal calculated hop count amount of the message
	 */
	protected Map<Message, Integer> maxHopCountCalculated;

	public ProphetV2RouterExtended(Settings s) {
		super(s);
		Settings prophetSettings = new Settings(PROPHET_NS);
		maxForwardTimes = prophetSettings.getInt(MAX_FORWARD_S);
	}

	public ProphetV2RouterExtended(ProphetV2RouterExtended r) {
		super(r);
		this.maxForwardTimes = r.maxForwardTimes;
	}

	@Override
	public void init(DTNHost host, List<MessageListener> mListeners) {
		super.init(host, mListeners);
		this.maxForwardTimesCalculated = new HashMap<Message, Integer>();
		this.maxHopCountCalculated = new HashMap<Message, Integer>();
	}

	@Override
	public MessageRouter replicate() {
		ProphetV2RouterExtended r = new ProphetV2RouterExtended(this);
		return r;
	}

	@Override
	protected void addToMessages(Message m, boolean newMessage) {
		System.out.println("addToMessages: " + m.getId() + ", TTL: " + m.getMessageTtl() + ", TTLe: " + m.getElapsedTtl() + ", TTLr: " + m.getResidualTtl());
		this.maxForwardTimesCalculated.put(m, getCalculatedForwardTime(m));
		this.maxHopCountCalculated.put(m, getCalculatedHopCount(m));
		super.addToMessages(m, newMessage);
	}

	@Override
	protected Message removeFromMessages(String id) {
		this.maxForwardTimesCalculated.remove(getMessage(id));
		this.maxHopCountCalculated.remove(getMessage(id));
		return super.removeFromMessages(id);
	}

	@Override
	public void update() {
		super.update();
		dropSomeMessages();
	}

	private int getCalculatedForwardTime(Message m) {
		if (this.getHost().equals(m.getTo())) { // host is receiver of message
			System.out.println("This should not be called when used in addToMessages");
			return 0;
		}

		double ratio_R_E = GTCH.getRatioOfResidualTtlToElapsedTtl(m);
		boolean HDS = ratio_R_E > 1;
		DTNHost neighbour = GTCH.getLastHopOfMessage(m);
		boolean destinationKnownByNeighbour = GTCH.isDestinationKnownByNeighbour(neighbour, m.getTo());
		boolean LDS = (ratio_R_E < 1) && destinationKnownByNeighbour;
		boolean isSource = this.getHost().equals(m.getFrom());

		if (true) {
			return GTCH.get_Nm_HDS_Source(this.lastEncouterTime.size());
		}

		int maxForwardTimesCalculatedTemp;

		if (this.getHost().equals(m.getFrom())) { // host is sender of message
			maxForwardTimesCalculatedTemp = GTCH.get_Nm_HDS_Source(this.lastEncouterTime.size());
		} else if (this.getHost().equals(m.getTo())) { // host is receiver of message
			System.out.println("This should not be called when used in addToMessages");
			maxForwardTimesCalculatedTemp = 0;
		} else { // host is relay
			maxForwardTimesCalculatedTemp = 0; // TODO: calculate max forward times

		}
		return maxForwardTimesCalculatedTemp;
	}

	private int getCalculatedHopCount(Message m) {	
		if (this.getHost().equals(m.getTo())) { // host is receiver of message
			System.out.println("This should not be called when used in addToMessages");
			return 0;
		}
		
		int maxHopCountCalculatedTemp;
		if (this.getHost().equals(m.getFrom())) { // host is sender of message
			maxHopCountCalculatedTemp = 0;
		} else if (this.getHost().equals(m.getTo())) { // host is receiver of message
			System.out.println("This should not be called when used in addToMessages");
			maxHopCountCalculatedTemp = 0;
		} else { // host is relay
			maxHopCountCalculatedTemp = 0; // TODO: calculate max forward times
		}
		return maxHopCountCalculatedTemp;
	}

	/**
	 * Drops messages that have been forwarded often enough according to forward times and hop count
	 */
	protected void dropSomeMessages() {
		Message[] messages = getMessageCollection().toArray(new Message[0]);
		for (int i = 0; i < messages.length; i++) {
			boolean forward = forwardTimes.get(messages[i]) >= maxForwardTimesCalculated.get(messages[i]);
			boolean hopcount = messages[i].getHopCount() >= maxHopCountCalculated.get(messages[i]);

			if (forward || hopcount) {
				deleteMessage(messages[i].getId(), true);
			}
		}
	}
}
