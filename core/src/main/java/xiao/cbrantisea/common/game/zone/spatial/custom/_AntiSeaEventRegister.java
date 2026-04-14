package xiao.cbrantisea.common.game.zone.spatial.custom;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.cbrantisea.CbrAntiSea;

public class _AntiSeaEventRegister implements ICustomEventHandler {

    private static class _AntiSeaEventRegisterHolder {
        private static final _AntiSeaEventRegister INSTANCE = new _AntiSeaEventRegister();
    }

    public static _AntiSeaEventRegister get() {
        return _AntiSeaEventRegisterHolder.INSTANCE;
    }

    private _AntiSeaEventRegister() {}

    @Override
    public String getEventHandlerName() {
        return String.format("%s:_AntiSeaEventRegister", CbrAntiSea.MOD_ID);
    }

    // 注册循环入口
    public static boolean registerInit(ICustomEventRegister eventRegister) {
        return eventRegister.register(get(), CustomEventType.GAME_START_FINISH_EVENT);
    }
    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent customEvent) {
        switch (customEventType) {
            case GAME_START_FINISH_EVENT -> {
                ICustomEventRegister eventRegister = BattleRoyale.getEventRegister();
                eventRegister.unregister(this, CustomEventType.GAME_START_FINISH_EVENT);
                eventRegister.register(this, CustomEventType.GAME_STOP_EVENT);

                _AntiSeaEventHandler.register(eventRegister);
            }
            case GAME_STOP_EVENT -> {
                ICustomEventRegister eventRegister = BattleRoyale.getEventRegister();
                eventRegister.unregister(this, CustomEventType.GAME_STOP_EVENT);
                eventRegister.register(this, CustomEventType.GAME_START_FINISH_EVENT);
            }
            default -> {
                onReceiveWrongEvent(customEventType);
            }
        }
    }
}
