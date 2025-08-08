package org.shivas.core.utils;

import com.google.common.base.Predicate;
import org.shivas.core.core.GameActor;
import org.shivas.core.core.stores.PlayerStore;
import org.shivas.core.database.models.Contact;
import org.shivas.core.database.models.GameItem;

public final class Filters {
	private Filters() {}
	
	public static Predicate<GameItem> EQUIPED_ITEM_FILTER = new Predicate<GameItem>() {
		public boolean apply(GameItem input) {
			return input != null && input.getPosition().equipment();
		}
	};
	
	public static Predicate<Contact> FRIEND_CONTACT_FILTER = new Predicate<Contact>() {
		public boolean apply(Contact input) {
			return input != null && input.getType() == Contact.Type.FRIEND;
		}
	};
	
	public static Predicate<Contact> ENNEMY_CONTACT_FILTER = new Predicate<Contact>() {
		public boolean apply(Contact input) {
			return input != null && input.getType() == Contact.Type.ENNEMY;
		}
	};

    public static Predicate<GameActor> STOREACTOR_FILTER = new Predicate<GameActor>() {
        public boolean apply(GameActor input) {
            return input instanceof PlayerStore;
        }
    };
}
