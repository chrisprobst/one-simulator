package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import core.Message;

/**
 * @author Andre Ippisch
 */
public class Priority {
	public static Queue<Message> getMessageQueue(Collection<Message> messageCollection, Comparator<Message> comparator) {
		Queue<Message> queue = new PriorityQueue<Message>(messageCollection.size(), comparator);
		queue.addAll(messageCollection);
		return queue;
	}

	public static Comparator<Message> getHopCountComparator(final boolean ascending) {
		return new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return (int) ((ascending ? 1 : -1) * Math.signum(o1.getHopCount() - o2.getHopCount()));
			}
		};
	}

	public static Comparator<Message> getRemainingTimeToLiveComparator(final boolean ascending) {
		return new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return (int) ((ascending ? 1 : -1) * Math.signum(o1.getTtl() - o2.getTtl()));
			}
		};
	}

	public static Comparator<Message> getSizeComparator(final boolean ascending) {
		return new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return (int) ((ascending ? 1 : -1) * Math.signum(o1.getSize() - o2.getSize()));
			}
		};
	}

	public static Collection<String> getCollectionOfMessagesToDropForSize(Collection<Message> orderedCollection, int size) {
		Collection<String> dropList = new HashSet<String>();
		Message message;
		int currentSize = 0;
		Iterator<Message> iterator = orderedCollection.iterator();
		while (iterator.hasNext() && (currentSize < size)) {
			message = iterator.next();
			currentSize += message.getSize();
			dropList.add(message.getId());
		}

		return dropList;
	}
}
