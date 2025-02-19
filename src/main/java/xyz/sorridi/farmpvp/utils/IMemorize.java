package xyz.sorridi.farmpvp.utils;

import lombok.NonNull;
import me.lucko.helper.Schedulers;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.Reply;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public interface IMemorize extends IPlaceHolders
{
    void memorize(@NonNull FPlayer player);
    void forget(@NonNull FPlayer player);

    /**
     * Forgets after a delay.
     * @param runnable Runnable to run.
     * @param player Player to forget.
     * @param delay Delay in seconds.
     * @param unit Unit of time.
     * @param from Class from where the task is called.
     */
    default void forgetTask(@Nullable Runnable runnable, @NonNull FPlayer player, long delay, TimeUnit unit, Class<?> from)
    {
        checkArgument(delay > 0, "Delay must be greater than 0.");

        if (runnable == null)
        {
            return;
        }

        long time = System.currentTimeMillis();

        Schedulers.sync().runLater(() ->
        {
            if (time <= player.getLastLogin())
            {
                return;
            }

            String name = player.getName();
            String fromName = from.getSimpleName();
            String message;

            try
            {
                runnable.run();

                message = Replace.of(FORGET_MESSAGE, NAME_FROM, name, fromName);

                FarmPvP.info(message);
            }
            catch (Exception e)
            {
                message = Replace.of(ERROR_MESSAGE, NAME_FROM, name, fromName);

                FarmPvP.severe(message);
                e.printStackTrace();
            }

            Reply.toAllWithPerm(DEBUG_PERM, message);

        }, unit.toSeconds(delay) * ONE_CLOCK);
    }
}
