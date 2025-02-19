package xyz.sorridi.farmpvp.modules.bounties.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.bounties.BountyModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.utils.ICDFormatter;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.builders.UseCoolDown;
import xyz.sorridi.stone.utils.Reply;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class BountiesLife implements ICDFormatter<FPlayer>, IMemorize
{
    private final BountyModule.Data data = Serve.of(BountyModule.Data.class);
    private final BountyModule.InternalData internals = Serve.of(BountyModule.InternalData.class);

    private final UseCoolDown<FPlayer> coolDown;

    /**
     * <ul>
     *     <li><b>Bounty</b> -> Bounty assigned from the player. </li>
     *     <li><b>Integer</b> -> Total bounties value for the head of the player.</li>
     *     <li><b>List<Bounty></b> -> Bounties assigned to the player.</li>
     * </ul>
     */
    private final HashMap<FPlayer, MutableTriple<Bounty, Integer, List<Bounty>>> bounties;

    public BountiesLife()
    {
        bounties = new HashMap<>();
        coolDown = new UseCoolDown<>(data.getCd(), TimeUnit.SECONDS);
    }

    public Optional<MutableTriple<Bounty, Integer, List<Bounty>>> getData(@NonNull FPlayer player)
    {
        return Optional.ofNullable(bounties.get(player));
    }

    public MutableTriple<Bounty, Integer, List<Bounty>> getDataOrThrow(@NonNull FPlayer player)
            throws NoSuchElementException
    {
        return getData(player).orElseThrow();
    }

    /*
     *  Getters and setters for MutableTriple
     */

    /**
     * Get the bounty that the player has assigned.
     * @param player The player.
     * @return The bounty that the player has assigned.
     */
    private Optional<Bounty> getLeft(@NonNull FPlayer player) throws NoSuchElementException
    {
        return Optional.ofNullable(getDataOrThrow(player).getLeft());
    }

    /**
     * Set the bounty assigned to the player.
     * @param player The player.
     * @param bounty The bounty to assign.
     */
    private void setLeft(@NonNull FPlayer player, @Nullable Bounty bounty) throws NoSuchElementException
    {
        getDataOrThrow(player).setLeft(bounty);
    }

    /**
     * Get the total bounties value assigned to the player.
     * @param player The player.
     * @return The total bounties value assigned to the player.
     */
    private Optional<Integer> getMiddle(@NonNull FPlayer player) throws NoSuchElementException
    {
        return Optional.ofNullable(getDataOrThrow(player).getMiddle());
    }

    /**
     * Set the total bounties value assigned to the player.
     * @param player The player.
     * @param value The value to set.
     */
    private void setMiddle(@NonNull FPlayer player, @Nullable Integer value) throws NoSuchElementException
    {
        getDataOrThrow(player).setMiddle(value);
    }

    /**
     * Get the list of bounties assigned to the player.
     * @param player The player.
     * @return The list of bounties assigned to the player.
     */
    private Optional<List<Bounty>> getRight(@NonNull FPlayer player) throws NoSuchElementException
    {
        return Optional.ofNullable(getDataOrThrow(player).getRight());
    }

    /**
     * Set the list of bounties assigned to the player.
     * @param player The player.
     * @param list The list to set.
     */
    private void setRight(@NonNull FPlayer player, @Nullable List<Bounty> list) throws NoSuchElementException
    {
        getDataOrThrow(player).setRight(list);
    }

    /*
     * Manage bounties.
     */

    /**
     * Get the bounty that the player has assigned.
     * @param player The player.
     * @return The bounty that the player has assigned.
     */
    public Optional<Bounty> getAssignedBounty(@NonNull FPlayer player) throws NoSuchElementException
    {
        return getLeft(player);
    }

    /**
     * Set the bounty assigned from the player.
     *
     * @param player The player.
     * @param bounty The bounty to assign.
     */
    public void setAssignedBounty(@NonNull FPlayer player, @Nullable Bounty bounty) throws NoSuchElementException
    {
        setLeft(player, bounty);
    }

    /**
     * Get the total bounties value assigned to the player.
     * @param player The player.
     * @return The total bounties value assigned to the player.
     */
    public Optional<Integer> getBountiesValue(@NonNull FPlayer player) throws NoSuchElementException
    {
        return getMiddle(player);
    }

    /**
     * Set the total bounties value assigned to the player.
     * @param player The player.
     * @param value The value to set.
     */
    public void setBountiesValue(@NonNull FPlayer player, int value) throws NoSuchElementException
    {
        setMiddle(player, value);
    }

    /**
     * Add a value to the total bounties value assigned to the player.
     * @param player The player.
     * @param value The value to add.
     */
    public void addBountiesValue(@NonNull FPlayer player, int value) throws NoSuchElementException
    {
        int sum = getBountiesValue(player).orElse(0) + value;

        setMiddle(player, sum == 0 ? null : sum);
    }

    /**
     * Remove a value from the total bounties value assigned to the player.
     * @param player The player.
     * @param value The value to remove.
     */
    public void removeBountiesValue(@NonNull FPlayer player, int value) throws NoSuchElementException
    {
        int sub = getBountiesValue(player).orElse(0) - value;

        setMiddle(player, sub == 0 ? null : sub);
    }

    /**
     * Get the list of bounties assigned to the player.
     * @param player The player.
     * @return The list of bounties assigned to the player.
     */
    public Optional<List<Bounty>> getBountiesAssignedTo(@NonNull FPlayer player) throws NoSuchElementException
    {
        return getRight(player);
    }

    /**
     * Set the list of bounties assigned to the player.
     * @param player The player.
     * @param list The list to set.
     */
    public void setBountiesAssignedTo(@NonNull FPlayer player, @Nullable List<Bounty> list)
            throws NoSuchElementException
    {
        setRight(player, list);
    }

    /**
     * Add a bounty to the list of bounties assigned to the player.
     * @param player The player.
     * @param bounty The bounty to add.
     */
    public void addBountiesAssignedTo(@NonNull FPlayer player, @NonNull Bounty bounty)
            throws NoSuchElementException
    {
        val data = getBountiesAssignedTo(player);

        if (data.isEmpty())
        {
            val list = new ArrayList<Bounty>();
            list.add(bounty);

            setRight(player, list);
        }
        else
        {
            data.get().add(bounty);
        }
    }

    /**
     * Remove a bounty from the list of bounties assigned to the player.
     * @param player The player.
     * @param bounty The bounty to remove.
     */
    public void removeBountiesAssignedTo(@NonNull FPlayer player, @NonNull Bounty bounty)
    {
        getBountiesAssignedTo(player).ifPresent(bounties -> bounties.remove(bounty));
    }

    /**
     * Adds a bounty to a player.
     * @param bounty The bounty to add.
     * @return If the bounty was added.
     */
    public boolean addBounty(@NonNull Bounty bounty) throws NoSuchElementException
    {
        FPlayer from = bounty.getFrom();
        val existingBounty = getAssignedBounty(from);

        if (existingBounty.isEmpty())
        {
            FPlayer target = bounty.getTarget();
            int value = bounty.getValue();

            setAssignedBounty(from, bounty);
            addBountiesAssignedTo(target, bounty);
            addBountiesValue(target, value);
        }

        return existingBounty.isEmpty();
    }

    /**
     * Removes a bounty that a player has assigned.
     * @param player The player that has assigned the bounty.
     * @return The bounty removed.
     */
    public Optional<Bounty> removeBounty(@NonNull FPlayer player) throws NoSuchElementException
    {
        val _bounty = getAssignedBounty(player);

        _bounty.ifPresent(bounty ->
        {
            FPlayer target = bounty.getTarget();

            setAssignedBounty(player, null);
            removeBountiesAssignedTo(target, bounty);
            removeBountiesValue(target, bounty.getValue());
        });

        return _bounty;
    }

    /**
     * Takes the bounty from a player.
     * @param killer The player that takes the bounty.
     * @param victim The player that the bounty is assigned to.
     * @return If the bounty was taken.
     */
    public Optional<List<Bounty>> takeBounties(@NonNull FPlayer killer, @NonNull FPlayer victim)
            throws NoSuchElementException
    {
        val _bounty = getBountiesAssignedTo(victim);

        _bounty.ifPresent(bounty ->
        {
            val totValue = getBountiesValue(victim);

            totValue.ifPresent(value ->
            {
                killer.addPoints(value);
                bounty.stream().map(Bounty::getFrom).toList().forEach(this::removeBounty);
            });
        });

        return _bounty;
    }

    /**
     * Checks if the player can assign a bounty considering the cd of the last bounty assigned.
     * @param player The player to check.
     * @return If the player can assign a bounty.
     */
    public boolean canAssignBounty(@NonNull FPlayer player)
    {
        return coolDown.isUsable(player);
    }

    /**
     * If the player can assign a bounty updates the cd of the assignment of bounties to the player.
     *
     * @param player The player to check.
     */
    public void assignBounty(@NonNull FPlayer player)
    {
        if (canAssignBounty(player))
        {
            coolDown.renew(player);
        }
    }

    /**
     * Checks if the player can be forgotten.
     * @param player The player to check.
     * @return If the player can be forgotten.
     */
    public boolean isForgettable(@NonNull FPlayer player) throws NoSuchElementException
    {
        return getAssignedBounty(player).isEmpty() && getBountiesAssignedTo(player).isEmpty();
    }

    @Override
    public String getUsableRemaining(@NotNull FPlayer player)
    {
        return StringConverter.fromMillisToHuman(TIME_FULL_ITA_PLU, TIME_FULL_ITA_SIN, coolDown.usableIn(player));
    }

    @Override
    public void memorize(@NotNull FPlayer player)
    {
        bounties.putIfAbsent(player, MutableTriple.of(null, null, null));
        coolDown.putIfAbsent(player);
    }

    @Override
    public void forget(@NotNull FPlayer player)
    {
        Runnable task = () ->
        {
            try
            {
                if (getAssignedBounty(player).isEmpty() && getBountiesAssignedTo(player).isEmpty())
                {
                    bounties.remove(player);
                }

                if (coolDown.isUsable(player))
                {
                    coolDown.remove(player);
                }
            }
            catch (NoSuchElementException ignored)
            {
                String message = "Ignored error while forgetting player " + player.getName() + ".";
                Reply.toAllWithPerm(internals.getDebugPermission(), message);
            }
        };

        forgetTask(task, player, data.getCd(), TimeUnit.MINUTES, getClass());
    }

}
