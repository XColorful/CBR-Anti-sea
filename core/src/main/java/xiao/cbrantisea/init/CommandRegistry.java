package xiao.cbrantisea.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import xiao.battleroyale.api.init.ICommandRegistry;
import xiao.cbrantisea.command.ServerCommand;

public class CommandRegistry implements ICommandRegistry {

    private static final CommandRegistry INSTANCE = new CommandRegistry();

    public static CommandRegistry get() {
        return INSTANCE;
    }

    private CommandRegistry() {}

    @Override
    public void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        ServerCommand.register(dispatcher);
    }

    @Override
    public void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
    }
}
