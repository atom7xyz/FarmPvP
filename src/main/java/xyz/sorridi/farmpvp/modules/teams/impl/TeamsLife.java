package xyz.sorridi.farmpvp.modules.teams.impl;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.teams.TeamsModule;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.data.SingleHashMap;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamsLife implements IMemorize
{
    private final TeamsModule.Data data = Serve.of(TeamsModule.Data.class);

    /**
     * <ul>
     *     <li>Team 1: Reverse lookup for teams.</li>
     *     <li>Team 2: Latest invite received.</li>
     * </ul>
     */
    private final HashMap<FPlayer, MutablePair<Team, Team>> revInvited;
    private final SingleHashMap<Team> teams;

    public TeamsLife()
    {
        revInvited  = new HashMap<>();
        teams       = new SingleHashMap<>();
    }

    public Optional<MutablePair<Team, Team>> getData(@NonNull FPlayer player)
    {
        return Optional.ofNullable(revInvited.get(player));
    }

    public MutablePair<Team, Team> getDataOrThrow(@NonNull FPlayer player) throws NoSuchElementException
    {
        return getData(player).orElseThrow();
    }

    /*
     *  Getters and setters for MutablePair
     */

    /**
     * Get the current team of a player.
     * @param player The player.
     * @return The current team of a player.
     */
    private Optional<Team> getLeft(@NonNull FPlayer player) throws NoSuchElementException
    {
        return Optional.ofNullable(getDataOrThrow(player).getLeft());
    }

    /**
     * Set the current team of a player.
     * @param player The player.
     * @param team The team to assign.
     */
    private void setLeft(@NonNull FPlayer player, @Nullable Team team) throws NoSuchElementException
    {
        getDataOrThrow(player).setLeft(team);
    }

    /**
     * Get last invite received for a team.
     * @param player The player.
     * @return The invite received for a team.
     */
    private Optional<Team> getRight(@NonNull FPlayer player) throws NoSuchElementException
    {
        return Optional.ofNullable(getDataOrThrow(player).getRight());
    }

    /**
     * Set the last invite received for a team.
     * @param player The player.
     * @param team The team to set.
     */
    private void setRight(@NonNull FPlayer player, @Nullable Team team) throws NoSuchElementException
    {
        getDataOrThrow(player).setRight(team);
    }

    /*
     * Manage teams.
     */

    /**
     * Creates a team.
     * @param owner The owner of the team.
     * @return If the team was created.
     */
    public boolean createTeam(@NonNull FPlayer owner) throws NoSuchElementException
    {
        val _team = getTeam(owner);

        if (_team.isEmpty())
        {
            Team team = new Team(owner);

            teams.put(team);
            setLeft(owner, team);
        }

        return _team.isEmpty();
    }

    /**
     * Leaves a team. (Does not check for team ownership)
     * @param player The player to leave the team.
     * @return The team that the player left.
     */
    public Optional<Team> leaveTeam(@NonNull FPlayer player) throws NoSuchElementException
    {
        val _team = getTeam(player);

        _team.ifPresent(team ->
        {
            team.remove(player);
            setLeft(player, null);
        });

        return _team;
    }

    /**
     * Disbands a team.
     * @param team The team to disband.
     */
    public void disbandTeam(@NonNull Team team)
    {
        team.getFPlayers().forEach(p -> setLeft(p, null));
        team.removeAll();
        teams.remove(team);
    }

    /**
     * Accepts the invite that a player has.
     * @param invited The player to accept the invite.
     * @return The team that the player accepted the invite from.
     */
    public boolean acceptInvite(@NonNull FPlayer invited) throws NoSuchElementException
    {
        val _team = getInvite(invited);

        _team.ifPresent(team ->
        {
            team.add(invited);
            team.removeInvite(invited);

            setLeft(invited, team);
            setRight(invited, null);
        });

        return _team.isPresent();
    }

    /**
     * Denies the latest invite to a team.
     * @param target The player to deny the invite.
     */
    public Optional<Team> denyInvite(@NonNull FPlayer target) throws NoSuchElementException
    {
        val _team = getInvite(target);

        _team.ifPresent(team ->
        {
            team.removeInvite(target);
            setRight(target, null);
        });

        return _team;
    }

    /**
     * Invites a player to a team. (Does not check for team ownership)
     * @param from The player that invites.
     * @param invited The player that is invited.
     * @return If the player was invited.
     */
    public boolean invite(@NonNull FPlayer from, @NonNull FPlayer invited) throws NoSuchElementException
    {
        val _team       = getTeam(from);
        val _invTeam    = getTeam(invited);

        _team.ifPresent(team ->
        {
            if (_invTeam.isEmpty())
            {
                team.addInvite(invited);
                setRight(invited, team);
            }
        });

        return _team.isPresent() && _invTeam.isEmpty();
    }

    /**
     * Kicks a player from a team.
     * @param from The player that kicks.
     * @param kicked The player that is kicked.
     * @return If the player was kicked.
     */
    public boolean kick(@NonNull FPlayer from, @NonNull FPlayer kicked) throws NoSuchElementException
    {
        val _team = getTeam(from);

        _team.ifPresent(team ->
        {
            team.remove(kicked);
            setLeft(kicked, null);
        });

        return _team.isPresent();
    }

    /**
     * Gets the team of a player.
     * @param target The player to check.
     * @return The team of the player.
     */
    public Optional<Team> getTeam(@NonNull FPlayer target) throws NoSuchElementException
    {
        return getLeft(target);
    }

    /**
     * Checks if a player is in a team.
     * @param fPlayer The player to check.
     * @return If the player is in a team.
     */
    public boolean isInATeam(@NonNull FPlayer fPlayer) throws NoSuchElementException
    {
        return getTeam(fPlayer).isPresent();
    }

    /**
     * Checks if two players are in the same team.
     * @param fPlayer The first player to check.
     * @param fPlayer2 The second player to check.
     * @return If the two players are in the same team.
     */
    public boolean isInSameTeam(@NonNull FPlayer fPlayer, @NonNull FPlayer fPlayer2) throws NoSuchElementException
    {
        val _team    = getTeam(fPlayer);
        val _team2   = getTeam(fPlayer2);

        return _team.isPresent() && _team2.isPresent() && _team.get().equals(_team2.get());
    }

    public boolean alreadyInvited(@NonNull FPlayer fPlayer, @NonNull FPlayer toCheck) throws NoSuchElementException
    {
        val _team    = getTeam(fPlayer);
        val _team2   = getInvite(toCheck);

        return _team.isPresent() && _team2.isPresent() && _team.get().equals(_team2.get());
    }

    /**
     * Gets the team that invited a player.
     * @param fPlayer The player to check.
     * @return The team that invited the player.
     */
    public Optional<Team> getInvite(@NonNull FPlayer fPlayer) throws NoSuchElementException
    {
        return getRight(fPlayer);
    }

    /**
     * Gets the invites that a team has sent.
     * @param team The team to check.
     * @return The invites that the team has sent.
     */
    public Optional<HashSet<FPlayer>> getInvites(@NonNull Team team) throws NoSuchElementException
    {
        if (team.getInvites().size() == 0)
        {
            return Optional.empty();
        }

        return Optional.of(team.getInvites());
    }

    /**
     * Formats the list of players in a team.
     * @param fPlayer The player to get the team from.
     * @return The formatted list of players in a team.
     */
    public Optional<String> getFormattedTeam(@NonNull FPlayer fPlayer) throws NoSuchElementException
    {
        return getTeam(fPlayer).map(this::getFormattedTeam);
    }

    /**
     * Formats the list of players in a team.
     * @param team The team to get the players from.
     * @return The formatted list of players in a team.
     */
    public String getFormattedTeam(@NonNull Team team)
    {
        return team
                .getPlayers()
                .stream()
                .map(player -> (player.isOnline() ? "&a" : "&c") + player.getName())
                .collect(Collectors.joining(", "));
    }

    /**
     * Checks if a player has the permission to edit teams.
     * @param target The player to check.
     * @return If the player has the permission to edit teams.
     */
    public boolean canEdit(@NonNull FPlayer target)
    {
        return target.hasPermission(data.getEditPermission());
    }

    public boolean isFull(@NonNull Team team)
    {
        return team.size() >= data.getMaxTeamSize();
    }

    @Override
    public void memorize(@NotNull FPlayer player)
    {
        revInvited.putIfAbsent(player, MutablePair.of(null, null));
    }

    @Override
    public void forget(@NotNull FPlayer player)
    {

    }

}
