package xiao.cbrantisea.common.game.zone.spatial.custom;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.event.custom.zone.DetermineZoneEvent;
import xiao.cbrantisea.CbrAntiSea;

public class _AntiSeaEventHandler implements ICustomEventHandler {

    private static class _AntiSeaEventHandlerHolder {
        private static final _AntiSeaEventHandler INSTANCE = new _AntiSeaEventHandler();
    }

    public static _AntiSeaEventHandler get() {
        return _AntiSeaEventHandlerHolder.INSTANCE;
    }

    private _AntiSeaEventHandler() {}

    @Override
    public String getEventHandlerName() {
        return String.format("%s:_AntiSeaEventHandler", CbrAntiSea.MOD_ID);
    }

    public static boolean register(ICustomEventRegister eventRegister) {
        return eventRegister.register(get(), DetermineZoneEvent.class);
    }

    public static boolean unregister(ICustomEventRegister eventRegister) {
        return eventRegister.unregister(get(), DetermineZoneEvent.class);
    }

    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent customEvent) {
        if (customEvent.getCustomEventClass() == DetermineZoneEvent.class) {
            AntiSeaManager.get().antiSea((DetermineZoneEvent) customEvent);
            unregister(BattleRoyale.getEventRegister());
        } else {
            onReceiveWrongEvent(customEventType);
        }
    }
}
