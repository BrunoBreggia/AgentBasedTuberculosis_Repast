/**
 * 
 */
package jzombies;

import java.util.List;

import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

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
		int time = 0; //(int)schedule.getTickCount();
		this.history.put(time, tag);
	}

	/**
	 * Susceptible gets infected
	 */
	public Infected getInfected() {
		Infected infected = new Infected(this);
		return infected;
	}

	@Watch(watcheeClassName="jzombies.Infected",
			watcheeFieldNames="moved",
			query="within_moore 1",
			whenToTrigger=WatcherTriggerSchedule.IMMEDIATE)
	public void run() {
		//get the grid location of this human
		GridPoint pt = grid.getLocation(this);
		// use the GridCellNgh class to create GridCells for the surrounding neighborhood
		GridCellNgh<Infected> nghCreator = new GridCellNgh<Infected> (grid, pt, Infected.class, 1, 1);
		List<GridCell<Infected>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithLeastInfected = null;
		int minCount = Integer.MAX_VALUE;
		for (GridCell<Infected>cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastInfected = cell.getPoint();
				minCount = cell.size();
			}
		}
	}
	
	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		}
	}
	
}
