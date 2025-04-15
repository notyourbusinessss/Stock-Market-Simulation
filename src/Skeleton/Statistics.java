package Skeleton;

import java.util.HashMap;

/**
 * This class contains the set of statistics for an object in the simulation. These sets are
 * contained within the Singleton StatisticsContainer class. See Main.Robot for an example of
 * how to add to this set.
 * */

public class Statistics {
	private SimulationInput input;
	private HashMap<String, Statistic> allStats;

	/**
	 * @param input The simulation input.
	 **/
	public Statistics(SimulationInput input) {
		this.input = input;
		this.allStats = new HashMap<String, Statistic>();
	}

	/**
	 * Add a new statistic (like WorkerStatistic) to the set of stats.
	 * @param name The name of the statistic.
	 * @param stat The Statistics object
	 * @return The statistics object that was added.
	 */
	public Statistic addStatistic(String name, Statistic stat) {
		this.allStats.putIfAbsent(name, stat);
		return this.allStats.get(name);
	}

	/**
	 * Get the statistics associated with the given name.
	 * @param name The name of the statistic.
	 * @return The associated Statistic object.
	 */
	public Statistic getStatistic(String name) {
		return this.allStats.get(name);
	}

	/**
	 * Print out the final results from the simulation.
	 * */
	public void printStatistics() {
		for (String key: allStats.keySet()){
			System.out.println(String.format("\tStatistics for %s:", key));
			allStats.get(key).printStatistic();
		}
	}
}
