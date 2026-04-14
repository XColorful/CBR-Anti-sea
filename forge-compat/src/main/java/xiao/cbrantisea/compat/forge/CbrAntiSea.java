package xiao.cbrantisea.compat.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.api.common.McSide;

@Mod(xiao.cbrantisea.CbrAntiSea.MOD_ID)
public class CbrAntiSea {

    public CbrAntiSea() {
        Dist dist = FMLLoader.getDist();
        McSide mcSide = dist.isClient() ? McSide.CLIENT : McSide.DEDICATED_SERVER;

        xiao.cbrantisea.CbrAntiSea.init(mcSide);
    }
}
