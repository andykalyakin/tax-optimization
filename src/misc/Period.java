package misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * All fields shown by the GUI taken from excel template
 * Use or extend this class for your search algorithm
 * 
 * @author chris
 *
 */
public class Period implements Serializable {

	private static final long serialVersionUID = 2365994127140139546L;
	
	protected int time;
	protected int income;
	protected int interesst; // this is not the interest rate, but interrestRate * periodMoney of the previous period
	protected Decision decision;
	protected int maximumLoss;
	protected int taxableProfit;
	protected int taxableProfitAfterLossCarryback;
	protected double taxA;
	protected double taxB;
	protected int taxes;
	protected int periodMoney;
	protected int lossCarryback;
	protected int lossCarryforward;
	protected int notUsedLossCarryforward;
	protected int taxRecalculation;
	protected int taxRefund;
	
	/**
	 * creates a new period
	 * 
	 * @param time the periods time 0 - n
	 * @param income the income for this period
	 * @param interesst the interest for this period
	 * @param decision the tax decision for this period
	 * @param lossCarryback the loss carryback (this is a negative value !!)
	 */
	public Period(int time, int income, int interesst, Decision decision, int lossCarryback) {
		this.time = time;
		this.income = income;
		this.interesst = interesst;
		this.decision = decision;
		this.lossCarryback = lossCarryback > 0 ? -1 * lossCarryback : lossCarryback;
		this.lossCarryforward = -1 * (income + interesst - lossCarryback);
	}
	
	/**
	 * creates a new period
	 * 
	 * @param time the periods time 0 - n
	 * @param income the income for this period
	 * @param decision the tax decision for this period
	 * @param lossCarryback the loss carryback (this is a negative value !!)
	 */
	public Period(int time, int income, Decision decision, int lossCarryback) {
		this.time = time;
		this.income = income;
		this.decision = decision;
		this.lossCarryback = lossCarryback > 0 ? -1 * lossCarryback : lossCarryback;
		this.lossCarryforward = -1 * (income + interesst - lossCarryback);
	}
	
	/**
	 * period number 0 - n
	 * @return the periods time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * ---Zahlungsueberschuss---
	 * @return the income for this period
	 */
	public int getIncome() {
		return income;
	}
	
	/**
	 * ---Zinseinahmen---
	 * @return the interesst for this period
	 */
	public int getInteresst() {
		return interesst;
	}
	
	/**
	 * ---Besteuerungsvariante---
	 * @return the decision for this period
	 */
	public Decision getDecision() {
		return decision;
	}
	
	/**
	 * ---Zahlungsueberschuss und Zinseinahmen---
	 * interesst + income
	 * @return the combined income for this period
	 */
	public int getIncomeAndInteresst() {
		return income + interesst;
	}
	
	/**
	 * ---Obere grenze des Verlustruecktrags---
	 * @return the maximum loss for this period
	 */
	public int getMaximumLossCarryback() {
		return maximumLoss;
	}
	
	/**
	 * ---Zu versteuerndes Einkommen---
	 * @return the income that must be taxed
	 */
	public int getTaxableProfit() {
		return taxableProfit;
	}
	
	public int getTaxableProfitAfterLossCarryback() {
		return taxableProfitAfterLossCarryback;
	}
	
	/**
	 * Factor to calculate taxes
	 * see slides for more info 
	 * or just calculate according to formula this was implemented for readability of tax formula
	 * @return TaxA
	 */
	public double getTaxA() {
		return taxA;
	}
	
	/**
	 * Factor to calculate taxes
	 * see slides for more info 
	 * or just calculate according to formula this was implemented for readability of tax formula
	 * @return TaxB
	 */	
	public double getTaxB() {
		return taxB;
	}
	
	/**
	 * ---Steuerzahlungen---
	 * @return the taxes that must be paid in this period
	 */
	public int getTaxes() {
		return taxes;
	}
	
	/**
	 * ---Vermoegen in der Periode---
	 * @return the money after this period (after tax payments)
	 */
	public int getPeriodMoney() {
		return periodMoney;
	}
	
	/**
	 * ---Realisierter verlustruecktrag---
	 * @return the loss carried back to the previous period
	 */
	public int getLossCarryback() {
		return lossCarryback;
	}
	
	/**
	 * ---Realisierter VerlustVortrag---
	 * @return the loss carried forward to the next period
	 */
	public int getLossCarryForward() {
		return lossCarryforward;
	}
	
	public int getNotUsedLossCarryforward() {
		return notUsedLossCarryforward;
	}
	
	public int getTaxRefund() {
		return taxRefund;
	}
	
	public int getTaxRecalculation() {
		return taxRecalculation;
	}
	
	public void setInteresst(int interesst) {
		this.interesst = interesst;
	}

	public void setMaximumLoss(int maximumLoss) {
		this.maximumLoss = maximumLoss;
	}

	public void setTaxableProfit(int taxableProfit) {
		this.taxableProfit = taxableProfit;
	}
	
	public void setTaxableProfitAfterLossCarryback(
			int taxableProfitAfterLossCarryback) {
		this.taxableProfitAfterLossCarryback = taxableProfitAfterLossCarryback;
	}

	public void setTaxA(double taxA) {
		this.taxA = taxA;
	}

	public void setTaxB(double taxB) {
		this.taxB = taxB;
	}

	public void setTaxes(int taxes) {
		this.taxes = taxes;
	}

	public void setPeriodMoney(int periodMoney) {
		this.periodMoney = periodMoney;
	}
	
	public void setDecision(Decision decision) {
		this.decision = decision;
	}
	
	public void setDecision(float decisionValue) {
		this.decision = Math.abs(decisionValue-0.0f)<Math.abs(decisionValue-1.0f)? Decision.SHARED : Decision.DIVIDED;
	}
	
	public void setLossCarryback(int lossCarryback) {
		this.lossCarryback = lossCarryback > 0 ? -1 * lossCarryback : lossCarryback;;
	}
	
	public void setLossCarryforward(int lossCarryforward) {
		this.lossCarryforward = lossCarryforward;
	}
	
	public void setTaxRefund(int taxRefund) {
		this.taxRefund = taxRefund;
	}
	
	public void setTaxRecalculation(int taxRecalculation) {
		this.taxRecalculation = taxRecalculation;
	}
	
	public void setNotUsedLossCarryforward(int notUsedLossCarryforward) {
		this.notUsedLossCarryforward = notUsedLossCarryforward;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((decision == null) ? 0 : decision.hashCode());
		result = prime * result + income;
		result = prime * result + interesst;
		result = prime * result + lossCarryback;
		result = prime * result + lossCarryforward;
		result = prime * result + maximumLoss;
		result = prime * result + periodMoney;
		long temp;
		temp = Double.doubleToLongBits(taxA);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(taxB);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + taxableProfit;
		result = prime * result + taxes;
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Period other = (Period) obj;
		if (decision != other.decision)
			return false;
		if (income != other.income)
			return false;
		if (interesst != other.interesst)
			return false;
		if (lossCarryback != other.lossCarryback)
			return false;
		if (lossCarryforward != other.lossCarryforward)
			return false;
		if (maximumLoss != other.maximumLoss)
			return false;
		if (periodMoney != other.periodMoney)
			return false;
		if (Double.doubleToLongBits(taxA) != Double
				.doubleToLongBits(other.taxA))
			return false;
		if (Double.doubleToLongBits(taxB) != Double
				.doubleToLongBits(other.taxB))
			return false;
		if (taxableProfit != other.taxableProfit)
			return false;
		if (taxes != other.taxes)
			return false;
		if (time != other.time)
			return false;
		return true;
	}
	
	public Period copy() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
		bos.close();
		byte[] byteData = bos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
		Period  period = (Period) new ObjectInputStream(bais).readObject();
		return period;
	}
}
