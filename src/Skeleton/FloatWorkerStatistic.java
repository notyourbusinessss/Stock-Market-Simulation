package Skeleton;

/**
 * Use this as a general class for holding Float-based
 * statistics. It's used in Skeleton.Unit, and Main.Robot.
 */

public class FloatWorkerStatistic extends Statistic {

	public FloatWorkerStatistic(String name) {
		super(name);
	}

	/**
	 * Summarize all the valeus that were added to this statistic.
	 * @return an int that summarizes this statistic.
	 */
	public float summarize() {
		float sum = 0;
		for (Object i : this) {
			sum += (Float) i;
		}
		return sum;
	}

	/**
	 * Print out the summary value.
	 */
	public void printStatistic() {
		System.out.printf("\t\tSummary value: %s%n", this.summarize());
	}
}