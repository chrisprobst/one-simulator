package util;

import routing.gtch.ProphetV2RouterExtended;
import core.DTNHost;
import core.Message;
import core.SimScenario;

/**
 * @author Andre Ippisch
 */
public class GTCH {
	public static double getRatioOfResidualTtlToElapsedTtl(Message m) {
		return getRatioOfResidualTtlToElapsedTtl(m.getResidualTtl(), m.getElapsedTtl());
	}

	public static double getRatioOfResidualTtlToElapsedTtl(double residualTtl, double elapsedTtl) {
		if (elapsedTtl <= 0) {
			return Double.MAX_VALUE;
		}
		return (double) residualTtl / elapsedTtl;
	}

	public static double getRatioOfElapsedTtlToResidualTtl(Message m) {
		return getRatioOfElapsedTtlToResidualTtl(m.getElapsedTtl(), m.getResidualTtl());
	}

	public static double getRatioOfElapsedTtlToResidualTtl(double elapsedTtl, double residualTtl) {
		if (residualTtl == 0) {
			return Double.MAX_VALUE;
		}
		return (double) elapsedTtl / residualTtl;
	}

	public static int getAmountOfNodesInNetwork() {
		return SimScenario.getInstance().getHosts().size();
	}

	public static DTNHost getLastHopOfMessage(Message m) {
		return m.getHops().get(m.getHopCount());
	}

	public static boolean isDestinationKnownByNeighbour(DTNHost neighbour, DTNHost destination) {
		if (!(neighbour.getRouter() instanceof ProphetV2RouterExtended)) {
			return false;
		}
		ProphetV2RouterExtended router = (ProphetV2RouterExtended) neighbour.getRouter();
		return router.getEncTimeFor(destination) != -1;
	}

	/**
	 * Forward Times<br>
	 * High Dissemination Speed (HDS)<br>
	 * Source
	 * 
	 * @param D_enc
	 * @return
	 */
	public static int get_Nm_HDS_Source(int D_enc) {
		return D_enc * get_Hm_HDS_Source(D_enc);
	}

	/**
	 * Hop Count<br>
	 * High Dissemination Speed (HDS)<br>
	 * Source
	 * 
	 * @param D_enc
	 * @return
	 */
	public static int get_Hm_HDS_Source(int D_enc) {
		return (int) Math.pow(2.0, D_enc);
	}

	/**
	 * Forward Times<br>
	 * High Dissemination Speed (HDS)<br>
	 * Relay
	 * 
	 * @param D_d
	 * @param TTL_e
	 * @param CON_time
	 * @return
	 */
	public static int get_Nm_HDS_Relay(int D_d, double TTL_e, double CON_time) {
		if (CON_time == 0) {
			return -1;
		}
		double numerator = Math.pow(2.0, D_d);
		double fraction = TTL_e / CON_time;
		double power = Math.pow(2.0, fraction);
		double denominator = D_d + power;
		return (int) (numerator / denominator);
	}

	/**
	 * Hop Count<br>
	 * High Dissemination Speed (HDS)<br>
	 * Relay
	 * 
	 * @param h_m
	 * @param R_ttl
	 * @return
	 */
	public static int get_Hm_HDS_Relay(int h_m, double R_ttl) {
		return h_m + (int) Math.round(R_ttl);
	}

	/**
	 * Forward Times<br>
	 * Low Dissemination Speed (HDS)<br>
	 * 
	 * @param D_d
	 * @param h_m
	 * @param TTL_e
	 * @param CON_time
	 * @return
	 */
	public static int get_Nm_LDS(int D_d, int h_m, double TTL_e, double CON_time) {
		if (h_m == 0 || CON_time == 0) {
			return -1;
		}
		double numerator = Math.pow(2.0, D_d) / h_m;
		double fraction = TTL_e / CON_time;
		double power = Math.pow(2.0, fraction);
		double denominator = D_d + power;
		return (int) (numerator / denominator);
	}

	/**
	 * Hop Count<br>
	 * Low Dissemination Speed (HDS)<br>
	 * 
	 * @param h_m
	 * @param R_ttl
	 * @return
	 */
	public static int get_Hm_LDS(int h_m, double R_ttl) {
		return h_m + (int) Math.round(R_ttl);
	}

	/**
	 * Forward Times<br>
	 * Decremential Dissemination Speed (HDS)<br>
	 * 
	 * @param D_d
	 * @param h_m
	 * @param TTL_e
	 * @param CON_time
	 * @return
	 */
	public static int get_Nm_DDS(int D_d, int h_m, double TTL_e, double CON_time) {
		if (h_m == 0 || CON_time == 0) {
			return -1;
		}
		double numerator = Math.pow(2.0, D_d) / h_m;
		double fraction = TTL_e / CON_time;
		double power = Math.pow(2.0, fraction);
		double denominator = D_d + power;
		return (int) (numerator / denominator);
	}

	/**
	 * Hop Count<br>
	 * Decremential Dissemination Speed (HDS)<br>
	 * 
	 * @param h_m
	 * @param R_ttl
	 * @return
	 */
	public static int get_Hm_DDS(int h_m, double R_ttl) {
		return h_m + (int) Math.round(R_ttl);
	}

}
