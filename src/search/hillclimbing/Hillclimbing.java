package search.hillclimbing;

import gui.AlgorithmFrame;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import misc.Decision;
import misc.Period;
import misc.TaxFormula;
import search.Search;

public class Hillclimbing extends Search {
	
	private enum Direction { UP, DOWN };
	
	private AlgorithmFrame frame;
	private List<Period> periods;
	private double interesstRate;
	
	private List<Period> lossPeriods;
	private double stepSize;
	private int tries = 10;
	
	private boolean drawPlots;
	private boolean completeOptimization = true;
	
	public Hillclimbing(List<Period> periods, double interesstRate, boolean drawPlots, double stepSize, boolean completeOptimization) {
		this.periods = periods;
		this.interesstRate = interesstRate;
		this.drawPlots = drawPlots;
		this.stepSize = stepSize;
		this.completeOptimization = completeOptimization;
		frame = new AlgorithmFrame(periods, "Hillclimbing");
		lossPeriods = new LinkedList<>();
	}

	@Override
	public void run() {
		frame.setTitle("Hillclimbing - calculating...");
		setDecisions();
		for (int i = 0; i < periods.size(); i++) {
			if (periods.get(i).getIncome() < 0) {
				lossPeriods.add(periods.get(i));
				if (completeOptimization) {
				optimizeLoss();
				} else {
					Period predeseccor = i == 0 ? new Period(-1, 0, Decision.SHARED, 0) : periods.get(i - 1);
					Period successor = i == periods.size() - 1 ? new Period(periods.size(), 0, Decision.SHARED, 0) : periods.get(i + 1);
					optimizeLoss(predeseccor, periods.get(i), successor);
				}
			}
		}
		if (drawPlots) {
			frame.updatePlots(null, null, null, null, null);
		}
		frame.setTitle("Hillclimbing - done.");
	}

	private void optimizeLoss() {
		if (lossPeriods.isEmpty()) return;
		Period resultPeriod = periods.get(periods.size() - 1);
		int bestResult = resultPeriod.getPeriodMoney();
		Period lastPeriod = lossPeriods.get(lossPeriods.size() -1);
		Period firstPeriod = lossPeriods.get(0);
		int currentPeriod = 0;
		while (lastPeriod.getLossCarryback() != lastPeriod.getMaximumLossCarryback()) {
			int lossCarryback = (int) Math.round(firstPeriod.getLossCarryback() * stepSize);
			if (lossCarryback > firstPeriod.getMaximumLossCarryback()) {
				lossCarryback = firstPeriod.getMaximumLossCarryback();
				for (int i = 0; i < lossPeriods.size(); i++) {
					if (lossPeriods.get(i).getLossCarryback() < lossPeriods.get(i).getMaximumLossCarryback()) {
						int loss = (int) Math.round(lossPeriods.get(i).getLossCarryback() * stepSize);
						if (loss > lossPeriods.get(i).getMaximumLossCarryback()) {
							loss = lossPeriods.get(i).getMaximumLossCarryback();
						}
						lossPeriods.get(i).setLossCarryback(loss);
					}
				}
			}
			firstPeriod.setLossCarryback(lossCarryback);
			TaxFormula.updatePeriods(periods, (float) interesstRate);
			if (resultPeriod.getPeriodMoney() > bestResult) {
				bestResult = resultPeriod.getPeriodMoney();
			} else {
				return;
			}
			if (lossPeriods.get(currentPeriod).getLossCarryback() == lossPeriods.get(currentPeriod).getMaximumLossCarryback()) {
				for (int i = 0; i < currentPeriod; i++) {
					lossPeriods.get(i).setLossCarryback(0);
				}
				currentPeriod ++;
				if (currentPeriod == lossPeriods.size()) {
					return;
				}
			}
		}
	}
	
	private void optimizeLoss(Period predeseccor, Period current,
			Period successor) {
		if (current.getMaximumLossCarryback() == 0) return;
		Random random = new Random();
		int bestValue = 0;
		frame.printDebugMessage("starting los optimization for " + current.getTime());
		for (int i = 0; i < tries; i++) {
			int position = -1 * random.nextInt(-1 * current.getMaximumLossCarryback());
			Direction direction = random.nextBoolean() ? Direction.UP : Direction.DOWN;
			current.setLossCarryback(position);
			TaxFormula.calculatePeriod(predeseccor, current, successor);
			if (current.getPeriodMoney() > bestValue) {
				bestValue = current.getPeriodMoney();
				frame.printDebugMessage("new best value: " + bestValue);
			}
			boolean finished = false;
			while (!finished) {
				if (direction.equals(Direction.UP)) {
					position += position * stepSize;
				} else {
					position -= position * stepSize;
				}
				if (position < current.getMaximumLossCarryback() || position >= 0) return;
				current.setLossCarryback(position);
				TaxFormula.calculatePeriod(predeseccor, current, successor);
				if (current.getPeriodMoney() < bestValue) {
					finished = true;
				} else {
					bestValue = current.getPeriodMoney();
					frame.printDebugMessage("new best value: " + bestValue);
				}
				frame.updatePeriodTable(periods);
//				frame.updatePlots(plotBestOutcomesPerIteration);
			}
		}
	}

	public void setDecisions() {
		for (int i = 1; i < periods.size(); i++) {
			Period predesseccor = i - 1 < 0 ? null : periods.get(i - 1);
			Period successor = i + 1 == periods.size() ? null : periods.get(i + 1);
			if (predesseccor != null) {
				periods.get(i).setInteresst((int) (predesseccor.getPeriodMoney() *  interesstRate));
			}
			TaxFormula.calculatePeriod(predesseccor, periods.get(i), successor);
			if (periods.get(i).getIncome() < 0 && periods.get(i).getMaximumLossCarryback() == 0) {
				periods.get(i).setDecision(Decision.DIVIDED);
				TaxFormula.calculatePeriod(predesseccor, periods.get(i), successor);
				continue;
			}
			int sharedDecision = periods.get(i).getPeriodMoney();
			periods.get(i).setDecision(periods.get(i).getDecision().equals(Decision.SHARED) ? Decision.DIVIDED : Decision.SHARED);
			TaxFormula.calculatePeriod(predesseccor, periods.get(i), successor);
			if (sharedDecision > periods.get(i).getPeriodMoney()) {
				periods.get(i).setDecision(Decision.SHARED);
				TaxFormula.calculatePeriod(predesseccor, periods.get(i), successor);
			}
		}
		frame.updatePeriodTable(periods);
	}

}
