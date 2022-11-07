/**
 * 
 */
package jzombies;

import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * @author Bruno M. Breggia
 *
 */
public class Infected extends Human {
	
	private boolean moved;
	private ISchedule schedule;
	static private String tag = "infected";

	public Infected(ContinuousSpace<Object> space, Grid<Object> grid) {
		// Call the superclass constructor
		super(space, grid);
		// Complete inner clinical history
		int time = (int)schedule.getTickCount();
		this.history.put(time, tag);
	}
	
	/**
	 * Constructor from Susceptible
	 */
	public Infected(Susceptible susceptible) {
		// Call the superclass constructor
		super(susceptible.space, susceptible.grid, susceptible.humanID);
		// Complete inner clinical history
		int time = (int)schedule.getTickCount();
		this.history.put(time, tag);
	}
	
	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			
			moved = true;
		}
	}

}
