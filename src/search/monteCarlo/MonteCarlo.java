package search.monteCarlo;

import gui.AlgorithmFrame;
import gui.AlgorithmFrame.PlotType;

import java.util.List;

import de.erichseifert.gral.data.DataTable;
import misc.Period;
import misc.TaxFormula;
import search.Search;
import search.particleSwarm.ParticlePosition;

/**
 * This is an implementation of the monte carlo algorithm. It is implemented in the same way as the initialization of the Particle swarm algorithm
 * because this is equivalent to Monte Carlo
 */
public class MonteCarlo extends Search {

	private List<Period> periods;
	private float interesstRate;
	private ParticlePosition bestResult;
	private AlgorithmFrame gui;
	private int numberOfIterations;
	
	// for the plots
	private boolean drawPlots;
	private DataTable outcomes;
	
	@SuppressWarnings("unchecked")
	public MonteCarlo(List<Period> periods, float interesstRate, int numberOfIterations, boolean drawPlots) {
		this.periods =  periods;
		this.interesstRate = interesstRate;
		this.numberOfIterations = numberOfIterations-1; // -1 because the initialization is also an iteration
		this.drawPlots = drawPlots;
		if (drawPlots) {
			outcomes = new DataTable(Integer.class);
		}
		gui = new AlgorithmFrame(periods, "Monte Carlo");
	}
	
	@Override
	public void run() {
		gui.setTitle("Monte Carlo - calculating...");
		bestResult = new ParticlePosition(periods, interesstRate);
		if (drawPlots)
			outcomes.add(bestResult.getOutcome());
		for (int i=0; i<numberOfIterations; ++i) {
			ParticlePosition newTry = new ParticlePosition(periods, interesstRate); // generate random particle
			if (newTry.getOutcome() > bestResult.getOutcome())
				bestResult.copy(newTry);
			if (drawPlots)
				outcomes.add(newTry.getOutcome());
		}
		updateGui();
		gui.printDebugMessage("Monte Carlo finished after "+numberOfIterations+" iterations with result: "+bestResult.getOutcome());
		if (drawPlots) {
			gui.setTitle("Monte Carlo - drawing plots...");
			gui.updatePlots(outcomes, PlotType.BarPlot,  "Outcome distribution", "Outcome", "Occurence");
		}
		gui.setTitle("Monte Carlo - done.");
	}
	
	
	private void updateGui() {
		// apply the best values to the periods
		for (int i=1; i<periods.size(); ++i) {
			periods.get(i).setDecision(bestResult.getDecision(i));
			periods.get(i).setLossCarryback(Math.round(bestResult.getCarryback(i)));
		}
		TaxFormula.updatePeriods(periods, interesstRate);
		gui.updatePeriodTable(periods);
	}
}
