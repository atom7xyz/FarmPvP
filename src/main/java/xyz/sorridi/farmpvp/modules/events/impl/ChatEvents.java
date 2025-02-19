package xyz.sorridi.farmpvp.modules.events.impl;

import lombok.NonNull;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvents implements TerminableModule
{

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    // TODO
                })
                .bindWith(terminableConsumer);
    }

}
