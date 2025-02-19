package xyz.sorridi.farmpvp.modules.player.essentials.commands;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.Optional;

import static xyz.sorridi.farmpvp.utils.Respective.reply;

public class GameModeCommand implements FunctionalCommandHandler<CommandSender>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final PlayerModule.InternalData internals = playerModule.getInternals();
    private final PlayerModule.Data.GameMode data = Serve.of(PlayerModule.Data.GameMode.class);

    @Override
    public void handle(@NotNull CommandContext<CommandSender> c)
    {
        val _arg0 = c.arg(0).parse(String.class);
        val _arg1 = c.arg(1).parse(String.class);

        Optional<FPlayer> _player;
        Optional<FPlayer> _target;

        FPlayer player = null;
        FPlayer target = null;

        String targetName;
        String arg0;
        String arg1;

        GameMode gameMode;

        boolean senderIsPlayer = c.sender() instanceof Player;

        if (senderIsPlayer)
        {
            _player = playersLife.getPlayerOrSendError((Player) c.sender());

            if (_player.isEmpty())
            {
                return;
            }

            player = _player.get();
            target = player;

            if (player.hasNotPermission(data.getGameModePermission()))
            {
                playerModule.replyNoPermission(player, data.getGameModePermission());
                return;
            }
        }

        if (_arg0.isEmpty())
        {
            reply(player, c, data.getHelpMessage());
            return;
        }

        arg0 = _arg0.get();

        switch (arg0)
        {
            case "0", "survival", "s"   -> gameMode = GameMode.SURVIVAL;
            case "1", "creative", "c"   -> gameMode = GameMode.CREATIVE;
            case "2", "adventure", "a"  -> gameMode = GameMode.ADVENTURE;
            case "3", "spectator", "sp" -> gameMode = GameMode.SPECTATOR;
            default ->
            {
                reply(player, c, data.getHelpMessage());
                return;
            }
        }

        if (_arg1.isPresent())
        {
            arg1 = _arg1.get();

            _target = playersLife.getPlayer(arg1);

            if (_target.isEmpty())
            {
                reply(player, c, internals.getUnknownPlayer());
                return;
            }

            target = _target.get();
        }
        else
        {
            if (!senderIsPlayer)
            {
                reply(player, c, data.getHelpMessage());
                return;
            }
        }

        targetName = target.getName();

        String gm = StringConverter.toProperCase(gameMode.name());

        String message = Replace.of(data.getChangeMessage(), GAMEMODE, gm);
        String otherMessage = Replace.of(data.getChangeOtherMessage(), TARGET_GAMEMODE, targetName, gm);

        if (target != player)
        {
            reply(player, c, otherMessage);
            target.reply(message);
        }
        else
        {
            reply(player, c, message);
        }

        target.getPlayer().setGameMode(gameMode);
    }

}
