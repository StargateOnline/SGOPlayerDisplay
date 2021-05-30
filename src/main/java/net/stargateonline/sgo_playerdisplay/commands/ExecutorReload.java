package net.stargateonline.sgo_playerdisplay.commands;

import net.stargateonline.sgo_playerdisplay.PlayerDisplayTokens;
import net.stargateonline.sgo_playerdisplay.config.Config;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorReload implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Config.reloadConfig();
        CommandRefresh.updateAllTab();
        src.sendMessage(Text.of(TextColors.YELLOW, PlayerDisplayTokens.PLUGIN_NAME + " successfully reloaded"));
        return CommandResult.success();
    }
}
