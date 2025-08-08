package org.shivas.core.core.contacts;

import java.util.Collection;
import java.util.Map;

import org.atomium.repository.EntityRepository;
import org.shivas.protocol.client.types.BaseContactType;
import org.shivas.core.core.events.EventDispatcher;
import org.shivas.core.core.events.EventDispatchers;
import org.shivas.core.core.events.EventListener;
import org.shivas.core.core.events.events.FriendConnectionEvent;
import org.shivas.core.database.models.Account;
import org.shivas.core.database.models.Contact;
import org.shivas.core.utils.Converters;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import static org.shivas.common.collections.CollectionQuery.from;

public class ContactList {

	private final Account owner;
	private final EntityRepository<Long, Contact> repository;
	
	private boolean notificationListener;
	
	private final Map<Integer, Contact> contacts = Maps.newHashMap();
	
	private final EventDispatcher event = EventDispatchers.create();
	
	public ContactList(Account owner, EntityRepository<Long, Contact> repository) {
		this.owner = owner;
		this.repository = repository;
	}

	public Account getOwner() {
		return owner;
	}
	
	public EventDispatcher getEvent() {
		return event;
	}

	public void notifyOwnerConnection() {
		event.publish(new FriendConnectionEvent(owner));
	}

	public boolean isNotificationListener() {
		return notificationListener;
	}

	public void setNotificationListener(boolean notificationListener) {
		this.notificationListener = notificationListener;
	}
	
	public void subscribeToFriends(EventListener listener) {
		if (!notificationListener) return;
		
		for (Contact contact : contacts.values()) {
			if (contact.getType() != Contact.Type.FRIEND) continue;
			
			contact.getTarget().getContacts().getEvent().subscribe(listener);
		}
	}
	
	public void unscribeFromFriends(EventListener listener) {
		for (Contact contact : contacts.values()) {
			if (contact.getType() != Contact.Type.FRIEND) continue;
			
			contact.getTarget().getContacts().getEvent().unsubscribe(listener);
		}
	}

	public void add(Contact contact) {
		if (contact.getOwner() != owner) {
			return;
		}
		
		contacts.put(contact.getTarget().getId(), contact);
	}
	
	public Contact add(Account target, Contact.Type type)
		throws EgocentricAddException, AlreadyAddedException
	{
		if (target == owner) {
			throw new EgocentricAddException();
		} else if (hasContact(target)) {
			throw new AlreadyAddedException();
		}
		
		Contact contact = new Contact();
		contact.setOwner(owner);
		contact.setTarget(target);
		contact.setType(type);
		
		repository.persistLater(contact);
		contacts.put(target.getId(), contact);
		
		return contact;
	}
	
	public boolean remove(Integer targetId) {
		return contacts.remove(targetId) != null;
	}
	
	public boolean delete(Account target) {
		Contact contact = contacts.remove(target.getId());
		if (contact != null) {
			repository.deleteLater(contact);
			return true;
		}
		return false;
	}

	public boolean hasContact(int targetId) {
		return contacts.containsKey(targetId);
	}
	
	public boolean hasContact(Account account) {
		return hasContact(account.getId());
	}
	
	public boolean hasContact(int targetId, Contact.Type type) {
		Contact contact = contacts.get(targetId);
		if (contact == null) return false;
		
		return contact.getType() == type;
	}
	
	public boolean hasContact(Account target, Contact.Type type) {
		return hasContact(target.getId(), type);
	}
	
	public Collection<BaseContactType> toBaseContactType(Predicate<Contact> predicate) {
		return from(contacts.values())
			  .filter(predicate)
			  .transform(Converters.CONTACT_TO_BASEFRIENDTYPE)
			  .computeSet();
	}
	
}
