package xyz.sorridi.farmpvp.modules.teams.commands;

import lombok.val;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.modules.teams.TeamsModule;
import xyz.sorridi.farmpvp.modules.teams.impl.Team;
import xyz.sorridi.farmpvp.modules.teams.impl.TeamsLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.NoSuchElementException;
import java.util.Optional;

public class TeamCommand implements FunctionalCommandHandler<Player>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final TeamsModule teamsModule = Serve.of(TeamsModule.class);
    private final TeamsLife teamsLife = teamsModule.getTeamsLife();

    private final TeamsModule.Data data = teamsModule.getData();
    private final TeamsModule.InternalData internals = teamsModule.getInternals();

    @Override
    public void handle(CommandContext<Player> c) throws NoSuchElementException
    {
        Optional<FPlayer> _player = playersLife.getPlayerOrSendError(c.sender());

        if (_player.isEmpty())
        {
            return;
        }

        Optional<Team> _team;
        Optional<Team> _targetTeam;
        Optional<FPlayer> _target;

        Team team;
        Team targetTeam;

        FPlayer player = _player.get();
        FPlayer target;

        Player thePlayer = player.getPlayer();

        val _arg0 = c.arg(0).parse(String.class);
        val _arg1 = c.arg(1).parse(String.class);

        String playerName = player.getName();
        String targetName;
        String ownerName;
        String arg0;
        String arg1;

        if (_arg0.isEmpty())
        {
            _team = teamsLife.getTeam(player);

            if (_team.isEmpty())
            {
                player.reply(data.getNoTeam());
                return;
            }

            team = _team.get();
            ownerName = team.getOwner().getName();

            val members = teamsLife.getFormattedTeam(team);
            player.reply(Replace.of(data.getTeamInfo(), TEAM_INFO, ownerName, team.getSize(), members));

            return;
        }

        arg0 = _arg0.get().toLowerCase();
        _team = teamsLife.getTeam(player);
        
        if (_arg1.isEmpty())
        {
            switch (arg0)
            {
                case "chat" ->
                {
                    if (_team.isEmpty())
                    {
                        player.reply(data.getNoTeam());
                        return;
                    }

                    boolean enabled = player.isTeamChatEnabled();

                    player.setTeamChatEnabled(!enabled);
                    player.reply(enabled ? data.getChatDisabled() : data.getChatEnabled());
                }
                case "create" ->
                {
                    if (teamsLife.createTeam(player))
                    {
                        player.reply(data.getTeamCreated());
                    }
                    else
                    {
                        player.reply(data.getAlreadyInATeam());
                    }
                }
                case "join" ->
                {
                    if (_team.isPresent())
                    {
                        player.reply(data.getAlreadyInATeam());
                        return;
                    }

                    _team = teamsLife.getInvite(player);

                    if (_team.isEmpty())
                    {
                        player.reply(data.getNoInvites());
                        return;
                    }

                    team = _team.get();
                    ownerName = team.getOwner().getName();

                    if (teamsLife.isFull(team))
                    {
                        player.reply(Replace.of(data.getTeamFull(), OWNER, ownerName));
                        return;
                    }

                    if (team.isMember(player))
                    {
                        player.reply(Replace.of(data.getAlreadyInTeamOf(), OWNER, ownerName));
                        return;
                    }

                    player.reply(Replace.of(data.getJoin(), OWNER, ownerName));
                    team.replyAll(Replace.of(data.getJoinAnnounce(), USER, playerName));

                    teamsLife.acceptInvite(player);
                }
                case "deny" ->
                {
                    _team = teamsLife.denyInvite(player);

                    if (_team.isEmpty())
                    {
                        player.reply(data.getNoInvites());
                        return;
                    }

                    team = _team.get();
                    ownerName = team.getOwner().getName();

                    player.reply(Replace.of(data.getDeny(), OWNER, ownerName));
                    team.replyAll(Replace.of(data.getDenyAnnounce(), USER, playerName));
                }
                case "leave" ->
                {
                    _team = teamsLife.leaveTeam(player);

                    if (_team.isEmpty())
                    {
                        player.reply(data.getNoTeam());
                        return;
                    }

                    team = _team.get();
                    ownerName = team.getOwner().getName();

                    if (team.isOwner(player))
                    {
                        player.reply(data.getDisband());
                        team.replyAllExceptOwner(Replace.of(data.getDisbandAnnounce(), OWNER, ownerName));
                        teamsLife.disbandTeam(team);
                        return;
                    }

                    player.reply(Replace.of(data.getLeave(), OWNER, ownerName));
                    team.replyAll(Replace.of(data.getLeaveAnnounce(), USER, playerName));
                }
                default -> teamsModule.replyHelp(player, data.getHelpMessage());
            }

            return;
        }

        arg1 = _arg1.get();
        _target = playersLife.getPlayer(arg1);

        if (_target.isEmpty())
        {
            player.reply(internals.getUnknownPlayer());
            return;
        }

        target = _target.get();
        targetName = target.getName();

        _targetTeam = teamsLife.getTeam(target);

        switch (arg0.toLowerCase())
        {
            case "invite" ->
            {
                if (_team.isEmpty())
                {
                    player.reply(data.getNoTeam());
                    return;
                }
                
                team = _team.get();
                
                if (!team.isOwner(player))
                {
                    player.reply(data.getMustBeOwner());
                    return;
                }

                if (_targetTeam.isPresent())
                {
                    targetTeam = _targetTeam.get();
                    ownerName = targetTeam.getOwner().getName();
                    
                    player.reply(Replace.of(data.getPlayerAlreadyInATeam(), USER_OWNER, targetName, ownerName));
                    return;
                }

                if (teamsLife.alreadyInvited(player, target))
                {
                    player.reply(Replace.of(data.getAlreadyInvited(), USER, targetName));
                    return;
                }

                teamsLife.invite(player, target);

                target.reply(Replace.of(data.getInvite(), OWNER, playerName));
                player.reply(Replace.of(data.getInvited(), USER, targetName));
                team.replyAllExceptOwner(Replace.of(data.getInvitedAnnounce(), USER, targetName));
            }
            case "kick" ->
            {
                if (_team.isEmpty())
                {
                    player.reply(data.getNoTeam());
                    return;
                }

                if (_targetTeam.isEmpty())
                {
                    player.reply(Replace.of(data.getPlayerNoTeam(), USER, targetName));
                    return;
                }

                team = _team.get();

                if (!team.isOwner(player))
                {
                    player.reply(data.getMustBeOwner());
                    return;
                }

                targetTeam = _targetTeam.get();
                ownerName = targetTeam.getOwner().getName();

                if (!team.isMember(target))
                {
                    player.reply(Replace.of(data.getPlayerNotInYourTeam(), USER, targetName));
                    return;
                }

                if (target.equals(player))
                {
                    player.reply(data.getKickNotPossible());
                    return;
                }

                teamsLife.kick(player, target);

                target.reply(data.getKick());
                player.reply(Replace.of(data.getKicked(), USER, targetName));
                team.replyAllExceptOwner(Replace.of(data.getKickAnnounce(), USER_OWNER, targetName, ownerName));
            }
            case "of" ->
            {
                if (_targetTeam.isEmpty())
                {
                    player.reply(Replace.of(data.getPlayerNoTeam(), USER, targetName));
                    return;
                }

                targetTeam = _targetTeam.get();
                ownerName = targetTeam.getOwner().getName();

                String members = teamsLife.getFormattedTeam(targetTeam);
                player.reply(Replace.of(data.getTeamInfo(), TEAM_INFO, ownerName, targetTeam.getSize(), members));
            }
            default -> player.reply(data.getHelpMessage());
        }
    }

}
