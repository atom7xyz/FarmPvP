package xyz.sorridi.farmpvp.modules.teams.impl;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.stone.utils.Reply;

import java.util.Collection;
import java.util.HashSet;

@Getter
public class Team
{
    private final FPlayer owner;
    private final HashSet<FPlayer> fPlayers;
    private final HashSet<Player> players;
    private final HashSet<FPlayer> invites;

    /**
     * Creates a new team.
     * @param owner The owner of the team.
     */
    public Team(@NonNull FPlayer owner)
    {
        this.owner = owner;
        fPlayers = new HashSet<>();
        players = new HashSet<>();
        invites = new HashSet<>();

        add(owner);
    }

    /**
     * Updates the entity id of the player.
     * <br>
     * I HATE SPIGOT'S DEFAULT EQUALS IMPLEMENTATION FOR CRAFTPLAYER!
     * <br>
     * WHY ENTITYID == ENTITYID? WHY NOT JUST UUID.EQUALS(UUID)? WHY? WHY? WHY? WHY? WHY? WHY? WHY? WHY?
     * <br>
     * I'M LOSING MY GRIP ON REALITY
     */
    public void updateEntityId(@NonNull FPlayer target)
    {
        if (!fPlayers.contains(target))
        {
            return;
        }

        players.stream()
                .filter(player -> player.getUniqueId().equals(target.getUuid())).findFirst()
                .ifPresent(players::remove);

        players.add(target.getPlayer());
    }

    /**
     * Adds a player to the team.
     * @param target The player to add.
     */
    public void add(@NonNull FPlayer target)
    {
        fPlayers.add(target);
        players.add(target.getPlayer());
    }

    /**
     * Removes a player from the team.
     * @param target The player to remove.
     * @return If the player was removed.
     */
    public void remove(@NonNull FPlayer target)
    {
        target.setTeamChatEnabled(false);

        fPlayers.remove(target);
        players.remove(target.getPlayer());
    }

    /**
     * Adds an invitation that the team has sent.
     * @param fPlayer The player to add the invite to.
     */
    public void addInvite(@NonNull FPlayer fPlayer)
    {
        invites.add(fPlayer);
    }

    /**
     * Removes an invitation that the team has sent.
     * @param fPlayer The player to remove the invite from.
     */
    public void removeInvite(@NonNull FPlayer fPlayer)
    {
        invites.remove(fPlayer);
    }

    /**
     * Get the size of the team.
     * @return the size of the team.
     */
    public int size()
    {
        return fPlayers.size();
    }

    /**
     * Check if the player is the owner of the team.
     * @param fPlayer The player to check.
     * @return If the player is the owner of the team.
     */
    public boolean isOwner(@NonNull FPlayer fPlayer)
    {
        return owner.equals(fPlayer);
    }
    
    /**
     * Check if the player is a member of the team.
     * @param fPlayer The player to check.
     * @return If the player is a member of the team.
     */
    public boolean isMember(@NonNull FPlayer fPlayer)
    {
        return fPlayers.contains(fPlayer) || isOwner(fPlayer);
    }

    /**
     * Removes all players from the team.
     */
    public void removeAll()
    {
        fPlayers.forEach(player -> player.setTeamChatEnabled(false));
        fPlayers.clear();
        players.clear();
    }

    /**
     * Gets the number of players in the team.
     * @return The number of players in the team.
     */
    public int getSize()
    {
        return fPlayers.size();
    }

    /**
     * Gets the number of invites that the team has sent.
     * @return The number of invites that the team has sent.
     */
    public int getNumInvites()
    {
        return invites.size();
    }

    /**
     * Gets the list of bukkit players in the team.
     * @return The list of bukkit players in the team.
     */
    public Collection<Player> getBukkitPlayers()
    {
        return players;
    }

    public Collection<String> getPlayerNames()
    {
        return players.stream().map(Player::getName).toList();
    }

    /**
     * Replies to all players in the team.
     * @param messages The messages to send.
     */
    public void replyAll(@NonNull String... messages)
    {
        Reply.toAll(players, messages);
    }

    /**
     * Replies to all players in the team.
     * @param messages The messages to send.
     */
    public <C extends Collection<String>> void replyAll(@NonNull C messages)
    {
        Reply.toAll(players, messages);
    }

    /**
     * Replies to all players in the team except the owner.
     * @param messages The messages to send.
     */
    public void replyAllExceptOwner(@NonNull String... messages)
    {
        Reply.toAllExcept(players, owner.getPlayer(), messages);
    }

    /**
     * Replies to all players in the team except the owner.
     * @param messages The messages to send.
     */
    public <C extends Collection<String>> void replyAllExceptOwner(@NonNull C messages)
    {
        Reply.toAllExcept(players, owner.getPlayer(), messages);
    }

}
