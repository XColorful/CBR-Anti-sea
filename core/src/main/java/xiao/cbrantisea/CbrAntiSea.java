package xiao.cbrantisea;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.cbrantisea.common.game.zone.spatial.custom.AntiSeaManager;
import xiao.cbrantisea.event.custom.CustomEventHandler;

import java.util.Random;

public class CbrAntiSea {
    public static final String MOD_ID = "cbrantisea";
    public static final String MOD_NAME_SHORT = "cbrsea";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random COMMON_RANDOM = new Random();

    protected static boolean initialized;
    protected static McSide mcSide = McSide.CLIENT;

    public static void init(McSide mcSide) {
        if (initialized) return;

        CbrAntiSea.mcSide = mcSide;

        CustomEventHandler.registerAll(BattleRoyale.getEventRegister());
        AntiSeaManager.init(mcSide);

        initialized = true;
    }

    public static McSide getMcSide() {
        return mcSide;
    }
}
