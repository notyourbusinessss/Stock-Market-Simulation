import Skeleton.SimulationInput;

import java.lang.Thread;
import java.util.ArrayList;

/**
 * The class that is responsible for running the simulation.
 * 
 * You will need to modify the run method to initialize, and run all of your units.
 * */
public class Matrix {
	public static void run(SimulationInput input) {
		// Run units
		Robot rob = new Robot("Rob", input);
		Thread trob = new Thread(rob);

		trob.start();
		try {
			trob.join();
		} catch (InterruptedException e) {}
	}
}
