package xyz.sorridi.farmpvp.modules.webs.impl;

import lombok.Getter;
import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.webs.WebModule;
import xyz.sorridi.farmpvp.utils.ICDFormatter;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Getter
public class WebsLife implements ICDFormatter<FPlayer>, IMemorize
{
    private final WebModule.Data data = Serve.of(WebModule.Data.class);
    
    private final HashMap<FPlayer, Queue<Long>> timings;
    
    public WebsLife()
    {
        timings = new HashMap<>();
    }

    /**
     * Adds a cobweb to the list of cobwebs placed.
     * @param player Player that placed the cobweb.
     * @return If the cobweb has been added.
     */
    public boolean add(@NonNull FPlayer player)
    {
        Queue<Long> queue = timings.get(player);
        long now = System.currentTimeMillis();

        if (queue.size() < data.getAmountForCd() || queue.size() == 0)
        {
            queue.add(now);
        }
        else
        {
            if (now - queue.peek() > data.getCd())
            {
                queue.poll();
                queue.add(now);
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the cobweb timings of a player.
     * @param player The player to get the timings of.
     * @return The cobweb timings of the player.
     */
    public Optional<Queue<Long>> getTimings(@NonNull FPlayer player)
    {
        return Optional.ofNullable(timings.get(player));
    }

    /**
     * Gets the last web timing of a player.
     * @param player The player to get the timing of.
     * @return The last web timing of the player.
     */
    public long getLast(@NonNull FPlayer player)
    {
        return getTimings(player)
            .filter(longs -> longs.size() != 0)
            .map(Queue::peek)
            .orElse(0L);
    }

    /**
     * Clears the cobweb timings. Used in server reloads.
     */
    public void clearTimings()
    {
        timings.clear();
    }

    @Override
    public String getUsableRemaining(@NonNull FPlayer target)
    {
        long remaining = data.getCd() - (System.currentTimeMillis() - getLast(target));
        return StringConverter.fromMillisToHuman(TIME_FULL_ITA_PLU, TIME_FULL_ITA_SIN, remaining);
    }

    @Override
    public void memorize(@NonNull FPlayer player)
    {
        timings.putIfAbsent(player, new LinkedList<>());
    }

    @Override
    public void forget(@NonNull FPlayer player)
    {
        Runnable task = () -> timings.remove(player);

        forgetTask(task, player, data.getCd(), TimeUnit.MILLISECONDS, getClass());
    }

}
