/*
 * This program comes with ABSOLUTELY NO WARRANTY.
 * (C) Philipp D. Schubert 2015
 * Multidimensional Scaling
 * please report bugs to:
 * schubert(at)cebitec.uni-bielefeld.de
 */
package graph.algorithms.gui.smacof.algorithms;

/**
 * A little stopwatch class which can measure the time between start() and end()
 * - the time can be returned in milliseconds, seconds and minutes.
 *
 * @author philipp
 */
public class StopWatch {

    private long starttime;
    private long endtime;

    /**
     * Starts the stop watch.
     */
    public void start() {
        this.starttime = System.currentTimeMillis();
    }

    /**
     * Stops the time measurement.
     */
    public void end() {
        this.endtime = System.currentTimeMillis();
    }

    /**
     * Returns the difference between start() and end() in milliseconds.
     *
     * @return time in milli seconds
     */
    public long getMillis() {
        return this.endtime - this.starttime;
    }

    /**
     * Returns the difference between start() and end() in seconds.
     *
     * @return time in seconds
     */
    public double getSecs() {
        return (this.endtime - this.starttime) / 1000.0;
    }

    /**
     * Returns the difference between start() and end() in minutes.
     *
     * @return time in minutes
     */
    public double getMins() {
        return (this.endtime - this.starttime) / (1000.0 * 60);
    }

}
