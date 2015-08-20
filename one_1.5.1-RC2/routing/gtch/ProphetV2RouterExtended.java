package routing.gtch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import routing.MessageRouter;
import routing.ProphetV2Router;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

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
	protected void addToMessages(Message m, boolean newMessage) {
		this.maxForwardTimesCalculated.put(m, getCalculatedForwardTime(m));
		this.maxHopCountCalculated.put(m, getCalculatedHopCount(m));
		something();
		super.addToMessages(m, newMessage);
	}

	@Override
	protected Message removeFromMessages(String id) {
		this.maxForwardTimesCalculated.remove(this.mapIdMessage.get(id));
		this.maxHopCountCalculated.remove(this.mapIdMessage.get(id));
		return super.removeFromMessages(id);
	}

	private int getCalculatedForwardTime(Message m) {
		int maxForwardTimesCalculatedTemp;
		if (this.getHost().equals(m.getFrom())) { // host is sender of message
			maxForwardTimesCalculatedTemp = 0; // TODO: calculate max forward times
		} else if (this.getHost().equals(m.getTo())) { // host is receiver of message
			maxForwardTimesCalculatedTemp = 0;
		} else { // host is relay
			maxForwardTimesCalculatedTemp = 0; // TODO: calculate max forward times
		}
		return maxForwardTimesCalculatedTemp;
	}

	private int getCalculatedHopCount(Message m) {
		int maxHopCountCalculatedTemp;
		if (this.getHost().equals(m.getFrom())) { // host is sender of message
			maxHopCountCalculatedTemp = 0; // TODO: calculate max forward times
		} else if (this.getHost().equals(m.getTo())) { // host is receiver of message
			maxHopCountCalculatedTemp = 0;
		} else { // host is relay
			maxHopCountCalculatedTemp = 0; // TODO: calculate max forward times
		}
		return maxHopCountCalculatedTemp;
	}

	private void something() {
		List<Connection> connections = this.getConnections();
		for (Connection connection : connections) {
			System.out.println(this.getHost().toString() + ", " + connection.toString());
		}
	}

	@Override
	public MessageRouter replicate() {
		ProphetV2RouterExtended r = new ProphetV2RouterExtended(this);
		return r;
	}
}
