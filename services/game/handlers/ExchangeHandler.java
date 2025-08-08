package org.shivas.core.services.game.handlers;

import org.shivas.protocol.client.enums.OrientationEnum;
import org.shivas.protocol.client.enums.TradeErrorEnum;
import org.shivas.protocol.client.enums.TradeTypeEnum;
import org.shivas.protocol.client.formatters.InfoGameMessageFormatter;
import org.shivas.protocol.client.formatters.TradeGameMessageFormatter;
import org.shivas.core.core.GameActor;
import org.shivas.core.core.exchanges.PlayerExchange;
import org.shivas.core.core.exchanges.Purchasable;
import org.shivas.core.core.exchanges.Salable;
import org.shivas.core.core.interactions.InteractionException;
import org.shivas.core.core.interactions.InteractionType;
import org.shivas.core.core.interactions.PlayerExchangeInvitation;
import org.shivas.core.core.maps.GameMap;
import org.shivas.core.core.npcs.GameNpc;
import org.shivas.core.core.npcs.NpcExchangeInteraction;
import org.shivas.core.core.stores.PlayerStore;
import org.shivas.core.core.stores.StoreInteraction;
import org.shivas.core.core.stores.StoreManagementInteraction;
import org.shivas.core.database.models.GameItem;
import org.shivas.core.database.models.Player;
import org.shivas.core.database.models.StoredItem;
import org.shivas.core.services.AbstractBaseHandler;
import org.shivas.core.services.CriticalException;
import org.shivas.core.services.game.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeHandler extends AbstractBaseHandler<GameClient> {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeHandler.class);

	public ExchangeHandler(GameClient client) {
		super(client);
	}

	@Override
	public void init() throws Exception {
        if (client.player().getStore().isActive()) {
            client.write(InfoGameMessageFormatter.earnedKamasMessage(client.player().getStore().getEarnedKamas()));

            client.player().getStore().close();
        }
    }

	@Override
	public void onClosed() { }

	@Override
	public void handle(String message) throws Exception {
		String[] args;
		switch (message.charAt(1)) {
		case 'A':
			parseAcceptMessage();
			break;

        case 'B':
            args = message.substring(2).split("\\|");
            parseBuyMessage(Long.parseLong(args[0]), Math.max(Integer.parseInt(args[1]), 0));
            break;

        case 'K':
            parseReadyMessage();
            break;
			
		case 'M':
			switch (message.charAt(2)) {
			case 'G':
				parseSetKamasMessage(Math.max(Long.parseLong(message.substring(3)), 0));
				break;
				
			case 'O':
                args = message.substring(4).split("\\|");
                long itemId = Long.parseLong(args[0]);
                int quantity = Math.max(Integer.parseInt(args[1]), 0);

                switch (message.charAt(3)) {
                case '+':
                    switch (client.interactions().current().getInteractionType()) {
                    case PLAYER_EXCHANGE:
                        parsePlayerExchangeAddItemMessage(itemId, quantity);
                        break;
                    case STORE_MANAGEMENT:
                        if (quantity > 0) parseStoreManagementAddMessage(itemId, quantity, Long.parseLong(args[2]));
                        else parseStoreManagementUpdateMessage(itemId, Long.parseLong(args[2]));
                        break;
                    }
                    break;
                case '-':
                    switch (client.interactions().current().getInteractionType()) {
                    case PLAYER_EXCHANGE:
                        parsePlayerExchangeRemoveItemMessage(itemId, quantity);
                        break;
                    case STORE_MANAGEMENT:
                        parseStoreManagementRemoveMessage(itemId);
                        break;
                    }
                    break;
                }
				break;
			}
			break;

        case 'q':
            parseEnableStoreMessage();
            break;
		
		case 'R':
			args = message.substring(2).split("\\|");
			parseRequestMessage(
					TradeTypeEnum.valueOf(Integer.parseInt(args[0])),
					args.length > 1 && args[1].length() > 0 ? Integer.parseInt(args[1]) : null,
					args.length > 2 && args[2].length() > 0 ? Short.parseShort(args[2]) : null
			);
			break;

        case 'S':
            args = message.substring(2).split("\\|");
            parseSellMessage(Long.parseLong(args[0]), Integer.parseInt(args[1]));
            break;
			
		case 'V':
			parseQuitMessage();
			break;
		}
	}

    private void parseRequestMessage(TradeTypeEnum type, Integer targetId, Short cellId) throws Exception {
		switch (type) {
		case PLAYER:
			assertTrue(targetId != null, "no id has been given");
			parsePlayerInvitationMessage(targetId);
			break;

        case STORE_MANAGEMENT:
            parseStoreManagementMessage();
            break;

        case STORE:
            assertTrue(targetId != null, "no id has been given");
            assertTrue(cellId != null, "no cellid has been given");
            parseViewStoreMessage(targetId);
            break;

        case NPC:
            parseNpcExchangeMessage(targetId);
            break;
			
		default:
			log.warn("unknown trade type {}", type.name());
			break;
		}
	}

    private void parsePlayerInvitationMessage(int targetId) throws Exception {
		Player target = client.service().repositories().players().find(targetId);
		assertTrue(target != null, "unknown player {}", targetId);
		
		if (target.getClient() == null) {
			client.write(TradeGameMessageFormatter.tradeRequestErrorMessage(TradeErrorEnum.UnknownOrOffline));
		} else if (target.getClient().isBusy()) {
			client.write(TradeGameMessageFormatter.tradeRequestErrorMessage(TradeErrorEnum.Busy));
		} else {
			client.interactions().push(new PlayerExchangeInvitation(client, target.getClient())).begin();
		}
	}

	private void parseAcceptMessage() throws Exception {
        client.interactions().remove(PlayerExchangeInvitation.class).accept();
	}

	private void parseQuitMessage() throws Exception {
        client.interactions().removeIf(
                InteractionType.PLAYER_EXCHANGE_INVITATION,
                InteractionType.PLAYER_EXCHANGE,
                InteractionType.STORE_MANAGEMENT,
                InteractionType.STORE,
                InteractionType.NPC_EXCHANGE
        ).cancel();
	}

	private void parseSetKamasMessage(long kamas) throws Exception {
		client.interactions().current(PlayerExchange.class).setKamas(client, kamas);
	}

    private void parsePlayerExchangeAddItemMessage(long itemId, int quantity) throws CriticalException, InteractionException {
        GameItem item = client.player().getBag().get(itemId);
        assertFalse(item == null, "unknown item %d", itemId);

        client.interactions().current(PlayerExchange.class).addItem(client, item, quantity);
    }

    private void parsePlayerExchangeRemoveItemMessage(long itemId, int quantity) throws CriticalException, InteractionException {
        GameItem item = client.player().getBag().get(itemId);
        assertFalse(item == null, "unknown item %d", itemId);

        client.interactions().current(PlayerExchange.class).removeItem(client, item, quantity);
    }

    private void parseReadyMessage() throws InteractionException {
        client.interactions().current(PlayerExchange.class).setReady(client);
    }

    private void parseStoreManagementMessage() throws InteractionException {
        client.interactions().push(new StoreManagementInteraction(client)).begin();
    }

    private void parseStoreManagementAddMessage(long itemId, int quantity, long price) throws CriticalException, InteractionException {
        GameItem item = client.player().getBag().get(itemId);
        assertFalse(item == null, "unknown item %d", itemId);

        client.interactions().current(StoreManagementInteraction.class).add(item, quantity, price);
    }

    private void parseStoreManagementUpdateMessage(long itemId, long price) throws CriticalException, InteractionException {
        StoredItem stored = client.player().getStore().get(itemId);
        assertFalse(stored == null, "unknown item %d", itemId);

        client.interactions().current(StoreManagementInteraction.class).update(stored, price);
    }

    private void parseStoreManagementRemoveMessage(long itemId) throws CriticalException, InteractionException {
        StoredItem stored = client.player().getStore().get(itemId);
        assertFalse(stored == null, "unknown item %d", itemId);

        client.interactions().current(StoreManagementInteraction.class).remove(stored);
    }

    private void parseEnableStoreMessage() throws InteractionException, CriticalException {
        assertFalse(client.isBusy(), "you are busy");

        GameMap map = client.player().getLocation().getMap();

        if (client.player().getStore().isEmpty()) {
            client.write(InfoGameMessageFormatter.emptyStoreMessage());
        } else if (!map.hasAvailableStorePlaces()) {
            client.write(InfoGameMessageFormatter.notEnoughStorePlacesMessage(GameMap.MAX_STORE_PER_MAP));
        } else {
            PlayerStore store = client.player().getStore();

            client.account().setCurrentStore(store);
            client.player().getLocation().setOrientation(OrientationEnum.SOUTH_WEST);
            map.enter(store);

            client.kick();
        }
    }

    private void parseViewStoreMessage(int targetId) throws CriticalException, InteractionException {
        GameMap map = client.player().getLocation().getMap();

        GameActor actor = map.get(targetId);
        assertTrue(actor != null, "there is no store %d on this map %d", targetId, map.getId());
        assertTrue(actor instanceof PlayerStore, "actor %d is not a store", targetId);

        PlayerStore store = (PlayerStore) actor;

        client.interactions().push(new StoreInteraction(client, store)).begin();
    }

    private void parseBuyMessage(long itemId, int quantity) throws InteractionException {
        client.interactions().current(Purchasable.class).purchase(itemId, quantity);
    }

    private void parseSellMessage(long itemId, int quantity) throws InteractionException {
        client.interactions().current(Salable.class).sell(itemId, quantity);
    }

    private void parseNpcExchangeMessage(int npcId) throws CriticalException, InteractionException {
        GameActor actor = client.player().getLocation().getMap().get(npcId);
        assertTrue(actor != null, "unknown npc %d", npcId);
        assertTrue(actor instanceof GameNpc, "actor %d isn't a npc", npcId);

        client.interactions().push(new NpcExchangeInteraction(client, (GameNpc) actor)).begin();
    }

}
