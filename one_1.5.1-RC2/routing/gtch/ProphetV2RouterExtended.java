package routing.gtch;

import routing.ProphetV2Router;
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
	protected int maxForward;

	public ProphetV2RouterExtended(Settings s) {
		super(s);
		Settings prophetSettings = new Settings(PROPHET_NS);
		maxForward = prophetSettings.getInt(MAX_FORWARD_S);
	}

	public ProphetV2RouterExtended(ProphetV2RouterExtended r) {
		super(r);
		this.maxForward = r.maxForward;
	}
}
