package net.stargateonline.sgo_playerdisplay.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorRefresh implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        CommandRefresh.updateAllTab();
        src.sendMessage(Text.of(TextColors.YELLOW, "All players' display elements have been refreshed"));
        return CommandResult.success();
    }
}
