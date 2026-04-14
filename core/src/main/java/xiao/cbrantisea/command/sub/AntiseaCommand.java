package xiao.cbrantisea.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.cbrantisea.common.game.zone.spatial.custom.AntiSeaManager;

import static xiao.cbrantisea.command.CommandArg.*;

public class AntiseaCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(ANTISEA)
                .then(Commands.literal(ANTISEA_RELOAD_MASKS)
                        .executes(AntiseaCommand::reloadMasks)
                )
                .then(Commands.literal(ANTISEA_CALCULATE_EXPECTATIONS)
                        .executes(AntiseaCommand::calculateExpectations)
                );
    }

    private static int reloadMasks(CommandContext<CommandSourceStack> context) {
        AntiSeaManager.reloadMasks();
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Reloaded masks"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int calculateExpectations(CommandContext<CommandSourceStack> context) {
        AntiSeaManager.calculateExpectationsToLog();
        context.getSource().sendSuccess(() -> Component.literal("AntiSea: Calculated expctations"), false);
        return Command.SINGLE_SUCCESS;
    }
}
