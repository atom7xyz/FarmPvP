package xyz.sorridi.farmpvp.modules.player.commands;

import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.Optional;

public class IgnorePingsCommand implements FunctionalCommandHandler<Player>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final PlayerModule.Data.Chat data = Serve.of(PlayerModule.Data.Chat.class);

    @Override
    public void handle(@NotNull CommandContext<Player> c)
    {
        Optional<FPlayer> _player = playersLife.getPlayerOrSendError(c.sender());

        if (_player.isEmpty())
        {
            return;
        }

        FPlayer player = _player.get();

        player.setIgnorePings(!player.isIgnorePings());
        player.reply(player.isIgnorePings() ? data.getEnabledPings() : data.getDisabledPings());
    }

}
