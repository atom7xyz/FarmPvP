package xyz.sorridi.farmpvp.modules.events.impl;

import lombok.NonNull;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.World;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.annotations.events.Event;

@Event
public class WorldEvents implements TerminableModule
{

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(WeatherChangeEvent.class)
                .handler(e ->
                {
                    World world = e.getWorld();

                    world.setThunderDuration(0);
                    world.setWeatherDuration(0);

                    e.setCancelled(true);
                })
                .bindWith(terminableConsumer);

        Events.subscribe(PlayerSpawnLocationEvent.class)
                .handler(e -> e.setSpawnLocation(FarmPvP.spawnPoint))
                .bindWith(terminableConsumer);
    }

}
