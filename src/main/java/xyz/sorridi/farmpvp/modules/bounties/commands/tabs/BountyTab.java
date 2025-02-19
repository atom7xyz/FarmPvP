package xyz.sorridi.farmpvp.modules.bounties.commands.tabs;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalTabHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.bounties.BountyModule;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.List;

public class BountyTab implements FunctionalTabHandler<Player>, IPlaceHolders
{
    private final BountyModule.Data data = Serve.of(BountyModule.Data.class);

    @Nullable
    @Override
    public List<String> handle(CommandContext<Player> c)
    {
        return switch (c.args().size())
        {
            case 1 -> data.getTabArgs1();
            case 2 -> null;
            case 3 ->
            {
                val _arg0 = c.arg(0).parse(String.class);

                if (_arg0.isPresent() && _arg0.get().equalsIgnoreCase("add"))
                {
                    yield data.getTabArgs3();
                }

                yield EMPTY_LIST;
            }
            default -> EMPTY_LIST;
        };
    }

}
