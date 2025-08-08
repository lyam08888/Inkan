package org.shivas.core.core.events;

public final class EventDispatchers {
	private EventDispatchers(){}
	
	public static EventDispatcher create(){
		return new ThreadedEventDispatcher();
	}
}
