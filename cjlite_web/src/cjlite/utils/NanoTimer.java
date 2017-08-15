/**
 * 
 */
package cjlite.utils;

/**
 * @author kevin
 * 
 */
public final class NanoTimer {

	private long startTime;

	public void start() {
		startTime = System.nanoTime();
	}

	/**
	 * @return ms, NOT nano time
	 */
	public long stop() {
		long duration = System.nanoTime() - startTime;
		return duration / (1000000);
	}
}
