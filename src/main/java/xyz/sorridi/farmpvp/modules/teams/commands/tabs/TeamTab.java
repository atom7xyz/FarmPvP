package xyz.sorridi.farmpvp.modules.teams.commands.tabs;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalTabHandler;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.modules.teams.TeamsModule;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class TeamTab implements FunctionalTabHandler<Player>, IPlaceHolders
{
    private static final HashSet<String> ADMITTED = new HashSet<>(List.of("invite", "kick", "of"));

    private final TeamsModule.Data data = Serve.of(TeamsModule.Data.class);

    @Nullable
    @Override
    public List<String> handle(CommandContext<Player> c)
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
