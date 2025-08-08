package org.shivas.core.core.actions;

import org.shivas.data.Container;
import org.shivas.data.entity.Action;
import org.shivas.core.config.Config;
import org.shivas.core.core.GameActor;
import org.shivas.core.core.maps.GameMap;

import java.util.Map;

public class TeleportAction implements Action {

	public static final String NAME = "TELEPORT";
	
	private static final String START_PLACEHOLDER = "%start%";
	
	public static TeleportAction make(Map<String, String> parameters, Container ctner, Config config) {		
		String mapRaw  = parameters.get("map"),
			   cellRaw = parameters.get("cell");

		int mapId = mapRaw.equalsIgnoreCase(START_PLACEHOLDER) ?
				config.startMapId() :
				Integer.parseInt(mapRaw);
		
		GameMap map = ctner.get(GameMap.class).byId(mapId);
		
		short cell = mapRaw.equalsIgnoreCase(START_PLACEHOLDER) ? 
				config.startCell() : 
				Short.parseShort(cellRaw);
		
		return new TeleportAction(map, cell);
	}
	
	private final GameMap map;
	private final short cell;

	public TeleportAction(GameMap map, short cell) {
		this.map = map;
		this.cell = cell;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean able(Object target) {
		return target instanceof GameActor;
	}

	@Override
	public void apply(Object arg1) {
		GameActor target = (GameActor) arg1;
		
		target.teleport(map, cell);
	}

}
