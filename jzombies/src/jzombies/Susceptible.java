/**
 * 
 */
package jzombies;

import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/**
 * @author Bruno Breggia
 *
 */
public class Susceptible extends Human {
	
	private ISchedule schedule;
	static private String tag = "susceptible";

	public Susceptible(ContinuousSpace<Object> space, Grid<Object> grid) {
		// Call the superclass constructor
		super(space, grid);
		// Complete inner clinical history
		int time = (int)schedule.getTickCount();
		this.history.put(time, tag);
	}

	/**
	 * Susceptible gets infected
	 */
	public Infected getInfected() {
		Infected infected = new Infected(this);
		return infected;
	}

}
