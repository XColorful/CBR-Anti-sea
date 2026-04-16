package xiao.cbrantisea.compat.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.api.common.McSide;
import xiao.cbrantisea.CbrAntiSea;

@Mod(CbrAntiSea.MOD_ID)
public class CbrAntiSeaForge {

    public CbrAntiSeaForge() {
        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        CbrAntiSea.init(mcSide);
    }
}
