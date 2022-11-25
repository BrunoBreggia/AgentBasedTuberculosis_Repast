/**
 * 
 */
package jzombies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import geocovid.DataSet;
import geocovid.agents.BuildingAgent;
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
	
	// attributes
	private ISchedule schedule;
	static private String tag = "susceptible";
	private Map<Integer, Integer> socialInteractions = new HashMap<>();
	/** Indice estado de markov donde esta (0 es la casa, 1 es el trabajo/estudio, 2 es ocio, 3 es otros) */
	private int currentState = 0;
	/** Ubicacion actual dentro de parcela o null si afuera*/ 
	private int[] currentPosition = {0,0};
	/** Parcela actual o null si en exterior */
	private BuildingAgent currentBuilding = null;

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
	
	/**
	 * Aumenta cantidad de contactos con el humano de la Id dada.
	 * @param humanId id HumanAgent
	 */
	public void addSocialInteraction(int humanId) {
		// Aca uso la ID del humano como key, por si se quiere saber cuantos contactos se repiten
		if (socialInteractions.containsKey(humanId))
			socialInteractions.put(humanId, socialInteractions.get(humanId) + 1);
		else
			socialInteractions.put(humanId, 1);
	}
	
	/**
	 * Informa la cantidad de contactos en el dia y reinicia el Map.
	 * @see DataSet#COUNT_UNIQUE_INTERACTIONS
	 * @return contactos personales diarios
	 */
	public int getSocialInteractions() {
		int count = 0;
		if (DataSet.COUNT_UNIQUE_INTERACTIONS) {
			count = socialInteractions.size();
		}
		else {
			for (Object value : socialInteractions.values())
				count += (Integer)value;
		}
		socialInteractions.clear();
		return count;
	}
	
	/**
	 * Re-ingresa al contexto al salir de UTI.
	 */
	public void addRecoveredToContext() {
		// Si esta hospitalizado o vive afuera no vuelve a entrar
		if (hospitalized)
			return;
		context.add(this);
		
		currentState = 0;
		currentPosition = homePlace.insertHuman(this);
		currentBuilding = homePlace;
		// switchLocation();
	}
	
	
}
