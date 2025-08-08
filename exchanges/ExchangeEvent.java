package org.shivas.core.core.exchanges;

import org.shivas.core.core.events.Event;
import org.shivas.core.core.events.EventType;
import org.shivas.core.services.game.GameClient;

/**
 * Created with IntelliJ IDEA.
 * User: Blackrush
 * Date: 19/09/12
 * Time: 15:19
 */
public abstract class ExchangeEvent implements Event {

    private final PlayerExchange exchange;
    private final GameClient source;

    public ExchangeEvent(PlayerExchange exchange, GameClient source) {
        this.exchange = exchange;
        this.source = source;
    }

    public PlayerExchange getExchange() {
        return exchange;
    }

    public abstract ExchangeEventType getExchangeEventType();

    @Override
    public EventType type() {
        return EventType.EXCHANGE;
    }

    public GameClient getSource() {
        return source;
    }
}
