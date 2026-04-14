package xiao.cbrantisea.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.cbrantisea.api.data.TempDataTag;
import xiao.cbrantisea.common.game.zone.spatial.custom.AntiSeaManager;

import static xiao.cbrantisea.command.CommandArg.*;

public class TempCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEMP)
                .then(Commands.literal(ANTISEA)
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(TempCommand::turnAntiSea)
                        )
                        .then(Commands.literal(ANTISEA_CENTER1)
                                .then(Commands.argument(POS, Vec2Argument.vec2())
                                        .executes(TempCommand::setCenter1)
                                )
                        )
                        .then(Commands.literal(ANTISEA_CENTER2)
                                .then(Commands.argument(POS, Vec2Argument.vec2())
                                        .executes(TempCommand::setCenter2)
                                )
                        )
                        .then(Commands.literal(ANTISEA_MAX_RETRY)
                                .then(Commands.argument(INT, IntegerArgumentType.integer(1))
                                        .executes(TempCommand::setMaxRetry)
                                )
                        )
                        .then(Commands.literal(ANTISEA_SEND_TO_CHAT)
                                .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                        .executes(TempCommand::setSendToChat)
                                )
                        )
                );
    }

    private static int turnAntiSea(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        AntiSeaManager.setEnabled(turn);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(TempDataTag.ANTISEA, TempDataTag.ENABLE_ANTISEA, turn);
        tempDataManager.saveTempData();
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Turn antisea to " + turn), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setCenter1(CommandContext<CommandSourceStack> context) {
        Vec2 center = Vec2Argument.getVec2(context, POS);
        AntiSeaManager.setCenter1(center);
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Temporary set center 1 to " + center.x + ", " + center.y), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int setCenter2(CommandContext<CommandSourceStack> context) {
        Vec2 center = Vec2Argument.getVec2(context, POS);
        AntiSeaManager.setCenter2(center);
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Temporary set center 2 to " + center.x + ", " + center.y), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMaxRetry(CommandContext<CommandSourceStack> context) {
        int maxRetry = IntegerArgumentType.getInteger(context, INT);
        AntiSeaManager.setMaxRetryTime(maxRetry);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeInt(TempDataTag.ANTISEA, TempDataTag.MAX_RETRY, maxRetry);
        tempDataManager.saveTempData();
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Set maxRetry to " + maxRetry), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setSendToChat(CommandContext<CommandSourceStack> context) {
        boolean turn = BoolArgumentType.getBool(context, BOOL);
        AntiSeaManager.setSendToChat(turn);
        TempDataManager tempDataManager = TempDataManager.get();
        tempDataManager.writeBool(TempDataTag.ANTISEA, TempDataTag.SEND_TO_CHAT, turn);
        tempDataManager.saveTempData();
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Turn sendToChat to " + turn), false);
        return Command.SINGLE_SUCCESS;
    }
}
