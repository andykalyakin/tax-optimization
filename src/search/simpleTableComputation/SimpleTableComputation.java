package search.simpleTableComputation;

import gui.AlgorithmFrame;

import java.util.List;

import javax.swing.JOptionPane;

import misc.Period;
import misc.TaxFormula;
import search.Search;

/**
 * This algorithm simple computes the table with the user specified values
 */
public class SimpleTableComputation extends Search {

	private List<Period> periods;
	private float interesstRate;
	private AlgorithmFrame gui;
	
	// for the plots
	private boolean drawPlots;
	
	public SimpleTableComputation(List<Period> periods, float interesstRate, boolean drawPlots) {
		this.periods =  periods;
		this.interesstRate = interesstRate;
		this.drawPlots = drawPlots;
		gui = new AlgorithmFrame(periods, "Table for user specified values");
	}
	
	@Override
	public void run() {
		// give the user a hint if one loss carryback was not allowed
		boolean showHint = false;
		for (int i=1; i<periods.size(); ++i) {
			Period current = periods.get(i);
			Period predecessor = periods.get(i-1);
			TaxFormula.updateInterest(current, predecessor, interesstRate);
			current.setMaximumLoss(TaxFormula.calculateMaximumLosscarryback(predecessor, current));
			if (current.getLossCarryback() < current.getMaximumLossCarryback()) { // loss carrybacks are negative
				current.setLossCarryback(current.getMaximumLossCarryback());
				showHint=true;
			}
			TaxFormula.recalculatePeriodMoney(current, predecessor, false, interesstRate, false);
		}
		
		gui.updatePeriodTable(periods);
		if (drawPlots) {
			gui.updatePlots(null, null, null, null, null);
		}
		
		if (showHint) {
			gui.printDebugMessage("One of your supplied carrybacks was smaller than max loss carryback. It has been fixed automatically.");
			JOptionPane.showMessageDialog(gui, "You supplied a loss carryback smaller than the allowed maximum loss caryyback. The value has been adapted.",
					"Warning!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			gui.printDebugMessage("All supplied carrybacks were okay.");
		}
	}
}
