package gtch;

import routing.ProphetV2Router;
import core.Settings;

/**
 * This is an extension of the ProphetV2Router
 * 
 * @author Andre Ippisch
 */
public class ProphetV2RouterExtended extends ProphetV2Router {

	public ProphetV2RouterExtended(Settings s) {
		super(s);
		System.out.println("Extended version of ProphetV2Router is used");
	}

	public ProphetV2RouterExtended(ProphetV2Router r) {
		super(r);
	}

}
