package jzombies;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class JZombiesBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		context.setId("jzombies");
		
		// Create a Network
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("infection network", context, true);
		netBuilder.buildNetwork();
		
		// Create ContinuousSpace projection
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(), 
																			new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);
		
		// Create Grid projection
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid <Object> grid = gridFactory.createGrid("grid", context,
													new GridBuilderParameters<Object>(new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));
		
		// Populate with susceptible people
		Parameters params = RunEnvironment.getInstance().getParameters();
		int susceptibleCount = params.getInteger("susceptible_count");
		for (int i=0; i<susceptibleCount; i++) {
			context.add(new Susceptible(space, grid));
		}
		
		// Populate with infected people
		int infectedCount = params.getInteger("infected_count");
		for (int i=0; i<infectedCount; i++) {
			context.add(new Infected(space, grid));
		}
		
		// Update the grid location of the agents
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		return context;
	}

}
