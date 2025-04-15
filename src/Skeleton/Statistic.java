package Skeleton;

import javax.naming.event.ObjectChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is used to represent a Skeleton.Statistic. For an example,
 * see Skeleton.WorkerStatistic. We can give it a name like "ActiveUnits",
 * and this will represent some metric of your simulation (in that
 * case it would number of units with the same name performing
 * a particular action). There's also the RobotActionsPerformed
 * statistic in the Robot class.
 *
 * In general, you should be able to get by using the Skeleton.WorkerStatistic,
 * but it would be helpful to make a different ones for more specialized summarization,
 * or value parsing.
 */

public abstract class Statistic implements Iterable<Object> {
	protected ArrayList<Object> values;
	private final String name;

	public Statistic(String name) {
		this.name = name;
		this.values = new ArrayList<Object>();
	}

	public String getName() {
		return this.name;
	}

	// Stats are all iterable, making it easier to use in for-loops
	public Iterator<Object> iterator() {
		return values.iterator();
	}

	// Use these methods to add/remove/get values
	public synchronized void addValue(Object item) {
		this.values.add(item);
	}
	public synchronized void removeValue() {
		this.removeValue(0);
	}
	public synchronized void removeValue(int index) {
		this.values.remove(index);
	}
	public Object getValue(int index) {
		return this.values.get(index);
	}

	// Implement these to summarize, and print the statistic
	public abstract float summarize();
	public abstract void printStatistic();
}