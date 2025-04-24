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
		StockMarket stockMarket = new StockMarket(new SimulationInput(), 1000, 50.00, 1000, 0);
		Thread A = new Thread(stockMarket);
		A.start();

        try {
            A.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
