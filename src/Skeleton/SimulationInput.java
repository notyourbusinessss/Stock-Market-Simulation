package Skeleton;

import java.lang.RuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * This class helps you retrieve values from the given input.
 * */
public class SimulationInput {
	// The inner implementation of the input
	private ArrayList<ArrayList<String>> input;

	public SimulationInput() {
		this(new ArrayList<ArrayList<String>>());
	}

	/**
	 * The primary constructor for the Skeleton.SimulationInput object.
	 * 
	 * @param input The input in the form of an array of string arrays.
	 * */
	public SimulationInput(ArrayList<ArrayList<String>> input) {
		this.input = input;
	}

	/**
	 * Use to set the input array to something else (complet overwrite).
	 * 
	 * @param input The input in the form of an array of string arrays.
	 * */
	public void setInputArray(ArrayList<ArrayList<String>> input) {
		this.input = input;
	}

	/**
	 * Add a new input key/value pairing.
	 * 
	 * @param key The name of the input (used for searching).
	 * @param value The value of the input.
	 * */
	public void addInput(String key, Collection<String> value) {
		this.input.add(new ArrayList<String>(List.of(key)));
		this.input.get(this.input.size()-1).addAll(value);
	}

	/**
	 * Return the first value in the input converted to an Integer.
	 * 
	 * @param key The key to search for.
	 * */
	public Integer getIntegerInput(String key) {
		return Integer.valueOf(this.getInput(key).get(0));
	}

	/**
	 * Return the values in the input.
	 * 
	 * @param key The key to search for.
	 * */
	public ArrayList<String> getInput(String key) {
		return new ArrayList<String>(this.findKey(key));
	}

	/**
	 * Searches for a given key in the input array.
	 * 
	 * @param key The input key to search for (the first value).
	 * @return An array of values found at the key.
	 * @throws RuntimeException Throws this failure when we can't
	 * 		   find the input key. This should fail the entire
	 * 		   simulation.
	 **/
	private ArrayList<String> findKey(String key) throws RuntimeException {
		for (ArrayList<String> pairing : input) {
			if (pairing.get(0).equals(key)) {
				return new ArrayList<String>(pairing.subList(1, pairing.size()));
			}
		}
		throw new RuntimeException(String.format("Cannot find input key: %s", key));
	}
}
