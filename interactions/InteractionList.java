package org.shivas.core.core.interactions;

import com.google.common.collect.Lists;
import org.shivas.core.core.events.EventDispatcher;
import org.shivas.core.core.events.EventDispatchers;
import org.shivas.core.core.events.EventListener;
import org.shivas.core.core.events.events.NewInteractionEvent;
import org.shivas.core.services.game.GameClient;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

public class InteractionList {
	
	private GameClient client;
	private List<Interaction> interactions = Lists.newArrayList();
	
	protected final EventDispatcher event = EventDispatchers.create();

	public InteractionList(GameClient client) {
		this.client = client;
	}

	public void subscribe(EventListener listener) {
		event.subscribe(listener);
	}

	public void unsubscribe(EventListener listener) {
		event.unsubscribe(listener);
	}
	
	public int size() {
		return interactions.size();
	}
	
	private GameClient getOther(LinkedInteraction interaction) {
		if (interaction.getSource() == client) {
			return interaction.getTarget();
		}
		if (interaction.getTarget() == client) {
			return interaction.getSource();
		}
		
		throw new InvalidParameterException("owner is neither source nor target");
	}
	
	private void onAdded(Interaction interaction, boolean checkLink) {
		event.publish(new NewInteractionEvent(client, interaction));
		
		if (checkLink && interaction instanceof LinkedInteraction) {
			InteractionList other = getOther((LinkedInteraction) interaction).interactions();
			
			other.interactions.add(interaction);
			other.onAdded(interaction, false);
		}
	}
	
	private void onRemoved(Interaction interaction) {
		if (interaction instanceof LinkedInteraction) {
			InteractionList other = getOther((LinkedInteraction) interaction).interactions();
			
			other.interactions.remove(interaction);
		}
	}
	
	public <T extends Interaction> T push(final T action) {
		interactions.add(action);
		onAdded(action, true);
		
		return action;
	}
	
	public <T extends Interaction> T front(final T action) {
		interactions.add(0, action);
		onAdded(action, true);
		
		return action;
	}
	
	public Interaction current() {
		return interactions.get(interactions.size() - 1);
	}
	
	public <T extends Interaction> T current(Class<T> clazz) {
		return clazz.cast(current());
	}
	
	public Interaction remove() {
        if (interactions.isEmpty()) return null;
		Interaction interaction = interactions.remove(interactions.size() - 1);
		onRemoved(interaction);
		
		return interaction;
	}
	
	public <T extends Interaction> T remove(Class<T> clazz) {
		return clazz.cast(remove());
	}

    public Interaction removeIf(InteractionType... types) {
        Iterator<Interaction> it = interactions.iterator();
        while (it.hasNext()) {
            Interaction interaction = it.next();
            for (InteractionType type : types) {
                if (interaction.getInteractionType() == type) {
                    it.remove();
                    onRemoved(interaction);

                    return interaction;
                }
            }
        }

        return null;
    }

    public <T extends Interaction> T removeIf(Class<T> clazz, InteractionType... types) {
        Interaction interaction = removeIf(types);
        return interaction != null ? clazz.cast(interaction) : null;
    }
	
}
