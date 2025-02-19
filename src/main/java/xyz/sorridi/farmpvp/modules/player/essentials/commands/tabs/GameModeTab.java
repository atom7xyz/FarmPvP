package xyz.sorridi.farmpvp.modules.player.essentials.commands.tabs;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalTabHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.HashSet;
import java.util.List;

public class GameModeTab implements FunctionalTabHandler<CommandSender>, IPlaceHolders
{
    private static final HashSet<String> ADMITTED = new HashSet<>(
            List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator", "s", "c", "a", "sp")
    );

    private final PlayerModule.Data.GameMode data = Serve.of(PlayerModule.Data.GameMode.class);

    @Nullable
    @Override
    public List<String> handle(CommandContext<CommandSender> c)
    {
        return switch (c.args().size())
        {
            case 1 -> data.getTabArgs1();
            case 2 ->
            {
                val _arg0 = c.arg(0).parse(String.class);

                if (_arg0.isPresent())
                {
                    yield ADMITTED.contains(_arg0.get()) ? null : EMPTY_LIST;
                }

                yield EMPTY_LIST;
            }
            default -> EMPTY_LIST;
        };
    }

}
