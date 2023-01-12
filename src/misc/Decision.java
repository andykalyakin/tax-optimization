package misc;

/**
 * enum for tax decision
 * values correspond to excel values
 * use these values so the formulas work!
 * 
 * @author chris
 *
 */
public enum Decision {

	/**
	 * tax calculation based on income + interesst
	 */
	SHARED(0),
	/**
	 * 25% tax on interesst, income according to tax function
	 */
	DIVIDED(1);
	
	private int value;
	
	private Decision(int value) {
		this.value = value;
	}
	
	/**
	 * get the excel value for the tax formulas
	 * 
	 * @return SHARED = 0, DEVIDED = 1
	 */
	public int getValue() {
		return value;
	}
}
