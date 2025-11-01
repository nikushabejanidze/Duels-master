package com.meteordevelopments.duels.command.commands.duels;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.Permissions;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.command.commands.duels.subcommands.*;
import org.bukkit.command.CommandSender;

public class DuelsCommand extends BaseCommand {

    public DuelsCommand(final DuelsPlugin plugin) {
        // Keep existing typed permission (old code), new code used literal "duels.admin" which maps to this constant
        super(plugin, "duels", Permissions.ADMIN, false);
        child(
                new HelpCommand(plugin),
                new SavekitCommand(plugin),
                new DeletekitCommand(plugin),
                new LoadkitCommand(plugin),
                new SetitemCommand(plugin),
                new OptionsCommand(plugin),
                new BindCommand(plugin),
                new CreateCommand(plugin),
                new DeleteCommand(plugin),
                new SetCommand(plugin),
                new ToggleCommand(plugin),
                new TeleportCommand(plugin),
                new CreatequeueCommand(plugin),
                new DeletequeueCommand(plugin),
                new AddsignCommand(plugin),
                new DeletesignCommand(plugin),
                new SetlobbyCommand(plugin),
                // Old code had an extra command that the new code didn't include; keep it so you don't lose functionality
                new SetKitLobbyCommand(plugin),
                new LobbyCommand(plugin),
                new InfoCommand(plugin),
                new ListCommand(plugin),
                new EditCommand(plugin),
                new SetratingCommand(plugin),
                new ResetCommand(plugin),
                new ResetratingCommand(plugin),
                new PlaysoundCommand(plugin),
                new ReloadCommand(plugin),
                new DisableCommand(plugin),
                new EnableCommand(plugin)
        );
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        // Keep the safer placeholder-blocking behavior from the old code
        if (containsPlaceholder(args)) {
            lang.sendMessage(sender, "ERROR.command.invalid-argument", "arg", String.join(" ", args));
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.usage", "command", label);
    }

    private boolean containsPlaceholder(String[] args) {
        for (String arg : args) {
            if (arg.contains("%") || arg.contains("<") || arg.contains(">")) {
                return true;
            }
        }
        return false;
    }
}
