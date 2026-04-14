package xiao.cbrantisea.event.custom;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.cbrantisea.CbrAntiSea;
import xiao.cbrantisea.common.game.zone.spatial.custom._AntiSeaEventRegister;

public class CustomEventHandler {

    public static void registerAll(ICustomEventRegister customEventRegister) {
        _AntiSeaEventRegister.registerInit(customEventRegister);

        if (BattleRoyale.getMcSide().isClientSide()) {
            registerClient(customEventRegister);
        }
    }

    public static void registerClient(ICustomEventRegister customEventRegister) {
    }

    private static void register(ICustomEventRegister customEventRegister, ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        if (customEventRegister.register(eventHandler, customEventType, priority, receiveCanceled)) {
            CbrAntiSea.LOGGER.debug("{} registered to {}", eventHandler.getEventHandlerName(), customEventType);
        } else {
            CbrAntiSea.LOGGER.debug("Failed to register {} to {}", eventHandler.getEventHandlerName(), customEventType);
        }
    }
}
