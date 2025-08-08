package org.shivas.core.core.stores;

import org.shivas.core.core.events.Event;
import org.shivas.core.core.events.EventType;

/**
 * Created with IntelliJ IDEA.
 * User: Blackrush
 * Date: 22/09/12
 * Time: 19:26
 */
public abstract class StoreEvent implements Event {
    public enum Type {
        CLOSE,
        REFRESH
    }

    public static final StoreEvent CLOSE = new StoreEvent() {
        public Type getStoreEventType() {
            return Type.CLOSE;
        }
    };

    public static final StoreEvent REFRESH = new StoreEvent() {
        public Type getStoreEventType() {
            return Type.REFRESH;
        }
    };

    @Override
    public EventType type() {
        return EventType.STORE;
    }

    public abstract Type getStoreEventType();
}
