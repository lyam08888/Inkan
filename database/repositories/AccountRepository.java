package org.shivas.core.database.repositories;

import org.atomium.EntityManager;
import org.atomium.exception.LoadingException;
import org.atomium.repository.impl.AbstractRefreshableEntityRepository;
import org.atomium.util.query.Op;
import org.atomium.util.query.Query;
import org.atomium.util.query.UpdateQueryBuilder;
import org.joda.time.DateTime;
import org.shivas.common.crypto.Cipher;
import org.shivas.common.crypto.Sha1SaltCipher;
import org.shivas.core.config.Config;
import org.shivas.core.core.channels.ChannelList;
import org.shivas.core.core.contacts.ContactList;
import org.shivas.core.core.gifts.GiftList;
import org.shivas.core.core.players.PlayerList;
import org.shivas.core.database.RepositoryContainer;
import org.shivas.core.database.models.Account;
import org.shivas.protocol.client.enums.ChannelEnum;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class AccountRepository extends AbstractRefreshableEntityRepository<Integer, Account> {
	
	public static final String TABLE_NAME = "accounts";
	
	private final UpdateQueryBuilder saveQuery;
	private final Query loadQuery, refreshQuery, setRefreshedQuery;
	
	private final RepositoryContainer repositories;
    private final Config config;

	@Inject
	public AccountRepository(EntityManager em, Config config, RepositoryContainer repositories) {
		super(em, config.databaseRefreshRate());
        this.config = config;

        this.repositories = repositories;
		
		this.saveQuery = em.builder()
				.update(TABLE_NAME)
				.value("rights").value("banned").value("muted")
				.value("points").value("connected").value("channels")
				.value("last_connection").value("last_address").value("nb_connections")
				.where("id", Op.EQ);
		this.setRefreshedQuery = em.builder()
				.update(TABLE_NAME)
				.value("refreshed", false)
				.toQuery();
		this.loadQuery = em.builder()
				.select(TABLE_NAME)
				.toQuery();
		this.refreshQuery = em.builder()
				.select(TABLE_NAME)
				.where("refreshed", Op.EQ, true)
				.toQuery();
	}

	@Override
	protected Query buildLoadQuery() {
		return loadQuery;
	}

	@Override
	protected Query getRefreshQuery() {
		return refreshQuery;
	}

	@Override
	protected Query getSetRefreshedQuery() {
		return setRefreshedQuery;
	}

	@Override
	protected Query buildSaveQuery(Account entity) {
		return saveQuery.toQuery()
			.setParameter("id", entity.getId())
			.setParameter("rights", entity.hasRights())
			.setParameter("banned", entity.isBanned())
			.setParameter("muted", entity.isMuted())
			.setParameter("points", entity.getPoints())
			.setParameter("connected", entity.isConnected())
			.setParameter("channels", entity.getChannels())
			.setParameter("last_connection", entity.getLastConnection())
			.setParameter("last_address", entity.getLastAddress())
			.setParameter("nb_connections", entity.getNbConnections())
			.setParameter("friend_notification_listener", entity.getContacts().isNotificationListener());
	}
	
	@Override
	public int load() throws LoadingException {
		em.execute(em.builder().update(TABLE_NAME).value("connected", false).toQuery());
		
		return super.load();
	}

	@Override
	protected Integer getPrimaryKey(ResultSet rset) throws SQLException {
		return rset.getInt("id");
	}

	@Override
	protected Account load(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		
		Account account = new Account(
				id,
				0,
				result.getString("name"),
				result.getString("password"),
				result.getString("salt"),
				result.getString("nickname"),
				result.getString("question"),
				result.getString("answer"),
				result.getBoolean("rights"),
				result.getBoolean("banned"),
				result.getBoolean("muted"),
				result.getInt("community"),
				result.getInt("points"),
				parseDateTime(result, "subscription_end"),
				result.getBoolean("connected"),
				ChannelList.parseChannelList(result.getString("channels")),
				parseDateTime(result, "last_connection"),
				result.getString("last_address"),
				result.getInt("nb_connections")
		);
		
		account.setPlayers(new PlayerList(account, repositories.players(), config));
		account.setContacts(new ContactList(account, repositories.contacts()));
		account.getContacts().setNotificationListener(result.getBoolean("friend_notification_listener"));
		account.setGifts(new GiftList(account, repositories.gifts()));
		
		if (account.hasRights() && !account.getChannels().contains(ChannelEnum.Admin)) {
			account.getChannels().add(ChannelEnum.Admin);
		}
		
		return account;
	}

	private DateTime parseDateTime(ResultSet result, String columnName) throws SQLException {
		String string = result.getString(columnName);
		if (string == null) {
			return null;
		}
		return em.builder().dateTimeFormatter().parseDateTime(string);
	}

	@Override
	protected void refresh(Account entity, ResultSet rset) throws SQLException {		
		entity.setPassword(rset.getString("password"));
		entity.setSalt(rset.getString("salt"));
		entity.setBanned(rset.getBoolean("banned"));
		entity.setMuted(rset.getBoolean("muted"));
		entity.setPoints(rset.getInt("points"));
		entity.setSubscriptionEnd(parseDateTime(rset, "subscription_end"));
	}

	public Cipher passwordCipher(Account account) {
		return new Sha1SaltCipher(account.getSalt());
	}
	
	public Account find(String name) {
		return super.find(account -> account.getName().equals(name));
	}
	
	public Account findByNickname(final String nickname) {
		return super.find(account -> account.getNickname().equals(nickname));
	}

	@Override
	protected void unhandledException(Exception e) {
	}
	
}
