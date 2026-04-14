package xiao.cbrantisea.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.cbrantisea.command.sub.AntiseaCommand;
import xiao.cbrantisea.command.sub.TempCommand;

import static xiao.battleroyale.command.CommandArg.MOD_ID;
import static xiao.battleroyale.command.CommandArg.MOD_NAME_SHORT;

public class ServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(get(MOD_ID));
        dispatcher.register(get(MOD_NAME_SHORT));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        root.then(AntiseaCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(TempCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        return root;
    }
}
