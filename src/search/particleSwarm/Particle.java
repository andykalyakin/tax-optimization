package search.particleSwarm;

import java.util.List;

import misc.Period;

/**
 * This class represents a particle for the particle swarm algorithm.
 */
public class Particle {
	private ParticlePosition currentPosition;
	private ParticlePosition bestPosition;
	private ParticleVelocity particleVelocity;
	
	private List<Period> periods;
	private float interestRate;
	
	/**
	 * Generates a new Particle with random start position and random velocities
	 * @param periods Periods of Particle swarm. Warning: the periods get modified, which although is not a problem in the implementation of particle swarm.
	 */
	public Particle(List<Period> periods, float interestRate) {
		this.periods = periods;
		this.interestRate =  interestRate;
		currentPosition = new ParticlePosition(periods, interestRate);
		bestPosition = new ParticlePosition(currentPosition);
		particleVelocity = new ParticleVelocity(periods);
	}
	
	
	/**
	 * Update the velocities and position of this particle. This constitutes one iteration step.
	 * @param globalBestPosition the global best position of the particle swarm
	 * @return true if the best position of the particle has changed.
	 */
	public boolean updateVelocitiesAndPositions(ParticlePosition globalBestPosition) {
		particleVelocity.updateVelocities(currentPosition, bestPosition, globalBestPosition);
		currentPosition.updatePosition(periods, interestRate, particleVelocity);
		if (currentPosition.getOutcome() > bestPosition.getOutcome()) {
			bestPosition.copy(currentPosition);
			return true;
		} else
			return false;
	}
	
	
	/**
	 * Returns the best position of this particle
	 */
	public ParticlePosition getBestParticlePosition() {
		return bestPosition;
	}

}
