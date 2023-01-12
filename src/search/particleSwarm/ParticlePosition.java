package search.particleSwarm;

import java.util.List;

import misc.Period;
import misc.TaxFormula;


/**
 * A class representing the position of a particle
 */
public class ParticlePosition {
	private float[] decisions; // will get rounded to 0 or 1 and mapped to a Decision
	private float[] carrybacks;
	private int outcome; // the resulting outcome for those decisions and carrybacks
	
	/**
	 * Generates a new random ParticlePosition
	 * @param periods Periods of Particle swarm. Warning: the periods get modified, which although is not a problem in the implementation of particle swarm.
	 */
	public ParticlePosition(List<Period> periods, float interestRate) {
		// periods[0] does only contain the input money and does not need to be optimized, so values at the zero position are not used.
		// however we include them into the arrays to have less of -1 / +1 computations with the period indices
		decisions = new float[periods.size()];
		carrybacks = new float[periods.size()];
		// set the decisions and carrybacks
		for (int i=1; i<periods.size(); ++i) {
			Period current = periods.get(i);
			Period predecessor = periods.get(i-1);
			decisions[i] = ParticleSwarm.random.nextFloat(); // between 0 and 1
			current.setDecision(decisions[i]);
			TaxFormula.updateInterest(current, predecessor, interestRate);
			current.setMaximumLoss(TaxFormula.calculateMaximumLosscarryback(predecessor, current));
			carrybacks[i] = ParticleSwarm.random.nextFloat() * current.getMaximumLossCarryback(); // between 0 and max loss carryback
			current.setLossCarryback(Math.round(carrybacks[i]));
			TaxFormula.recalculatePeriodMoney(current, predecessor, false, interestRate, false);
		}
		outcome = periods.get(periods.size()-1).getPeriodMoney();
	}
	
	
	/**
	 * copy constructor
	 * @param toCopy the particle position to copy
	 */
	public ParticlePosition(ParticlePosition toCopy) {
		copy(toCopy);
	}
	
	
	/**
	 * Copies a particle position into this object
	 * @param toCopy The position to copy
	 */
	public void copy(ParticlePosition toCopy) {
		if (decisions==null || toCopy.decisions.length != decisions.length) {
			decisions = new float[toCopy.decisions.length];
			carrybacks = new float[toCopy.decisions.length];
		}
		for (int i=0; i<toCopy.decisions.length; ++i) {
			decisions[i] = toCopy.decisions[i];
			carrybacks[i] = toCopy.carrybacks[i];
		}
		outcome = toCopy.outcome;
	}
	
	
	public float getDecision(int i) {
		return decisions[i];
	}
	
	
	public float getCarryback(int i) {
		return carrybacks[i];
	}
	
	
	public int getOutcome() {
		return outcome;
	}
	
	
	/**
	 * Applies the velocities to the current position and updates the best position
	 * @return true if the best position has changed
	 */
	public void updatePosition(List<Period> periods, float interestRate, ParticleVelocity velocity) {
		// update the decisions and carrybacks
		for (int i=1; i<periods.size(); ++i) {
			Period current = periods.get(i);
			Period predecessor = periods.get(i-1);
			
			decisions[i]+=velocity.getDecisionVelocity(i);
			if (decisions[i] > 1.0f)
				decisions[i] = 1.0f;
			else if (decisions[i] < 0.0f)
				decisions[i] = 0.0f;
			current.setDecision(decisions[i]);
			
			TaxFormula.updateInterest(current, predecessor, interestRate);
			current.setMaximumLoss(TaxFormula.calculateMaximumLosscarryback(predecessor, current));
			
			carrybacks[i]+=velocity.getCarrybackVelocity(i);
			if (carrybacks[i] > 0)
				carrybacks[i] = 0;
			else {
				if (carrybacks[i] < current.getMaximumLossCarryback()) // maxLossCarryback is <= 0
					carrybacks[i] = current.getMaximumLossCarryback();
			}
			current.setLossCarryback(Math.round(carrybacks[i]));
			TaxFormula.recalculatePeriodMoney(current, predecessor, false, interestRate, false);
		}
		outcome = periods.get(periods.size()-1).getPeriodMoney();
	}
}
