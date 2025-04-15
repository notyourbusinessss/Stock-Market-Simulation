package Skeleton;

/**
 * Use this as a general class for holding Integer-based
 * statistics. It's used in Skeleton.Unit, and Robot.
 */

public class WorkerStatistic extends Statistic {

	public WorkerStatistic(String name) {
		super(name);
	}

	/**
	 * Summarize all the valeus that were added to this statistic.
	 * @return an int that summarizes this statistic.
	 */
	public float summarize() {
		float sum = 0;
		for (Object i : this) {
			sum += (Integer) i;
		}
		return sum;
	}

	/**
	 * Print out the summary value.
	 */
	public void printStatistic() {
		System.out.printf("\t\tSummary value: %f%n", this.summarize());
	}
}