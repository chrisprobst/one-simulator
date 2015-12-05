package report;

import java.util.List;
import java.util.stream.Collectors;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.SimClock;

/**
 * 
 * Time from to Host Replica Snd-Q Dr-Po creation-time Arrival-time Hopcount TTL
 * Size (id Cr-Time Ar-Time Rep Hopcount Size TTL)
 * 
 * 
 * 
 * Created by chrisprobst on 05.11.15.
 */
public class DetailedEventLogReport extends Report implements MessageListener {

	private boolean firstLine = true;

	private String getFromToAction(String action, Message m, DTNHost current, String embed) {
		// return String.format(
		// "{ \"Time\": %f, \"Action\": \"%s\", \"From\": %s, \"To\": %s,
		// \"Message\": %s%s }\n",
		// SimClock.getTime(),
		// action,
		// from.toDetailedString(),
		// to.toDetailedString(),
		// m.toDetailedString(),
		// embed);

		String mainLine = String.format(
				"Time: %f, Action: %s, From: %s, To: %s, Current: %s, Send-Policy: %s, Drop-Policy: %s, Message-ID: %s, Creation-Time: %f, Arrival-Time: %f, Replications: %d, Hop-Count: %d, TTL: %d, Size: %d%s\n",
				SimClock.getTime(), action, m.getFrom().toString(), m.getTo().toString(), current.toString(), 
				current.getRouter().getSendQueueMode(), current.getRouter().getDropPolicyMode(),m.getId(),
				m.getCreationTime(), m.getReceiveTime(), m.getReplications(), m.getHopCount(), m.getTtl(), m.getSize(), embed);

		List<String> bufferLines = current.getRouter().getMessageCollection().stream()
				.map(msg -> String.format(
						"    (ID: %s, Creation-Time: %f, Arrival-Time: %f, Replications: %d, Hop-Count: %d, TTL: %d, Size: %d)\n",
						msg.getId(), msg.getCreationTime(), msg.getReceiveTime(), msg.getReplications(),
						msg.getHopCount(), msg.getTtl(), msg.getSize()))
				.collect(Collectors.toList());
		
		return mainLine + String.join("", bufferLines) + "\n";
	}

	private String getFromToAction(String action, Message m, DTNHost current) {
		return getFromToAction(action, m, current, "");
	}

	private void writeString(String string) {
		out.print(string);
	}

	public DetailedEventLogReport() {
		init();
	}

	/**
	 * Method is called when a new message is created
	 *
	 * @param m
	 *            Message that was created
	 */
	public void newMessage(Message m) {
		// Same as 'StartSend'
	}

	/**
	 * Method is called when a message's transfer is started
	 *
	 * @param m
	 *            The message that is going to be transferred
	 * @param from
	 *            Node where the message is transferred from
	 * @param to
	 *            Node where the message is transferred to
	 */
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (m.getFrom() == from) {
			// SEND
			writeString(getFromToAction("StartSend", m, from));
		} else if (m.getTo() == to) {
			// DELIVER
			writeString(getFromToAction("StartDeliver", m, from));
		} else {
			// FORWARDED
			writeString(getFromToAction("StartForward", m, from));
		}
	}

	/**
	 * Method is called when a message is deleted
	 *
	 * @param m
	 *            The message that was deleted
	 * @param where
	 *            The host where the message was deleted
	 * @param dropped
	 *            True if the message was dropped, false if removed
	 */
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		if (dropped) {
			// DROP
			writeString(getFromToAction("DropMessage", m, where));
		} else {
			if (m.getTtl() <= 0) {
				// EXPIRE
				writeString(getFromToAction("ExpireMessage", m, where));
			} else {
				// REMOVED
				writeString(getFromToAction("RemoveMessage", m, where));
			}
		}
	}

	/**
	 * Method is called when a message's transfer was aborted before it finished
	 *
	 * @param m
	 *            The message that was being transferred
	 * @param from
	 *            Node where the message was being transferred from
	 * @param to
	 *            Node where the message was being transferred to
	 */
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		if (m.getFrom() == from) {
			// ABORT_SEND
			writeString(getFromToAction("AbortSend", m, from));
		} else if (m.getTo() == to) {
			// ABORT_DELIVER
			writeString(getFromToAction("AbortDeliver", m, from));
		} else {
			// ABORT_FORWARDED
			writeString(getFromToAction("AbortForward", m, from));
		}
	}

	/**
	 * Method is called when a message is successfully transferred from a node
	 * to another.
	 *
	 * @param m
	 *            The message that was transferred
	 * @param from
	 *            Node where the message was transferred from
	 * @param to
	 *            Node where the message was transferred to
	 * @param firstDelivery
	 *            Was the target node final destination of the message and
	 *            received this message for the first time.
	 */
	public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
		if (m.getFrom() == from) {
			// COMPLETE_SEND
			writeString(getFromToAction("CompletedSend", m, from));
		} else if (m.getTo() == to) {
			// COMPLETE_DELIVER
			writeString(
					getFromToAction("CompleteDeliver", m, from, String.format(", FirstDelivery: %b", firstDelivery)));
		} else {
			// COMPLETE_FORWARDED
			writeString(getFromToAction("CompleteForward", m, from));
		}
	}
}
