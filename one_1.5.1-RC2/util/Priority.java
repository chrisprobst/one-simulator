package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import core.Message;

/**
 * @author Andre Ippisch
 */
public class Priority {

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
