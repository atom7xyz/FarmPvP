package xyz.sorridi.farmpvp.modules.player.commands;

import lombok.NonNull;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.command.CommandSender;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.data.Array;
import xyz.sorridi.stone.utils.description.Versioning;

public class FarmPvPCommand implements FunctionalCommandHandler<CommandSender>
{
    private static final String[] VERSION_MESSAGE = Array.of(
            "&8&m-----------------------",
            "&8▎ &cv{ver} &7&o({hash})",
            "&8▎ &7autore: &c{authors}",
            "&8▎ &7sito: &c{site}",
            "&8&m-----------------------"
    );

    private static final String[] VERSION_TO_REPLACE = Array.of(
            "{ver}",
            "{hash}",
            "{authors}",
            "{site}"
    );

    public FarmPvPCommand()
    {
        Versioning versioning = new Versioning(FarmPvP.instance);

        Replace.of(
                VERSION_MESSAGE,
                VERSION_TO_REPLACE,
                versioning.getVersion(),
                versioning.getHash(),
                versioning.getAuthors(),
                versioning.getSite()
        );
    }

    @Override
    public void handle(@NonNull CommandContext<CommandSender> c)
    {
        c.reply(VERSION_MESSAGE);
    }
}