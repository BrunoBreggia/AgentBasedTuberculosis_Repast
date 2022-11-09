/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

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
		int time = 0; //(int)schedule.getTickCount();
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
	
	/**
	 * This infected person will kind of chase the healthy people
	 */
	@ScheduledMethod(start=1, interval=1)
	public void step() {
		// get the grid location of this Infected
		GridPoint pt = grid.getLocation(this);
		// use the GridCellNgh class to create GridCells for the surrounding neighbourhood
		GridCellNgh<Susceptible> nghCreator = new GridCellNgh<Susceptible> (grid, pt, Susceptible.class, 1, 1);
		List<GridCell<Susceptible>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithMostSusceptibles= null;
		int maxCount = -1;
		for (GridCell<Susceptible> cell : gridCells) {
			if (cell.size() > maxCount) {
				pointWithMostSusceptibles = cell.getPoint();
				maxCount = cell.size();
			}
		}
		
		moveTowards(pointWithMostSusceptibles);
		infect();
	}
	
	/**
	 * Move in the direction of a specific point
	 */
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

	/**
	 * Analyzes who can be infected nearby
	 */
	public void infect() {
		GridPoint pt = grid.getLocation(this);
		List<Object> susceptibles = new ArrayList<Object>();
		
		// Hay probabilidad de contagio si hay un susceptible en el mismo lugar que un infectado
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Susceptible) {
				susceptibles.add(obj);
			}
		}
		
		if (susceptibles.size()>0) {
			int index = RandomHelper.nextIntFromTo(0, susceptibles.size()-1);
			Object obj = susceptibles.get(index);
			NdPoint spacePt = space.getLocation(obj);
			Context<Object> context = ContextUtils.getContext(obj);
			context.remove(obj);
			int humanID = ((Susceptible)obj).humanID;
			Infected infected = new Infected(space, grid);
			context.add(infected);
			space.moveTo(infected, spacePt.getX(), spacePt.getY());
			grid.moveTo(infected, pt.getX(), pt.getY());
			
			Network<Object> net = (Network<Object>) context.getProjection("infection network");
			net.addEdge(this, infected);
		}
	}

}
