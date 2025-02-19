package xyz.sorridi.farmpvp.modules.player.economy.commands;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.events.api.LevelChangeEvent;
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

public class LevelsCommand implements FunctionalCommandHandler<CommandSender>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final PlayerModule.InternalData internals = playerModule.getInternals();
    private final PlayerModule.Data.Levels data = Serve.of(PlayerModule.Data.Levels.class);

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

        int levels;

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
                levels = target.getLevels();
                target.reply(Replace.of(data.getSeeMessage(), LEVELS, levels));
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
                levels = target.getLevels();
                Respective.reply(player, c, Replace.of(data.getSeeOtherMessage(), TARGET_LVLS, targetName, levels));
            }
            case "reset" ->
            {
                if (hasNotPerm(senderIsPlayer, player))
                {
                    return;
                }

                FarmPvP.callEvent(new LevelChangeEvent(target, 0, LevelChangeEvent.Operation.SET));

                Respective.reply(player, c, Replace.of(data.getResetMessage(), TARGET, targetName));
                target.reply(Replace.of(data.getResetStaffMessage(), TARGET_STAFFER, targetName, playerName));
            }
            case "set", "add", "remove" ->
            {
                if (hasNotPerm(senderIsPlayer, player))
                {
                    return;
                }

                val _levels = c.arg(2).parse(Integer.class);

                if (_levels.isEmpty())
                {
                    playerModule.replyHelp(c, data.getHelpMessage());
                    return;
                }

                levels = _levels.orElse(MIN_LEVELS);

                if (levels <= MIN_LEVELS || levels > MAX_LEVELS)
                {
                    c.reply(Replace.of(data.getMinMaxEditMessage(), MIN_MAX, MIN_LEVELS, MAX_LEVELS));
                    return;
                }

                String messageTarget = null;
                String messageStaffer = null;

                val totStaffLvls = Array.of(levels, playerName, 0);
                val totTargetLvls = Array.of(levels, targetName, 0);

                switch (arg0)
                {
                    case "set" ->
                    {
                        FarmPvP.callEvent(new LevelChangeEvent(target, levels, LevelChangeEvent.Operation.SET));

                        totStaffLvls[2] = flag.getLevels();
                        totTargetLvls[2] = flag.getLevels();

                        messageStaffer  = Replace.of(data.getSetMessage(), TOT_TARGET_LVLS, totTargetLvls);
                        messageTarget   = Replace.of(data.getSetStaffMessage(), TOT_STAFF_LVLS, totStaffLvls);
                    }
                    case "add" ->
                    {
                        FarmPvP.callEvent(new LevelChangeEvent(target, levels, LevelChangeEvent.Operation.ADD));

                        totStaffLvls[2] = flag.getLevels();
                        totTargetLvls[2] = flag.getLevels();

                        messageStaffer  = Replace.of(data.getAddMessage(), TOT_TARGET_LVLS, totTargetLvls);
                        messageTarget   = Replace.of(data.getAddStaffMessage(), TOT_STAFF_LVLS, totStaffLvls);
                    }
                    case "remove" ->
                    {
                        FarmPvP.callEvent(new LevelChangeEvent(target, levels, LevelChangeEvent.Operation.REMOVE));

                        totStaffLvls[2] = flag.getLevels();
                        totTargetLvls[2] = flag.getLevels();

                        messageStaffer  = Replace.of(data.getRemoveMessage(), TOT_TARGET_LVLS, totTargetLvls);
                        messageTarget   = Replace.of(data.getRemoveStaffMessage(), TOT_STAFF_LVLS, totStaffLvls);
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
