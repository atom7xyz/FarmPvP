package xyz.sorridi.farmpvp.modules.player.economy.commands.tabs;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalTabHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.List;

public class LevelsTab implements FunctionalTabHandler<CommandSender>, IPlaceHolders
{
    private final PlayerModule.Data.Levels data = Serve.of(PlayerModule.Data.Levels.class);

    @Nullable
    @Override
    public List<String> handle(CommandContext<CommandSender> c)
    {
        return switch (c.args().size())
        {
            case 1 -> data.getTabArgs1();
            case 2 -> null;
            case 3 ->
            {
                val _arg0 = c.arg(0).parse(String.class);
                String arg0;

                if (_arg0.isPresent())
                {
                    arg0 = _arg0.get();

                    if (arg0.equalsIgnoreCase("add") || arg0.equalsIgnoreCase("remove"))
                    {
                        yield data.getTabArgs3();
                    }
                }

                yield EMPTY_LIST;
            }
            default -> EMPTY_LIST;
        };
    }

}
