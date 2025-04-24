import Skeleton.SimulationInput;
import Skeleton.StatisticsContainer;
import Skeleton.Unit;
import Skeleton.WorkerStatistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Example class that implements the Skeleton.Unit abstract class.
 **/
class Robot extends Unit {
	public Robot(String name, SimulationInput input) {
		super(name, input);
		this.getStats().addStatistic(
				"RobotActionsPerformed",
				new WorkerStatistic("RobotActionsPerformed")
		);
	}

	public Robot(SimulationInput input) {
		super("DefaultRobotName", input);
	}

	/**
	 * The example robot yells something from the input, as well
	 * as it's given name.
	 * */
	public void performAction() {
		System.out.println(
			// Note how the value you receive is an arraylist object (so
			// you can set multiple values for the input field if needed).
			this.getSimInput().getInput("RobotsMustYell").get(0)
		);
		System.out.println(String.format("THIS IS %s", this.getName()));
	}

	/** Submit the number of actions performed (always 1).
	 *
	 * Use submitStatisics for things you want to submit AFTER the action is complete
	 * (this could be for things involving combination metrics, or conditional metrics).
	 * **/
	public void submitStatistics() {
		this.getStats().getStatistic("RobotActionsPerformed").addValue(1);
	}
}


/**
 * The main class is responsible for the testing. It has a helper method
 * that makes it easier to run many tests.
 **/
public class Main {
	/**
	 * Runs a test with the given input and returns the statistics
	 * produced from the test run. Simplifies the testing process.
	 * 
	 * @param input The input to run the test with.
	 * @return The statistics of the test run.
	 **/
	public static StatisticsContainer runTest(SimulationInput input) {
		// Initialize the stats singleton here so the input can
		// be ignored in future calls
		StatisticsContainer stats = StatisticsContainer.getInstance(input);
		StockMarket market = new StockMarket(input, 1000, 50.0, 10000, 0);
		Thread marketThread = new Thread(market);
		marketThread.start();

// Wait for market thread to finish
		try {
			marketThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}


		return stats;
	}

	/**
	 * See method above for details.
	 **/
	public static StatisticsContainer runTest(ArrayList<ArrayList<String>> input) {
		return runTest(new SimulationInput(input));
	}

	public static void main(String[] args) {
		/*
		You can either prepare your input as an array, or add it directly to your
		Skeleton.SimulationInput object (see below).

			ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
			input.add(
				// The time to run in seconds
				new ArrayList<String>(Arrays.asList("Time", "600"))
			);
			input.add(
				// The number of actions units must perform per second
				new ArrayList<String>(Arrays.asList("ActionsPerSecond", "60"))
			);
		*/ 

		SimulationInput si = new SimulationInput();
		si.addInput("Time", List.of("10")); // In seconds
		si.addInput("ActionsPerSecond", List.of("1"));
		si.addInput("RobotsMustYell", List.of("HELLO, WORLD"));

		// Run the simulation
		StatisticsContainer stats = runTest(si);

		// Post the finalized statistics
		stats.printStatisticsContainer();

		System.out.println("Bye !!!!!!");
		System.exit(0);
		/*
			Add many more tests below using different input. Try to probe for edge cases and organize
			your tests properly.
		*/

		// You can change the input, and then reset the statistics singleton with:
		// input = new Skeleton.SimulationInput();
		// // ... Add input
		// si.resetInstance(input);
	}
}
