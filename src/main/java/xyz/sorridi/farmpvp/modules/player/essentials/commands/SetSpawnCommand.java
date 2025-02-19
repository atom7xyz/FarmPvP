package xyz.sorridi.farmpvp.modules.player.essentials.commands;

import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.Optional;

public class SetSpawnCommand implements FunctionalCommandHandler<Player>, IPlaceHolders
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
        Location location = thePlayer.getLocation();

        data.setSpawn(location);
        FarmPvP.spawnPoint = location;

        player.reply("&aPunto di spawn impostato alla tua posizione.");
    }

}
