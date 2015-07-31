package util;

/**
 * @author Andre Ippisch
 */
public class GTCH {
	public static double getRatioOfResidualTtlToElapsedTtl(int residualTtl, int elapsedTtl) {
		if (elapsedTtl <= 0) {
			return Double.MAX_VALUE;
		}
		return (double) residualTtl / elapsedTtl;
	}
}
