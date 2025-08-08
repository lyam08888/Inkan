package org.shivas.core.database.repositories;

import org.atomium.EntityManager;
import org.atomium.repository.impl.AbstractEntityRepository;
import org.atomium.util.pk.IntegerPrimaryKeyGenerator;
import org.atomium.util.query.*;
import org.shivas.common.statistics.CharacteristicType;
import org.shivas.data.Container;
import org.shivas.data.entity.Breed;
import org.shivas.data.entity.Experience;
import org.shivas.data.entity.Waypoint;
import org.shivas.protocol.client.enums.Gender;
import org.shivas.protocol.client.enums.OrientationEnum;
import org.shivas.core.config.Config;
import org.shivas.core.core.Colors;
import org.shivas.core.core.Location;
import org.shivas.core.core.PlayerLook;
import org.shivas.core.core.experience.PlayerExperience;
import org.shivas.core.core.items.PlayerBag;
import org.shivas.core.core.maps.GameMap;
import org.shivas.core.core.spells.SpellList;
import org.shivas.core.core.statistics.PlayerStatistics;
import org.shivas.core.core.stores.PlayerStore;
import org.shivas.core.core.waypoints.WaypointList;
import org.shivas.core.database.RepositoryContainer;
import org.shivas.core.database.models.Player;
import org.shivas.core.database.models.Spell;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class PlayerRepository extends AbstractEntityRepository<Integer, Player> {
	
	public static final String TABLE_NAME = "players";
	
	private final Config config;
	private final Container ctner;
	private final RepositoryContainer repositories;
	
	private DeleteQueryBuilder deleteQuery;
	private InsertQueryBuilder persistQuery;
	private UpdateQueryBuilder saveQuery;
	private SelectQueryBuilder loadQuery;

	@Inject
	public PlayerRepository(EntityManager em, Config config, Container ctner, RepositoryContainer repositories) {
		super(em, new IntegerPrimaryKeyGenerator());
		this.config = config;
		this.ctner = ctner;
		this.repositories = repositories;
		
		this.deleteQuery = em.builder().delete(TABLE_NAME).where("id", Op.EQ);
		this.persistQuery = em.builder()
				.insert(TABLE_NAME)
				.values("id", "owner_id", "name", "breed_id", "gender",
						"skin", "size", "color1", "color2", "color3",
						"level", "experience", "kamas",
						"map_id", "cell", "orientation",
						"saved_map_id", "saved_cell",
						"waypoints",
						"stat_points", "spell_points", "energy", "life", "action_points", "movement_points",
						"vitality", "wisdom", "strength", "intelligence", "chance", "agility");
		this.saveQuery = em.builder()
				.update(TABLE_NAME)
				.value("gender").value("skin").value("size").value("color1").value("color2").value("color3")
				.value("level").value("experience").value("kamas")
				.value("map_id").value("cell").value("orientation")
				.value("saved_map_id").value("saved_cell")
				.value("stat_points").value("spell_points").value("energy").value("life")
				.value("action_points").value("movement_points")
				.value("waypoints")
				.value("vitality").value("wisdom")
				.value("strength").value("intelligence").value("chance").value("agility")
				.where("id", Op.EQ);
		this.loadQuery = em.builder().select(TABLE_NAME);
	}
	
	private Location newDefaultLocation() {
		return new Location(config.startMap(), config.startCell(), OrientationEnum.SOUTH_EAST);
	}

	public Player createDefault(String name, int breed, Gender gender, int color1, int color2, int color3) {
		Player player = new Player(
				name,
				ctner.get(Breed.class).byId(breed),
				gender,
				new PlayerExperience(ctner.get(Experience.class).byId(config.startLevel())),
				newDefaultLocation(),
				newDefaultLocation()
		);
		
		player.setLook(new PlayerLook(
				player,
				player.getBreed().getDefaultSkin(gender),
				config.startSize(),
				new Colors(color1, color2, color3)
		));
		
		player.setStats(new PlayerStatistics(
				player,
				config.startActionPoints() != null ? config.startActionPoints() : player.getBreed().getStartActionPoints(),
				config.startMovementPoints() != null ? config.startMovementPoints() : player.getBreed().getStartMovementPoints(),
				config.startVitality(),
				config.startWisdom(),
				config.startStrength(),
				config.startIntelligence(),
				config.startChance(),
				config.startAgility()
		));
		
		player.setBag(new PlayerBag(player, repositories.items(), this, config.startKamas()));
		
		player.setSpells(new SpellList(player, repositories.spells()).fill());
		
		player.setWaypoints(new WaypointList(player));

        player.setStore(new PlayerStore(player, repositories.storedItems()));
		
		if (config.addAllWaypoints()) {
			for (Waypoint waypoint : ctner.get(Waypoint.class).all()) {
				player.getWaypoints().add(waypoint);
			}
		}
		
		return player;
	}
	
	@Override
	protected void onPersisted(Player entity) {
		for (Spell spell : entity.getSpells()) {
			repositories.spells().persistLater(spell);
		}
	}

	public boolean nameExists(String name) {
		for (Player player : entities.values()) {
			if (player.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public Player find(String name){
		for (Player player : entities.values()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	
	public Player findByIdOrName(String raw){
		try {
			int playerId = Integer.parseInt(raw);
			return find(playerId);
		} catch (NumberFormatException e) {
			return find(raw);
		}
	}

	@Override
	protected Query buildDeleteQuery(Player entity) {
		Query query = deleteQuery.toQuery();
		query.setParameter("id", entity.getPublicId());
		
		return query;
	}
	
	/**
	 * bind player's values to query
	 * @param query
	 * @param entity
	 * @return same
	 */
	private Query bindValues(Query query, Player entity) {
		query.setParameter("id", entity.getPublicId());
		query.setParameter("owner_id", entity.getOwner());
		query.setParameter("name", entity.getName());
		query.setParameter("breed_id", entity.getBreed().getId());
		query.setParameter("gender", entity.getGender());
		query.setParameter("skin", entity.getLook().skin());
		query.setParameter("size", entity.getLook().size());
		query.setParameter("color1", entity.getLook().colors().first());
		query.setParameter("color2", entity.getLook().colors().second());
		query.setParameter("color3", entity.getLook().colors().third());
		query.setParameter("level", entity.getExperience().level());
		query.setParameter("experience", entity.getExperience().current());
		query.setParameter("kamas", entity.getBag().getKamas());
		query.setParameter("map_id", entity.getLocation().getMap().getId());
		query.setParameter("cell", entity.getLocation().getCell());
		query.setParameter("orientation", entity.getLocation().getOrientation());
		query.setParameter("saved_map_id", entity.getLocation().getMap().getId());
		query.setParameter("saved_cell", entity.getLocation().getCell());
		query.setParameter("waypoints", WaypointList.serialize(entity.getWaypoints()));
		query.setParameter("stat_points", entity.getStats().statPoints());
		query.setParameter("spell_points", entity.getStats().spellPoints());
		query.setParameter("energy", entity.getStats().energy().current());
		query.setParameter("life", entity.getStats().life().current());
		query.setParameter("action_points", entity.getStats().get(CharacteristicType.ActionPoints).base());
		query.setParameter("movement_points", entity.getStats().get(CharacteristicType.MovementPoints).base());
		query.setParameter("vitality", entity.getStats().get(CharacteristicType.Vitality).base());
		query.setParameter("wisdom", entity.getStats().get(CharacteristicType.Wisdom).base());
		query.setParameter("strength", entity.getStats().get(CharacteristicType.Strength).base());
		query.setParameter("intelligence", entity.getStats().get(CharacteristicType.Intelligence).base());
		query.setParameter("chance", entity.getStats().get(CharacteristicType.Chance).base());
		query.setParameter("agility", entity.getStats().get(CharacteristicType.Agility).base());
		
		return query;
	}

	@Override
	protected Query buildPersistQuery(Player entity) {
		return bindValues(persistQuery.toQuery(), entity);
	}

	@Override
	protected Query buildSaveQuery(Player entity) {
		return bindValues(saveQuery.toQuery(), entity);
	}

	@Override
	protected Query buildLoadQuery() {
		return loadQuery.toQuery();
	}

	@Override
	protected Player load(ResultSet result) throws SQLException {		
		Player player = new Player(
				result.getInt("id"),
				repositories.accounts().find(result.getInt("owner_id")),
				result.getString("name"),
				ctner.get(Breed.class).byId(result.getInt("breed_id")),
				Gender.valueOf(result.getInt("gender")),
				new PlayerExperience(
						ctner.get(Experience.class).byId(result.getInt("level")),
						result.getLong("experience")
				),
				new Location(
						ctner.get(GameMap.class).byId(result.getInt("map_id")), 
						result.getShort("cell"),
						OrientationEnum.valueOf(result.getInt("orientation"))
				),
				new Location(
						ctner.get(GameMap.class).byId(result.getInt("saved_map_id")),
						result.getShort("saved_cell"),
						OrientationEnum.SOUTH_WEST
				)
		);
		
		player.setLook(new PlayerLook(
				player,
				result.getShort("skin"),
				result.getShort("size"),
				new Colors(
						result.getInt("color1"),
						result.getInt("color2"),
						result.getInt("color3")
				)
		));
		
		player.setStats(new PlayerStatistics(
				player,
				result.getShort("stat_points"),
				result.getShort("spell_points"),
				result.getInt("energy"),
				result.getInt("life"),
				result.getShort("action_points"),
				result.getShort("movement_points"),
				result.getShort("vitality"),
				result.getShort("wisdom"),
				result.getShort("strength"),
				result.getShort("intelligence"),
				result.getShort("chance"),
				result.getShort("agility")
		));
		
		player.setBag(new PlayerBag(player, repositories.items(), this, result.getLong("kamas")));
		
		player.setSpells(new SpellList(player, repositories.spells()));
		
		player.setWaypoints(WaypointList.populate(
				ctner.get(Waypoint.class),
				new WaypointList(player),
				result.getString("waypoints")
		));

        player.setStore(new PlayerStore(player, repositories.storedItems()));
		
		player.getOwner().getPlayers().add(player);
		
		return player;
	}

	@Override
	protected void unhandledException(Exception e) {
	}
	
}
