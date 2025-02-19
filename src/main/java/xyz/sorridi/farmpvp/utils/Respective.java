package xyz.sorridi.farmpvp.utils;

import me.lucko.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.stone.utils.data.Array;

import java.util.List;

public class Respective
{

    public static void reply(FPlayer player, CommandContext<CommandSender> c, List<String> message)
    {
        if (player != null)
        {
            player.reply(message);
        }
        else
        {
            c.reply(Array.of(message, String[].class));
        }
    }

    public static void reply(FPlayer player, CommandContext<CommandSender> c, String message)
    {
        if (player != null)
        {
            player.reply(message);
        }
        else
        {
            c.reply(message);
        }
    }

}
