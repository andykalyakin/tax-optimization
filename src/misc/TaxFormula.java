package misc;

import java.util.List;

/**
 * Tax formulas taken from excel template
 * 
 * @author chris
 * 
 */
public class TaxFormula {

	/**
	 * calculate the maximum loss carryback according to excel template.
	 * The maximum loss carryback is dependent of:
	 * - from the current period: decision, income and interest. Thus, the decision
	 * 		and must be set and the interest computed before calling this method.
	 * 		Especially the maximum loss does not depent on the outcome of the
	 * 		current period
	 * - from the predecessor period: The predecessor period should be updated
	 * 		completely before calling this method, as the interest of the current
	 * 		period depends on the outcome of the predecessor.
	 * 
	 * @param predesseccor
	 *            the previous period, if there is no previous period (null) a
	 *            neutral period will be created
	 * @param current
	 *            the current period
	 * @return the maximum loss carryback
	 */
	public static int calculateMaximumLosscarryback(Period predesseccor,
			Period current) {
		if (predesseccor == null) {
			predesseccor = new Period(current.getTime() - 1, 0, 0,
					Decision.SHARED, 0);
			predesseccor.setPeriodMoney(current.getPeriodMoney());
		}
		int currentmin = Math.min(0, (1 - current.getDecision().getValue())
				* current.getIncomeAndInteresst()
				+ current.getDecision().getValue() * current.getIncome());
		int premax = Math.max(0, predesseccor.getTaxableProfit());
		int lossLimit = Math.max(-1000000, Math.max(currentmin, -premax));
		return lossLimit;
	}

	/**
	 * calculate the taxable profit according to excel template
	 * 
	 * @param predesseccor
	 *            the previous period, if there is no previous period (null) a
	 *            neutral period will be created
	 * @param current
	 *            the current period
	 * @return the taxable profit
	 */
	public static int calculateTaxableProfit(Period predesseccor,
			Period current) {
		if (predesseccor == null) {
			predesseccor = new Period(current.getTime() - 1, 0, 0,
					Decision.SHARED, 0);
			predesseccor.setPeriodMoney(current.getPeriodMoney());
		}
		int taxableProfit = 0;
		if (current.getDecision().equals(Decision.SHARED)) {

			int preMin = (int) Math.round(Math.min(
					-1000000 - 0.6
							* (predesseccor.getIncomeAndInteresst() - 1000000),
					0));
			int currentMax = Math.max(
					predesseccor.getNotUsedLossCarryforward(), preMin);

			taxableProfit = (int) Math.ceil(Math.max(0, current.getIncomeAndInteresst()
					+ currentMax));
		} else {
			int currentMin = (int) Math.round(Math.min(
					-1000000 - (0.6 * (current.getIncome() - 1000000)), 0));
			int currentMax = Math.max(
					predesseccor.getNotUsedLossCarryforward(), currentMin);

			taxableProfit = (int) Math.ceil(Math.max(0, current.getIncome() + currentMax));
		}

		return taxableProfit;
	}

	/**
	 * From the successor only the loss carryback is used.
	 * 
	 * This result is not used for the outcome of the current period i, but only
	 * in the successor period i+1. As the loss carryback and max loss carryback  of i+1
	 * do not depend on any other attributes of period i+1 despite income, interest and decision,
	 * there is no circular dependancy between the loss carryback and max loss carryback of i+1
	 * with the outcome or any other field of period i.
	 * 
	 * 	 @param successor
	 *       From the successor only the loss carryback is used.
	 */
	public static int calculateTaxesAfterLossCarryback(Period current,
			Period successor) {
		if (successor == null) {
			return Math.max(current.getTaxableProfit(), 0);
		} else {
			return Math.max(
					current.getTaxableProfit() + successor.getLossCarryback(),
					0);
		}
	}

	/**
	 * calculate the TaxA according to excel template
	 * 
	 * @param current
	 *            the current period
	 * @return the TaxA
	 */
	public static double calculateTaxA(Period current) {
		double taxA = ((current.getTaxableProfit() - 8004) / 10000.0);
		return taxA;
	}

	/**
	 * calculate the TaxB according to excel template
	 * 
	 * @param current
	 *            the current period
	 * @return the TaxB
	 */
	public static double calculateTaxB(Period current) {
		double taxB = (current.getTaxableProfit() - 13469) / 10000.0;
		return taxB;
	}

	/**
	 * calculate the taxes according to excel template
	 * 
	 * @param current
	 *            the current period
	 * @return the taxes
	 */
	public static int calculateTaxes(Period current) {
		int taxes = 0;
		if (current.getDecision().equals(Decision.DIVIDED)) {
			taxes = (int) Math.round(0.25 * current.getInteresst());
		}

		if (current.getTaxableProfit() >= 0
				&& current.getTaxableProfit() <= 8354) {
			taxes += 0;
		}
		if (current.getTaxableProfit() >= 8355
				&& current.getTaxableProfit() <= 13469) {
			taxes += (int) Math.round((974.58 * current.getTaxA() + 1.400) * current
					.getTaxA());
		}
		if (current.getTaxableProfit() >= 13470
				&& current.getTaxableProfit() <= 52881) {
			taxes += (int) Math.round((228.74 * current.getTaxB() + 2397)
					* current.getTaxB() + 971);
		}
		if (current.getTaxableProfit() >= 52882
				&& current.getTaxableProfit() <= 250730) {
			taxes += (int) Math.round(0.42 * current.getTaxableProfit() - 8239);
		}
		if (current.getTaxableProfit() >= 250731) {
			taxes += (int) Math.round(0.45 * current.getTaxableProfit() - 15761);
		}
		return taxes;
	}

	public static int calculateTaxRecalculation(Period current) {
		int taxes = 0;
		double taxA = (current.getTaxableProfitAfterLossCarryback() - 8354d) / 10000;
		double taxB = (current.getTaxableProfitAfterLossCarryback() - 13469d) / 10000;
		if (current.getDecision().equals(Decision.DIVIDED)) {
			taxes = (int) Math.ceil(0.25 * current.getInteresst());
		}

		if (current.getTaxableProfitAfterLossCarryback() >= 0
				&& current.getTaxableProfitAfterLossCarryback() <= 8354) {
			taxes += 0;
		}
		if (current.getTaxableProfitAfterLossCarryback() >= 8355
				&& current.getTaxableProfitAfterLossCarryback() <= 13469) {
			taxes += (int) Math.ceil((974.58 * taxA + 1.400) * taxA);
		}
		if (current.getTaxableProfitAfterLossCarryback() >= 13470
				&& current.getTaxableProfitAfterLossCarryback() <= 52881) {
			taxes += (int) Math.ceil((228.74 * taxB + 2397)
					* taxB + 971);
		}
		if (current.getTaxableProfitAfterLossCarryback() >= 52882
				&& current.getTaxableProfitAfterLossCarryback() <= 250730) {
			taxes += (int) Math.ceil(0.42 * current.getTaxableProfitAfterLossCarryback() - 8239);
		}
		if (current.getTaxableProfitAfterLossCarryback() >= 250731) {
			taxes += (int) Math.ceil(0.45 * current.getTaxableProfitAfterLossCarryback() - 15761);
		}
		return taxes;
	}

	/**
	 * Tax refund of the current (!) period is computed from the predecessor period
	 */
	public static int calculateTaxRefund(Period predesessor) {
		return predesessor.getTaxes() - predesessor.getTaxRecalculation();
	}

	public static int calculatelossCarryForward(Period current) {
		return Math.min(
				0,
				current.getDecision().getValue()
						* (current.getIncome() - current.getLossCarryback())
						+ (1 - current.getDecision().getValue())
						* (current.getIncomeAndInteresst() - current
								.getLossCarryback()));
	}

	public static int calculateNotusedLossCarryforward(Period predesseccor,
			Period current) {
		return Math.min(
				Math.min(predesseccor.getNotUsedLossCarryforward(), 0)
						* Math.max(0, current.getIncome())
						+ current.getDecision().getValue()
						* Math.max(0, current.getIncome())
						+ (1 - current.getDecision().getValue())
						* Math.max(current.getIncomeAndInteresst(), 0)
						+ current.getLossCarryForward()
						- current.getTaxableProfit(), 0);
	}

	/**
	 * combined method to calculate (and update) a whole period.
	 * 
	 * To understand the effect of the succesor also read the comments of calculateMaximumLosscarryback
	 * and calculateTaxesAfterLossCarryback. The facit is: although it might look so, there is no circular
	 * dependency between the periods.
	 * 
	 * @param predesseccor
	 *            the previous period
	 * @param current
	 *            the current period
	 * @param successor
	 *            the next period. From the successor only the loss carryback is used
	 */
	public static void calculatePeriod(Period predesseccor, Period current,
			Period successor) {
		current.setMaximumLoss(calculateMaximumLosscarryback(predesseccor,
				current));
		current.setTaxableProfit(calculateTaxableProfit(predesseccor, current));
		current.setTaxA(calculateTaxA(current));
		current.setTaxB(calculateTaxB(current));
		current.setTaxes(calculateTaxes(current));
		current.setTaxableProfitAfterLossCarryback(TaxFormula
				.calculateTaxesAfterLossCarryback(current, successor));
		current.setTaxRecalculation(calculateTaxRecalculation(current));
		current.setTaxRefund(TaxFormula.calculateTaxRefund(predesseccor));
		current.setPeriodMoney(predesseccor.getPeriodMoney()
				+ current.getIncomeAndInteresst() - current.getTaxes()
				+ current.getTaxRefund());
		current.setLossCarryforward(calculatelossCarryForward(current));
		current.setNotUsedLossCarryforward(calculateNotusedLossCarryforward(
				predesseccor, current));
	}
	
	
	/**
	 * Recalculate (and update) the outcome of a current period with all fields, which the outcome is
	 * dependent on. This includes also the update of some fields (the taxes) of the predecessor period.
	 * 
	 * Mind that the outcome of the current period is not dependent on the taxable profit after loss carryback
	 * and tax recalculation of the current period. Thus, those are NOT updated.
	 * 
	 * You are assumed to have the decision and the loss carryback set to allowed values before calling
	 * this method. Also usually you will have the interest set before calling this method. However, you
	 * can also decide to recompute it.
	 * 
	 * To understand the effect of the taxes also read the comments of calculateMaximumLosscarryback
	 * and calculateTaxesAfterLossCarryback. The facit is: although it might look so, there is no circular
	 * dependency between the periods.
	 * 
	 * @param predecessor
	 *            the previous period
	 * @param current
	 *            the current period
	 * @param recalculateInterest
	 *            whether to recompute the interest of the current period
	 * @param interestRate
	 *            only needed if recalculateInterest==true
	 * @param recalculateMaxLossCarryback
	 *            whether to recompute the maxLossCarryback of the current period
	 */
	public static void recalculatePeriodMoney(Period current, Period predecessor, boolean recalculateInterest,
			float interestRate, boolean recalculateMaxLossCarryback) {
		// stuff that needs to be updated on the predecessor
		predecessor.setTaxableProfitAfterLossCarryback(TaxFormula
				.calculateTaxesAfterLossCarryback(predecessor, current));
		// TODO need to recompute aR_t and bR_t of the predecessor before the following call
		predecessor.setTaxRecalculation(calculateTaxRecalculation(predecessor));
		
		// update the current period
		if (recalculateInterest)
			updateInterest(current, predecessor, interestRate);
		if (recalculateMaxLossCarryback)
			current.setMaximumLoss(calculateMaximumLosscarryback(predecessor, current));
		current.setTaxableProfit(calculateTaxableProfit(predecessor, current));
		current.setTaxA(calculateTaxA(current));
		current.setTaxB(calculateTaxB(current));
		current.setTaxes(calculateTaxes(current));
		
		
		current.setTaxRefund(TaxFormula.calculateTaxRefund(predecessor));
		current.setPeriodMoney(predecessor.getPeriodMoney()
				+ current.getIncomeAndInteresst() - current.getTaxes()
				+ current.getTaxRefund());
		current.setLossCarryforward(calculatelossCarryForward(current));
		current.setNotUsedLossCarryforward(calculateNotusedLossCarryforward(
				predecessor, current));
	}
	

	/**
	 * Set the interest (Zinsen) of the current period. It depends on the outcome
	 * of the previous period, thus the previous period should be completely
	 * computed before calling this method.
	 * 
	 * @param current
	 * 			The period to change
	 * @param predecessor
	 * 			the predecessor period. Must be completely calculated.
	 * @param interestRate
	 * 			The interest rate (Zinssatz)
	 */
	public static void updateInterest(Period current, Period predecessor, float interestRate) {
		current.setInteresst((int) Math.round(interestRate * predecessor.getPeriodMoney()));
	}
	
	/**
	 * updates all values of the periods, that are dynamically computed.
	 */
	public static void updatePeriods(List<Period> periods, float interesstRate) {
		// period 0 is a dummy period and only contains the start money in periodMoney
		updatePeriodsStartingAt(periods, interesstRate, 1);
	}
	
	/**
	 * updates all values of the periods with index higher than startIndex.
	 */
	public static void updatePeriodsStartingAt(List<Period> periods, float interesstRate, int startIndex) {
		// period 0 is a dummy period and only contains the start money in periodMoney
		if (startIndex <= 0)
			startIndex = 1;
		for (int i = startIndex; i < periods.size(); ++i) {
			Period current = periods.get(i);
			Period predecessor = periods.get(i-1);
			Period successor = i + 1 >= periods.size() ? null : periods.get(i + 1);
			updateInterest(current, predecessor, interesstRate);
			TaxFormula.calculatePeriod(predecessor, current, successor);
		}
	}
	
/*
	// formula tests
	//
	public static void main(String[] args) {
		Period p0 = new Period(0, 0, 0, Decision.SHARED, 0);
		p0.setPeriodMoney(600000);
		Period p1 = new Period(1, 0, (int) (p0.getPeriodMoney() * 0.05),
				Decision.DIVIDED, 0);
		Period p2 = new Period(2, -150000, 0, Decision.DIVIDED, -27447);
		Period p3 = new Period(3, 50000, 0, Decision.DIVIDED, 0);
		Period p4 = new Period(4, 0, 0, Decision.SHARED, 0);
		Period p5 = new Period(5, -150000, 0, Decision.DIVIDED, 0);
		Period p6 = new Period(6, 50000, 0, Decision.DIVIDED, 0);

		calculatePeriod(p0, p1, p2);
		System.out.println("period 1: " + p1.getPeriodMoney());

		p2.setInteresst((int) (p1.getPeriodMoney() * 0.05));
		calculatePeriod(p1, p2, p3);
		System.out.println("period 2: " + p2.getPeriodMoney());

		p3.setInteresst((int) (p2.getPeriodMoney() * 0.05));
		calculatePeriod(p2, p3, p4);
		System.out.println("period 3: " + p3.getPeriodMoney());

		p4.setInteresst((int) (p3.getPeriodMoney() * 0.05));
		calculatePeriod(p3, p4, p5);
		System.out.println("period 4: " + p4.getPeriodMoney());

		p5.setInteresst((int) (p4.getPeriodMoney() * 0.05));
		calculatePeriod(p4, p5, p6);
		System.out.println("period 5: " + p5.getPeriodMoney());
		
		p6.setInteresst((int) (p5.getPeriodMoney() * 0.05));
		calculatePeriod(p5, p6, null);
		System.out.println("period 6: " + p6.getPeriodMoney());
	}*/
}
