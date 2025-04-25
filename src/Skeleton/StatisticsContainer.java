package Skeleton;

import java.util.LinkedHashMap;

/**
 * Singleton object for all statistics. At this level, we have one set of Skeleton.Statistics
 * for each Skeleton.Unit. The name of the object is the key used here in the HashMap.
 *
 * See the submitStatistics method in the Robot, and the Skeleton.Unit to see how
 * you should create, and add statistics.
 * */

public class StatisticsContainer {

	//Contains each statistic for the unit of a given name
	private LinkedHashMap<String, Statistics> allStatistics;
	private static StatisticsContainer stats;
	private SimulationInput input;

	/**
	 * Constructor that creates the hash table.
	 */
	private StatisticsContainer (SimulationInput input){
		this.input = input;
		this.allStatistics = new LinkedHashMap<String, Statistics>();
	}


	/**
	 * Constructs a container with statistics split into components.
	 */
	private StatisticsContainer (SimulationInput input, String[] compNames) {
		this(input);
		for (String comp: compNames){
			this.allStatistics.put(comp, new Statistics(this.input));
		}
	}

	/**
	 * @return A statistics object.
	 **/
	public static StatisticsContainer getInstance() {
		return StatisticsContainer.getInstance(new SimulationInput());
	}

	/**
	 * Returns the statistics object if it exists, otherwise
	 * it creates it.
	 * @return A statistics object.
	 * */
	public static StatisticsContainer getInstance(SimulationInput si) {
		if (StatisticsContainer.stats == null) {
			StatisticsContainer.stats = new StatisticsContainer(si);
		}
		return StatisticsContainer.stats;
	}

	/**
	 * Add a statistic component to the hash table
	 */
	public Statistics addComponent (String component){
		allStatistics.put(component, new Statistics(this.input));
		return this.getComponent(component);
	}


	/**
	 * Get the server statistics for the given server name.
	 * @return Server statistics.
	 */
	public Statistics getComponent (String component){
		return this.allStatistics.get(component);
	}


	/**
	 * Print the statistics container.
	 */
	public void printStatisticsContainer (){
		for (String key: this.allStatistics.keySet()){
			System.out.println(String.format("Statistics for %s:", key));
			this.allStatistics.get(key).printStatistics();
			System.out.println("\n\n");
		}
	}
}