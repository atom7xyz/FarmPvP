package xyz.sorridi.farmpvp.modules.events.impl;

import lombok.NonNull;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.sorridi.farmpvp.annotations.events.Event;

@Event
public class ItemEvents implements TerminableModule
{

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(PlayerPickupItemEvent.class)
                .handler(e -> e.setCancelled(true))
                .bindWith(terminableConsumer);

        Events.subscribe(PlayerDropItemEvent.class)
                .handler(e -> e.setCancelled(true))
                .bindWith(terminableConsumer);
    }

}
