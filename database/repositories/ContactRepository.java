package org.shivas.core.database.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.atomium.EntityManager;
import org.atomium.repository.BaseEntityRepository;
import org.atomium.repository.impl.AbstractEntityRepository;
import org.atomium.util.pk.LongPrimaryKeyGenerator;
import org.atomium.util.query.Op;
import org.atomium.util.query.Query;
import org.atomium.util.query.QueryBuilder;
import org.shivas.core.database.models.Account;
import org.shivas.core.database.models.Contact;

@Singleton
public class ContactRepository extends AbstractEntityRepository<Long, Contact> {
	
	public static final String TABLE_NAME = "contacts";
	
	private final QueryBuilder persist, save, delete;
	private final Query load;
	
	private final BaseEntityRepository<Integer, Account> accounts;

	@Inject
	public ContactRepository(EntityManager em, BaseEntityRepository<Integer, Account> accounts) {
		super(em, new LongPrimaryKeyGenerator());
		
		this.persist = em.builder().insert(TABLE_NAME).values("id", "owner", "target", "type");
		this.save 	 = em.builder().update(TABLE_NAME).value("owner").value("target").value("type").where("id", Op.EQ);
		this.delete  = em.builder().delete(TABLE_NAME).where("id", Op.EQ);
		this.load 	 = em.builder().select(TABLE_NAME).toQuery();
		
		this.accounts = accounts;
	}

	@Override
	protected Query buildDeleteQuery(Contact entity) {
		Query query = delete.toQuery();
		query.setParameter("id", entity.getId());
		
		return query;
	}
	
	private Query bindValues(Query query, Contact entity) {
		query.setParameter("id", entity.getId());
		query.setParameter("owner", entity.getOwner().getId());
		query.setParameter("target", entity.getTarget().getId());
		query.setParameter("type", entity.getType().ordinal());
		
		return query;
	}

	@Override
	protected Query buildPersistQuery(Contact entity) {
		return bindValues(persist.toQuery(), entity);
	}

	@Override
	protected Query buildSaveQuery(Contact entity) {
		return bindValues(save.toQuery(), entity);
	}

	@Override
	protected Query buildLoadQuery() {
		return load;
	}

	@Override
	protected Contact load(ResultSet result) throws SQLException {
		Contact contact = new Contact();
		contact.setId(result.getLong("id"));
		contact.setOwner(accounts.find(result.getInt("owner")));
		contact.setTarget(accounts.find(result.getInt("target")));
		contact.setType(Contact.Type.valueOf(result.getInt("type")));
		
		contact.getOwner().getContacts().add(contact);
		
		return contact;
	}

	@Override
	protected void unhandledException(Exception e) {
	}

}
