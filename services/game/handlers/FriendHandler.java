package org.shivas.core.services.game.handlers;

import org.shivas.protocol.client.enums.ContactAddErrorEnum;
import org.shivas.protocol.client.formatters.BasicGameMessageFormatter;
import org.shivas.protocol.client.formatters.FriendGameMessageFormatter;
import org.shivas.core.core.contacts.AlreadyAddedException;
import org.shivas.core.core.contacts.ContactList;
import org.shivas.core.core.contacts.EgocentricAddException;
import org.shivas.core.database.models.Account;
import org.shivas.core.database.models.Contact;
import org.shivas.core.services.AbstractBaseHandler;
import org.shivas.core.services.game.GameClient;
import org.shivas.core.utils.Filters;

public class FriendHandler extends AbstractBaseHandler<GameClient> {

	public FriendHandler(GameClient client) {
		super(client);
	}

	@Override
	public void init() throws Exception {
		client.account().getContacts().notifyOwnerConnection();
		client.account().getContacts().subscribeToFriends(client.eventListener());
	}

	@Override
	public void onClosed() {
		client.account().getContacts().unscribeFromFriends(client.eventListener());
	}

	@Override
	public void handle(String message) throws Exception {
		switch (message.charAt(1)) {
		case 'A':
			parseAddMessage(message.charAt(2) == '%' || message.charAt(2) == '*' ?
					message.substring(3) :
					message.substring(2)
			);
			break;
			
		case 'D':
			parseDeleteMessage(message.charAt(2) == '%' || message.charAt(2) == '*' ?
					message.substring(3) :
					message.substring(2)
			);
			break;
			
		case 'L':
			parseListMessage();
			break;
			
		case 'O':
			parseEnableNotificationMessage(message.charAt(2) == '+');
			break;
		}
	}

	private void parseAddMessage(String name) {
		Account target = findAccountOrPlayer(name);
		if (target == null) {
			client.write(FriendGameMessageFormatter.addFriendErrorMessage(ContactAddErrorEnum.NOT_FOUND));
			return;
		}

		try {
			Contact contact = client.account().getContacts().add(target, Contact.Type.FRIEND);
			
			client.write(FriendGameMessageFormatter.addFriendMessage(contact.toBaseContactType()));
		} catch (EgocentricAddException e) {
			client.write(FriendGameMessageFormatter.addFriendErrorMessage(ContactAddErrorEnum.EGOCENTRIC));
		} catch (AlreadyAddedException e) {
			client.write(FriendGameMessageFormatter.addFriendErrorMessage(ContactAddErrorEnum.ALREADY_ADDED));
		}
	}

	private void parseDeleteMessage(String name) {
		Account target = findAccountOrPlayer(name);
		if (target != null && client.account().getContacts().delete(target)) {
			client.write(FriendGameMessageFormatter.deleteFriendMessage());
		} else {
			client.write(FriendGameMessageFormatter.deleteFriendErrorMessage());
		}
	}

	private void parseListMessage() {
		client.write(FriendGameMessageFormatter.
				friendListMessage(client.account().getContacts().toBaseContactType(Filters.FRIEND_CONTACT_FILTER)));
	}
	
	private void parseEnableNotificationMessage(boolean enable) {
		ContactList contacts = client.account().getContacts();
		contacts.setNotificationListener(enable);
		contacts.subscribeToFriends(client.eventListener());
		// does it need a database update ?
		
		client.write(BasicGameMessageFormatter.noOperationMessage());
	}

}
