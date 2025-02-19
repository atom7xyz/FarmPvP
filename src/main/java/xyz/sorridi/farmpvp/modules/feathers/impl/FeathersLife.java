package xyz.sorridi.farmpvp.modules.feathers.impl;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.modules.feathers.FeathersModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.utils.ICDFormatter;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.builders.UseCoolDown;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.concurrent.TimeUnit;

@Getter
public class FeathersLife implements ICDFormatter<Player>, IMemorize
{
    private final FeathersModule.Data data = Serve.of(FeathersModule.Data.class);

    private final UseCoolDown<Player> coolDown;

    public FeathersLife()
    {
        coolDown = new UseCoolDown<>(data.getCd(), TimeUnit.SECONDS);
    }

    @Override
    public String getUsableRemaining(@NotNull Player player)
    {
        return StringConverter.fromMillisToHuman(TIME_SHORT_ITA, TIME_SHORT_ITA, coolDown.usableIn(player));
    }

    @Override
    public void memorize(@NotNull FPlayer player)
    {
        coolDown.putIfAbsent(player.getPlayer());
    }

    @Override
    public void forget(@NotNull FPlayer player)
    {
        Runnable task = () ->
        {
            Player thePlayer = player.getPlayer();

            if (coolDown.isUsable(thePlayer))
            {
                coolDown.remove(thePlayer);
            }
        };

        forgetTask(task, player, data.getCd(), TimeUnit.SECONDS, getClass());
    }

}
