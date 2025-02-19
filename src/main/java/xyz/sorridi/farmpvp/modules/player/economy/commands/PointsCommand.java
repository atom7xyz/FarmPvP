package xyz.sorridi.farmpvp.modules.player.economy.commands;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.Respective;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.data.Array;

import java.util.Optional;

public class PointsCommand implements FunctionalCommandHandler<CommandSender>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final PlayerModule.InternalData internals = playerModule.getInternals();
    private final PlayerModule.Data.Points data = Serve.of(PlayerModule.Data.Points.class);

    @Override
    public void handle(@NotNull CommandContext<CommandSender> c)
    {
        val _arg0 = c.arg(0).parse(String.class);
        val _arg1 = c.arg(1).parse(String.class);

        Optional<FPlayer> _player;
        Optional<FPlayer> _target;

        FPlayer player = null;
        FPlayer target = null;

        Flag flag;

        String playerName;
        String targetName;
        String arg0;
        String arg1;

        boolean senderIsPlayer = c.sender() instanceof Player;

        double points;

        if (senderIsPlayer)
        {
            _player = playersLife.getPlayerOrSendError((Player) c.sender());

            if (_player.isEmpty())
            {
                return;
            }

            player = _player.get();
            target = player;
        }

        playerName = senderIsPlayer ? player.getName() : CONSOLE_NAME;

        if (_arg0.isEmpty())
        {
            if (senderIsPlayer)
            {
                points = target.getPoints();
                target.reply(Replace.of(data.getSeeMessage(), POINTS, points));
            }
            else
            {
                playerModule.replyHelp(c, data.getHelpMessage());
            }

            return;
        }

        arg0 = _arg0.get().toLowerCase();

        if (_arg1.isEmpty())
        {
            playerModule.replyHelp(c, data.getHelpMessage());
            return;
        }

        arg1 = _arg1.get();
        _target = playersLife.getPlayer(arg1);

        if (_target.isEmpty())
        {
            Respective.reply(player, c, Replace.of(internals.getUnknownPlayer(), TARGET, arg1));
            return;
        }

        target = _target.get();
        targetName = target.getName();
        flag = target.getFlag();

        switch (arg0)
        {
            case "of" ->
            {
                points = target.getPoints();
                Respective.reply(player, c, Replace.of(data.getSeeOtherMessage(), TARGET_PTS, targetName, points));
            }
            case "reset" ->
            {
                if (hasNotPerm(senderIsPlayer, player))
                {
                    return;
                }

                flag.resetPoints();

                Respective.reply(player, c, Replace.of(data.getResetMessage(), TARGET, targetName));
                target.reply(Replace.of(data.getResetStaffMessage(), TARGET_STAFFER, targetName, playerName));
            }
            case "set", "add", "remove" ->
            {
                if (hasNotPerm(senderIsPlayer, player))
                {
                    return;
                }

                val _points = c.arg(2).parse(Double.class);

                if (_points.isEmpty())
                {
                    playerModule.replyHelp(c, data.getHelpMessage());
                    return;
                }

                points = _points.orElse(MIN_POINTS);

                if (points <= MIN_POINTS || points > MAX_POINTS)
                {
                    c.reply(Replace.of(data.getMinMaxEditMessage(), MIN_MAX, MIN_POINTS, MAX_POINTS));
                    return;
                }

                String messageTarget = null;
                String messageStaffer = null;

                val totStaffPts = Array.of(points, playerName, 0);
                val totTargetPts = Array.of(points, targetName, 0);

                switch (arg0)
                {
                    case "set" ->
                    {
                        flag.setPoints(points);

                        totStaffPts[2] = flag.getPoints();
                        totTargetPts[2] = flag.getPoints();

                        messageStaffer  = Replace.of(data.getSetMessage(), TOT_TARGET_PTS, totTargetPts);
                        messageTarget   = Replace.of(data.getSetStaffMessage(), TOT_STAFF_PTS, totStaffPts);
                    }
                    case "add" ->
                    {
                        flag.addPoints(points);

                        totStaffPts[2] = flag.getPoints();
                        totTargetPts[2] = flag.getPoints();

                        messageStaffer  = Replace.of(data.getAddMessage(), TOT_TARGET_PTS, totTargetPts);
                        messageTarget   = Replace.of(data.getAddStaffMessage(), TOT_STAFF_PTS, totStaffPts);
                    }
                    case "remove" ->
                    {
                        flag.removePoints(points);

                        totStaffPts[2] = flag.getPoints();
                        totTargetPts[2] = flag.getPoints();

                        messageStaffer  = Replace.of(data.getRemoveMessage(), TOT_TARGET_PTS, totTargetPts);
                        messageTarget   = Replace.of(data.getRemoveStaffMessage(), TOT_STAFF_PTS, totStaffPts);
                    }
                }

                Respective.reply(player, c, messageStaffer);
                target.reply(messageTarget);
            }
            default -> playerModule.replyHelp(c, data.getHelpMessage());
        }
    }

    private boolean hasNotPerm(boolean senderIsPlayer, FPlayer player)
    {
        boolean result = senderIsPlayer && player.hasNotPermission(data.getEditPermission());

        if (result)
        {
            playerModule.replyNoPermission(player, data.getEditPermission());
        }

        return result;
    }

}
