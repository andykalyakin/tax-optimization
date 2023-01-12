package search.particleSwarm;

import java.util.List;

import misc.Period;
import misc.TaxFormula;

/**
 * A class representing the velocity of a particle
 */
public class ParticleVelocity {
	private float[] decisionVelocities;
	private float[] carrybackVelocities;
	
	/**
	 * Generates a new random ParticleVelocity
	 * @param periods Periods of Particle swarm. the periods already have to have the correct decisions set !!!
	 */
	public ParticleVelocity(List<Period> periods) {
		// periods[0] does only contain the input money and does not need to be optimized, so values at the zero position are not used.
		// however we include them into the arrays to have less of -1 / +1 computations with the period indices
		decisionVelocities = new float[periods.size()];
		carrybackVelocities = new float[periods.size()];
		for (int i=1; i<periods.size(); ++i) {
			decisionVelocities[i] =  ParticleSwarm.random.nextFloat()*2.0f-1.0f; // between -1 and 1
			int maxLossCarryback = TaxFormula.calculateMaximumLosscarryback(periods.get(i-1), periods.get(i));
			carrybackVelocities[i] = ParticleSwarm.random.nextFloat() * 2.0f * maxLossCarryback - maxLossCarryback; // between -maxlosscaryyback and +maxlosscarryback
		}
	}
	
	
	/**
	 * Update the velocities. No bounds need to be enforced.
	 * @param currentPosition the current position of the particle
	 * @param bestParticlePosition The best position of the particle
	 * @param globalBestPosition The global best position of the particle swarm
	 */
	public void updateVelocities(ParticlePosition currentPosition, ParticlePosition bestParticlePosition, ParticlePosition globalBestPosition) {
		float randomWeightBestPosition = ParticleSwarm.random.nextFloat();
		float randomWeightGlobalBestPosition = ParticleSwarm.random.nextFloat();
		for (int i=1; i<decisionVelocities.length; ++i) {
			decisionVelocities[i] = ParticleSwarm.weightCurrentVelocity * decisionVelocities[i]
					+ ParticleSwarm.weightBestParticlePosition * randomWeightBestPosition * (bestParticlePosition.getDecision(i)-currentPosition.getDecision(i))
					+ ParticleSwarm.weightGlobalBestPosition * randomWeightGlobalBestPosition * (globalBestPosition.getDecision(i)-currentPosition.getDecision(i));
			carrybackVelocities[i] = ParticleSwarm.weightCurrentVelocity * carrybackVelocities[i]
					+ ParticleSwarm.weightBestParticlePosition * randomWeightBestPosition * (bestParticlePosition.getCarryback(i)-currentPosition.getCarryback(i))
					+ ParticleSwarm.weightGlobalBestPosition * randomWeightGlobalBestPosition * (globalBestPosition.getCarryback(i)-currentPosition.getCarryback(i));
			// we don't need to enforce any bounds for the velocities
		}
	}
	
	
	public float getDecisionVelocity(int i) {
		return decisionVelocities[i];
	}
	
	
	public float getCarrybackVelocity(int i) {
		return carrybackVelocities[i];
	}
}
