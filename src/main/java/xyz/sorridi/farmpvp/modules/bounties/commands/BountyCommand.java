package xyz.sorridi.farmpvp.modules.bounties.commands;

import lombok.NonNull;
import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.modules.bounties.BountyModule;
import xyz.sorridi.farmpvp.modules.bounties.impl.BountiesLife;
import xyz.sorridi.farmpvp.modules.bounties.impl.Bounty;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.Reply;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.data.Array;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;

public class BountyCommand implements FunctionalCommandHandler<Player>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final BountyModule bountyModule = Serve.of(BountyModule.class);
    private final BountiesLife bountiesLife = bountyModule.getBountiesLife();

    private final BountyModule.Data data = bountiesLife.getData();
    private final BountyModule.InternalData internals = bountiesLife.getInternals();

    @Override
    public void handle(@NonNull CommandContext<Player> c) throws NoSuchElementException
    {
        Optional<FPlayer> _player = playersLife.getPlayerOrSendError(c.sender());

        if (_player.isEmpty())
        {
            return;
        }

        Optional<FPlayer> _target;

        Optional<Integer> _value;
        Optional<Integer> _totVal;
        Optional<Bounty> _bounty;

        int value;
        int totVal;
        Bounty bounty;

        FPlayer player = _player.get();
        FPlayer target;

        Player thePlayer = player.getPlayer();

        Flag flag = player.getFlag();

        val _arg0 = c.arg(0).parse(String.class);
        val _arg1 = c.arg(1).parse(String.class);

        String playerName = player.getName();
        String targetName;
        String arg0;
        String arg1;

        if (_arg0.isEmpty())
        {
            _value = bountiesLife.getBountiesValue(player);
            value = _value.orElse(0);

            if (value > 0)
            {
                c.reply(Replace.of(data.getSeeMessage(), TOTAL, value));
            }
            else
            {
                c.reply(data.getOwnBountyZeroMessage());
            }

            return;
        }

        arg0 = _arg0.get();

        if (arg0.equalsIgnoreCase("remove"))
        {
            if (!bountiesLife.canAssignBounty(player))
            {
                printTime(player);
                return;
            }

            val removed = bountiesLife.removeBounty(player);

            if (removed.isEmpty())
            {
                c.reply(data.getNoBountyYetMessage());
                return;
            }

            val remBounty = removed.get();

            target = remBounty.getTarget();
            targetName = target.getName();

            int tot = remBounty.getValue();
            int newValue = bountiesLife.getBountiesValue(target).orElse(0);

            val toReplace   = Array.of(USER, TOTAL, TARGET, TOT_BNT);
            val replace     = Array.of(playerName, tot, targetName, newValue);
            val message     = Replace.of(data.getRemovedBroadcastMessage(), toReplace, replace);

            Reply.toAllExcept(thePlayer, message);
            player.reply(Replace.of(data.getRemoveMessage(), TARGET_TOT, targetName, tot));

            return;
        }

        if (_arg1.isEmpty())
        {
            player.reply(data.getHelpMessage());
            return;
        }

        arg1 = _arg1.get();
        _target = playersLife.getPlayer(arg1);

        switch (arg0.toLowerCase())
        {
            case "of" ->
            {
                if (_target.isEmpty())
                {
                    c.reply(internals.getUnknownPlayer());
                    return;
                }

                target = _target.get();
                targetName = target.getName();

                _value = bountiesLife.getBountiesValue(target);
                value = _value.orElse(0);

                if (value > 0)
                {
                    c.reply(Replace.of(data.getSeeOtherMessage(), TARGET_TOT, targetName, value));
                }
                else
                {
                    c.reply(Replace.of(data.getOtherBountyZeroMessage(), TARGET, targetName));
                }
            }
            case "reset" ->
            {
                if (player.hasNotPermission(data.getEditPermission()))
                {
                    bountyModule.replyNoPermission(player, data.getEditPermission());
                    return;
                }

                if (_target.isEmpty())
                {
                    c.reply(internals.getUnknownPlayer());
                    return;
                }

                target = _target.get();
                targetName = target.getName();

                val list = bountiesLife.getBountiesAssignedTo(target);
                val queue = new Stack<Bounty>();

                list.ifPresent(all -> all.forEach(queue::push));

                while (!queue.isEmpty())
                {
                    bounty = queue.pop();

                    FPlayer from = bounty.getFrom();
                    int tot = bounty.getValue();

                    bountiesLife.removeBounty(bounty.getFrom());
                    from.addPoints(tot, true, Sound.NOTE_PLING);
                }

                bountiesLife.setBountiesAssignedTo(target, null);

                val message = Replace.of(data.getResetStafferMessage(), TARGET_STAFFER, targetName, playerName);

                c.reply(Replace.of(data.getResetMessage(), TARGET, targetName));
                Reply.toAllExcept(thePlayer, message);
            }
            case "add" ->
            {
                val _points = c.arg(2).parse(Integer.class);

                if (_points.isEmpty())
                {
                    player.reply(data.getHelpMessage());
                    return;
                }

                if (_target.isEmpty())
                {
                    c.reply(internals.getUnknownPlayer());
                    return;
                }

                target = _target.get();
                targetName = target.getName();

                if (!target.isOnline())
                {
                    bountyModule.replyOffline(player, targetName);
                    return;
                }

                if (player == target)
                {
                    c.reply(data.getSetSelfMessage());
                    return;
                }

                int points = _points.orElse(0);

                if (points < data.getMinValue())
                {
                    c.reply(Replace.of(data.getMinValueMessage(), TOTAL, data.getMinValue()));
                    return;
                }

                if (points > data.getMaxValue())
                {
                    c.reply(Replace.of(data.getMaxValueMessage(), TOTAL, data.getMaxValue()));
                    return;
                }

                try
                {
                    _bounty = bountiesLife.getAssignedBounty(player);
                }
                catch (NoSuchElementException ignored)
                {
                    bountyModule.replyNoRecentData(player, targetName);
                    return;
                }

                if (_bounty.isPresent())
                {
                    bounty = _bounty.get();
                    value = bounty.getValue();

                    c.reply(Replace.of(data.getBountyAlreadySetMessage(), TARGET_TOT, targetName, value));
                    return;
                }

                if (!bountiesLife.canAssignBounty(player))
                {
                    printTime(player);
                    return;
                }

                if (!flag.hasPoints(points))
                {
                    c.reply(internals.getNoPoints());
                    return;
                }

                bountiesLife.addBounty(new Bounty(player, target, points));
                bountiesLife.assignBounty(player);

                flag.removePoints(points);

                _totVal = bountiesLife.getBountiesValue(target);
                totVal = _totVal.orElse(0);

                val repl = Array.of(FROM, TOTAL, TARGET, TOT_BNT);
                val msgs = Replace.of(data.getAddBroadcastMessage(), repl, playerName, points, targetName, totVal);

                Reply.toAllExcept(thePlayer, msgs);
                c.reply(Replace.of(data.getAddMessage(), TARGET_TOT, targetName, points));
            }
            default -> player.reply(data.getHelpMessage());
        }
    }

    /**
     * Prints the cd left to the player for the assignment of a new bounty.
     * @param player The player.
     */
    private void printTime(FPlayer player)
    {
        String message = internals.getCdMessage();
        String time = bountiesLife.getUsableRemaining(player);

        player.reply(Replace.of(message, TIME, time));
    }

}
