package xiao.cbrantisea.common.game.zone.spatial.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.custom.zone.DetermineZoneEvent;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.util.ChatUtils;
import xiao.cbrantisea.CbrAntiSea;
import xiao.cbrantisea.api.data.TempDataTag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

public class AntiSeaManager {

    private static class AntiSeaManagerHolder {
        private static final AntiSeaManager INSTANCE = new AntiSeaManager();
    }

    public static AntiSeaManager get() {
        return AntiSeaManagerHolder.INSTANCE;
    }

    private AntiSeaManager() {}

    public static void init(McSide mcSide) {
        // 专用服务端和客户端(单人游戏)都要读图
        get().loadMasks();
        Boolean enable = TempDataManager.get().getBool(TempDataTag.ANTISEA, TempDataTag.ENABLE_ANTISEA);
        if (enable != null) setEnabled(enable);
        Integer maxRetry = TempDataManager.get().getInt(TempDataTag.ANTISEA, TempDataTag.MAX_RETRY);
        if (maxRetry != null) setMaxRetryTime(maxRetry);
        Boolean sendChat = TempDataManager.get().getBool(TempDataTag.ANTISEA, TempDataTag.SEND_TO_CHAT);
        if (sendChat != null) setSendToChat(sendChat);
    }

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public static void setCenter1(Vec2 center) {
        get().map1Center = center;
    }
    public static void setCenter2(Vec2 center) {
        get().map2Center = center;
    }
    public static void setMaxRetryTime(int maxRetryTime) {
        MAX_RETRY_TIME = maxRetryTime;
    }
    public static void setSendToChat(boolean send) {
        SEND_TO_CHAT = send;
    }
    public static void reloadMasks() {
        get().loadMasks();
    }
    public static void calculateExpectationsToLog() {
        get().calculcateExpectations();
    }

    // 正方形边长
    private int mapSize = 8192; // 8000x8000
    // 地图中心
    private Vec2 map1Center = Vec2.ZERO;
    private Vec2 map2Center = new Vec2(0, -8192);

    private static boolean isEnabled = true;
    private static int MAX_RETRY_TIME = 20;
    private static boolean SEND_TO_CHAT = true;

    /**
     * 用 BitSet 极大节省内存 (256*256 bit = 8KB 每张图)
     * <li>12900KF L1 Cache: 48K+32K</li>
     * <li>12600KF L1 Cache: 48K+32K</li>
     * <li>9400F L1 Cache: 32K+32K</li>
     * <li>8272CL L1 Cache: 32K+32K</li>
     * <li>E5-2682v4 L1 Cache: 32K+32K</li>
     * <br>
     * 即使在较旧的 CPU (如 9400F / E5-2682v4) 上，8KB 也仅占 L1d Cache (32KB) 的 25%
     * 在 12代及以后的 P-Core 上，L1d 增大到 48KB，占比更低，可实现极致的判定性能
     */
    private static final int MASK_RES = 256;
    private BitSet map1Mask;
    private BitSet map2Mask;

    private void loadMasks() {
        this.map1Mask = readMask("/data/cbrantisea/masks/map1_256x256.png");
        this.map2Mask = readMask("/data/cbrantisea/masks/map2_256x256.png");
    }
    private void calculcateExpectations() {
        calculateExpectations("Map1", map1Mask);
        calculateExpectations("Map2", map2Mask);
    }
    private void calculateExpectations(String label, BitSet mask) {
        if (mask == null || mask.isEmpty()) return;

        int totalPixels = MASK_RES * MASK_RES;
        int seaPixels = mask.cardinality(); // BitSet中设为true的个数(水)

        // 1. 全图海占比
        double globalSeaRate = (double) seaPixels / totalPixels;

        // 2. 针对半径 3400 区域的精确统计
        // 3400 / (8192 / 256) = 106.25 像素半径
        double radius = 3400.0;
        double searchRadiusInPixels = radius / (mapSize / (double) MASK_RES);
        int centerIdx = MASK_RES / 2;
        int areaPixels = 0;
        int areaSeaPixels = 0;

        for (int y = 0; y < MASK_RES; y++) {
            for (int x = 0; x < MASK_RES; x++) {
                double distSq = Math.pow(x - centerIdx, 2) + Math.pow(y - centerIdx, 2);
                if (distSq <= Math.pow(searchRadiusInPixels, 2)) {
                    areaPixels++;
                    if (mask.get(y * MASK_RES + x)) {
                        areaSeaPixels++;
                    }
                }
            }
        }

        double areaSeaRate = (double) areaSeaPixels / areaPixels;

        // 3. 计算重试失败概率 (Geometric Distribution 几何分布的思想)
        // 假设单次刷海概率为 P，则连续 10 次刷海的概率为 P^10
        double failAfter10Retries = Math.pow(areaSeaRate, MAX_RETRY_TIME);

        CbrAntiSea.LOGGER.info("----- {} Statistical Analysis -----", label);
        CbrAntiSea.LOGGER.info("> Global Sea Rate: {}%", String.format("%.2f", globalSeaRate * 100));
        CbrAntiSea.LOGGER.info("> Target Area (R={}) Sea Rate: {}%", radius, String.format("%.2f", areaSeaRate * 100));
        CbrAntiSea.LOGGER.info("> Probability of failing after {} retries: {}%",
                MAX_RETRY_TIME, String.format("%.8f", failAfter10Retries * 100));
        CbrAntiSea.LOGGER.info("---------------------------------------------");
    }
    private BitSet readMask(String path) {
        BitSet bitSet = new BitSet(MASK_RES * MASK_RES);
        try (InputStream is = AntiSeaManager.class.getResourceAsStream(path)) {
            if (is == null) {
                CbrAntiSea.LOGGER.error("Mask file not found: {}", path);
                return bitSet;
            }
            BufferedImage image = ImageIO.read(is);
            for (int y = 0; y < MASK_RES; y++) {
                for (int x = 0; x < MASK_RES; x++) {
                    int color = image.getRGB(x, y);
                    // 提取蓝色通道 (Mask: 0xFF)
                    int b = color & 0xFF;
                    // 如果蓝色值大于一半 (128)，判定为水，记录到 BitSet
                    if (b > 128) {
                        bitSet.set(y * MASK_RES + x);
                    }
                }
            }
            CbrAntiSea.LOGGER.info("Successfully loaded mask: {}, size: {}x{}", path, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            CbrAntiSea.LOGGER.error("Failed to load Anti-Sea mask: {}", path, e);
        }
        return bitSet;
    }

    public void antiSea(DetermineZoneEvent event) {
        if (!isEnabled) return;

        // 开始计时
        long startTime = System.nanoTime();

        // 根据全局偏移确定地图
        Vec3 globalOffset = BattleRoyale.getGameManager().getGlobalCenterOffset();
        double distSq1 = (globalOffset.x - map1Center.x) * (globalOffset.x - map1Center.x)
                + (globalOffset.z - map1Center.y) * (globalOffset.z - map1Center.y);
        double distSq2 = (globalOffset.x - map2Center.x) * (globalOffset.x - map2Center.x)
                + (globalOffset.z - map2Center.y) * (globalOffset.z - map2Center.y);
        BitSet targetMask = null;
        Vec2 targetCenter = null;
        if (distSq1 < 1) {
            targetMask = map1Mask;
            targetCenter = map1Center;
        } else if (distSq2 < 1) {
            targetMask = map2Mask;
            targetCenter = map2Center;
        }

        if (targetMask == null) {
            return;
        }

        // 刷水掩码判定
        Vec3 startCenter;
        int totalRetries = 0;
        for (int i = 0; i < MAX_RETRY_TIME; i++) { // 兜底
            totalRetries = i;
            startCenter = event.getStartCenterPos();
            if (!checkIsWater(targetMask, targetCenter, startCenter)) {
                CbrAntiSea.LOGGER.debug("AntiSeaManager: Zone center {} not in water", startCenter);
                break; // 没刷水就结束
            }

            // 刷水且重试次数没超，触发重新计算
            if (event.getRecalculateCount() < MAX_RETRY_TIME) {
                CbrAntiSea.LOGGER.debug("AntiSeaManager: Zone center {} is water, retrying... (Count: {})", startCenter, event.getRecalculateCount());
                event.calculateShapeAgain();
            } else {
                CbrAntiSea.LOGGER.warn("AntiSeaManager: Failed to find land in {} attempts, canceling zone determination.", MAX_RETRY_TIME);
                event.setCanceled(true);
                break;
            }
        }

        // 结束计时
        long durationNano = System.nanoTime() - startTime;
        // 转换为毫秒并保留 4 位小数（即精确到 0.1 微秒）
        double durationMillis = durationNano / 1_000_000.0;
        CbrAntiSea.LOGGER.debug("AntiSeaManager: antiSea finished. Retries: {}, Time: {} ms ({} ns)",
                totalRetries, String.format("%.4f", durationMillis), durationNano);

        if (SEND_TO_CHAT && totalRetries > 0) {
            ChatUtils.sendComponentMessageToAllPlayers(event.getServerLevel(), Component.literal("AntiSea: Retried " + totalRetries + " times").withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * 将世界坐标映射到 256x256 的掩码中并检查是否为水
     */
    private boolean checkIsWater(BitSet mask, Vec2 center, @Nullable Vec3 pos) {
        // 生成失败就不重试了
        if (pos == null) return false;

        float halfSize = mapSize / 2f;
        // 计算点相对于地图左上角(Min X, Min Z)的偏移
        double relativeX = pos.x - (center.x - halfSize);
        double relativeZ = pos.z - (center.y - halfSize);

        // 转换为 0-255 的索引
        int xIdx = (int) ((relativeX / mapSize) * MASK_RES);
        int zIdx = (int) ((relativeZ / mapSize) * MASK_RES);

        // 边界检查：地图范围外，默认认为不是水
        if (xIdx < 0 || xIdx >= MASK_RES || zIdx < 0 || zIdx >= MASK_RES) {
            return false;
        }

        return mask.get(zIdx * MASK_RES + xIdx);
    }
}
