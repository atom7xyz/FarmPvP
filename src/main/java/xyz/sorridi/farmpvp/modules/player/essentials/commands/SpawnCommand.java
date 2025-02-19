package xyz.sorridi.farmpvp.modules.player.essentials.commands;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import io.papermc.lib.PaperLib;
import lombok.NonNull;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.ICDFormatter;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.bukkit.location.LocationEvaluate;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.Optional;

@Setter
public class SpawnCommand implements FunctionalCommandHandler<Player>, IPlaceHolders, ICDFormatter<FPlayer>
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final PlayerModule.Data.Spawn data = Serve.of(PlayerModule.Data.Spawn.class);

    @Override
    public void handle(@NotNull CommandContext<Player> c)
    {
        Player thePlayer = c.sender();
        Optional<FPlayer> _player = playersLife.getPlayerOrSendError(thePlayer);

        if (_player.isEmpty())
        {
            return;
        }

        FPlayer player = _player.get();
        Optional<Location> _flagLocation = player.getFlag().getLocation();

        if (_flagLocation.isPresent())
        {
            player.reply(data.getMustNotHaveBanner());
            return;
        }

        if (player.getTeleportIn() != -1)
        {
            player.reply(data.getAlreadyTeleporting());
            return;
        }

        player.setTeleportIn(data.getTeleportDelay());
        player.reply(data.getTeleportInit());

        Location preLocation = thePlayer.getLocation();

        Schedulers.sync()
                .runRepeating(task ->
                {
                    boolean isStill = LocationEvaluate.isSimilar(preLocation, thePlayer.getLocation());

                    if (!player.isOnline() || !isStill || player.isDead())
                    {
                        player.setTeleportIn(-1);

                        if (!isStill && !player.isDead())
                        {
                            ActionBarAPI.sendActionBar(thePlayer, EMPTY_STRING);
                            player.reply(data.getTeleportCancelled());
                        }

                        task.stop();
                    }
                    else
                    {
                        int teleportIn = player.getTeleportIn();

                        if (teleportIn == 0)
                        {
                            PaperLib.teleportAsync(thePlayer, FarmPvP.spawnPoint);
                            ActionBarAPI.sendActionBar(thePlayer, EMPTY_STRING);

                            player.setTeleportIn(-1);
                            task.stop();
                        }
                        else
                        {
                            String message = Replace.of(data.getTeleportingBar(), TIME, getUsableRemaining(player));
                            ActionBarAPI.sendActionBar(thePlayer, message);

                            player.decrementTeleportIn();
                        }
                    }
                }, 0, ONE_CLOCK);
    }

    @Override
    public String getUsableRemaining(@NonNull FPlayer target)
    {
        long remaining = target.getTeleportIn() * 1000L;
        return StringConverter.fromMillisToHuman(TIME_FULL_ITA_PLU, TIME_FULL_ITA_SIN, remaining);
    }

}
